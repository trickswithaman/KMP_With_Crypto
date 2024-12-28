package org.example.project.crypto.presentation.coin_list

import androidx.compose.runtime.Immutable
import org.example.project.crypto.presentation.model.CoinUi

@Immutable
data class CoinListState(
    val isLoading: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedCoin: CoinUi? = null,
)
