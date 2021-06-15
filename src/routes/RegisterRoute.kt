package com.example.routes

import com.example.data.checkIfUserExist
import com.example.data.collections.User
import com.example.data.registerUser
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.registerRoute(){
    route("/register"){
        post {
            val request = try{
                call.receive<AccountRequest>()
            }catch (e : ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val userExist = checkIfUserExist(request.username)
            if (!userExist) {
                if (registerUser(User(request.username, request.password))) {
                    call.respond(OK, SimpleResponse(true, "register successfully"))
                } else {
                    call.respond(OK, SimpleResponse(false, "An unknown occured"))
                }
            }else{
                call.respond(OK,SimpleResponse(false,"user already exist"))
            }
        }
    }
}