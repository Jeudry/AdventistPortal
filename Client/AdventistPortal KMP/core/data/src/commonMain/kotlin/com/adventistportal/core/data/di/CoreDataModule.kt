package com.adventistportal.core.data.di

import com.adventistportal.core.data.auth.DataStoreSessionStorage
import com.adventistportal.core.data.auth.KtorAuthService
import com.adventistportal.core.data.logging.KermitLogger
import com.adventistportal.core.data.networking.HttpClientFactory
import com.adventistportal.core.domain.auth.AuthService
import com.adventistportal.core.domain.auth.SessionStorage
import com.adventistportal.core.domain.logging.AdventistPortalLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCoreDataModule: Module

val coreDataModule = module {
    includes(platformCoreDataModule)
    single<AdventistPortalLogger> { KermitLogger }
    single {
        HttpClientFactory(get(), get()).create(get())
    }
    singleOf(::KtorAuthService) bind AuthService::class
    singleOf(::DataStoreSessionStorage) bind SessionStorage::class
}