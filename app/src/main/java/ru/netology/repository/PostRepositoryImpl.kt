package ru.netology.repository


import androidx.paging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.api.ApiService
import java.io.IOException
import ru.netology.dao.PostDao
import ru.netology.dao.PostRemoteKeyDao
import ru.netology.db.AppDb
import ru.netology.dto.*
import ru.netology.entity.PostEntity
import ru.netology.entity.toEntity
import ru.netology.enumeration.AttachmentType
import ru.netology.error.ApiError
import ru.netology.error.AppError
import ru.netology.error.NetworkError
import ru.netology.error.UnknownError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    appDb: AppDb,
    private val postDao: PostDao,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: ApiService
    ) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5),
        remoteMediator = PostRemoteMediator(apiService,appDb, postDao,postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource
    ).flow.map {
        it.map { postEntity ->
            postEntity.toDto()
        }
    }

    override var responseCode: Int = 0

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            responseCode = response.code()
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val response = apiService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override fun getById(id: Long): Flow<Post?> {
        return postDao.getById(id).map {
            it?.toDto()
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            responseCode = response.code()
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            postDao.removeById(id)
            val response = apiService.removeById(id)
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
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val postForDb = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(postForDb))
            responseCode = response.code()
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: java.lang.Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = apiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val postForDb = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(postForDb))
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

            val response = apiService.upload(media)
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