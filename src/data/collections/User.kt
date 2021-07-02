package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val password: String,
    val following: List<String> = listOf(),
    val followers: List<String> = listOf(),
    val image: String = "R.drawable.image",
    val bio: String = "",
    val score:Int= 1000,
    @BsonId
    val _id: String = ObjectId().toString()
)