package com.example.shopcraft.ui.view

import com.example.shopcraft.product.entity.ProductStatus
import kotlinx.html.*

/**
 * Reusable Compose-like DSL Component Extensions for Kotlin HTML DSL (kotlinx.html).
 * Formatted with Terracotta Editorial design tokens.
 */

fun FlowContent.card(
    extraClasses: String = "",
    block: DIV.() -> Unit
) {
    div(classes = "${UiStyles.Cards.BASE} $extraClasses".trim()) {
        block()
    }
}

fun FlowContent.metricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: String
) {
    val borderHover = UiStyles.Badges.metricBorder(color)
    val textValueColor = UiStyles.Badges.metricText(color)

    div(classes = "bg-white border border-[#3A241C]/15 rounded-[16px] p-5 $borderHover transition-colors") {
        p(classes = "text-xs font-mono font-medium text-[#6F5A4E] uppercase tracking-wider") { +title }
        p(classes = "text-3xl font-serif font-bold $textValueColor mt-1.5 mb-1 tracking-tight") { +value }
        p(classes = "text-xs font-mono text-[#6F5A4E]/80") { +subtitle }
    }
}

fun FlowContent.primaryButton(
    text: String,
    onClick: String? = null,
    id: String? = null,
    type: ButtonType = ButtonType.button,
    block: BUTTON.() -> Unit = {}
) {
    button(type = type, classes = UiStyles.Buttons.PRIMARY) {
        id?.let { this.id = it }
        onClick?.let { attributes["onclick"] = it }
        block()
        +text
    }
}

fun FlowContent.dangerButton(
    text: String,
    onClick: String? = null,
    block: BUTTON.() -> Unit = {}
) {
    button(classes = UiStyles.Buttons.DANGER_SM) {
        onClick?.let { attributes["onclick"] = it }
        block()
        +text
    }
}

fun FlowContent.ghostButton(
    text: String,
    onClick: String? = null,
    block: BUTTON.() -> Unit = {}
) {
    button(type = ButtonType.button, classes = UiStyles.Buttons.GHOST) {
        onClick?.let { attributes["onclick"] = it }
        block()
        +text
    }
}

fun FlowContent.actionButton(
    text: String,
    onClick: String? = null,
    block: BUTTON.() -> Unit = {}
) {
    button(classes = UiStyles.Buttons.CARD_ACTION) {
        onClick?.let { attributes["onclick"] = it }
        block()
        +text
    }
}

fun FlowContent.statusBadge(status: ProductStatus) {
    span(classes = UiStyles.Badges.status(status)) {
        +status.name
    }
}

fun FlowContent.skuBadge(sku: String) {
    span(classes = UiStyles.Badges.SKU) {
        +sku
    }
}

fun FlowContent.roleBadge(role: String, id: String? = null) {
    span(classes = UiStyles.Badges.ROLE) {
        id?.let { this.id = it }
        +role
    }
}

fun FlowContent.auditActionBadge(action: String) {
    span(classes = "font-mono font-bold text-xs px-2.5 py-0.5 rounded-[6px] ${UiStyles.Badges.auditAction(action)}") {
        +action
    }
}

fun FlowContent.sectionHeader(
    title: String,
    dotColor: String? = null,
    badgeText: String? = null
) {
    div(classes = "flex items-center justify-between") {
        h2(classes = UiStyles.Typography.SECTION_TITLE) {
            dotColor?.let {
                span(classes = "w-2.5 h-2.5 rounded-full $it")
            }
            +title
        }
        badgeText?.let {
            span(classes = "text-xs font-mono text-[#6F5A4E]") {
                +it
            }
        }
    }
}

fun FlowContent.emptyState(
    title: String,
    subtitle: String,
    iconSvg: String? = null
) {
    div(classes = UiStyles.Cards.EMPTY) {
        renderEmptyStateBody(title, subtitle, iconSvg)
    }
}

fun TagConsumer<*>.emptyState(
    title: String,
    subtitle: String,
    iconSvg: String? = null
) {
    div(classes = UiStyles.Cards.EMPTY) {
        renderEmptyStateBody(title, subtitle, iconSvg)
    }
}

private fun FlowContent.renderEmptyStateBody(
    title: String,
    subtitle: String,
    iconSvg: String?
) {
    div(classes = "w-12 h-12 mx-auto rounded-full bg-[#F3E9D8] border border-[#3A241C]/20 flex items-center justify-center text-[#C56A3C] mb-3.5") {
        unsafe {
            raw(iconSvg ?: """<svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/></svg>""")
        }
    }
    h3(classes = "text-xl font-serif font-bold text-[#3A241C]") { +title }
    p(classes = "text-sm font-serif text-[#6F5A4E] mt-1.5 max-w-sm mx-auto") {
        +subtitle
    }
}
