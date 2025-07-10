package com.speechai.speechai.screens.history

import android.webkit.WebHistoryItem
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.audio.AudioUtils.getTotalScoreColor
import com.speechai.speechai.auth.AuthViewModel
import com.speechai.speechai.backgroundColor
import com.speechai.speechai.composables.CustomTopBar
import com.speechai.speechai.composables.Loading
import com.speechai.speechai.composables.SignInBenefitsContent
import com.speechai.speechai.composables.SmallBadge
import com.speechai.speechai.data.models.DetailedAudioAnalysisModel
import com.speechai.speechai.greenColor
import com.speechai.speechai.lightGreenColor
import com.speechai.speechai.models.Screen
import com.speechai.speechai.models.StateTag
import com.speechai.speechai.models.StateTagModel
import com.speechai.speechai.models.getStateTagModel
import com.speechai.speechai.quaternaryColor
import com.speechai.speechai.redColor
import com.speechai.speechai.screens.history.HistoryUtils.emptyStates
import com.speechai.speechai.screens.history.HistoryUtils.menuItems
import com.speechai.speechai.screens.home.HomeScreenRoute
import com.speechai.speechai.secondaryColor
import com.speechai.speechai.subtitleTextColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.utils.bounceClick
import com.speechai.speechai.utils.formatTime
import com.speechai.speechai.whiteColor


@Composable
fun HistoryScreen(
    navController: NavController,
    onRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val historyViewModel = hiltViewModel<HistoryViewModel>()
    val historyState by historyViewModel.audioHistory.collectAsState()
    val authViewModel = hiltViewModel<AuthViewModel>()
    var selectedIndex by remember {
        mutableStateOf(StateTag.OTHER)
    }
    var isLoggedIn by remember {
        mutableStateOf(authViewModel.getCurrentUser() != null)
    }
    
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            historyViewModel.getAudioHistory()
        }
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column {
            CustomTopBar(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                startComposable = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                color = secondaryColor,
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = whiteColor,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                },
                endComposable = {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFEDDD53),
                                        Color(0xFFFFA12D),
                                    )
                                ),
                                shape = CircleShape
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.crown),
                            contentDescription = null,
                            tint = whiteColor,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                },
                midComposable = {
                    Text(
                        text = stringResource(R.string.title),
                        style = CustomTextStyle.copy(
                            color = whiteColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )


            AnimatedContent(
                targetState = isLoggedIn,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 200)))
                        .togetherWith(fadeOut(animationSpec = tween(200)))
                }
            ) {
                if (it) {
                    Column {
                        LazyRow(
                            modifier = Modifier
                                .padding(top = 30.dp, bottom = 20.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            itemsIndexed(menuItems) { index, item ->
                                val stateTagModel = getStateTagModel(
                                    when (item) {
                                        "excellent" -> StateTag.EXCELLENT
                                        "fair" -> StateTag.FAIR
                                        "bad" -> StateTag.BAD
                                        else -> StateTag.OTHER
                                    }
                                ) ?: StateTagModel(
                                    title = "all",
                                    borderColor = greenColor,
                                    bgColor = lightGreenColor,
                                    tag = StateTag.OTHER
                                )

                                val bgColor = if (stateTagModel.tag == selectedIndex)
                                    stateTagModel.bgColor else
                                    secondaryColor

                                val borderColor = if (stateTagModel.tag == selectedIndex)
                                    stateTagModel.borderColor else
                                    tertiaryColor

                                NavigationChip(
                                    index = index,
                                    bgColor = bgColor,
                                    borderColor = borderColor,
                                    onSelected = {
                                        selectedIndex = stateTagModel.tag
                                    },
                                    item = item
                                )
                            }
                        }

                        HistoryItemsScreen(
                            onRecordClick = onRecordClick,
                            selectedIndex = selectedIndex,
                            historyState = historyState,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                        )

                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        SignInBenefitsContent(
                            modifier = Modifier
                                .align(Alignment.CenterStart),
                            onResult = { state ->
                                isLoggedIn = state?.data != null
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            backgroundColor
                        )
                    )
                )
                .align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItemsScreen(
    onRecordClick: () -> Unit,
    selectedIndex: StateTag,
    historyState: HistoryUiState,
    modifier: Modifier = Modifier,
) {
    val selectedHistory = when (selectedIndex) {
        StateTag.EXCELLENT -> historyState.excellentHistory
        StateTag.FAIR -> historyState.fairHistory
        StateTag.BAD -> historyState.badHistory
        else -> historyState.allHistory
    }
    val selectedEmptyState = emptyStates.find { it.tag == selectedIndex }

    if (historyState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "fetching your speech history",
                    style = CustomTextStyle.copy(
                        color = whiteColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                )
                Loading(
                    modifier = Modifier
                        .padding(top = 30.dp)
                )
            }
        }
    } else if (historyState.error == null) {
        if (selectedHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Light,
                                color = whiteColor,
                            )) {
                                append("${selectedEmptyState?.title.orEmpty()}\n")
                            }
                            withStyle(SpanStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = whiteColor,
                            )) {
                                append(selectedEmptyState?.subtitle.orEmpty())
                            }
                        },
                        style = CustomTextStyle.copy(
                            lineHeight = 40.sp
                        ),
                        modifier = Modifier
                    )

                    SmallBadge(
                        startComposable = {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(redColor,shape = CircleShape)
                                    .border(width = 0.5.dp, color = whiteColor.copy(alpha = 0.3f), shape = CircleShape)
                            )
                        },
                        backgroundColor = secondaryColor,
                        borderColor = tertiaryColor,
                        text = selectedEmptyState?.buttonText.orEmpty(),
                        icon = Icons.Default.KeyboardArrowRight,
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .bounceClick {
                                onRecordClick()
                            }
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                selectedHistory.forEach {
                    stickyHeader {
                        Text(
                            text = it.title,
                            style = CustomTextStyle.copy(
                                color = subtitleTextColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor)
                                .padding(start = 4.dp, bottom = 10.dp)
                        )
                    }

                    itemsIndexed(it.history) { index, item ->
                        HistoryItem(
                            item = item,
                        )
                    }
                }

            }
        }
    }

    if (historyState.error != null) {
        // add error screen
    }
}

@Composable
fun HistoryItem(
    item: DetailedAudioAnalysisModel,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = secondaryColor,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = tertiaryColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .bounceClick {

                }
                .size(28.dp)
                .background(
                    color = tertiaryColor,
                    shape = CircleShape
                )
                .padding(4.dp)
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center),
                tint = whiteColor
            )
        }

        Text(
            text = formatTime(item.audioMetadata?.durationMillis ?: 0),
            style = CustomTextStyle.copy(
                color = whiteColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )

        Text(
            text = if (item.totalScore == null) "" else "${item.totalScore} %",
            style = CustomTextStyle.copy(
                color = whiteColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .background(
                    color = getTotalScoreColor(
                        score = item.totalScore
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = whiteColor,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(24.dp)
        )
    }
}

@Composable
fun NavigationChip(
    index: Int,
    item: String,
    bgColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    onSelected: (Int) -> Unit = {}
) {
    Box(
        modifier = modifier
            .bounceClick {
                onSelected(index)
            }
            .background(
                color = bgColor,
                shape = RoundedCornerShape(100.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(100.dp)
            )
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = item,
            style = CustomTextStyle.copy(
                color = whiteColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}