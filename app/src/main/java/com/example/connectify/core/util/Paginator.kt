package com.example.connectify.core.util

interface Paginator<T> {

    suspend fun loadFirstItems()
    suspend fun loadNextItems()
}