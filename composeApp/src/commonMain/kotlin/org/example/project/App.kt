package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import kmp_with_crypto.composeapp.generated.resources.Res
import kmp_with_crypto.composeapp.generated.resources.compose_multiplatform
import org.example.project.core.presentation.util.ObserveAsEvents
import org.example.project.crypto.presentation.coin_detail.CoinDetailScreen
import org.example.project.crypto.presentation.coin_list.CoinListAction
import org.example.project.crypto.presentation.coin_list.CoinListEvent
import org.example.project.crypto.presentation.coin_list.CoinListScreen
import org.example.project.crypto.presentation.coin_list.CoinListViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalSharedTransitionApi::class)
@Composable
@Preview
fun App(
    viewModel: CoinListViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(events = viewModel.events) { event ->
        when (event) {
            is CoinListEvent.Error -> {
                println("error: ${event.error}")
            }
        }
    }
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

    SharedTransitionLayout {
        ListDetailPaneScaffold(
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    CoinListScreen(animatedPaneScope = this, state = state, onAction = { action ->
                        viewModel.onAction(action)
                        when (action) {
                            is CoinListAction.OnCoinClicked -> {
                                navigator.navigateTo(pane = ListDetailPaneScaffoldRole.Detail)
                            }

                            CoinListAction.OnRefresh -> TODO()
                        }
                    })
                }
            },
            detailPane = {
                AnimatedPane {
                    CoinDetailScreen(animatedPaneScope = this, state = state)
                }
            }
        )
    }
    MaterialTheme {
        /*var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }*/
    }
}