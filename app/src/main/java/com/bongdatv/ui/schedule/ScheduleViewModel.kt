package com.bongdatv.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bongdatv.data.model.Fixture
import com.bongdatv.data.repository.SportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

data class ScheduleUiState(
    val fixturesByDate: Map<String, List<Fixture>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: SportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val upcoming = repository.getUpcomingFixtures().getOrDefault(emptyList())
            val live = repository.getLiveFixtures().getOrDefault(emptyList())
            val all = (live + upcoming).sortedBy { it.startTime }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            val grouped = all.groupBy { fixture ->
                try {
                    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val date = isoFormat.parse(fixture.startTime.take(19))
                    displayFormat.format(date!!)
                } catch (e: Exception) {
                    "Khác"
                }
            }

            _uiState.value = ScheduleUiState(fixturesByDate = grouped, isLoading = false)
        }
    }
}
