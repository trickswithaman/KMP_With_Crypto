package org.example.project.crypto.presentation.coin_list

import org.example.project.core.domain.util.NetworkError
import org.koin.core.logger.MESSAGE

sealed interface CoinListEvent {
    data class Error(val error: NetworkError): CoinListEvent
}