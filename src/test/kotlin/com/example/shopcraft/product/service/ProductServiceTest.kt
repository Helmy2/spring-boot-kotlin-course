package com.example.shopcraft.product.service

import com.example.shopcraft.common.exception.DuplicateResourceException
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.dto.ProductPatchRequest
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
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
    fun `given existing sku, when createProduct, then throws DuplicateResourceException`() {
        val request = ProductRequest(
            sku = "SKU-DUPLICATE",
            name = "Duplicate Item",
            description = "Some description",
            price = BigDecimal("19.99"),
            stockQuantity = 10,
            status = ProductStatus.ACTIVE
        )
        whenever(productRepository.existsBySku("SKU-DUPLICATE")).thenReturn(true)

        assertThatThrownBy { productService.createProduct(request) }
            .isInstanceOf(DuplicateResourceException::class.java)
            .hasMessage("Product with SKU 'SKU-DUPLICATE' already exists")

        verify(productRepository).existsBySku("SKU-DUPLICATE")
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
        val p1 = Product(id = 1L, sku = "SKU-1", name = "P1", price = BigDecimal("10.00"), stockQuantity = 5)
        val p2 = Product(id = 2L, sku = "SKU-2", name = "P2", price = BigDecimal("20.00"), stockQuantity = 15)
        whenever(productRepository.findAll()).thenReturn(listOf(p1, p2))

        val responses = productService.getAllProducts()

        assertThat(responses).hasSize(2)
        assertThat(responses[0].sku).isEqualTo("SKU-1")
        assertThat(responses[1].sku).isEqualTo("SKU-2")
        verify(productRepository).findAll()
    }

    @Test
    fun `given existing product, when updateProduct, then replaces all fields and returns updated response`() {
        val existing = Product(
            id = 5L,
            sku = "SKU-OLD",
            name = "Old Name",
            description = "Old Desc",
            price = BigDecimal("15.00"),
            stockQuantity = 2
        )
        val request = ProductRequest(
            sku = "SKU-NEW",
            name = "New Name",
            description = "New Desc",
            price = BigDecimal("25.00"),
            stockQuantity = 12,
            status = ProductStatus.ARCHIVED
        )
        whenever(productRepository.findById(5L)).thenReturn(Optional.of(existing))
        whenever(productRepository.save(any<Product>())).thenAnswer { it.arguments[0] as Product }

        val response = productService.updateProduct(5L, request)

        assertThat(response.sku).isEqualTo("SKU-NEW")
        assertThat(response.name).isEqualTo("New Name")
        assertThat(response.price).isEqualByComparingTo("25.00")
        assertThat(response.stockQuantity).isEqualTo(12)
        assertThat(response.status).isEqualTo(ProductStatus.ARCHIVED)
        verify(productRepository).save(existing)
    }

    @Test
    fun `given conflicting sku with another product, when updateProduct, then throws DuplicateResourceException`() {
        val request = ProductRequest(
            sku = "SKU-COLLISION",
            name = "Updated Item",
            description = "Some description",
            price = BigDecimal("29.99"),
            stockQuantity = 20,
            status = ProductStatus.ACTIVE
        )
        val existingProduct = Product(
            id = 1L,
            sku = "SKU-ORIGINAL",
            name = "Original Item",
            description = null,
            price = BigDecimal("29.99"),
            stockQuantity = 20,
            status = ProductStatus.ACTIVE
        )
        val otherProductWithSku = Product(
            id = 2L,
            sku = "SKU-COLLISION",
            name = "Other Item",
            description = null,
            price = BigDecimal("39.99"),
            stockQuantity = 15,
            status = ProductStatus.ACTIVE
        )
        whenever(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct))
        whenever(productRepository.findBySku("SKU-COLLISION")).thenReturn(otherProductWithSku)

        assertThatThrownBy { productService.updateProduct(1L, request) }
            .isInstanceOf(DuplicateResourceException::class.java)
            .hasMessage("Product with SKU 'SKU-COLLISION' already exists")

        verify(productRepository).findById(1L)
        verify(productRepository).findBySku("SKU-COLLISION")
    }

    @Test
    fun `given existing product, when patchProduct, then modifies only non-null fields`() {
        val existing = Product(
            id = 7L,
            sku = "SKU-UNCHANGED",
            name = "Original Name",
            description = "Original Desc",
            price = BigDecimal("50.00"),
            stockQuantity = 10,
            status = ProductStatus.ACTIVE
        )
        val patchRequest = ProductPatchRequest(price = BigDecimal("59.99"))
        whenever(productRepository.findById(7L)).thenReturn(Optional.of(existing))
        whenever(productRepository.save(any<Product>())).thenAnswer { it.arguments[0] as Product }

        val response = productService.patchProduct(7L, patchRequest)

        assertThat(response.sku).isEqualTo("SKU-UNCHANGED")
        assertThat(response.name).isEqualTo("Original Name")
        assertThat(response.price).isEqualByComparingTo("59.99")
        assertThat(response.stockQuantity).isEqualTo(10)
        verify(productRepository).save(existing)
    }

    @Test
    fun `given existing product, when deleteProduct, then deletes from repository`() {
        val existing = Product(
            id = 8L,
            sku = "SKU-DEL",
            name = "To Delete",
            price = BigDecimal("10.00"),
            stockQuantity = 1
        )
        whenever(productRepository.findById(8L)).thenReturn(Optional.of(existing))

        productService.deleteProduct(8L)

        verify(productRepository).delete(existing)
    }

    @Test
    fun `given non-existing product, when deleteProduct, then throws ProductNotFoundException`() {
        whenever(productRepository.findById(999L)).thenReturn(Optional.empty())

        assertThatThrownBy { productService.deleteProduct(999L) }
            .isInstanceOf(ProductNotFoundException::class.java)

        verify(productRepository).findById(999L)
    }

    @Test
    fun `given filter criteria and pageable, when getProducts, then returns paged response`() {
        val criteria = ProductFilterCriteria(search = "pro")
        val pageable = PageRequest.of(0, 5)
        val product = Product(
            id = 1L,
            sku = "SKU-PRO",
            name = "Pro Device",
            price = BigDecimal("200.00"),
            stockQuantity = 10
        )
        val page = PageImpl(listOf(product), pageable, 1L)
        whenever(productRepository.findAll(any<Specification<Product>>(), any<PageRequest>())).thenReturn(page)

        val response = productService.getProducts(criteria, pageable)

        assertThat(response.content).hasSize(1)
        assertThat(response.content[0].sku).isEqualTo("SKU-PRO")
        assertThat(response.totalElements).isEqualTo(1L)
        assertThat(response.totalPages).isEqualTo(1)
    }
}
