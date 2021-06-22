package com.example.routes

import com.example.data.collections.Meme
import com.example.data.collections.User
import com.example.data.getUser
import com.example.data.requests.SearchRequest
import com.example.data.responses.SimpleResponse
import com.example.data.updateUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.userRoute(){
    route("/getuserinfo"){
        authenticate {
            post {
                 val username = call.principal<UserIdPrincipal>()!!.name
                val user = getUser(username) ?: false
                call.respond(OK,user)
            }
        }
    }
    route("/updateuserinfo"){
        authenticate {
            post {
                val user = try {
                    call.receive<User>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if(updateUser(user)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
}