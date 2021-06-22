package com.example.routes

import com.example.data.collections.Meme
import com.example.data.responses.SimpleResponse
import com.example.data.saveMeme
import io.ktor.application.*
import io.ktor.auth.*
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
}
