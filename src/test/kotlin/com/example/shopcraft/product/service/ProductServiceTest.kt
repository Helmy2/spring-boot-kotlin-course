package com.example.shopcraft.product.service

import com.example.shopcraft.product.dto.ProductRequest
import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.repository.ProductRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.Optional

class ProductServiceTest {

    private val productRepository: ProductRepository = mock()
    private val productService: ProductService = ProductServiceImpl(productRepository)

    @Test
    fun `given valid product request, when createProduct, then persists entity and returns response`() {
        val request = ProductRequest(
            sku = "SKU-KEY-0001",
            name = "Mechanical Keyboard",
            description = "RGB tactile switches",
            price = BigDecimal("129.99"),
            stockQuantity = 50,
            status = ProductStatus.ACTIVE
        )
        val savedProduct = Product(
            id = 1L,
            sku = request.sku,
            name = request.name,
            description = request.description,
            price = request.price,
            stockQuantity = request.stockQuantity,
            status = request.status
        )
        whenever(productRepository.save(any<Product>())).thenReturn(savedProduct)

        val response = productService.createProduct(request)

        assertThat(response.id).isEqualTo(1L)
        assertThat(response.sku).isEqualTo("SKU-KEY-0001")
        assertThat(response.name).isEqualTo("Mechanical Keyboard")
        assertThat(response.price).isEqualByComparingTo("129.99")
        assertThat(response.stockQuantity).isEqualTo(50)
        assertThat(response.status).isEqualTo(ProductStatus.ACTIVE)
        verify(productRepository).save(any<Product>())
    }

    @Test
    fun `given existing product id, when getProductById, then returns matching response`() {
        val existingProduct = Product(
            id = 42L,
            sku = "SKU-MOU-0002",
            name = "Ergonomic Mouse",
            description = "Wireless precision mouse",
            price = BigDecimal("79.99"),
            stockQuantity = 100,
            status = ProductStatus.ACTIVE
        )
        whenever(productRepository.findById(42L)).thenReturn(Optional.of(existingProduct))

        val response = productService.getProductById(42L)

        assertThat(response.id).isEqualTo(42L)
        assertThat(response.sku).isEqualTo("SKU-MOU-0002")
        assertThat(response.name).isEqualTo("Ergonomic Mouse")
        assertThat(response.price).isEqualByComparingTo("79.99")
        verify(productRepository).findById(42L)
    }

    @Test
    fun `given non-existing product id, when getProductById, then throws ProductNotFoundException`() {
        whenever(productRepository.findById(999L)).thenReturn(Optional.empty())

        assertThatThrownBy { productService.getProductById(999L) }
            .isInstanceOf(ProductNotFoundException::class.java)
            .hasMessage("Product with id '999' was not found")

        verify(productRepository).findById(999L)
    }

    @Test
    fun `given products exist in repository, when getAllProducts, then returns mapped list of responses`() {
        val product1 = Product(
            id = 1L,
            sku = "SKU-001",
            name = "Product 1",
            price = BigDecimal("10.00"),
            stockQuantity = 5
        )
        val product2 = Product(
            id = 2L,
            sku = "SKU-002",
            name = "Product 2",
            price = BigDecimal("20.00"),
            stockQuantity = 15
        )
        whenever(productRepository.findAll()).thenReturn(listOf(product1, product2))

        val responses = productService.getAllProducts()

        assertThat(responses).hasSize(2)
        assertThat(responses[0].id).isEqualTo(1L)
        assertThat(responses[0].sku).isEqualTo("SKU-001")
        assertThat(responses[1].id).isEqualTo(2L)
        assertThat(responses[1].sku).isEqualTo("SKU-002")
        verify(productRepository).findAll()
    }
}
