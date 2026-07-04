package com.bongdatv.ui.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.bongdatv.R
import com.bongdatv.data.model.Fixture
import com.bongdatv.data.model.LiveGoals
import com.bongdatv.data.model.LiveStatus
import com.bongdatv.ui.theme.AccentGreen
import com.bongdatv.ui.theme.CardBackground
import com.bongdatv.ui.theme.LiveRed
import com.bongdatv.ui.theme.TextPrimary
import com.bongdatv.ui.theme.TextSecondary

private const val WEB_REFERER = "https://sv2.hoiquan7.live/trang-chu"
private const val WEB_ORIGIN = "https://sv2.hoiquan7.live"
private const val WEB_USER_AGENT =
    "Mozilla/5.0 (Linux; Android TV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126 Safari/537.36"

@Composable
fun PlayerScreen(
    fixtureId: String,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val state by viewModel.uiState.collectAsState()
    val selectedStreamUrl = state.selectedStream?.sourceUrl
    val detailToggleFocusRequester = remember { FocusRequester() }
    var initialFocusFixtureId by remember { mutableStateOf<Int?>(null) }

    val exoPlayer = remember(context) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(WEB_USER_AGENT)
            .setDefaultRequestProperties(
                mapOf(
                    "Referer" to WEB_REFERER,
                    "Origin" to WEB_ORIGIN
                )
            )
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
    }

    LaunchedEffect(fixtureId) {
        viewModel.loadFixture(fixtureId)
    }

    DisposableEffect(activity) {
        val window = activity?.window
        val alreadyKeepingScreenOn = window?.attributes?.flags
            ?.and(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            if (!alreadyKeepingScreenOn) {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }

    LaunchedEffect(state.fixture?.id, state.isLoading) {
        val loadedFixtureId = state.fixture?.id
        if (loadedFixtureId != null && !state.isLoading && initialFocusFixtureId != loadedFixtureId) {
            initialFocusFixtureId = loadedFixtureId
            detailToggleFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(state.showOverlay, state.fixture?.id, state.isLoading) {
        if (!state.showOverlay && state.fixture != null && !state.isLoading) {
            detailToggleFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(selectedStreamUrl) {
        if (selectedStreamUrl.isNullOrBlank()) {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        } else {
            exoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(selectedStreamUrl)))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    BackHandler { onBack() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when {
                        keyEvent.key.isOverlayToggleKey() -> {
                            viewModel.toggleOverlay()
                            true
                        }

                        else -> false
                    }
                } else {
                    false
                }
            }
            .focusable()
    ) {
        AndroidView(
            factory = { ctx ->
                object : PlayerView(ctx) {
                    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
                        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                            return false
                        }
                        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode.isOverlayToggleKey()) {
                            viewModel.toggleOverlay()
                            return true
                        }
                        return super.dispatchKeyEvent(event)
                    }
                }.apply {
                    player = exoPlayer
                    useController = false
                    isFocusable = false
                    isFocusableInTouchMode = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (state.isLoading) {
            CenterNotice(message = "Đang tải trận đấu...")
        } else if (state.selectedStream == null && state.error != null) {
            CenterNotice(message = state.error!!)
        }

        if (state.showOverlay && state.fixture != null) {
            MatchDetailOverlay(
                fixture = state.fixture!!,
                goals = state.goals,
                status = state.status,
                streamOptions = state.streamOptions,
                selectedStream = state.selectedStream,
                onStreamSelected = viewModel::selectStream,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            )
        }

        if (!state.isLoading && state.fixture != null) {
            DetailOverlayToggleButton(
                isOverlayVisible = state.showOverlay,
                onToggle = viewModel::toggleOverlay,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .focusRequester(detailToggleFocusRequester)
            )
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

private fun Int.isOverlayToggleKey(): Boolean =
    this == KeyEvent.KEYCODE_MENU

private fun Key.isOverlayToggleKey(): Boolean =
    this == Key.Menu

private fun Key.isSelectKey(): Boolean =
    this == Key.DirectionCenter ||
        this == Key.Enter ||
        this == Key.NumPadEnter

@Composable
private fun DetailOverlayToggleButton(
    isOverlayVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val actionLabel = if (isOverlayVisible) {
        "Ẩn thông tin trận đấu"
    } else {
        "Hiện thông tin trận đấu"
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                color = Color.Black.copy(alpha = if (isOverlayVisible) 0.72f else 0.56f),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) AccentGreen else TextSecondary.copy(alpha = 0.55f),
                shape = RoundedCornerShape(8.dp)
            )
            .semantics { contentDescription = actionLabel }
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown && keyEvent.key.isSelectKey()) {
                    onToggle()
                    true
                } else {
                    false
                }
            }
            .focusable()
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_settings_24),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                if (isFocused || isOverlayVisible) AccentGreen else TextPrimary
            ),
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun CenterNotice(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MatchDetailOverlay(
    fixture: Fixture,
    goals: LiveGoals?,
    status: LiveStatus?,
    streamOptions: List<PlayerStreamOption>,
    selectedStream: PlayerStreamOption?,
    onStreamSelected: (PlayerStreamOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val homeScore = goals?.home ?: fixture.score?.home ?: 0
    val awayScore = goals?.away ?: fixture.score?.away ?: 0
    val statusText = when {
        status?.short != null && status.elapsed != null -> "${status.short} ${status.elapsed}'"
        status?.short != null -> status.short
        fixture.isLive -> "LIVE"
        fixture.status?.description != null -> fixture.status.description
        else -> ""
    }

    Column(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .widthIn(max = 460.dp)
            .background(Color.Black.copy(alpha = 0.72f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = fixture.league?.name ?: fixture.sport?.name ?: "",
            color = TextSecondary,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = fixture.title,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TeamSummary(
                name = fixture.homeTeam?.name.orEmpty(),
                logoUrl = fixture.homeTeam?.logoUrl,
                modifier = Modifier.width(145.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp)
            ) {
                Text(
                    text = "$homeScore - $awayScore",
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                if (statusText.isNotBlank()) {
                    Text(
                        text = statusText,
                        color = if (fixture.isLive) LiveRed else TextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }

            TeamSummary(
                name = fixture.awayTeam?.name.orEmpty(),
                logoUrl = fixture.awayTeam?.logoUrl,
                modifier = Modifier.width(145.dp)
            )
        }

        if (streamOptions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                streamOptions.forEach { stream ->
                    StreamChip(
                        stream = stream,
                        isSelected = stream.sourceUrl == selectedStream?.sourceUrl,
                        onClick = { onStreamSelected(stream) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamSummary(
    name: String,
    logoUrl: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = logoUrl,
            contentDescription = name,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            color = TextPrimary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StreamChip(
    stream: PlayerStreamOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor = when {
        isFocused -> AccentGreen
        isSelected -> TextPrimary
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .widthIn(max = 150.dp)
            .background(
                color = if (isSelected) AccentGreen.copy(alpha = 0.22f) else CardBackground,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = if (isFocused || isSelected) 1.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = stream.name.ifBlank { "Nguồn ${stream.id}" },
            color = TextPrimary,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
