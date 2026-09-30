package com.example.shopcraft.auth

import com.example.shopcraft.auth.entity.Role
import com.example.shopcraft.auth.security.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class SecurityRbacIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    @WithAnonymousUser
    fun `given anonymous user, when GET products, then returns 200 OK`() {
        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk)
    }

    @Test
    @WithAnonymousUser
    fun `given anonymous user, when POST products, then returns 401 Unauthorized ProblemDetail`() {
        val payload = """
            {
                "sku": "SKU-ADM-0001",
                "name": "Anonymous Attempt",
                "price": 99.99,
                "stockQuantity": 10,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
            .andExpect(jsonPath("$.title").value("Unauthorized"))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `given user with ROLE_USER, when POST products, then returns 403 Forbidden ProblemDetail`() {
        val payload = """
            {
                "sku": "SKU-ADM-0002",
                "name": "Customer Attempt",
                "price": 99.99,
                "stockQuantity": 10,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.title").value("Access Denied"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `given user with ROLE_ADMIN, when POST products, then returns 201 Created`() {
        val payload = """
            {
                "sku": "SKU-ADM-0003",
                "name": "Admin Created Product",
                "price": 149.99,
                "stockQuantity": 20,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.sku").value("SKU-ADM-0003"))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `given user with ROLE_USER, when GET admin audit-logs, then returns 403 Forbidden`() {
        mockMvc.perform(get("/api/v1/admin/audit-logs"))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.title").value("Access Denied"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `given user with ROLE_ADMIN, when GET admin audit-logs, then returns 200 OK`() {
        mockMvc.perform(get("/api/v1/admin/audit-logs"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
    }

    @Test
    fun `given valid bearer token in Authorization header, when request protected endpoint, then authenticates successfully`() {
        val token = jwtTokenProvider.generateToken("admin@shopcraft.com", Role.ROLE_ADMIN)

        val payload = """
            {
                "sku": "SKU-ADM-0004",
                "name": "Bearer Token Product",
                "price": 199.99,
                "stockQuantity": 15,
                "status": "ACTIVE"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/api/v1/products")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.sku").value("SKU-ADM-0004"))
    }
}
