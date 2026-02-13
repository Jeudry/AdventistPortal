package com.rosafiesta.quotes.infra.db.entities

import com.rosafiesta.core.domain.types.QuoteId
import com.rosafiesta.core.domain.types.UserId
import com.rosafiesta.inventory.infra.db.entities.ArticleVariantEntity
import com.rosafiesta.shared.domain.quotes.enums.QuoteStatus
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(name = "quotes", schema = "quote_service")
class QuoteEntity(
    @Id
    var id: QuoteId? = null,

    @Column(nullable = false)
    var clientId: UserId = UUID.randomUUID(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: QuoteStatus = QuoteStatus.DRAFT,

    @Column(nullable = false)
    var eventStartDate: Instant = Instant.now(),

    @Column(nullable = false)
    var eventEndDate: Instant = Instant.now(),
    
    var venueAddress: String? = null,

    @OneToMany(mappedBy = "quote", cascade = [CascadeType.ALL], orphanRemoval = true)
    var items: MutableList<QuoteItemEntity> = mutableListOf(),

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: Instant? = null
)

@Entity
@Table(name = "quote_items", schema = "quote_service")
class QuoteItemEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    var quote: QuoteEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    var variant: ArticleVariantEntity? = null,

    @Column(nullable = false)
    var quantity: Int = 0,

    @Column(nullable = false, precision = 19, scale = 4)
    var unitPrice: BigDecimal = BigDecimal.ZERO,

    @CreationTimestamp
    var addedAt: Instant? = null
)