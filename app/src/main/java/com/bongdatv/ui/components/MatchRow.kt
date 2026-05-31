package com.bongdatv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.bongdatv.data.model.Fixture
import com.bongdatv.ui.theme.TextPrimary

@Composable
fun MatchRow(
    title: String,
    fixtures: List<Fixture>,
    modifier: Modifier = Modifier,
    onMatchClick: (Fixture) -> Unit = {}
) {
    if (fixtures.isEmpty()) return

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(fixtures, key = { it.id }) { fixture ->
                MatchCard(
                    fixture = fixture,
                    onClick = { onMatchClick(fixture) }
                )
            }
        }
    }
}
