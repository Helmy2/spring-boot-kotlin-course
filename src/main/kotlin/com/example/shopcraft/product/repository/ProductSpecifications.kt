package com.example.shopcraft.product.repository

import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification

object ProductSpecifications {

    fun withFilter(criteria: ProductFilterCriteria): Specification<Product> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            criteria.search?.takeIf { it.isNotBlank() }?.let { query ->
                val searchPattern = "%${query.trim().lowercase()}%"
                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get("name")), searchPattern),
                        cb.like(cb.lower(root.get("description")), searchPattern)
                    )
                )
            }

            criteria.minPrice?.let { min ->
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min))
            }

            criteria.maxPrice?.let { max ->
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), max))
            }

            criteria.status?.let { status ->
                predicates.add(cb.equal(root.get<ProductStatus>("status"), status))
            }

            criteria.inStock?.let { inStock ->
                if (inStock) {
                    predicates.add(cb.greaterThan(root.get("stockQuantity"), 0))
                } else {
                    predicates.add(cb.equal(root.get<Int>("stockQuantity"), 0))
                }
            }

            if (predicates.isEmpty()) cb.conjunction() else cb.and(*predicates.toTypedArray())
        }
    }
}
