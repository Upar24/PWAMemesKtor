package com.example.data

import com.example.data.collections.Meme
import com.example.data.collections.User
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val db= client.getDatabase("PWAMemesdb")
private val users= db.getCollection<User>("users")
private val memes= db.getCollection<Meme>("memes")
private val comments= db.getCollection<User>("comments")

suspend fun registerUser(user: User) : Boolean{
    return users.insertOne(user).wasAcknowledged()
}
suspend fun checkIfUserExist(username:String):Boolean{
    return users.findOne(User::username eq username) != null
}
suspend fun checkPasswordForUsername(username:String, passwordToCheck:String):Boolean{
    val actualPassword = users.findOne(User::username eq username)?.password ?: return false
    return actualPassword==passwordToCheck
}