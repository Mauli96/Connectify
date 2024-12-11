package com.example.connectify.core.util

class DefaultPaginator<T>(
    private val onFirstLoadUpdated: (Boolean) -> Unit,
    private val onNextLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextPage: Int) -> Resource<List<T>>,
    private val onSuccess: (items: List<T>, firstPage: Boolean) -> Unit,
    private val onError: suspend (UiText) -> Unit
) : Paginator<T> {

    private var page = 0

    override suspend fun loadFirstItems() {
        onFirstLoadUpdated(true)
        val result = onRequest(0)
        when(result) {
            is Resource.Success -> {
                val items = result.data ?: emptyList()
                page = 1
                onSuccess(items, true)
                onFirstLoadUpdated(false)
            }
            is Resource.Error -> {
                onError(result.uiText ?: UiText.unknownError())
                onFirstLoadUpdated(false)
            }
        }
    }

    override suspend fun loadNextItems() {
        onNextLoadUpdated(true)
        val result = onRequest(page)
        when(result) {
            is Resource.Success -> {
                val items = result.data ?: emptyList()
                page++
                onSuccess(items, false)
                onNextLoadUpdated(false)
            }
            is Resource.Error -> {
                onError(result.uiText ?: UiText.unknownError())
                onNextLoadUpdated(false)
            }
        }
    }
}