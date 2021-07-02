package com.example.routes

import com.example.data.decreaseScore
import com.example.data.getLeaderBoard
import com.example.data.increaseScore
import com.example.data.requests.PointRequest
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

fun Route.scoreRoute(){
    route("/decreasescore"){
        authenticate {
            post {
                val point = try {
                    call.receive<PointRequest>()
                } catch (e: ContentTransformationException) {
                    call.respond(BadRequest)
                    return@post
                }
                if(decreaseScore(point.username,point.point)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
    route("/increasescore"){
        authenticate {
            post {
                val point = try {
                    call.receive<PointRequest>()
                }catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                if(increaseScore(point.username,point.point)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
    route("/leaderboard"){
        get {
            val leaderList = getLeaderBoard()
            call.respond(OK,leaderList)
        }
    }

}