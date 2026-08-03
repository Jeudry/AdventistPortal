package com.adventistportal.chat.data.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.adventistportal.chat.data.participant.KtorChatParticipantService
import com.adventistportal.chat.data.chat.KtorChatService
import com.adventistportal.chat.data.chat.OfflineFirstChatRepository
import com.adventistportal.chat.data.chat.WebSocketChatConnectionClient
import com.adventistportal.chat.data.lifecycle.AppLifecycleObserver
import com.adventistportal.chat.data.message.KtorChatMessageService
import com.adventistportal.chat.data.message.OfflineFirstMessageRepository
import com.adventistportal.chat.data.network.ConnectionErrorHandler
import com.adventistportal.chat.data.network.ConnectionRetryHandler
import com.adventistportal.chat.data.network.KtorWebSocketConnector
import com.adventistportal.chat.data.notification.KtorDeviceTokenService
import com.adventistportal.chat.data.participant.OfflineFirstChatParticipantRepository
import com.adventistportal.chat.database.DatabaseFactory
import com.adventistportal.chat.domain.chat.ChatConnectionClient
import com.adventistportal.chat.domain.participant.ChatParticipantService
import com.adventistportal.chat.domain.chat.ChatRepository
import com.adventistportal.chat.domain.chat.ChatService
import com.adventistportal.chat.domain.message.ChatMessageService
import com.adventistportal.chat.domain.message.MessageRepository
import com.adventistportal.chat.domain.notification.DeviceTokenService
import com.adventistportal.chat.domain.participant.ChatParticipantRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformChatDataModule: Module

val chatDataModule = module {
    includes(platformChatDataModule)

    singleOf(::KtorChatParticipantService) bind ChatParticipantService::class
    singleOf(::KtorChatService) bind ChatService::class
    singleOf(::OfflineFirstChatRepository) bind ChatRepository::class
    singleOf(::OfflineFirstMessageRepository) bind MessageRepository::class
    singleOf(::WebSocketChatConnectionClient) bind ChatConnectionClient::class
    singleOf(::ConnectionRetryHandler)
    singleOf(::KtorWebSocketConnector)
    singleOf(::KtorChatMessageService) bind ChatMessageService::class
    singleOf(::KtorDeviceTokenService) bind DeviceTokenService::class
    singleOf(::OfflineFirstChatParticipantRepository) bind ChatParticipantRepository::class
    single {
        Json {
            ignoreUnknownKeys = true
        }
    }
    single {
        get<DatabaseFactory>()
            .create()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}