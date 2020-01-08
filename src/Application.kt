package cn.hsiangsun

import cn.hsiangsun.auth.SimpleJWT
import cn.hsiangsun.bean.LoginRegister
import cn.hsiangsun.bean.User
import cn.hsiangsun.exception.InvalidCredentialsException
import cn.hsiangsun.exception.InvalidLoginException
import cn.hsiangsun.model.Users
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.JSONPObject
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.auth.principal
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.entity.findAll
import me.liuwj.ktorm.entity.findList
import me.liuwj.ktorm.entity.findOne
import java.util.*
import kotlin.reflect.full.memberProperties


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

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
            val post = call.receive<LoginRegister>()
            val query = Users.findList { it.name eq post.user and (it.password eq post.password) }
            if (query.isEmpty()) throw InvalidLoginException()
            call.respond(mapOf("token" to simpleJWT.sign(post.user)))
        }

        get("/sql"){
            //val query = Users.select().orderBy(Users.id.desc()).map { Users.createEntity(it) }
            var query = Users.findOne { it.id eq 1 }

            //@TODO 不能正确响应我想要到结果
            call.respond(mapOf( query?.name to query?.pwd ))


        }


        //需要token验证的api
        authenticate {
            get("/auth"){
                call.principal<UserIdPrincipal>()

                //Database.connect(url="jdbc:mysql://167.86.69.114:3306/test?seSSL=false", driver = "com.mysql.jdbc.Driver",user="root",password = "962464")
                for (row in cn.hsiangsun.model.Users.select()){
                    println(row[cn.hsiangsun.model.Users.name] +"="+row[cn.hsiangsun.model.Users.password])
                }
            }
        }




    }
}

