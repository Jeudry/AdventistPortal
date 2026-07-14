package com.adventistportal.chat.api.controllers

import com.adventistportal.core.api.utils.requestUserId
import com.adventistportal.core.domain.types.ChatMessageId
import com.adventistportal.chat.service.ChatMessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
class ChatMessageController(private val chatMessageService: ChatMessageService) {
    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @PathVariable("messageId") messageId: ChatMessageId,
    ) {
        chatMessageService.deleteMessage(messageId, requestUserId)
    }


}