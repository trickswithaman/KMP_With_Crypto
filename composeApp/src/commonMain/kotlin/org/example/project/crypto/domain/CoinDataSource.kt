package org.example.project.crypto.domain

import kotlinx.datetime.LocalDateTime
import org.example.project.core.domain.util.NetworkError

interface CoinDataSource {
    suspend fun getCoins(): org.example.project.core.domain.util.Result<List<Coin>, NetworkError>
    suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Result<List<CoinPrice>, NetworkError>
}