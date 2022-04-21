package vamsee.application.socialmedia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import vamsee.application.socialmedia.models.Post

class PostAdapter(options: FirestoreRecyclerOptions<Post >, val listener: IpostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options) {

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postText: TextView = itemView.findViewById(R.id.postTitle)
        val userText: TextView = itemView.findViewById(R.id.userName)
        val createdAt: TextView = itemView.findViewById(R.id.createdAt)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        val postImg: ImageView = itemView.findViewById(R.id.post_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder =  PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))

        viewHolder.likeButton.setOnClickListener{
            listener.clickedLikeButton(snapshots.getSnapshot(viewHolder.adapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        holder.likeCount.text = model.likedBy.size.toString()
        Glide.with(holder.userImage.context).load(model.createdBy.imageId).circleCrop().into(holder.userImage)
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        Glide.with(holder.postImg.context).load(model.imageUrl).into(holder.postImg)


        val auth = Firebase.auth
        val currentUserId = auth.currentUser!!.uid
        val isliked = model.likedBy.contains(currentUserId)

        if (isliked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        }
        else
        {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_notliked))
        }
    }
}
interface IpostAdapter{
    fun clickedLikeButton(postId: String)
}