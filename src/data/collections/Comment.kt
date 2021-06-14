package com.example.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Comment(
    val memeId: String,
    val idUser: String,
    val commentText: String,
    val date: Long,
    @BsonId
    val _id: String = ObjectId().toString()
)