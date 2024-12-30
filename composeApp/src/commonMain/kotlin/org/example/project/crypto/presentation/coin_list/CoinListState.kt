package org.example.project.crypto.presentation.coin_list

import androidx.compose.runtime.Immutable
import org.example.project.crypto.presentation.model.CoinUi

@Immutable
data class CoinListState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val originalCoins: List<CoinUi> = emptyList(), // <--- ADDED
    val coins: List<CoinUi> = emptyList(),
    val selectedCoin: CoinUi? = null,
//    val searchQuery: String = "",
//    val searchResult : List<CoinUi> = emptyList(),
//    val isLoading: Boolean = false,
//    val coins: List<CoinUi> = emptyList(),
//    val selectedCoin: CoinUi? = null,
)
