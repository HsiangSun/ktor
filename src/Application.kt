package cn.hsiangsun

import cn.hsiangsun.auth.SimpleJWT
import cn.hsiangsun.exception.InvalidCredentialsException
import cn.hsiangsun.exception.InvalidLoginException
import cn.hsiangsun.route.apiHtml
import cn.hsiangsun.route.apiUsers
import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.request
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.freemarker.FreeMarker
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.routing
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.jackson.KtormModule


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean =false) {

    //make datasource
    val config = HikariConfig("/hikari.properties")
    val datasource = HikariDataSource(config)
    //create mysql connection
    Database.connect(datasource)
    //token secret salt
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
    //跨域配置
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
            configure(SerializationFeature.INDENT_OUTPUT, true)
            registerModule(KtormModule())
        }
    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }



    //application api
    routing {
        apiUsers(simpleJWT)
        apiHtml()
    }


}



