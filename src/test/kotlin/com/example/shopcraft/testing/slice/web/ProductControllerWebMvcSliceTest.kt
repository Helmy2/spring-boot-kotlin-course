package com.example.shopcraft.testing.slice.web

import com.example.shopcraft.common.config.SecurityConfig
import com.example.shopcraft.common.exception.GlobalExceptionHandler
import com.example.shopcraft.product.controller.ProductController
import com.example.shopcraft.product.exception.ProductNotFoundException
import com.example.shopcraft.product.service.ProductService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ProductController::class)
@Import(SecurityConfig::class, GlobalExceptionHandler::class)
class ProductControllerWebMvcSliceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var productService: ProductService

    @Test
    fun `given invalid product payload with blank name, when POST api v1 products, then returns 400 Bad Request with field error`() {
        TODO("Step 3 - Perform a POST request to /api/v1/products with an invalid JSON body containing a blank name. Assert HTTP 400 Bad Request status and verify the JSON response contains validation field errors for the name attribute.")
    }

    @Test
    fun `given valid product request, when POST api v1 products, then returns 201 Created with Location header`() {
        TODO("Step 4 - Mock productService to return a valid created product response. Perform a POST request to /api/v1/products and assert HTTP 201 Created, the presence of the Location header, and the matching product ID and SKU in the JSON response.")
    }

    @Test
    fun `given non-existing product id, when GET api v1 products by id, then returns 404 Not Found with RFC 7807 problem detail`() {
        whenever(productService.getProductById(999L)).thenThrow(ProductNotFoundException(999L))

        mockMvc.perform(get("/api/v1/products/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.detail").value("Product with id '999' was not found"))
    }
}
