package com.adventistportal.chat.domain.message

import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.core.domain.util.DataError
import com.adventistportal.core.domain.util.EmptyResult
import com.adventistportal.core.domain.util.Result

interface ChatMessageService {
    suspend fun fetchMessages(
        chatId: String,
        before: String? = null
    ): Result<List<ChatMessage>, DataError.Remote>

    suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote>
}