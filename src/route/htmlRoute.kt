package cn.hsiangsun.route

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route


fun Routing.apiHtml(){
    static("/static"){
        resources("static")
    }
    route("/html"){

        get("/index"){
            call.respond(FreeMarkerContent("index.ftl", mapOf("data" to "user login")))
        }

        post("/login"){
            val post = call.receiveParameters()
            if (post["username"] != null && post["password"] != null){
                call.respond("ok")
            }else{
                call.respond("false")
            }
        }
    }
}


