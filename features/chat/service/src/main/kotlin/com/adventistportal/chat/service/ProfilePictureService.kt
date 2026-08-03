package com.adventistportal.chat.service

import com.adventistportal.chat.domain.events.ProfilePictureUpdatedEv
import com.adventistportal.chat.domain.exceptions.ChatParticipantNotFoundEx
import com.adventistportal.chat.domain.exceptions.InvalidProfilePictureEx
import com.adventistportal.chat.domain.models.ProfilePictureUploadCredentials
import com.adventistportal.core.domain.types.UserId
import com.adventistportal.chat.infra.database.repositories.ChatParticipantRepository
import com.adventistportal.chat.infra.storage.SupabaseStorageService
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProfilePictureService(
  private val supabaseStorageService: SupabaseStorageService,
  private val chatParticipantRepository: ChatParticipantRepository,
  private val applicationEventPublisher: ApplicationEventPublisher,
  @param:Value("\${supabase.url}") private val supabaseUrl: String,
) {
  
  private val logger = LoggerFactory.getLogger(javaClass)
  
  fun generateUploadCredentials(
    userId: UserId,
    mimeType: String
  ): ProfilePictureUploadCredentials {
    return supabaseStorageService.generateSignedUploadUrl(
      userId = userId,
      mimeType = mimeType
    )
  }
  
  @Transactional
  fun deleteProfilePicture(userId: UserId) {
    val participant = chatParticipantRepository.findByIdOrNull(userId)
      ?: throw ChatParticipantNotFoundEx(userId)
    
    participant.profilePictureUrl?.let { url ->
      chatParticipantRepository.save(
        participant.apply {
          profilePictureUrl = null
        }
      )
      
      supabaseStorageService.deleteFile(url)
      
      applicationEventPublisher.publishEvent(
        ProfilePictureUpdatedEv(
          userId = userId,
          newUrl = null
        )
      )
    }
  }
  
  @Transactional
  fun confirmProfilePictureUpload(userId: UserId, url: String) {
    if(!url.startsWith(supabaseUrl)){
      throw InvalidProfilePictureEx("Invalid profile picture url")
    }
    
    val participant = chatParticipantRepository.findByIdOrNull(userId)
      ?: throw ChatParticipantNotFoundEx(userId)
    
    val oldUrl = participant.profilePictureUrl
    
    chatParticipantRepository.save(
      participant.apply {
        profilePictureUrl = url
      }
    )
    
    try { 
      oldUrl?.let {
        supabaseStorageService.deleteFile(oldUrl) 
      }
    } catch (e: Exception){
      logger.warn("Deleting old profile picture for user $userId failed", e)
    }
    
    applicationEventPublisher.publishEvent(
      ProfilePictureUpdatedEv(
        userId = userId,
        newUrl = url
      )
    )
  }
}