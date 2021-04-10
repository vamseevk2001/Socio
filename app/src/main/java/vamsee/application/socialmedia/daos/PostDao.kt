package vamsee.application.socialmedia.daos

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import vamsee.application.socialmedia.models.Post
import vamsee.application.socialmedia.models.User

class PostDao {

    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("Posts")
    val auth = Firebase.auth

    fun addPost(text: String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val post = Post(text, user, currentTime)
            postCollection.document().set(post)
        }
    }
}