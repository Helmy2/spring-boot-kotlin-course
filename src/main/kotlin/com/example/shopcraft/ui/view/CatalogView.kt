package com.example.shopcraft.ui.view

import com.example.shopcraft.product.dto.ProductResponse
import kotlinx.html.*

object CatalogView {

    fun renderCatalogPage(products: List<ProductResponse>): HTML.() -> Unit =
        Layout.render(title = "Catalog Storefront", activeNav = "storefront") {
            // Hero Section
            section(classes = "relative pt-12 sm:pt-16 pb-8") {
                div(classes = "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center space-y-4") {
                    div(classes = "inline-flex items-center gap-2 px-3.5 py-1 rounded-[6px] bg-white border border-[#3A241C]/20 text-xs font-mono text-[#C56A3C]") {
                        span(classes = "w-2 h-2 rounded-full bg-[#C56A3C]")
                        +"Autumn 2026 Collection"
                    }
                    h1(classes = "text-4xl sm:text-5xl lg:text-6xl font-serif font-bold tracking-tight text-[#3A241C] max-w-3xl mx-auto leading-tight") {
                        +"Modern Craftsmanship, "
                        span(classes = UiStyles.Typography.GRADIENT_TEXT) {
                            +"Direct to You"
                        }
                    }
                    p(classes = "text-lg sm:text-xl font-serif text-[#6F5A4E] max-w-2xl mx-auto leading-relaxed") {
                        +"Handcrafted goods, studio essentials, and timeless objects curated for intentional living and tested with Spring Boot."
                    }
                }
            }

            // Controls & Filter Bar
            section(classes = "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-4 pb-2") {
                div(classes = "bg-white border border-[#3A241C]/15 rounded-[16px] p-4 flex flex-col md:flex-row items-center justify-between gap-4 shadow-sm") {
                    // Search Bar with HTMX debouncing
                    div(classes = "relative w-full md:w-96") {
                        div(classes = "absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-[#6F5A4E]") {
                            unsafe {
                                raw("""<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>""")
                            }
                        }
                        input(type = InputType.text, classes = UiStyles.Forms.SEARCH_INPUT) {
                            id = "catalog-search"
                            name = "search"
                            placeholder = "Search by name or SKU..."
                            attributes["autocomplete"] = "off"
                            hxGet("/ui/products")
                            hxTrigger("keyup changed delay:300ms, search")
                            hxTarget("#product-grid")
                            hxIndicator("#search-indicator")
                            hxInclude("#status-filter")
                        }
                        div(classes = "absolute inset-y-0 right-0 pr-3.5 flex items-center") {
                            div(classes = "htmx-indicator") {
                                id = "search-indicator"
                                unsafe {
                                    raw("""<svg class="animate-spin w-4 h-4 text-[#C56A3C]" viewBox="0 0 24 24" fill="none"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>""")
                                }
                            }
                        }
                    }

                    // Status Filter & Quick Controls
                    div(classes = "flex items-center gap-3 w-full md:w-auto justify-end") {
                        label(classes = "text-sm font-serif font-medium text-[#3A241C]") { +"Status:" }
                        select(classes = UiStyles.Forms.SELECT_INLINE) {
                            id = "status-filter"
                            name = "status"
                            hxGet("/ui/products")
                            hxTrigger("change")
                            hxTarget("#product-grid")
                            hxIndicator("#search-indicator")
                            hxInclude("#catalog-search")

                            option { value = ""; +"All Products" }
                            option { value = "ACTIVE"; +"Active Only" }
                            option { value = "DRAFT"; +"Drafts" }
                            option { value = "ARCHIVED"; +"Archived" }
                        }

                        button(classes = "min-h-[44px] px-3.5 py-2 rounded-[10px] bg-white border border-[#3A241C]/20 hover:bg-[#F8F0E4] text-xs font-mono text-[#3A241C] transition-colors cursor-pointer flex items-center gap-1.5") {
                            attributes["onclick"] = "document.getElementById('catalog-search').value=''; document.getElementById('status-filter').value=''; htmx.trigger('#catalog-search', 'keyup');"
                            unsafe {
                                raw("""<svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/></svg>""")
                            }
                            +"Reset"
                        }
                    }
                }
            }

            // Products Section
            section(classes = "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10") {
                div(classes = "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6") {
                    id = "product-grid"
                    renderCards(this, products)
                }
            }
        }

    fun renderProductGridFragment(products: List<ProductResponse>): TagConsumer<StringBuilder>.() -> Unit = {
        if (products.isEmpty()) {
            emptyState(
                title = "No products found",
                subtitle = "Try adjusting your search query or switching filters to see available inventory."
            )
        } else {
            for (product in products) {
                div(classes = UiStyles.Cards.PRODUCT) {
                    renderCardDetails(product)
                }
            }
        }
    }

    private fun renderCards(container: FlowContent, products: List<ProductResponse>) {
        if (products.isEmpty()) {
            container.emptyState(
                title = "No products found",
                subtitle = "Try adjusting your search query or switching filters to see available inventory."
            )
        } else {
            for (product in products) {
                container.div(classes = UiStyles.Cards.PRODUCT) {
                    renderCardDetails(product)
                }
            }
        }
    }

    private fun FlowContent.renderCardDetails(product: ProductResponse) {
        div {
            // Card Header: SKU & Status
            div(classes = "flex items-center justify-between gap-2 mb-3.5") {
                skuBadge(product.sku)
                statusBadge(product.status)
            }

            // Visual Decorative Canvas
            div(classes = "aspect-[4/3] rounded-[10px] bg-[#F8F0E4] border border-[#3A241C]/15 flex items-center justify-center p-4 relative overflow-hidden group-hover:bg-[#F3E9D8] transition-colors mb-4") {
                unsafe {
                    raw("""
                    <svg class="w-16 h-16 text-[#C56A3C]/50 transform group-hover:scale-105 transition-transform duration-300" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
                    </svg>
                    """.trimIndent())
                }
            }

            // Product Information
            h3(classes = "text-xl font-serif font-bold text-[#3A241C] group-hover:text-[#C56A3C] transition-colors mb-1") {
                +product.name
            }
            p(classes = "text-sm font-serif text-[#6F5A4E] line-clamp-2 leading-relaxed mb-4") {
                +(product.description ?: "Handcrafted artisanal catalog piece designed for precision performance.")
            }
        }

        // Card Footer: Price & Add to Cart
        div(classes = "mt-4 pt-3.5 border-t border-[#3A241C]/15 space-y-3") {
            div(classes = "flex items-baseline justify-between") {
                div {
                    span(classes = "text-xs font-mono text-[#6F5A4E]") { +"Price" }
                    p(classes = "text-2xl font-mono font-bold text-[#3A241C]") {
                        +"$${product.price}"
                    }
                }
                div(classes = "text-right") {
                    span(classes = "text-xs font-mono text-[#6F5A4E]") { +"Stock" }
                    if (product.stockQuantity > 0) {
                        p(classes = "text-xs font-mono text-[#16A34A] font-semibold") {
                            +"${product.stockQuantity} in stock"
                        }
                    } else {
                        p(classes = "text-xs font-mono text-[#DC2626] font-semibold") {
                            +"0 in stock"
                        }
                    }
                }
            }

            actionButton(
                text = "Add to Cart",
                onClick = "showToast('Added ${product.name} to cart!', 'success')"
            ) {
                unsafe {
                    raw("""<svg class="w-4 h-4 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/></svg>""")
                }
            }
        }
    }
}
