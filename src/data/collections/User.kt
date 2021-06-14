package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    val username: String,
    val password: String,
    val following: List<String> = listOf(),
    val followers: List<String> = listOf(),
    @BsonId
    val _id: String = ObjectId().toString()
)