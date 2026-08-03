package com.adventistportal.chat.data.di

import com.adventistportal.chat.data.lifecycle.AppLifecycleObserver
import com.adventistportal.chat.data.network.ConnectionErrorHandler
import com.adventistportal.chat.data.network.ConnectivityObserver
import com.adventistportal.chat.data.notification.FirebasePushNotificationService
import com.adventistportal.chat.database.DatabaseFactory
import com.adventistportal.chat.domain.notification.PushNotificationService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformChatDataModule = module {
    single { DatabaseFactory(androidContext()) }
    singleOf(::AppLifecycleObserver)
    singleOf(::ConnectivityObserver)
    singleOf(::ConnectionErrorHandler)

    singleOf(::FirebasePushNotificationService) bind PushNotificationService::class
}