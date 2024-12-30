package org.example.project.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.domain.util.onError
import org.example.project.core.domain.util.onSuccess
import org.example.project.crypto.data.mappers.toCoinUi
import org.example.project.crypto.domain.CoinDataSource
import org.example.project.crypto.presentation.coin_detail.DataPoint
import org.example.project.crypto.presentation.model.CoinUi
import org.example.project.crypto.presentation.model.toCoinUi
import kotlin.time.Duration.Companion.days

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListState())
    val state: StateFlow<CoinListState> = _state

    private val _events = Channel<CoinListEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadCoins()
    }

    fun onAction(action: CoinListAction) {
        when (action) {
            CoinListAction.OnRefresh -> loadCoins()
            is CoinListAction.OnCoinClicked -> {
                _state.update { it.copy(selectedCoin = action.coinUi) }
                selectCoin(action.coinUi)
            }
            is CoinListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                searchCoin(action.query)
            }
            else -> {}
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(selectedCoin = coinUi) }
        viewModelScope.launch {
            coinDataSource.getCoinHistory(
                coinId = coinUi.id,
                start = Clock.System.now().minus(7.days)
                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                end = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ).onSuccess { history ->
                val dataPoints = history
                    .sortedBy { it.dateTime }
                    .map { coinPrice ->
                        DataPoint(
                            x = coinPrice.dateTime.hour.toFloat(),
                            y = coinPrice.priceUsd.toFloat(),
                            xLabel = formatDateTime(coinPrice.dateTime),
                        )
                    }
                _state.update { it.copy(selectedCoin = it.selectedCoin?.copy(coinPriceHistory = dataPoints)) }
            }.onError { networkError ->
                _events.send(CoinListEvent.Error(networkError))
            }
        }
    }

   /* private fun searchCoin(query: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    coins = if (query.isBlank()) {
                        it.coins
                    } else {
                        it.coins.filter { coin ->
                            coin.name.contains(query, ignoreCase = true) || coin.symbol.contains(
                                query,
                                ignoreCase = true
                            )
                        }
                    }
                )
            }
        }
    }*/
   private fun searchCoin(query: String) {

       _state.update {
           it.copy(
               coins = if (query.isBlank()) {
                   it.originalCoins
               } else {
                   it.originalCoins.filter { coin ->
                       coin.name.contains(query, ignoreCase = true) || coin.symbol.contains(
                           query,
                           ignoreCase = true
                       )
                   }
               }
           )
       }
   }
        // this fun call for call coins api
        private fun loadCoins() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }
                coinDataSource
                    .getCoins()
                    .onSuccess { coins ->
                        val coinUiList = coins.map { it.toCoinUi() }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                originalCoins = coinUiList,
                                coins = coinUiList
                            )
                        }
                    }
                    .onError { error ->
                        _state.update { it.copy(isLoading = false) }
                        _events.send(CoinListEvent.Error(error))
                    }
            }
        }
   /* private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            coins = coins.map { it.toCoinUi() }
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }
        }
    }*/
}



@OptIn(FormatStringsInDatetimeFormats::class)
fun formatDateTime(time: LocalDateTime): String {
    val formatDate = LocalDateTime.Format {
        amPmHour(Padding.NONE)
        amPmMarker("AM", "PM")
        byUnicodePattern("\nM/d")
    }
    return formatDate.format(time)
}