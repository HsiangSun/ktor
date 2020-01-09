package cn.hsiangsun.route

import cn.hsiangsun.auth.SimpleJWT
import cn.hsiangsun.bean.LoginRegister
import cn.hsiangsun.bean.User
import cn.hsiangsun.exception.InvalidCredentialsException
import cn.hsiangsun.exception.InvalidLoginException
import cn.hsiangsun.model.Users
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.ContentType
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import me.liuwj.ktorm.dsl.and
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.entity.asSequence
import me.liuwj.ktorm.entity.findList
import me.liuwj.ktorm.entity.toList
import java.util.*

fun Routing.apiUsers(simpleJWT: SimpleJWT) {
    route("/users"){
        get("/test"){
            call.respond(mapOf("msg" to "ok"))
        }

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
            //var query = Users.findOne { it.id eq 1 }

            var query = Users.asSequence().toList()
            call.respond(query)
        }

        post("/add"){
            val post = call.receive<User>()
            println("recive == $post")
            Users.insert { it ->
                it.id to 10/*UUID.randomUUID().toString().filter { it.toInt() in 48..57 }.toInt()*/
                it.name to post.name
                it.password to post.pwd }
            call.respond("Success")
        }

        //需要token验证的api
        authenticate {
            get("/auth"){
                call.principal<UserIdPrincipal>()?:throw InvalidCredentialsException()
                call.respond(mapOf("msg" to "Success"))
            }
        }
    }
}