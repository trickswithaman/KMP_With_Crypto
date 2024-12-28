package org.example.project.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.core.domain.util.onError
import org.example.project.core.domain.util.onSuccess
import org.example.project.crypto.domain.CoinDataSource
import org.example.project.crypto.presentation.model.toCoinUi

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
//                _state.update { it.copy(selectedCoinUi = action.coinUi) }
                selectCoin(action.coinUi)
            }
        }
    }

/*    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(selectedCoinUi = coinUi) }
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
                _state.update { it.copy(selectedCoinUi = it.selectedCoinUi?.copy(coinPriceHistory = dataPoints)) }
            }.onError { networkError ->
                _events.send(CoinListEvent.Error(networkError))
            }
        }
    }*/

    // this fun call for call coins api
    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading =  true) }
            coinDataSource
                .getCoins()
                .onSuccess { coins ->
                    _state.update { it.copy(
                        isLoading = false,
                        coins = coins.map { it.toCoinUi() }
                    ) }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(CoinListEvent.Error(error))
                }


        }
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