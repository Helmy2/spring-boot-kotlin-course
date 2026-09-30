package com.example.shopcraft.testing.slice.data

import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.repository.ProductRepository
import com.example.shopcraft.product.repository.ProductSpecifications
import com.example.shopcraft.testing.support.ProductTestDataFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import java.math.BigDecimal

@DataJpaTest
class ProductRepositoryDataJpaSliceTest {

    @Autowired
    private lateinit var testEntityManager: TestEntityManager

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun `given persisted product in test entity manager, when findBySku after clearing persistence context, then queries database directly`() {
        val product = ProductTestDataFactory.createProductEntity(
            id = null,
            sku = "SKU-JPA-0001",
            name = "Direct Database Query Product"
        )
        val persisted = testEntityManager.persistAndFlush(product)
        testEntityManager.clear()

        val found = productRepository.findBySku("SKU-JPA-0001")

        assertThat(found).isNotNull
        assertThat(found?.id).isEqualTo(persisted.id)
        assertThat(found?.name).isEqualTo("Direct Database Query Product")
    }

    @Test
    fun `given products in database, when queried with dynamic price specification, then returns matching entities`() {
        val productBudget = Product(
            sku = "SKU-FILTER-001",
            name = "Budget Earbuds",
            price = BigDecimal("29.99"),
            stockQuantity = 50,
            status = ProductStatus.ACTIVE
        )
        val productMidrange = Product(
            sku = "SKU-FILTER-002",
            name = "Midrange Headphones",
            price = BigDecimal("149.99"),
            stockQuantity = 20,
            status = ProductStatus.ACTIVE
        )
        val productPremium = Product(
            sku = "SKU-FILTER-003",
            name = "Audiophile Studio Monitor",
            price = BigDecimal("499.99"),
            stockQuantity = 5,
            status = ProductStatus.ARCHIVED
        )
        testEntityManager.persist(productBudget)
        testEntityManager.persist(productMidrange)
        testEntityManager.persist(productPremium)
        testEntityManager.flush()
        testEntityManager.clear()

        val criteria = ProductFilterCriteria(
            minPrice = BigDecimal("100.00"),
            maxPrice = BigDecimal("200.00"),
            status = ProductStatus.ACTIVE
        )
        val spec = ProductSpecifications.withFilter(criteria)
        val results = productRepository.findAll(spec)

        assertThat(results).hasSize(1)
        assertThat(results[0].sku).isEqualTo("SKU-FILTER-002")
        assertThat(results[0].name).isEqualTo("Midrange Headphones")
    }
}
