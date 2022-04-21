package vamsee.application.socialmedia

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*
import vamsee.application.socialmedia.daos.PostDao
import java.io.IOException

class createPostActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var databaseReference: DatabaseReference? = null
    private var mProgressBar: ProgressBar? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        post.setOnClickListener {
           uploadImage()
               //post_text()
           // finish()
        }

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads")
        mProgressBar = findViewById(R.id.progressBar)


        chooseImage.setOnClickListener {
            progressDialog = ProgressDialog(this)
            launchGallery()
        }

    }

    private fun launchGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

//    private fun post_text() {
//        val input = editText.text.toString().trim()
//        if (input.isNotEmpty()) {
//            val post = PostDao()
//            post.addPost(input, postImgUrl)
//        }
//
//    }

    private fun getFileExtension(uri: Uri): String? {
        val cr: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun uploadImage() {
        if (filePath != null) {
            val fileReference: StorageReference? = storageReference?.child(
                System.currentTimeMillis().toString() + "." +
                        getFileExtension(filePath!!))

            fileReference?.putFile(filePath!!)
                ?.addOnSuccessListener {
                    val handler: Handler = Handler()
                    handler.postDelayed(Runnable {
                        mProgressBar?.progress = 0
                    }, 5000)
                    progressDialog!!.dismiss()
                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_LONG).show()
                    val result:Task<Uri> = it.storage.downloadUrl
                    var postImgUrl: String? = null
                    result.addOnSuccessListener {
                        postImgUrl = it.toString()
                        val input = editText.text.toString().trim()
                        if (input.isNotEmpty() && postImgUrl?.isNotEmpty() == true) {
                            val post = PostDao()
                            postImgUrl?.let { it1 -> post.addPost(input, it1) }
                            finish()
                        }
                        else{
                            Toast.makeText(this, "caption cannot be empty", Toast.LENGTH_LONG).show()
                        }
                    }

                }
                ?.addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show();

                }
                ?.addOnProgressListener {
                    progressDialog!!.setTitle("Uploading...")
                    progressDialog!!.show()
                    val progress: Double = (100.0 * it.bytesTransferred / it.totalByteCount)
                    //mProgressBar?.progress = progress.toInt()
                }


        } else {
            Toast.makeText(this, "No file selected!!", Toast.LENGTH_LONG).show()
        }
    }
}