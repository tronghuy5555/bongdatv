package com.bongdatv.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
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
import coil.compose.AsyncImage
import com.bongdatv.data.model.Fixture
import com.bongdatv.ui.theme.AccentGreen
import com.bongdatv.ui.theme.CardBackground
import com.bongdatv.ui.theme.LiveRed
import com.bongdatv.ui.theme.TextPrimary
import com.bongdatv.ui.theme.TextSecondary

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel = hiltViewModel(),
    onMatchClick: (fixtureId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Đang tải...", color = TextSecondary, fontSize = 18.sp)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Lịch thi đấu",
            color = TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        state.fixturesByDate.forEach { (date, fixtures) ->
            Text(
                text = date,
                color = AccentGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            fixtures.forEach { fixture ->
                ScheduleMatchItem(
                    fixture = fixture,
                    onClick = {
                        onMatchClick(fixture.id.toString())
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ScheduleMatchItem(
    fixture: Fixture,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(10.dp))
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) AccentGreen else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time
        val time = try {
            fixture.startTime.substring(11, 16)
        } catch (e: Exception) {
            ""
        }
        Text(
            text = time,
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.width(50.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Home team
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            AsyncImage(
                model = fixture.homeTeam?.logoUrl,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = fixture.homeTeam?.name ?: "",
                color = TextPrimary,
                fontSize = 14.sp
            )
        }

        // Score or VS
        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.Center) {
            if (fixture.isLive) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${fixture.score?.home ?: 0} - ${fixture.score?.away ?: 0}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .background(LiveRed, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(text = "LIVE", color = Color.White, fontSize = 9.sp)
                    }
                }
            } else {
                Text(text = "VS", color = TextSecondary, fontSize = 14.sp)
            }
        }

        // Away team
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = fixture.awayTeam?.name ?: "",
                color = TextPrimary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                model = fixture.awayTeam?.logoUrl,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // League
        Text(
            text = fixture.league?.name ?: "",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.width(100.dp)
        )
    }
}
