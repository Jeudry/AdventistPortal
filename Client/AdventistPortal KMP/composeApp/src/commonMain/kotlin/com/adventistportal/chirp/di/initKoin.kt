package com.adventistportal.di

import com.adventistportal.auth.presentation.di.authPresentationModule
import com.adventistportal.chat.data.di.chatDataModule
import com.adventistportal.chat.presentation.di.chatPresentationModule
import com.adventistportal.core.data.di.coreDataModule
import com.adventistportal.core.presentation.di.corePresentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            coreDataModule,
            authPresentationModule,
            appModule,
            chatPresentationModule,
            corePresentationModule,
            chatDataModule
        )
    }
}