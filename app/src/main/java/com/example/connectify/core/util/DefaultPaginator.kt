package com.example.connectify.core.util

class DefaultPaginator<T>(
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextPage: Int) -> Resource<List<T>>,
    private val onSuccess: (items: List<T>, firstPage: Boolean) -> Unit,
    private val onError: suspend (UiText) -> Unit
) : Paginator<T> {

    private var page = 0

    override suspend fun loadFirstItems() {
        onLoadUpdated(true)
        val result = onRequest(0)
        when(result) {
            is Resource.Success -> {
                val items = result.data ?: emptyList()
                page = 1
                onSuccess(items, true)
                onLoadUpdated(false)
            }
            is Resource.Error -> {
                onError(result.uiText ?: UiText.unknownError())
                onLoadUpdated(false)
            }
        }
    }

    override suspend fun loadNextItems() {
        onLoadUpdated(true)
        val result = onRequest(page)
        when(result) {
            is Resource.Success -> {
                val items = result.data ?: emptyList()
                page++
                onSuccess(items, false)
                onLoadUpdated(false)
            }
            is Resource.Error -> {
                onError(result.uiText ?: UiText.unknownError())
                onLoadUpdated(false)
            }
        }
    }
}