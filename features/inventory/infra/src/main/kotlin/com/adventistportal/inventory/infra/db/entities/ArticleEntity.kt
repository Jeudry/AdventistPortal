package com.adventistportal.inventory.infra.db.entities

import com.adventistportal.core.domain.types.ArticleId
import com.adventistportal.inventory.infra.db.embeded.ArticleDimensions
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "articles", schema = "inventory_service")
class ArticleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: ArticleId? = null,

    @Column(nullable = false)
    var nameTemplate: String,

    @Column(columnDefinition = "TEXT")
    var descriptionTemplate: String? = null,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: CategoryEntity? = null,

    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL], orphanRemoval = true)
    var variants: MutableList<ArticleVariantEntity> = mutableListOf(),

    @CreationTimestamp
    var createdAt: Instant? = null,

    @UpdateTimestamp
    var updatedAt: Instant? = null,

    // Soft-delete marker: set explicitly when the article is deleted, not on every update.
    var deletedAt: Instant? = null
)

@Entity
@Table(name = "article_variants", schema = "inventory_service")
class ArticleVariantEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    var sku: String,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    var imageUrl: String? = null,

    @Column(nullable = false)
    var isActive: Boolean = true,

    @Column(nullable = false)
    var stock: Int = 0,

    /** Replacement value in cents, for insurance and asset records. */
    @Column(nullable = false)
    var replacementCostCents: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    var article: ArticleEntity? = null,

    @ElementCollection
    @CollectionTable(name = "article_variant_attributes", schema = "inventory_service", joinColumns = [JoinColumn(name = "variant_id")])
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    var attributes: MutableMap<String, String> = mutableMapOf(),

    @ElementCollection
    @CollectionTable(name = "article_variant_dimensions", schema = "inventory_service", joinColumns = [JoinColumn(name = "variant_id")])
    var dimensions: MutableList<ArticleDimensions> = mutableListOf()
)