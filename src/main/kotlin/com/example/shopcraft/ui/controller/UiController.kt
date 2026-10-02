package com.example.shopcraft.ui.controller

import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.math.BigDecimal

@Controller
class UiController(
    private val productService: ProductService,
    private val auditService: AuditService
) {

    @GetMapping("/")
    fun storefront(): ResponseEntity<String> {
        // TODO: Step 4 - Query all products from productService and return HtmlResponseHelper.page with CatalogView.renderCatalogPage
        TODO("Step 4: Implement storefront endpoint returning full catalog HTML page")
    }

    @GetMapping("/ui/products")
    fun productGrid(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) status: ProductStatus?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) inStock: Boolean?
    ): ResponseEntity<String> {
        // TODO: Step 4 - Query filtered products via productService.getProducts and return HtmlResponseHelper.fragment with CatalogView.renderProductGridFragment
        TODO("Step 4: Implement productGrid HTMX partial endpoint for live search and filtering")
    }

    @GetMapping("/ui/admin")
    fun adminDashboard(): ResponseEntity<String> {
        // TODO: Step 4 - Query all products and recent audit events, returning HtmlResponseHelper.page with AdminView.renderAdminPage
        TODO("Step 4: Implement adminDashboard endpoint returning full operations center HTML page")
    }

    @GetMapping("/ui/audit")
    fun auditFeed(): ResponseEntity<String> {
        // TODO: Step 4 - Query latest audit events and return HtmlResponseHelper.fragment with AdminView.renderAuditFeedFragment
        TODO("Step 4: Implement auditFeed HTMX partial endpoint for real-time telemetry updates")
    }
}
