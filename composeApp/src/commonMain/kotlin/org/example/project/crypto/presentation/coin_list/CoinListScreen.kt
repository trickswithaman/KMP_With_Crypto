package org.example.project.crypto.presentation.coin_list

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.crypto.presentation.coin_list.components.CoinListItem
import org.example.project.crypto.presentation.coin_list.components.SearchTextField


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CoinListScreen(
    animatedPaneScope: AnimatedPaneScope,
    state: CoinListState,
    onAction: (CoinListAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column {
        SearchTextField(
            searchQuery = state.searchQuery,
            onSearchQueryChange = { query ->
               onAction(CoinListAction.OnSearchQueryChange(query))
            },
            onImeSearch = {
                keyboardController?.hide()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )


        if (state.isLoading) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(state.coins.size, key = { state.coins[it].id }) { coin ->
                    val coin = state.coins[coin]

                    CoinListItem(
                        animatedPaneScope = animatedPaneScope,
                        coin = coin,
                        onItemClick = {
                            onAction(CoinListAction.OnCoinClicked(coinUi = coin))
                                      },

                        modifier = Modifier.fillParentMaxWidth()
                    )
                    HorizontalDivider()
                }
            }
        }
    }

}


//@PreviewLightDark
//@PreviewDynamicColors
//@Composable
//fun CoinListScreenPreview(){
//    MVVMCleanArchitectureInComposeTheme {
//        CoinListScreen(
//            state = CoinListState(coins = (1..100)
//                .map { previewCoin.copy(id = it.toString()) }),
//            onAction = {}
//        )
//    }
//}