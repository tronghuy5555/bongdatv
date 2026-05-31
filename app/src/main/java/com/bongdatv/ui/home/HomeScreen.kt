package com.bongdatv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Text
import com.bongdatv.data.model.Fixture
import com.bongdatv.ui.components.HeroBanner
import com.bongdatv.ui.components.MatchRow
import com.bongdatv.ui.theme.AccentGreen
import com.bongdatv.ui.theme.CardBackground
import com.bongdatv.ui.theme.TextPrimary
import com.bongdatv.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMatchClick: (fixtureId: String, streamUrl: String) -> Unit,
    onNavigateToSchedule: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Đang tải...", color = TextSecondary, fontSize = 18.sp)
        }
        return
    }

    if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = state.error!!, color = TextSecondary, fontSize = 16.sp)
        }
        return
    }

    val selectedSport = state.selectedSport
    val filteredLive = if (selectedSport == null) state.liveFixtures
        else state.liveFixtures.filter { it.sport?.id == selectedSport.id }
    val filteredUpcoming = if (selectedSport == null) state.upcomingFixtures
        else state.upcomingFixtures.filter { it.sport?.id == selectedSport.id }
    val filteredFinished = if (selectedSport == null) state.finishedFixtures
        else state.finishedFixtures.filter { it.sport?.id == selectedSport.id }

    val heroFixture = filteredLive.firstOrNull { it.isHot }
        ?: filteredLive.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BongDa TV",
                color = AccentGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            NavButton(text = "Lịch thi đấu", onClick = onNavigateToSchedule)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sport tabs
        SportTabRow(
            sports = state.sports,
            selectedSport = state.selectedSport,
            onSportSelected = { viewModel.selectSport(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hero banner
        HeroBanner(
            fixture = heroFixture,
            modifier = Modifier.padding(horizontal = 24.dp),
            onClick = {
                heroFixture?.let { navigateToPlayer(it, onMatchClick) }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        MatchRow(
            title = "Đang phát trực tiếp",
            fixtures = filteredLive,
            onMatchClick = { navigateToPlayer(it, onMatchClick) }
        )

        MatchRow(
            title = "Sắp diễn ra",
            fixtures = filteredUpcoming,
            onMatchClick = { navigateToPlayer(it, onMatchClick) }
        )

        MatchRow(
            title = "Kết quả",
            fixtures = filteredFinished,
            onMatchClick = { navigateToPlayer(it, onMatchClick) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun NavButton(text: String, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(CardBackground, RoundedCornerShape(8.dp))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) AccentGreen else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 14.sp)
    }
}

private fun navigateToPlayer(
    fixture: Fixture,
    onMatchClick: (String, String) -> Unit
) {
    val stream = fixture.fixtureCommentators
        .firstOrNull()?.commentator?.streams
        ?.firstOrNull()?.sourceUrl ?: ""
    onMatchClick(fixture.id.toString(), stream)
}
