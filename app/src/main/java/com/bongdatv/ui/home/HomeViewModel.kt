package com.bongdatv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bongdatv.data.model.Fixture
import com.bongdatv.data.model.Sport
import com.bongdatv.data.repository.SportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val sports: List<Sport> = emptyList(),
    val liveFixtures: List<Fixture> = emptyList(),
    val upcomingFixtures: List<Fixture> = emptyList(),
    val finishedFixtures: List<Fixture> = emptyList(),
    val selectedSport: Sport? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val sportsResult = repository.getSports()
            val liveResult = repository.getLiveFixtures()
            val upcomingResult = repository.getUpcomingFixtures()
            val finishedResult = repository.getFinishedFixtures()

            _uiState.value = _uiState.value.copy(
                sports = sportsResult.getOrDefault(emptyList()),
                liveFixtures = liveResult.getOrDefault(emptyList()),
                upcomingFixtures = upcomingResult.getOrDefault(emptyList()),
                finishedFixtures = finishedResult.getOrDefault(emptyList()),
                isLoading = false,
                error = if (liveResult.isFailure && upcomingResult.isFailure)
                    "Không thể tải dữ liệu. Vui lòng thử lại." else null
            )
        }
    }

    fun selectSport(sport: Sport?) {
        _uiState.value = _uiState.value.copy(selectedSport = sport)
    }
}
