package com.adventistportal.chat.data.message

import com.adventistportal.chat.data.dto.ChatMessageDto
import com.adventistportal.chat.data.mappers.toDomain
import com.adventistportal.chat.domain.message.ChatMessageService
import com.adventistportal.chat.domain.models.ChatMessage
import com.adventistportal.core.data.networking.delete
import com.adventistportal.core.data.networking.get
import com.adventistportal.core.domain.util.DataError
import com.adventistportal.core.domain.util.EmptyResult
import com.adventistportal.core.domain.util.Result
import com.adventistportal.core.domain.util.map
import io.ktor.client.HttpClient

class KtorChatMessageService(
    private val httpClient: HttpClient
): ChatMessageService {

    override suspend fun deleteMessage(messageId: String): EmptyResult<DataError.Remote> {
        return httpClient.delete(
            route = "/messages/$messageId"
        )
    }

    override suspend fun fetchMessages(
        chatId: String,
        before: String?
    ): Result<List<ChatMessage>, DataError.Remote> {
        return httpClient.get<List<ChatMessageDto>>(
            route = "/chat/$chatId/messages",
            queryParams = buildMap {
                this["pageSize"] = ChatMessageConstants.PAGE_SIZE
                if(before != null) {
                    this["before"] = before
                }
            }
        ).map { it.map { it.toDomain() } }
    }
}