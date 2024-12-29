package org.example.project.crypto.domain

import kotlinx.datetime.LocalDateTime
import org.example.project.core.domain.util.NetworkError
import org.example.project.core.domain.util.Result

interface CoinDataSource {
    suspend fun getCoins(): Result<List<Coin>, NetworkError>
    suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Result<List<CoinPrice>, NetworkError>
}