package com.connectify.android.core.util

interface Paginator<T> {

    suspend fun loadFirstItems()
    suspend fun loadNextItems()
}