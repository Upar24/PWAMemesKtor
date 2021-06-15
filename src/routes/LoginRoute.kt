package com.example.routes

import com.example.data.checkIfUserExist
import com.example.data.checkPasswordForUsername
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.loginRoute(){
    route("/login"){
        post {
            val request = try {
                call.receive<AccountRequest>()
            }catch (e:ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val userExist = checkIfUserExist(request.username)
            if(!userExist){
                call.respond(OK, SimpleResponse(false,"user doesnt exist"))
            }else{
                val isPasswordCorrect = checkPasswordForUsername(request.username, request.password)
                if(isPasswordCorrect){
                    call.respond(OK,SimpleResponse(true,"you are log in"))
                }else{
                    call.respond(OK,SimpleResponse(false,"either email or password is incorrect"))
                }
            }
        }
    }
}