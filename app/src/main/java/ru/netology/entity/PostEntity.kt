package ru.netology.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val authorAvatar: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val shares: Int = 0
) {
    fun toDto() = Post(id, author, content, published, authorAvatar, likes, shares, likedByMe)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.authorAvatar, dto.likedByMe, dto.likes, dto.shares)

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)