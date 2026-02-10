package com.rosafiesta.inventory.infra.db.entities

import com.rosafiesta.core.domain.types.ArticleId
import com.rosafiesta.core.domain.types.CategoryId
import com.rosafiesta.core.domain.types.DiscountId
import com.rosafiesta.inventory.domain.model.DiscountType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "discounts", schema = "inventory_service")
class DiscountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: DiscountId? = null,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: DiscountType,

    @Column(nullable = false, precision = 19, scale = 4)
    var value: BigDecimal,

    var startDate: Instant? = null,
    var endDate: Instant? = null,

    @Column(nullable = false)
    var isActive: Boolean = true,

    var targetCategoryId: CategoryId? = null,
    var targetArticleId: ArticleId? = null,
    var targetVariantId: UUID? = null,

    @Column(nullable = false)
    var priority: Int = 0,

    @CreationTimestamp
    var createdAt: Instant? = null,

    @UpdateTimestamp
    var updatedAt: Instant? = null
)