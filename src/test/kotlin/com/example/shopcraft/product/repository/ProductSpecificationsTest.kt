package com.example.shopcraft.product.repository

import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import java.math.BigDecimal

@DataJpaTest
class ProductSpecificationsTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        productRepository.deleteAll()

        productRepository.save(
            Product(
                sku = "SKU-KEY-001",
                name = "Mechanical Keyboard",
                description = "RGB tactile switches for gaming",
                price = BigDecimal("150.00"),
                stockQuantity = 20,
                status = ProductStatus.ACTIVE
            )
        )
        productRepository.save(
            Product(
                sku = "SKU-MOU-002",
                name = "Wireless Mouse",
                description = "Ergonomic productivity mouse",
                price = BigDecimal("60.00"),
                stockQuantity = 0,
                status = ProductStatus.ACTIVE
            )
        )
        productRepository.save(
            Product(
                sku = "SKU-PAD-003",
                name = "Desk Mat Pad",
                description = "Smooth mousepad surface",
                price = BigDecimal("25.00"),
                stockQuantity = 50,
                status = ProductStatus.DRAFT
            )
        )
    }

    @Test
    fun `given search query, when filtered, then matches name or description case insensitively`() {
        val criteria = ProductFilterCriteria(search = "gaming")
        val spec = ProductSpecifications.withFilter(criteria)

        val results = productRepository.findAll(spec)

        assertThat(results).hasSize(1)
        assertThat(results[0].sku).isEqualTo("SKU-KEY-001")
    }

    @Test
    fun `given price range, when filtered, then returns products within price boundaries`() {
        val criteria = ProductFilterCriteria(
            minPrice = BigDecimal("50.00"),
            maxPrice = BigDecimal("160.00")
        )
        val spec = ProductSpecifications.withFilter(criteria)

        val results = productRepository.findAll(spec)

        assertThat(results).hasSize(2)
        assertThat(results.map { it.sku }).containsExactlyInAnyOrder("SKU-KEY-001", "SKU-MOU-002")
    }

    @Test
    fun `given inStock true, when filtered, then returns only products with positive stock`() {
        val criteria = ProductFilterCriteria(inStock = true)
        val spec = ProductSpecifications.withFilter(criteria)

        val results = productRepository.findAll(spec)

        assertThat(results).hasSize(2)
        assertThat(results.map { it.sku }).containsExactlyInAnyOrder("SKU-KEY-001", "SKU-PAD-003")
    }

    @Test
    fun `given status filter, when filtered, then returns only products matching status`() {
        val criteria = ProductFilterCriteria(status = ProductStatus.DRAFT)
        val spec = ProductSpecifications.withFilter(criteria)

        val results = productRepository.findAll(spec)

        assertThat(results).hasSize(1)
        assertThat(results[0].sku).isEqualTo("SKU-PAD-003")
    }

    @Test
    fun `given combined criteria, when filtered, then matches intersection of all predicates`() {
        val criteria = ProductFilterCriteria(
            search = "mouse",
            status = ProductStatus.ACTIVE,
            inStock = false
        )
        val spec = ProductSpecifications.withFilter(criteria)

        val results = productRepository.findAll(spec)

        assertThat(results).hasSize(1)
        assertThat(results[0].sku).isEqualTo("SKU-MOU-002")
    }
}
