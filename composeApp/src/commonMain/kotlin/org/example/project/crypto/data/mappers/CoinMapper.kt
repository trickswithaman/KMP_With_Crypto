package org.example.project.crypto.data.mappers

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.presentation.util.getDrawableIdForCoin
import org.example.project.crypto.data.networking.dto.CoinDto
import org.example.project.crypto.data.networking.dto.CoinPriceDto
import org.example.project.crypto.data.networking.dto.SearchDto
import org.example.project.crypto.domain.Coin
import org.example.project.crypto.domain.CoinPrice
import org.example.project.crypto.presentation.model.CoinUi
import org.example.project.crypto.presentation.model.toDisplayableNumber

fun CoinDto.toCoin(): Coin {
    return Coin(
        id = id,
        rank = rank,
        name = name,
        symbol = symbol,
        marketCapUsd = marketCapUsd,
        priceUsd = priceUsd,
        changePercent24Hr = changePercent24Hr,
    )
}

fun SearchDto.toCoinUi(): CoinUi {
    return CoinUi(
        id = id,
        rank = rank,
        symbol = symbol,
        name = name,
        marketCapUsd = priceUsd.toDisplayableNumber(),
        priceUsd = priceUsd.toDisplayableNumber(),
        changePercent24Hr = changePercent24Hr.toDisplayableNumber(),
        iconRes = getDrawableIdForCoin(symbol)
    )
}



fun CoinPriceDto.toCoinPrice(): CoinPrice {
    val instant = Instant.fromEpochMilliseconds(time)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    return CoinPrice(
        priceUsd = priceUsd,
        dateTime = localDateTime
    )
}
