package ru.netology.dto

data class Post(
    val id: Int,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val likedByMe: Boolean,
    val media: String? = null
    ) {
}