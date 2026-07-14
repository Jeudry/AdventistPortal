package com.adventistportal.user.infrastructure.database.repositories

import com.adventistportal.core.domain.types.UserId
import com.adventistportal.user.infrastructure.database.entities.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository: JpaRepository<RefreshTokenEntity, String>{
    fun findByUserIdAndHashedToken(userId: UserId, hashedToken: String): RefreshTokenEntity?
    fun deleteByUserIdAndHashedToken(userId: UserId, hashedToken: String)
    fun deleteByUserId(userId: UserId)
}