package vamsee.application.socialmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_post.*
import vamsee.application.socialmedia.daos.PostDao

class createPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        post.setOnClickListener{
            post_text()
            finish()
        }
    }

    private fun post_text(){
        val input = editText.text.toString().trim()
        if(input.isNotEmpty()){
            val post = PostDao()
            post.addPost(input)
        }

    }
}