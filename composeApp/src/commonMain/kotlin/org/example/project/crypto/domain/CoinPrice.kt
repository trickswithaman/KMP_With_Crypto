package org.example.project.crypto.domain

import kotlinx.datetime.LocalDateTime

data class CoinPrice(
    val priceUsd: Double,
    val dateTime: LocalDateTime
)