package com.bongdatv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bongdatv.data.model.LiveGoals
import com.bongdatv.data.model.LiveStatus
import com.bongdatv.data.repository.SportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val status: LiveStatus? = null,
    val goals: LiveGoals? = null,
    val showOverlay: Boolean = true
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: SportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    fun startLiveUpdates(referenceId: String) {
        if (referenceId.isEmpty()) return
        viewModelScope.launch {
            while (true) {
                val result = repository.getLiveStats(listOf(referenceId))
                result.getOrNull()?.results?.firstOrNull()?.let { liveFixture ->
                    _uiState.value = _uiState.value.copy(
                        status = liveFixture.fixture.status,
                        goals = liveFixture.goals
                    )
                }
                delay(30_000)
            }
        }
    }

    fun toggleOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = !_uiState.value.showOverlay)
    }
}
