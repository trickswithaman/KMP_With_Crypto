package org.example.project.crypto.data.networking

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.example.project.core.data.networking.constructUrl
import org.example.project.core.data.networking.safeCall
import org.example.project.core.domain.util.NetworkError
import org.example.project.crypto.domain.Coin
import org.example.project.crypto.domain.CoinDataSource
import org.example.project.core.domain.util.Result
import org.example.project.core.domain.util.map
import org.example.project.crypto.data.mappers.toCoin
import org.example.project.crypto.data.mappers.toCoinPrice
import org.example.project.crypto.data.networking.dto.CoinHistoryDto
import org.example.project.crypto.data.networking.dto.CoinResponseDto
import org.example.project.crypto.data.networking.dto.SearchDto
import org.example.project.crypto.data.networking.dto.SearchResponseDto
import org.example.project.crypto.domain.CoinPrice

class RemoteCoinDataSource(
    private val httpClient: HttpClient
): CoinDataSource {


    override suspend fun getSearchResult (
        name: String
    ): Result<List<SearchDto>, NetworkError>{
        return safeCall <SearchResponseDto>{
            httpClient.get(
                urlString = constructUrl("/assets/$name")
            ){
                parameter("n", name)
            }
        }.map { response ->
            response.data
        }
    }

    override suspend fun getCoins(): Result<List<Coin>, NetworkError> {
        return safeCall<CoinResponseDto> {
            httpClient.get(
                urlString = constructUrl("/assets")
            )
        }.map { response ->
            response.data.map { it.toCoin() }
        }
    }



    override suspend fun getCoinHistory(
        coinId: String,
        start: LocalDateTime,
        end: LocalDateTime
    ): Result<List<CoinPrice>, NetworkError> {

        val startMillis = start.toInstant(TimeZone.UTC).toEpochMilliseconds()
        val endMillis = end.toInstant(TimeZone.UTC).toEpochMilliseconds()


        return safeCall<CoinHistoryDto> {
            httpClient.get(
                urlString = constructUrl("/assets/$coinId/history")
            ) {
                parameter("interval", "m15")
                parameter("start", startMillis)
                parameter("end", endMillis)


            }
        }.map { response ->
            response.data.map { it.toCoinPrice() }

        }


    }

}