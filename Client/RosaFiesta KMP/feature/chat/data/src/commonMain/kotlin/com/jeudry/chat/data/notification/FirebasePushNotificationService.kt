package com.adventistportal.chat.data.notification

import com.adventistportal.chat.domain.notification.PushNotificationService
import kotlinx.coroutines.flow.Flow

expect class FirebasePushNotificationService: PushNotificationService {
    override fun observeDeviceToken(): Flow<String?>
}