package com.adventistportal.chat.api.exception_handling

import com.adventistportal.chat.domain.exceptions.ChatMessageNotFoundEx
import com.adventistportal.chat.domain.exceptions.ChatNotFoundEx
import com.adventistportal.chat.domain.exceptions.ChatParticipantNotFoundEx
import com.adventistportal.chat.domain.exceptions.InvalidChatSizeEx
import com.adventistportal.chat.domain.exceptions.InvalidProfilePictureEx
import com.adventistportal.chat.domain.exceptions.StorageEx
import com.adventistportal.core.domain.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ChatExceptionHandler(

) {
  @ExceptionHandler(
    ChatNotFoundEx::class,
    ChatMessageNotFoundEx::class,
    ChatParticipantNotFoundEx::class
  )
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun onForbidden(
    e: Exception
  ) = mapOf(
    "code" to "NOT_FOUND",
    "message" to e.message
  )
  
  @ExceptionHandler(InvalidChatSizeEx::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun onInvalidChatSize(
    e: InvalidChatSizeEx
  ) = mapOf(
    "code" to "INVALID_CHAT_SIZE",
    "message" to e.message
  )
  
  @ExceptionHandler(InvalidProfilePictureEx::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun onInvalidProfilePicture(
    e: InvalidProfilePictureEx
  ) = mapOf(
    "code" to "INVALID_PROFILE_PICTURE",
    "message" to e.message
  )
  
  @ExceptionHandler(StorageEx::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun onStorageEx(
    e: StorageEx
  ) = mapOf(
    "code" to "STORAGE_ERROR",
    "message" to e.message
  )
}