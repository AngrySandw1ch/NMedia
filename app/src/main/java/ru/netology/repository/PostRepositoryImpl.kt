package ru.netology.repository


import android.icu.number.IntegerWidth
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.*
import ru.netology.api.PostsApi
import ru.netology.dto.Post
import java.io.IOException
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call
import ru.netology.dao.PostDao
import ru.netology.entity.PostEntity
import ru.netology.entity.toDto
import ru.netology.entity.toEntity
import ru.netology.error.ApiError
import ru.netology.error.AppError
import ru.netology.error.NetworkError
import ru.netology.error.UnknownError

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)
    override var responseCode: Int = 0

    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            responseCode = response.code()
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun save(post: Post) {
        try {
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            responseCode = response.code()
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (response.body() == null) {
                throw ApiError(response.code(), response.message())
            }
            responseCode = response.code()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun likeById(id: Long) {
        try {
            val posts = data.single()
            val post = posts.first {
                it.id == id
            }
            val postForDb = post.copy(likes = post.likes + 1, likedByMe = !post.likedByMe)
            if (postForDb != null) {
                dao.insert(PostEntity.fromDto(postForDb))
            }
            val response = PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (response.body() == null) {
                throw ApiError(response.code(), response.message())
            }
            responseCode = response.code()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: java.lang.Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val posts = data.single()
            val post = posts.first {
                it.id == id
            }
            val postForDb = post.copy(likes = post.likes - 1, likedByMe = !post.likedByMe)
            if (postForDb != null) {
                dao.insert(PostEntity.fromDto(postForDb))
            }
            val response = PostsApi.service.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            if (response.body() == null) {
                throw ApiError(response.code(), response.message())
            }
            responseCode = response.code()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}