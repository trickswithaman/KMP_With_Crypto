package org.example.project.crypto.data.networking.dto

data class SearchDto(
    val id: String,
    val name: String,
    val symbol: String,
    val rank: Int,
    val priceUsd: Double,
    val changePercent24Hr: Double,
    val marketCapUsd: Double,
)
