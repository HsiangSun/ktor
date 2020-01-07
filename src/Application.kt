package cn.hsiangsun

import cn.hsiangsun.auth.SimpleJWT
import cn.hsiangsun.bean.LoginRegister
import cn.hsiangsun.bean.User
import cn.hsiangsun.bean.Users
import cn.hsiangsun.exception.InvalidCredentialsException
import cn.hsiangsun.exception.InvalidLoginException
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*
import io.ktor.auth.jwt.jwt
import io.ktor.jackson.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val simpleJWT = SimpleJWT("thisissolt123456")
    install(Authentication){
        jwt {
            verifier(simpleJWT.verifier)
            validate{
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }

    install(StatusPages){
        exception<InvalidCredentialsException>{
            e -> call.respond(HttpStatusCode.Unauthorized, mapOf("ok" to false,"error" to (e.message ?: "")))
        }
        exception<InvalidLoginException>{
            e -> call.respond(HttpStatusCode.Forbidden,mapOf("ok" to false,"error" to (e.message ?: "")))
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }


    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    val client = HttpClient(Apache) {
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        //login
        post("/login"){
            var post = call.receive<LoginRegister>()
            var user = Users.getOrPut(post.user){User(post.user,post.password)}
            //验证失败并抛出异常
            if (user.password != post.password) throw InvalidLoginException()
            call.respond(mapOf("token" to simpleJWT.sign(user.name)))
        }


        get("/auth"){
            val principal = call.principal<UserIdPrincipal>() ?: throw InvalidCredentialsException("Invalid token")
            println(principal)
            call.respond(mapOf("msg" to "login success!"))
        }

    }
}

