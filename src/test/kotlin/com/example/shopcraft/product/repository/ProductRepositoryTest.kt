package com.example.shopcraft.product.repository

import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Test
    fun `given new product, when save and findByIdOrNull, then entity is successfully persisted and retrieved`() {
        val product = Product(
            sku = "SKU-MON-0003",
            name = "4K Gaming Monitor",
            description = "144Hz IPS panel",
            price = BigDecimal("399.99"),
            stockQuantity = 25,
            status = ProductStatus.ACTIVE
        )

        val saved = productRepository.save(product)
        val retrieved = productRepository.findByIdOrNull(saved.id!!)

        assertThat(retrieved).isNotNull
        assertThat(retrieved?.sku).isEqualTo("SKU-MON-0003")
        assertThat(retrieved?.name).isEqualTo("4K Gaming Monitor")
        assertThat(retrieved?.price).isEqualByComparingTo("399.99")
    }

    @Test
    fun `given saved product, when findBySku, then returns matching product`() {
        val product = Product(
            sku = "SKU-HEAD-0004",
            name = "Noise Cancelling Headphones",
            price = BigDecimal("249.99"),
            stockQuantity = 15
        )
        productRepository.save(product)

        val found = productRepository.findBySku("SKU-HEAD-0004")

        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo("Noise Cancelling Headphones")
    }

    @Test
    fun `given existing sku, when existsBySku, then returns true`() {
        val product = Product(
            sku = "SKU-DESK-0005",
            name = "Standing Desk",
            price = BigDecimal("499.00"),
            stockQuantity = 8
        )
        productRepository.save(product)

        val exists = productRepository.existsBySku("SKU-DESK-0005")
        val notExists = productRepository.existsBySku("NON-EXISTENT-SKU")

        assertThat(exists).isTrue()
        assertThat(notExists).isFalse()
    }
}
