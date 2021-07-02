package com.example.data

import com.example.data.collections.Comment
import com.example.data.collections.Meme
import com.example.data.collections.User
import org.litote.kmongo.`in`
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine
private val db= client.getDatabase("PWAMemesdb")
private val users= db.getCollection<User>("users")
private val memes= db.getCollection<Meme>("memes")
private val comments= db.getCollection<Comment>("comments")
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
suspend fun getUser(username:String): User? {
    return users.findOne(User::username eq username)
}
suspend fun updateUser(user: User):Boolean{
    return users.updateOneById(user._id, user).wasAcknowledged()
}
suspend fun isFollowing(username:String, usernameToFollow:String): Boolean{
    val user = users.findOne(User::username eq username) ?: return false
    return usernameToFollow in user.following
}
suspend fun saveMeme(meme:Meme):Boolean{
    val memeExists= memes.findOneById(meme._id) != null
    return if(memeExists){
        memes.updateOneById(meme._id,meme).wasAcknowledged()
    }else{
        memes.insertOne(meme).wasAcknowledged()
    }
}
suspend fun getAllMemes():List<Meme>{
    return memes.find("{type : 'meme'}").toList()
}
suspend fun getAllTrash():List<Meme>{
    return memes.find("{type : 'trash'}").toList()
}
suspend fun getLeaderBoard():List<User>{
    return users.find().descendingSort(User::score).toList()
}
suspend fun getUserMemes(username: String):List<Meme>{
    return memes.find(Meme::usernameAuthor eq username ).toList()
}
suspend fun getUserTrash(username: String):List<Meme>{
    return memes.find(Meme::usernameKeyword eq username,Meme::type eq "trash").toList()
}
suspend fun isMemeLiked(username:String, meme:Meme): Boolean{
    val meme = memes.findOne(Meme::_id eq meme._id) ?: return false
    return username in meme.liked
}
suspend fun toggleLikeMeme(username: String,meme: Meme):Boolean{
    val isLiked = isMemeLiked(username,meme)
    val isUnliked = isMemeUnliked(username, meme)
    val usernameAuthor = meme.usernameAuthor
    val scoreMeme = meme.keyword.size + 1
    return if(isLiked){
        val newLikes = memes.findOneById(meme._id)!!.liked - username
        decreaseScore(usernameAuthor,scoreMeme)
        memes.updateOneById(meme._id, setValue(Meme::liked, newLikes)).wasAcknowledged()
    }else{
        if(isUnliked){
            val newUnlikes = memes.findOneById(meme._id)!!.unliked - username
            memes.updateOneById(meme._id, setValue(Meme::unliked, newUnlikes)).wasAcknowledged()
        }
        val newLikes = memes.findOneById(meme._id)!!.liked + username
        increaseScore(usernameAuthor,scoreMeme)
        memes.updateOneById(meme._id, setValue(Meme::liked,newLikes)).wasAcknowledged()
    }
}
suspend fun isMemeUnliked(username:String, meme:Meme): Boolean{
    val meme = memes.findOne(Meme::_id eq meme._id) ?: return false
    return username in meme.unliked
}
suspend fun isCommentLiked(username: String, comment: Comment):Boolean{
    val idComment = comment._id
    val comment1 = comments.findOne(Comment::_id eq idComment) ?: return false
    return username in comment1.liked
}
suspend fun isMemeSaved(username:String,meme:Meme):Boolean{
    val meme = memes.findOne(Meme::_id eq meme._id) ?: return false
    return username in meme.saved
}
suspend fun toggleSaveMeme(username: String,meme: Meme):Boolean{
    val isSaved = isMemeSaved(username,meme)
    val usernameAuthor = meme.usernameAuthor
    val scoreMeme = 10
    return if(isSaved){
        val newSaves = memes.findOneById(meme._id)!!.saved - username
        decreaseScore(usernameAuthor,scoreMeme)
        memes.updateOneById(meme._id, setValue(Meme::saved, newSaves)).wasAcknowledged()
    }else{
        val newSaves = memes.findOneById(meme._id)!!.saved + username
        increaseScore(usernameAuthor,scoreMeme)
        memes.updateOneById(meme._id, setValue(Meme::saved,newSaves)).wasAcknowledged()
    }
}
suspend fun decreaseScore(username: String,scoreDecrease:Int):Boolean{
    val user = users.findOne(User::username eq username) ?: return false
    val updateScore = user.score - scoreDecrease
    return users.updateOneById(user._id, setValue(User::score,updateScore)).wasAcknowledged()
}
suspend fun increaseScore(username: String,scoreIncrease:Int):Boolean{
    val user = users.findOne(User::username eq username) ?: return false
    val updateScore = user.score + scoreIncrease
    return users.updateOneById(user._id, setValue(User::score,updateScore)).wasAcknowledged()
}

//SOME FEATURE I WANTED TO IMPLEMENT BUT I DONT WANT TO DELETE THE CODE HEHE..


suspend fun toggleFollow(username: String, usernameToToggle: String) : String {
    val isFollowing = isFollowing(username, usernameToToggle)
    if (isFollowing) {
        val newFollowings = users.findOne(User::username eq username)!!.following - usernameToToggle
        val newFollowers = users.findOne(User::username eq usernameToToggle)!!.followers - username
        users.updateOne(User::username eq username, setValue(User::following, newFollowings))
        users.updateOne(User::username eq usernameToToggle, setValue(User::followers,newFollowers))
        return "unfollowing"
    } else {
        val newFollowings = users.findOne(User::username eq username)!!.following + usernameToToggle
        val newFollowes = users.findOne(User::username eq usernameToToggle)!!.followers + username
        users.updateOne(User::username eq username, setValue(User::following, newFollowings))
        users.updateOne(User::username eq usernameToToggle,setValue(User::followers,newFollowes))
        return "following"
    }
}
suspend fun getFollowingMemes(username: String): List<Meme>? {
    val following = users.findOne(User::username eq username)?.following ?: return null
    return memes.find(Meme::usernameAuthor `in` following).toList()
}
suspend fun saveComment(comment: Comment):Boolean{
    return comments.insertOne(comment).wasAcknowledged()
}
suspend fun getCommentPost(meme: Meme):List<Comment>{
    return comments.find(Comment::idMeme eq meme._id).toList()
}
suspend fun toggleUnlikeMeme(username: String,meme: Meme):Boolean{
    val isUnliked = isMemeUnliked(username,meme)
    val isLiked = isMemeLiked(username,meme)
    return if(isUnliked){
        val newUnlikes = memes.findOneById(meme._id)!!.unliked - username
        memes.updateOneById(meme._id, setValue(Meme::unliked, newUnlikes)).wasAcknowledged()
    }else{
        if(isLiked){
            val newLikes = memes.findOneById(meme._id)!!.liked - username
            memes.updateOneById(meme._id, setValue(Meme::liked, newLikes)).wasAcknowledged()
        }
        val newUnlikes = memes.findOneById(meme._id)!!.unliked + username
        memes.updateOneById(meme._id, setValue(Meme::liked,newUnlikes)).wasAcknowledged()
    }
}
suspend fun toggleLikeComment(username: String,comment: Comment):Boolean{
    val isLiked = isCommentLiked(username,comment)
    return if(isLiked){
        val newLikes = comments.findOneById(comment._id)!!.liked - username
        comments.updateOneById(comment._id, setValue(Comment::liked, newLikes)).wasAcknowledged()
    }else{
        val newLikes = comments.findOneById(comment._id)!!.liked + username
        comments.updateOneById(comment._id, setValue(Comment::liked,newLikes)).wasAcknowledged()
    }
}

























