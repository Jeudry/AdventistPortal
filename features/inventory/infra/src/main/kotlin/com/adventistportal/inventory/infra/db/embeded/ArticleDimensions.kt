package com.adventistportal.inventory.infra.db.embeded

import jakarta.persistence.Embeddable

@Embeddable
data class ArticleDimensions(
    var label: String,
    var widthCm: Double? = null,
    var heightCm: Double? = null,
    var depthCm: Double? = null,
    var weightKg: Double? = null
) {
    fun getVolumeM3(): Double {
        if (widthCm == null || heightCm == null || depthCm == null) return 0.0
        return (widthCm!! / 100) * (heightCm!! / 100) * (depthCm!! / 100)
    }
}