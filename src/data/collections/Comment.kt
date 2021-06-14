package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    val idMeme: String,
    val idUser: String,
    val commentText: String,
    val date: Long,
    val liked: List<String> = listOf(),
    val unliked: List<String> = listOf(),
    val liking: Boolean = false,
    val unliking: Boolean = false,
    @BsonId
    val _id: String = ObjectId().toString()
)