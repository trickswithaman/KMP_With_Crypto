package org.example.project.crypto.presentation.coin_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmp_with_crypto.composeapp.generated.resources.Res
import kmp_with_crypto.composeapp.generated.resources.change_last_24h
import kmp_with_crypto.composeapp.generated.resources.dollar
import kmp_with_crypto.composeapp.generated.resources.go_back
import kmp_with_crypto.composeapp.generated.resources.market_cap
import kmp_with_crypto.composeapp.generated.resources.price
import kmp_with_crypto.composeapp.generated.resources.stock
import kmp_with_crypto.composeapp.generated.resources.trending
import kmp_with_crypto.composeapp.generated.resources.trending_down
import org.example.project.crypto.presentation.coin_detail.components.InfoCard
import org.example.project.crypto.presentation.coin_list.CoinListState
import org.example.project.crypto.presentation.coin_list.components.currentModeIsPortrait
import org.example.project.crypto.presentation.coin_list.components.getScreenSize
import org.example.project.crypto.presentation.coin_list.components.putIfPortrait
import org.example.project.crypto.presentation.model.toDisplayableNumber
import org.example.project.ui.theme.greenBackground
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CoinDetailScreen(
    animatedPaneScope: AnimatedPaneScope,
    state: CoinListState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}

) {
    val contentColor = if(isSystemInDarkTheme()) {
        androidx.compose.ui.graphics.Color.White
    } else {
        androidx.compose.ui.graphics.Color.Black
    }
    if(state.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if(state.selectedCoin != null) {
        val coin = state.selectedCoin
        Box {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(coin.iconRes),
                    contentDescription = coin.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(100.dp)
                        .putIfPortrait(
                            Modifier.sharedElement(
                                state = rememberSharedContentState(key = "image/${coin.id}"),
                                animatedVisibilityScope = animatedPaneScope,
                                boundsTransform = { _, _ ->
                                    tween(durationMillis = 1000)
                                }
                            )
                        )
                )
                Text(
                    text = coin.name,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
                Text(
                    text = coin.symbol,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    InfoCard(
                        title = stringResource( Res.string.market_cap),
                        formattedText = "$ ${coin.marketCapUsd.formatted}",
                        icon = Res.drawable.stock
                    )
                    InfoCard(
                        title = stringResource(Res.string.price),
                        formattedText = "$ ${coin.priceUsd.formatted}",
                        icon = Res.drawable.dollar
                    )
                    val absoluteChangeFormatted =
                        (coin.priceUsd.value * (coin.changePercent24Hr.value / 100))
                            .toDisplayableNumber()
                    val isPositive = coin.changePercent24Hr.value > 0.0
                    val contentColor = if(isPositive) {
                        if(isSystemInDarkTheme()) androidx.compose.ui.graphics.Color.Green else greenBackground
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    InfoCard(
                        title = stringResource(Res.string.change_last_24h),
                        formattedText = absoluteChangeFormatted.formatted,
                        icon = if(isPositive) {
                            Res.drawable.trending
                        } else {
                            Res.drawable.trending_down
                        },
                        contentColor = contentColor
                    )
                }

                AnimatedVisibility(
                    visible = coin.coinPriceHistory.isNotEmpty()
                ) {
                    var selectedDataPoint by remember {
                        mutableStateOf<DataPoint?>(null)
                    }
                    var labelWidth by remember {
                        mutableFloatStateOf(0f)
                    }
                    var totalChartWidth by remember {
                        mutableFloatStateOf(0f)
                    }
                    val amountOfVisibleDataPoints = if(labelWidth > 0) {
                        ((totalChartWidth - 2.5 * labelWidth) / labelWidth).toInt()
                    } else {
                        0
                    }
                    val startIndex = (coin.coinPriceHistory.lastIndex - amountOfVisibleDataPoints)
                        .coerceAtLeast(0)
                    val screenSize = getScreenSize()
                    val aspectRatio = remember(screenSize) { calculateAspectRatio(screenSize) }
                    LineChart(
                        dataPoints = coin.coinPriceHistory,
                        style = ChartStyle(
                            charLineColor = MaterialTheme.colorScheme.primary,
                            unselectedColor = MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.3f
                            ),
                            selectedColor = MaterialTheme.colorScheme.primary,
                            helperLinesThicknessPx = 5f,
                            axisLinesThicknessPx = 5f,
                            labelFontSize = 14.sp,
                            minYLabelSpacing = 25.dp,
                            verticalPadding = 8.dp,
                            horizontalPadding = 8.dp,
                            xAxisLabelSpacing = 8.dp,
                        ),
                        visibleDataPointsIndices = startIndex..coin.coinPriceHistory.lastIndex,
                        unit = "$",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio)
                            .onSizeChanged { totalChartWidth = it.width.toFloat() },
                        selectedDataPoint = selectedDataPoint,
                        onSelectedDataPoint = {
                            selectedDataPoint = it
                        },
                        onXLabelWidthChange = { labelWidth = it }
                    )
                }
            }
            if (currentModeIsPortrait()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.go_back),
                    tint = contentColor,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(24.dp)
                        .size(36.dp)
                )
            }
        }

    }
}

fun calculateAspectRatio(screenSize: IntSize): Float {
    val aspectRatio = screenSize.height.toFloat() / screenSize.width.toFloat()

    return when {
        aspectRatio > 1.42 -> 14f / 9f   // Mobile devices (portrait mode)
        aspectRatio in 0.5..1.42 -> 20f / 9f   // Tablets
        else -> 26f / 9f  // Desktop or landscape-oriented screens
    }
}