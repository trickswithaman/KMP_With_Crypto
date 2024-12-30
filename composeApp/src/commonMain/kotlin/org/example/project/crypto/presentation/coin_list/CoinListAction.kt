package org.example.project.crypto.presentation.coin_list

import org.example.project.crypto.presentation.model.CoinUi

sealed interface CoinListAction {
    data class  OnSearchQueryChange(val query: String): CoinListAction
    data class OnCoinClicked(val coinUi: CoinUi) : CoinListAction
    data object OnRefresh : CoinListAction
}