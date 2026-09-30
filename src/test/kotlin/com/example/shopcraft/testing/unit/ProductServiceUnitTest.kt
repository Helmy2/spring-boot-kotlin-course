package com.example.shopcraft.testing.unit

import com.example.shopcraft.common.exception.DuplicateResourceException
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.repository.ProductRepository
import com.example.shopcraft.product.service.ProductService
import com.example.shopcraft.product.service.ProductServiceImpl
import com.example.shopcraft.testing.support.ProductTestDataFactory
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.Optional

class ProductServiceUnitTest {

    private val productRepository: ProductRepository = mock()
    private val productService: ProductService = ProductServiceImpl(productRepository)

    @Test
    fun `given valid product request, when createProduct, then captures argument and asserts exact fields saved`() {
        val request = ProductTestDataFactory.createProductRequest(
            sku = "SKU-CAPT-0001",
            name = "Ergonomic Mechanical Keyboard",
            description = "Custom hot-swappable switches",
            price = BigDecimal("159.99"),
            stockQuantity = 30
        )
        whenever(productRepository.existsBySku("SKU-CAPT-0001")).thenReturn(false)
        whenever(productRepository.save(any<Product>())).thenAnswer { invocation ->
            val entity = invocation.arguments[0] as Product
            Product(
                id = 100L,
                sku = entity.sku,
                name = entity.name,
                description = entity.description,
                price = entity.price,
                stockQuantity = entity.stockQuantity,
                status = entity.status
            )
        }

        val response = productService.createProduct(request)

        val captor = argumentCaptor<Product>()
        verify(productRepository).save(captor.capture())
        val captured = captor.firstValue
        assertThat(captured.sku).isEqualTo("SKU-CAPT-0001")
        assertThat(captured.name).isEqualTo("Ergonomic Mechanical Keyboard")
        assertThat(captured.description).isEqualTo("Custom hot-swappable switches")
        assertThat(captured.price).isEqualByComparingTo("159.99")
        assertThat(captured.stockQuantity).isEqualTo(30)
        assertThat(response.id).isEqualTo(100L)
        assertThat(response.sku).isEqualTo("SKU-CAPT-0001")
    }

    @Test
    fun `given existing product, when updateProduct with conflicting sku, then verifies repository save is never called`() {
        val existing = ProductTestDataFactory.createProductEntity(id = 1L, sku = "SKU-ORIGINAL")
        val conflicting = ProductTestDataFactory.createProductEntity(id = 2L, sku = "SKU-CONFLICT")
        val updateRequest = ProductTestDataFactory.createProductRequest(sku = "SKU-CONFLICT")
        whenever(productRepository.findById(1L)).thenReturn(Optional.of(existing))
        whenever(productRepository.findBySku("SKU-CONFLICT")).thenReturn(conflicting)

        assertThatThrownBy {
            productService.updateProduct(1L, updateRequest)
        }.isInstanceOf(DuplicateResourceException::class.java)
            .hasMessageContaining("SKU 'SKU-CONFLICT' already exists")

        verify(productRepository, never()).save(any())
    }

    @Test
    fun `given product request with duplicate sku, when createProduct, then throws DuplicateResourceException and never saves`() {
        val request = ProductTestDataFactory.createProductRequest(sku = "SKU-DUP-0001")
        whenever(productRepository.existsBySku("SKU-DUP-0001")).thenReturn(true)

        assertThatThrownBy {
            productService.createProduct(request)
        }.isInstanceOf(DuplicateResourceException::class.java)
            .hasMessageContaining("SKU 'SKU-DUP-0001' already exists")

        verify(productRepository, never()).save(any())
    }
}
