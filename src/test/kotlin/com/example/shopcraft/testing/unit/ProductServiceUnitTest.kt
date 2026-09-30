package com.example.shopcraft.testing.unit

import com.example.shopcraft.common.exception.DuplicateResourceException
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.repository.ProductRepository
import com.example.shopcraft.product.service.ProductService
import com.example.shopcraft.product.service.ProductServiceImpl
import com.example.shopcraft.testing.support.ProductTestDataFactory
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ProductServiceUnitTest {

    private val productRepository: ProductRepository = mock()
    private val productService: ProductService = ProductServiceImpl(productRepository)

    @Test
    fun `given existing product, when updateProduct with conflicting sku, then verifies repository save is never called`() {
        TODO("Step 1 - Create an existing product and a conflicting product with another ID in the repository mocks. Call updateProduct with the conflicting SKU, assert DuplicateResourceException is thrown, and verify productRepository.save is never executed.")
    }

    @Test
    fun `given valid product request, when createProduct, then captures argument and asserts exact fields saved`() {
        TODO("Step 2 - Mock repository behavior to simulate a new product creation. Call createProduct and use an argument captor to verify that the entity passed into productRepository.save contains the exact expected SKU, name, price, and stock quantity.")
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
