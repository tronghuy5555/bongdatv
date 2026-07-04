package com.bongdatv.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bongdatv.data.model.Fixture
import com.bongdatv.data.model.FixtureCommentator
import com.bongdatv.data.model.LiveGoals
import com.bongdatv.data.model.LiveStatus
import com.bongdatv.data.model.Stream
import com.bongdatv.data.repository.SportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerStreamOption(
    val id: Int,
    val name: String,
    val sourceUrl: String
)

data class PlayerUiState(
    val fixture: Fixture? = null,
    val streamOptions: List<PlayerStreamOption> = emptyList(),
    val selectedStream: PlayerStreamOption? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
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

    private var fixtureLoadJob: Job? = null
    private var liveUpdatesJob: Job? = null

    fun loadFixture(fixtureId: String) {
        if (fixtureId.isBlank()) {
            _uiState.update {
                it.copy(
                    fixture = null,
                    streamOptions = emptyList(),
                    selectedStream = null,
                    isLoading = false,
                    error = "Không tìm thấy trận đấu.",
                    showOverlay = true
                )
            }
            return
        }

        fixtureLoadJob?.cancel()
        fixtureLoadJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    fixture = null,
                    streamOptions = emptyList(),
                    selectedStream = null,
                    isLoading = true,
                    error = null,
                    status = null,
                    goals = null,
                    showOverlay = true
                )
            }

            val result = repository.getFixtureById(fixtureId)
            result.fold(
                onSuccess = { fixture ->
                    val streams = fixture.toPlayerStreamOptions()
                    _uiState.update {
                        it.copy(
                            fixture = fixture,
                            streamOptions = streams,
                            selectedStream = streams.firstOrNull(),
                            isLoading = false,
                            error = if (streams.isEmpty()) {
                                "Chưa có link phát cho trận đấu này."
                            } else {
                                null
                            }
                        )
                    }
                    startLiveUpdates(fixture.referenceId)
                },
                onFailure = {
                    liveUpdatesJob?.cancel()
                    _uiState.update { current ->
                        current.copy(
                            fixture = null,
                            streamOptions = emptyList(),
                            selectedStream = null,
                            isLoading = false,
                            error = "Không thể tải trận đấu. Vui lòng thử lại.",
                            showOverlay = true
                        )
                    }
                }
            )
        }
    }

    fun selectStream(stream: PlayerStreamOption) {
        _uiState.update {
            it.copy(
                selectedStream = stream,
                error = null
            )
        }
    }

    private fun startLiveUpdates(referenceId: String?) {
        liveUpdatesJob?.cancel()
        if (referenceId.isNullOrBlank()) return

        liveUpdatesJob = viewModelScope.launch {
            while (isActive) {
                val result = repository.getLiveStats(listOf(referenceId))
                result.getOrNull()?.results?.firstOrNull()?.let { liveFixture ->
                    _uiState.update {
                        it.copy(
                            status = liveFixture.fixture.status,
                            goals = liveFixture.goals
                        )
                    }
                }
                delay(30_000)
            }
        }
    }

    fun toggleOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = !_uiState.value.showOverlay)
    }

    fun showOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = true)
    }

    fun hideOverlay() {
        _uiState.value = _uiState.value.copy(showOverlay = false)
    }

    override fun onCleared() {
        fixtureLoadJob?.cancel()
        liveUpdatesJob?.cancel()
        super.onCleared()
    }

    private fun Fixture.toPlayerStreamOptions(): List<PlayerStreamOption> =
        fixtureCommentators
            .sortedWith(compareBy<FixtureCommentator> { it.priority }.thenBy { it.id })
            .flatMap { fixtureCommentator ->
                val commentatorName = fixtureCommentator.commentator.nickname
                    ?: fixtureCommentator.commentator.name
                    ?: ""

                fixtureCommentator.commentator.streams
                    .sortedWith(compareBy<Stream> { it.priority }.thenBy { it.id })
                    .map { stream ->
                        PlayerStreamOption(
                            id = stream.id,
                            name = listOf(commentatorName, stream.name)
                                .filter { it.isNotBlank() }
                                .joinToString(" - "),
                            sourceUrl = stream.sourceUrl
                        )
                    }
            }
            .distinctBy { it.sourceUrl }
}
