package com.adventistportal.notification.infrastructure.database

import com.adventistportal.core.domain.types.UserId
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(
  name = "device_tokens",
  schema = "notification_service",
  indexes = [
    Index(name = "idx_device_tokens_user_id", columnList = "userId"),
    Index(name = "idx_device_tokens_token", columnList = "token", unique = true)
  ]
) 
class DeviceTokenEntity (
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  var id: Long = 0,
  @Column(nullable = false)
  var userId: UserId,
  @Column(nullable = false)
  var token: String,
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  var platform: PlatformEntity,
  @CreationTimestamp
  var createdAt: Instant = Instant.now(),
) {
    constructor() : this(0, UUID.randomUUID(), "", PlatformEntity.ANDROID, Instant.now())
}