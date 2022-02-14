package ru.netology.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.dto.Media
import ru.netology.dto.MediaUpload
import ru.netology.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>
    var responseCode: Int
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
}