package ru.netology.repository

import androidx.lifecycle.LiveData
import ru.netology.dto.Post

interface PostRepository {
    /*fun getAll(): List<Post>
    fun likeById(id: Long)
    fun unLikeById(id: Long)
    fun shareById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)*/

    fun getAllAsync(callback: GetAllCallback)
    fun likeByIdAsync(id: Long, callback: LikeByIdCallback)
    fun unLikeByIdAsync(id: Long, callback: UnLikeByIdCallback)
    fun removeByIdAsync(id: Long, callback: RemoveByIdCallback)
    fun saveAsync(post: Post, callback: SaveCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface LikeByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    interface UnLikeByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

   interface RemoveByIdCallback {
       fun onSuccess() {}
       fun onError(e: Exception) {}
   }

    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
}