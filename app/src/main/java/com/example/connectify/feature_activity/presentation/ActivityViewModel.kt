package com.example.connectify.feature_activity.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connectify.core.data.connectivity.ConnectivityObserver
import com.example.connectify.core.domain.models.Activity
import com.example.connectify.core.domain.states.NetworkConnectionState
import com.example.connectify.core.domain.states.PagingState
import com.example.connectify.core.presentation.util.UiEvent
import com.example.connectify.core.util.DefaultPaginator
import com.example.connectify.feature_activity.domain.use_case.GetActivitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val activityUseCase: GetActivitiesUseCase,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _pagingState = MutableStateFlow<PagingState<Activity>>(PagingState())
    val pagingState = _pagingState
        .onStart { loadInitialActivities() }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingState())

    val networkState: StateFlow<NetworkConnectionState> =
        connectivityObserver.networkConnection
            .stateIn(viewModelScope, SharingStarted.Lazily, NetworkConnectionState.Available)

    private val _eventFlow = Channel<UiEvent>()
    val eventFlow = _eventFlow.receiveAsFlow()

    private val paginator = DefaultPaginator(
        onFirstLoadUpdated = { isFirstLoading ->
            _pagingState.update {
                it.copy(
                    isFirstLoading = isFirstLoading
                )
            }
        },
        onNextLoadUpdated = { isNextLoading ->
            _pagingState.update {
                it.copy(
                    isNextLoading = isNextLoading
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
            _eventFlow.send(UiEvent.ShowSnackbar(uiText))
        }
    )

    private fun loadInitialActivities() {
        viewModelScope.launch {
            paginator.loadFirstItems()
        }
    }

    fun loadNextActivities() {
        viewModelScope.launch {
            paginator.loadNextItems()
        }
    }
}