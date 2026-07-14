package com.adventistportal.notification.infrastructure.database

import com.adventistportal.core.domain.types.UserId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceTokenRepository: JpaRepository<DeviceTokenEntity, Long> {
  fun findByUserIdIn(userIds: List<UserId>): List<DeviceTokenEntity>
  fun findByToken(token: String): DeviceTokenEntity?
  fun deleteByToken(token: String)
}