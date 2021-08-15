package ru.sedavnyh.vkgroupscan.data

import javax.inject.Inject

class Repository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) {
    val local = localDataSource
    val remote = remoteDataSource
}