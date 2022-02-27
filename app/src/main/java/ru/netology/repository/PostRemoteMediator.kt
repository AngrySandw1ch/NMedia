package ru.netology.repository

import androidx.paging.*
import androidx.room.withTransaction
import ru.netology.api.ApiService
import ru.netology.dao.PostDao
import ru.netology.dao.PostRemoteKeyDao
import ru.netology.db.AppDb
import ru.netology.dto.Post
import ru.netology.entity.PostEntity
import ru.netology.entity.PostRemoteKeyEntity
import ru.netology.entity.toEntity
import ru.netology.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val service: ApiService,
    private val db: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    postRemoteKeyDao.max()?.let {
                        service.getAfter(it, state.config.pageSize)
                    } ?: service.getLatest(state.config.initialLoadSize)
                }
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBefore(id, state.config.pageSize)
                }
            }


            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        if (postRemoteKeyDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                listOf(
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = PostRemoteKeyEntity.KeyType.BEFORE,
                                        id = body.last().id,
                                    ),
                                )
                            )
                        } else {
                            postRemoteKeyDao.insert(PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id,
                            ))
                        }
                    }
                    LoadType.PREPEND -> {}
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                }
                postDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}