package org.example.project.di

import org.example.project.core.data.networking.HttpClientFactory
import org.example.project.crypto.data.networking.RemoteCoinDataSource
import org.example.project.crypto.domain.CoinDataSource
import org.example.project.crypto.presentation.coin_list.CoinListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    singleOf(::RemoteCoinDataSource).bind<CoinDataSource>()
    viewModelOf(::CoinListViewModel)
}