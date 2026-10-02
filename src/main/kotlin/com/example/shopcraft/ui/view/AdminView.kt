package com.example.shopcraft.ui.view

import com.example.shopcraft.audit.dto.AuditEventResponse
import com.example.shopcraft.product.dto.ProductResponse
import kotlinx.html.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AdminView {

    private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    fun renderAdminPage(
        products: List<ProductResponse>,
        auditEvents: List<AuditEventResponse>
    ): HTML.() -> Unit =
        Layout.render(title = "Admin Operations", activeNav = "admin") {
            div(classes = "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-10") {
                // Header & Metrics Bar
                renderHeaderAndMetrics(products, auditEvents)

                // Add Product Drawer / Panel
                renderNewProductPanel()

                // Dual Column Layout: Inventory Table + Audit Telemetry
                div(classes = "grid grid-cols-1 lg:grid-cols-3 gap-8") {
                    // Left: Products Inventory Table
                    div(classes = "lg:col-span-2 space-y-4") {
                        sectionHeader(
                            title = "Inventory Management",
                            dotColor = "bg-[#C56A3C]",
                            badgeText = "${products.size} records"
                        )

                        div(classes = "${UiStyles.Cards.BASE} overflow-hidden") {
                            div(classes = "overflow-x-auto") {
                                table(classes = UiStyles.Tables.TABLE) {
                                    thead(classes = UiStyles.Tables.THEAD) {
                                        tr {
                                            th(classes = UiStyles.Tables.TH) { +"SKU" }
                                            th(classes = UiStyles.Tables.TH) { +"Name" }
                                            th(classes = UiStyles.Tables.TH) { +"Price" }
                                            th(classes = UiStyles.Tables.TH) { +"Stock" }
                                            th(classes = UiStyles.Tables.TH) { +"Status" }
                                            th(classes = UiStyles.Tables.TD_RIGHT) { +"Actions" }
                                        }
                                    }
                                    tbody(classes = "divide-y divide-[#3A241C]/10 text-[#111827]") {
                                        id = "admin-product-table-body"
                                        if (products.isEmpty()) {
                                            tr {
                                                td(classes = "py-8 text-center text-[#6F5A4E] font-serif col-span-full") {
                                                    attributes["colspan"] = "6"
                                                    +"No products found. Use '+ New Product' to add one."
                                                }
                                            }
                                        } else {
                                            for (product in products) {
                                                tr(classes = UiStyles.Tables.TR_HOVER) {
                                                    id = "product-row-${product.id}"
                                                    td(classes = "${UiStyles.Tables.TD} font-mono font-medium text-[#C56A3C]") {
                                                        +product.sku
                                                    }
                                                    td(classes = "${UiStyles.Tables.TD} font-serif font-bold text-[#3A241C] max-w-[180px] truncate") {
                                                        +product.name
                                                    }
                                                    td(classes = "${UiStyles.Tables.TD} font-mono") {
                                                        +"$${product.price}"
                                                    }
                                                    td(classes = UiStyles.Tables.TD) {
                                                        if (product.stockQuantity > 0) {
                                                            span(classes = "text-[#16A34A] font-mono font-semibold") { +"${product.stockQuantity}" }
                                                        } else {
                                                            span(classes = "text-[#DC2626] font-mono font-bold") { +"0 (Out)" }
                                                        }
                                                    }
                                                    td(classes = UiStyles.Tables.TD) {
                                                        statusBadge(product.status)
                                                    }
                                                    td(classes = UiStyles.Tables.TD_RIGHT) {
                                                        dangerButton(
                                                            text = "Delete",
                                                            onClick = "deleteProductAdmin(${product.id}, '${product.name.replace("'", "\\'")}')"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Right: Real-time Audit Telemetry Feed
                    div(classes = "space-y-4") {
                        div(classes = "flex items-center justify-between") {
                            h2(classes = UiStyles.Typography.SECTION_TITLE) {
                                span(classes = "w-2.5 h-2.5 rounded-full bg-[#C56A3C]")
                                +"Security & Telemetry"
                            }
                            div(classes = "flex items-center gap-2") {
                                div(classes = "htmx-indicator") {
                                    id = "audit-indicator"
                                    unsafe {
                                        raw("""<svg class="animate-spin w-4 h-4 text-[#C56A3C]" viewBox="0 0 24 24" fill="none"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>""")
                                    }
                                }
                                button(classes = UiStyles.Buttons.PURPLE_SM) {
                                    hxGet("/ui/audit")
                                    hxTarget("#audit-feed")
                                    hxIndicator("#audit-indicator")
                                    +"Refresh"
                                }
                            }
                        }

                        card(extraClasses = "p-5") {
                            p(classes = "text-xs font-serif text-[#6F5A4E] mb-3") {
                                +"Real-time event stream intercepted via Spring AOP ("
                                code(classes = "text-[#C56A3C] font-mono") { +"@AuditLog" }
                                +") & Security listeners."
                            }
                            div(classes = "space-y-3 max-h-[560px] overflow-y-auto pr-1") {
                                id = "audit-feed"
                                renderAuditFeed(this, auditEvents)
                            }
                        }
                    }
                }
            }

            // Client-side Admin Script
            script {
                unsafe {
                    raw(adminInteractivityScript())
                }
            }
        }

    fun renderAuditFeedFragment(auditEvents: List<AuditEventResponse>): TagConsumer<StringBuilder>.() -> Unit = {
        if (auditEvents.isEmpty()) {
            div(classes = "text-xs font-serif text-[#6F5A4E] text-center py-8") {
                +"No audit events recorded yet."
            }
        } else {
            for (event in auditEvents) {
                div(classes = UiStyles.Cards.AUDIT_EVENT) {
                    renderAuditEventCardInner(event)
                }
            }
        }
    }

    private fun renderAuditFeed(container: FlowContent, auditEvents: List<AuditEventResponse>) {
        if (auditEvents.isEmpty()) {
            container.div(classes = "text-xs font-serif text-[#6F5A4E] text-center py-8") {
                +"No audit events recorded yet."
            }
        } else {
            for (event in auditEvents) {
                container.div(classes = UiStyles.Cards.AUDIT_EVENT) {
                    renderAuditEventCardInner(event)
                }
            }
        }
    }

    private fun FlowContent.renderAuditEventCardInner(event: AuditEventResponse) {
        div(classes = "flex items-center justify-between") {
            auditActionBadge(event.action)
            span(classes = "text-xs text-[#6F5A4E] font-mono") {
                +TIME_FORMATTER.format(event.timestamp)
            }
        }
        div(classes = "flex items-center gap-2 text-xs font-serif text-[#3A241C]") {
            span(classes = "font-medium text-[#6F5A4E]") { +"By:" }
            span(classes = "font-mono text-[#C56A3C] truncate max-w-[140px]") { +event.principal }
            span(classes = "text-[#6F5A4E]/40") { +"•" }
            span(classes = "font-medium text-[#6F5A4E]") { +"Resource:" }
            span(classes = "font-mono text-[#3A241C]") { +"${event.resourceType}:${event.resourceId ?: "N/A"}" }
        }
        if (!event.details.isNullOrBlank()) {
            p(classes = "text-xs text-[#6F5A4E] font-mono bg-white px-2.5 py-1 rounded-[6px] border border-[#3A241C]/15 truncate") {
                +event.details
            }
        }
    }

    private fun FlowContent.renderHeaderAndMetrics(products: List<ProductResponse>, auditEvents: List<AuditEventResponse>) {
        div(classes = "flex flex-col md:flex-row md:items-center md:justify-between gap-4") {
            div {
                h1(classes = UiStyles.Typography.PAGE_TITLE) {
                    +"Operations Control Center"
                }
                p(classes = UiStyles.Typography.SUBTITLE) {
                    +"Manage catalog items, monitor security auditing, and inspect system telemetry."
                }
            }

            div(classes = "flex items-center gap-3") {
                button(classes = UiStyles.Buttons.PRIMARY) {
                    attributes["onclick"] = "toggleNewProductDrawer()"
                    unsafe {
                        raw("""<svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>""")
                    }
                    +"+ New Product"
                }
            }
        }

        // Metrics Grid (4 cards)
        div(classes = "grid grid-cols-2 md:grid-cols-4 gap-4") {
            val totalProducts = products.size
            val activeProducts = products.count { it.status.name == "ACTIVE" }
            val lowStock = products.count { it.stockQuantity <= 5 }
            val totalAudits = auditEvents.size

            metricKpiCard("Total Catalog Items", totalProducts.toString(), "In database", "clay")
            metricKpiCard("Active SKUs", activeProducts.toString(), "Live on storefront", "emerald")
            metricKpiCard("Low Stock Alert", lowStock.toString(), "5 units or less", "rose")
            metricKpiCard("Audit Events", totalAudits.toString(), "Logged by AOP", "purple")
        }
    }

    private fun FlowContent.renderNewProductPanel() {
        div(classes = UiStyles.Cards.PANEL) {
            id = "new-product-form-container"

            div(classes = "flex items-center justify-between pb-3 border-b border-[#3A241C]/15") {
                div {
                    h3(classes = "text-xl font-serif font-bold text-[#3A241C]") { +"Create New Catalog Product" }
                    p(classes = "text-sm font-serif text-[#6F5A4E]") { +"Payload is validated against Bean Validation and @ValidSku rules." }
                }
                button(classes = "text-[#6F5A4E] hover:text-[#3A241C] cursor-pointer") {
                    attributes["onclick"] = "toggleNewProductDrawer()"
                    unsafe {
                        raw("""<svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>""")
                    }
                }
            }

            form(classes = "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4") {
                attributes["onsubmit"] = "createProductAdmin(event)"

                // SKU
                div {
                    label(classes = UiStyles.Forms.LABEL) {
                        +"SKU "
                        span(classes = "text-[#C56A3C] font-mono text-xs") { +"(e.g., SKU-TECH-1001)" }
                    }
                    input(type = InputType.text, classes = UiStyles.Forms.INPUT) {
                        id = "new-sku"
                        required = true
                        placeholder = "SKU-PROD-9999"
                    }
                }

                // Name
                div {
                    label(classes = UiStyles.Forms.LABEL) { +"Product Name" }
                    input(type = InputType.text, classes = UiStyles.Forms.INPUT) {
                        id = "new-name"
                        required = true
                        placeholder = "Artisanal Terracotta Vase"
                    }
                }

                // Status
                div {
                    label(classes = UiStyles.Forms.LABEL) { +"Initial Status" }
                    select(classes = UiStyles.Forms.SELECT) {
                        id = "new-status"
                        option { value = "ACTIVE"; +"ACTIVE" }
                        option { value = "DRAFT"; +"DRAFT" }
                        option { value = "ARCHIVED"; +"ARCHIVED" }
                    }
                }

                // Price
                div {
                    label(classes = UiStyles.Forms.LABEL) { +"Price ($ USD)" }
                    input(type = InputType.number, classes = UiStyles.Forms.INPUT) {
                        id = "new-price"
                        required = true
                        step = "0.01"
                        placeholder = "149.99"
                    }
                }

                // Stock
                div {
                    label(classes = UiStyles.Forms.LABEL) { +"Stock Quantity" }
                    input(type = InputType.number, classes = UiStyles.Forms.INPUT) {
                        id = "new-stock"
                        required = true
                        step = "1"
                        placeholder = "25"
                    }
                }

                // Description
                div(classes = "sm:col-span-2 lg:col-span-3") {
                    label(classes = UiStyles.Forms.LABEL) { +"Description" }
                    textArea(classes = UiStyles.Forms.INPUT) {
                        id = "new-description"
                        rows = "2"
                        placeholder = "Hand-thrown natural clay vessel fired with studio precision..."
                    }
                }

                div(classes = "sm:col-span-2 lg:col-span-3 flex justify-end gap-3 pt-2") {
                    ghostButton("Cancel", onClick = "toggleNewProductDrawer()")
                    button(type = ButtonType.submit, classes = UiStyles.Buttons.PRIMARY) {
                        id = "create-product-btn"
                        +"+ Create Product"
                    }
                }
            }
        }
    }

    private fun adminInteractivityScript(): String = """
        function toggleNewProductDrawer() {
            const drawer = document.getElementById('new-product-form-container');
            if (drawer) {
                drawer.classList.toggle('hidden');
            }
        }

        async function createProductAdmin(event) {
            event.preventDefault();
            const token = localStorage.getItem('shopcraft_access_token');
            if (!token) {
                showToast('Authentication required (ROLE_ADMIN). Please sign in.', 'error');
                openAuthModal();
                return;
            }

            const sku = document.getElementById('new-sku').value.trim();
            const name = document.getElementById('new-name').value.trim();
            const description = document.getElementById('new-description').value.trim();
            const price = parseFloat(document.getElementById('new-price').value);
            const stockQuantity = parseInt(document.getElementById('new-stock').value, 10);
            const status = document.getElementById('new-status').value;

            const submitBtn = document.getElementById('create-product-btn');
            submitBtn.disabled = true;
            submitBtn.innerText = 'Creating...';

            try {
                const response = await fetch('/api/v1/products', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({ sku, name, description, price, stockQuantity, status })
                });

                if (response.status === 201) {
                    showToast('Product ' + sku + ' created successfully!', 'success');
                    setTimeout(() => window.location.reload(), 600);
                } else if (response.status === 401) {
                    showToast('Session expired or authentication required. Please sign in.', 'error');
                    openAuthModal();
                } else if (response.status === 403) {
                    showToast('Access denied: ROLE_ADMIN authority required.', 'error');
                } else {
                    const err = await response.json();
                    let msg = err.message || 'Failed to create product';
                    if (err.errors && Array.isArray(err.errors)) {
                        msg = err.errors.map(e => e.message || e).join(', ');
                    }
                    showToast(msg, 'error');
                }
            } catch (e) {
                showToast('Network error: ' + e.message, 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerText = '+ Create Product';
            }
        }

        async function deleteProductAdmin(id, name) {
            if (!confirm('Are you sure you want to delete ' + name + '?')) return;
            const token = localStorage.getItem('shopcraft_access_token');
            if (!token) {
                showToast('Authentication required (ROLE_ADMIN). Please sign in.', 'error');
                openAuthModal();
                return;
            }

            try {
                const response = await fetch('/api/v1/products/' + id, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': 'Bearer ' + token
                    }
                });

                if (response.status === 204) {
                    showToast('Product removed.', 'success');
                    const row = document.getElementById('product-row-' + id);
                    if (row) {
                        row.classList.add('opacity-0', 'transition-opacity', 'duration-300');
                        setTimeout(() => row.remove(), 300);
                    }
                } else if (response.status === 401) {
                    showToast('Session expired. Please sign in.', 'error');
                    openAuthModal();
                } else if (response.status === 403) {
                    showToast('Access denied: ROLE_ADMIN authority required to delete products.', 'error');
                } else {
                    const err = await response.json();
                    showToast(err.message || 'Failed to delete product', 'error');
                }
            } catch (e) {
                showToast('Network error: ' + e.message, 'error');
            }
        }
    """.trimIndent()
}
