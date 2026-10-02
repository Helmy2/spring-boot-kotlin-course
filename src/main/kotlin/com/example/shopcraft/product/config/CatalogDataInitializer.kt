package com.example.shopcraft.product.config

import com.example.shopcraft.product.entity.Product
import com.example.shopcraft.product.entity.ProductStatus
import com.example.shopcraft.product.repository.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CatalogDataInitializer(
    private val productRepository: ProductRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        if (productRepository.count() == 0L) {
            productRepository.saveAll(
                listOf(
                    Product(
                        sku = "SKU-KEY-1001",
                        name = "Pro Tactile Mechanical Keyboard",
                        description = "Hot-swappable mechanical keyboard with custom tuned switches, sound dampening foam, and per-key RGB backlighting.",
                        price = BigDecimal("149.99"),
                        stockQuantity = 45,
                        status = ProductStatus.ACTIVE
                    ),
                    Product(
                        sku = "SKU-MOU-2002",
                        name = "Ultralight Wireless Gaming Mouse",
                        description = "Ergonomic 58g ultralight wireless mouse featuring optical switches and a 26K DPI sensor with zero latency.",
                        price = BigDecimal("89.99"),
                        stockQuantity = 80,
                        status = ProductStatus.ACTIVE
                    ),
                    Product(
                        sku = "SKU-AUD-3003",
                        name = "Studio Hi-Fi Wireless Headphones",
                        description = "Over-ear planar magnetic headphones with active noise cancellation, LDAC high-res audio codec, and 40-hour battery.",
                        price = BigDecimal("299.99"),
                        stockQuantity = 20,
                        status = ProductStatus.ACTIVE
                    ),
                    Product(
                        sku = "SKU-MON-4004",
                        name = "34-inch Curved UltraWide Monitor",
                        description = "WQHD 165Hz Nano IPS gaming and productivity monitor with HDR400, USB-C 90W power delivery, and KVM switch.",
                        price = BigDecimal("699.99"),
                        stockQuantity = 12,
                        status = ProductStatus.ACTIVE
                    ),
                    Product(
                        sku = "SKU-DES-5005",
                        name = "Motorized Standing Desk Frame",
                        description = "Dual-motor height adjustable sit-stand desk frame with 4 programmable memory presets and anti-collision sensor.",
                        price = BigDecimal("349.99"),
                        stockQuantity = 3,
                        status = ProductStatus.ACTIVE
                    ),
                    Product(
                        sku = "SKU-ACC-6006",
                        name = "Desk Mat Wool Felt & Vegan Leather",
                        description = "Minimalist dual-sided desk mat crafted from water-resistant merino wool felt and vegan walnut leather.",
                        price = BigDecimal("39.99"),
                        stockQuantity = 120,
                        status = ProductStatus.ACTIVE
                    ),
                    Product(
                        sku = "SKU-DRF-7007",
                        name = "Ergonomic Memory Foam Wrist Rest",
                        description = "Cooling gel memory foam wrist rest designed for mechanical keyboard ergonomics and wrist support.",
                        price = BigDecimal("24.99"),
                        stockQuantity = 50,
                        status = ProductStatus.DRAFT
                    )
                )
            )
        }
    }
}
