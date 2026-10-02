package com.example.shopcraft.ui.controller

import com.example.shopcraft.audit.service.AuditService
import com.example.shopcraft.product.dto.ProductFilterCriteria
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.service.ProductService
import com.example.shopcraft.ui.view.AdminView
import com.example.shopcraft.ui.view.CatalogView
import com.example.shopcraft.ui.view.HtmlResponseHelper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        val products = productService.getAllProducts()
        return HtmlResponseHelper.page(CatalogView.renderCatalogPage(products))
    }

    @GetMapping("/ui/products")
    fun productGrid(
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) status: ProductStatus?,
        @RequestParam(required = false) minPrice: BigDecimal?,
        @RequestParam(required = false) maxPrice: BigDecimal?,
        @RequestParam(required = false) inStock: Boolean?
    ): ResponseEntity<String> {
        val criteria = ProductFilterCriteria(
            search = search?.ifBlank { null },
            status = status,
            minPrice = minPrice,
            maxPrice = maxPrice,
            inStock = inStock
        )
        val pagedProducts = productService.getProducts(criteria, PageRequest.of(0, 100, Sort.by("id").descending()))
        return HtmlResponseHelper.fragment(CatalogView.renderProductGridFragment(pagedProducts.content))
    }

    @GetMapping("/ui/admin")
    fun adminDashboard(): ResponseEntity<String> {
        val products = productService.getAllProducts()
        val auditEvents = auditService.getAuditEvents(PageRequest.of(0, 50, Sort.by("timestamp").descending()))
        return HtmlResponseHelper.page(AdminView.renderAdminPage(products, auditEvents.content))
    }

    @GetMapping("/ui/audit")
    fun auditFeed(): ResponseEntity<String> {
        val auditEvents = auditService.getAuditEvents(PageRequest.of(0, 50, Sort.by("timestamp").descending()))
        return HtmlResponseHelper.fragment(AdminView.renderAuditFeedFragment(auditEvents.content))
    }
}
