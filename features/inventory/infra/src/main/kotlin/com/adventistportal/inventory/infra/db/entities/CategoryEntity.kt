package com.adventistportal.inventory.infra.db.entities

import com.adventistportal.core.domain.types.CategoryId
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "categories", schema = "inventory_service")
class CategoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: CategoryId? = null,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "icon_name")
    var iconName: String? = null,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: CategoryEntity? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableList<CategoryEntity> = mutableListOf()
)