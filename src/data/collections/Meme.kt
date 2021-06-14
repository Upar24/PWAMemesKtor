package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate

data class Meme(
    val idKeyword: String,
    val imageKeyword: String,
    val usernameKeyword: String,
    val keyword: List<String>,
    val type:String,
    val idAuthor: String= "",
    val imageAuthor: String= "",
    val usernameAuthor: String= "",
    val imageMeme: String?= null,
    val descMeme: String?= null,
    val liked: List<String> = listOf(),
    val unliked: List<String> = listOf(),
    val comments: List<String> = listOf(),
    val saved: List<String> = listOf(),
    val date: LocalDate? = null,
    val liking: Boolean = false,
    val unliking: Boolean = false,
    val saving: Boolean = false,
    @BsonId
    val _id: String = ObjectId().toString()
    
)