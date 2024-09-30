package com.example.connectify.feature_activity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.feature_activity.domain.use_case.GetActivitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityUseCase: GetActivitiesUseCase
) : ViewModel() {

    private val _pagingState = MutableStateFlow<PagingState<Activity>>(PagingState())
    val pagingState = _pagingState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val paginator = DefaultPaginator(
        onLoadUpdated = { isLoading ->
            _pagingState.update {
                it.copy(
                    isLoading = isLoading
                )
            }
        },
        onRequest = { page ->
            activityUseCase(page = page)
        },
        onSuccess = { activities, firstPage ->
            _pagingState.update {
                it.copy(
                    items = if(firstPage) activities else pagingState.value.items + activities,
                    endReached = activities.isEmpty()
                )
            }
        },
        onError = { uiText ->
            _eventFlow.emit(UiEvent.ShowSnackbar(uiText))
        }
    )

    init {
        loadInitialActivities()
    }


    fun loadNextActivities() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }

    private fun loadInitialActivities() {
        viewModelScope.launch {
            paginator.loadFirstItems()
        }
    }
}