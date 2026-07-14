package com.adventistportal.core.domain.events

import java.time.Instant

interface AdventistPortalEvent {
    val eventId: String
    val eventKey: String
    val occurredAt: Instant
    val exchange: String
}