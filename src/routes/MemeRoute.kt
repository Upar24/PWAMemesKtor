package com.example.routes

import com.example.data.*
import com.example.data.collections.Meme
import com.example.data.requests.UserRequest
import com.example.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate

fun Route.memeRoute() {
    route("/savetrash") {
        authenticate {
            post {
                val meme = try {
                    call.receive<Meme>()
                } catch (e: ContentTransformationException) {
                    call.respond(OK, SimpleResponse(false, "trash is not uploading"))
                    return@post
                }
                if (saveMeme(meme)){
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }

            }
        }
    }
    route("/savememe"){
        authenticate {
            post {
                val meme = try {
                    call.receive<Meme>()
                } catch (e: ContentTransformationException) {
                    call.respond(OK, SimpleResponse(false, "meme is not uploading"))
                    return@post
                }
                if (saveMeme(meme)){
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }

            }
        }
    }
    route("/togglelike"){
        authenticate {
            post{
                val username = call.principal<UserIdPrincipal>()!!.name
                val meme =call.receive<Meme>()
                meme.liking = isMemeLiked(username,meme)
                val success = toggleLikeMeme(username,meme)
                val isLiked = isMemeLiked(username,meme)
                val likedCount= if(isLiked) (meme.liked.size + 1).toString() else meme.liked.size.toString()
                if(success)call.respond(OK,SimpleResponse(isLiked,likedCount)) else call.respond(BadRequest)

            }
        }
    }
    route("/togglesave"){
        authenticate {
            post{
                val username = call.principal<UserIdPrincipal>()!!.name
                val meme =call.receive<Meme>()
                meme.saving = isMemeSaved(username,meme)
                val success = toggleSaveMeme(username,meme)
                val isSaved= isMemeSaved(username,meme)
                val savedCount=if(isSaved) (meme.saved.size + 1).toString() else (meme.saved.size).toString()
                if(success)call.respond(OK,SimpleResponse(isSaved,savedCount)) else call.respond(BadRequest)

            }
        }
    }
    route("/getallmemes"){
        get{
            val post = getAllMemes()
                call.respond(OK,post)
            }
        }
    route("/getalltrash"){
        get{
            val post = getAllTrash()
            call.respond(OK,post)
        }
    }
    route("/getusermemes"){
        post{
            val request = try {
                call.receive<UserRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(OK, SimpleResponse(false, "meme is not uploading"))
                return@post
            }
            val result = getUserMemes(request.username)
            call.respond(OK,result)
        }
    }
    route("/getusertrash"){
        post{
            val request = try {
                call.receive<UserRequest>()
            } catch (e: ContentTransformationException) {
                call.respond(OK, SimpleResponse(false, "meme is not uploading"))
                return@post
            }
            val result = getUserTrash(request.username)
            call.respond(OK,result)
        }
    }
}
