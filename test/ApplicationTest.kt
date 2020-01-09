package cn.hsiangsun

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import com.fasterxml.jackson.databind.*

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import kotlin.test.*
import io.ktor.server.testing.*
import java.util.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/users/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }

    @Test
    fun tets1(){
        var id = UUID.randomUUID().toString()
        println(id)
        var newId = id.filter { it.toInt() in 48..57 }

        //var id = "abcded2123adas213".filter { it.toInt() in 48..57 }
        println(newId)
        val list = newId.toList()
        println(list)
        println("=======================")
        list.spliterator()
        println(list)

        //var idNum = newId.toInt()

        //println(idNum)
    }

}
