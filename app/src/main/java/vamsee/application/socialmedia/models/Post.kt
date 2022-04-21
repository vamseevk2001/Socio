package vamsee.application.socialmedia.models

data class Post(
    val text: String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    val imageUrl: String = "",
    val likedBy: ArrayList<String> = ArrayList()
)