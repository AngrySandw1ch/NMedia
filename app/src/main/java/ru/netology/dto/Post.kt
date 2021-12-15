package ru.netology.dto

import ru.netology.enumeration.AttachmentType


data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val content: String,
    val published: String,
    val authorAvatar: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val likedByMe: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
    )

data class Attachment(
    val url: String,
    val type: AttachmentType
)