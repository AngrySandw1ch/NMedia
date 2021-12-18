package ru.netology.repository


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.api.Api
import ru.netology.dto.Post
import java.io.IOException
import ru.netology.dao.PostDao
import ru.netology.dto.Attachment
import ru.netology.dto.Media
import ru.netology.dto.MediaUpload
import ru.netology.entity.PostEntity
import ru.netology.entity.toDto
import ru.netology.entity.toEntity
import ru.netology.enumeration.AttachmentType
import ru.netology.error.ApiError
import ru.netology.error.AppError
import ru.netology.error.NetworkError
import ru.netology.error.UnknownError

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll().map(List<PostEntity>::toDto).flowOn(Dispatchers.Default)
    override var responseCode: Int = 0

    override suspend fun getAll() {
        try {
            val response = Api.service.getAll()
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
            val response = Api.service.getNewer(id)
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
            val response = Api.service.save(post)
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
            val response = Api.service.removeById(id)
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
            val response = Api.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val postForDb = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(postForDb))
            responseCode = response.code()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: java.lang.Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = Api.service.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val postForDb = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(postForDb))
            responseCode = response.code()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)

            val postWithAttachment = post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = Api.service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}