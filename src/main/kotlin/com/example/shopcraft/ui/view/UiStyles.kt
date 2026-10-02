package com.example.shopcraft.ui.view

import com.example.shopcraft.product.entity.ProductStatus

/**
 * Terracotta Editorial Design Tokens for ShopCraft.
 *
 * Implements a warm, natural palette centered on warm cream (paper), clay (terracotta),
 * ink-brown, and white surfaces. Free from generic AI dark-mode gradients and glow.
 */
object UiStyles {

    object Colors {
        const val CLAY = "#C56A3C"
        const val CLAY_HOVER = "#B35D32"
        const val CLAY_SOFT = "rgba(197, 106, 60, 0.14)"
        const val PAPER = "#F3E9D8"
        const val PAPER_SOFT = "#F8F0E4"
        const val INK = "#111827"
        const val INK_BROWN = "#3A241C"
        const val INK_MUTED = "#6F5A4E"
        const val RULE = "rgba(58, 36, 28, 0.22)"
        const val SURFACE = "#FFFFFF"
        const val SUCCESS = "#16A34A"
        const val WARNING = "#D97706"
        const val DANGER = "#DC2626"
    }

    object Buttons {
        const val PRIMARY = "min-h-[44px] px-5 py-2.5 rounded-[10px] bg-[#C56A3C] hover:bg-[#B35D32] text-white font-serif text-[16px] tracking-wide transition-all flex items-center justify-center gap-2 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#C56A3C] focus:ring-offset-2 focus:ring-offset-[#F3E9D8]"
        const val NAV_SIGN_IN = "min-h-[38px] px-4 py-1.5 rounded-[10px] bg-[#C56A3C] hover:bg-[#B35D32] text-white font-serif text-[15px] transition-colors cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#C56A3C]"
        const val SIGN_OUT = "min-h-[38px] px-3.5 py-1.5 rounded-[10px] text-xs font-mono text-[#DC2626] hover:bg-[#DC2626]/10 border border-[#DC2626]/30 transition-colors cursor-pointer"
        const val GHOST = "min-h-[44px] px-4 py-2.5 rounded-[10px] bg-transparent hover:bg-[#3A241C]/10 text-[#3A241C] border border-[#3A241C]/20 font-serif text-[16px] transition-colors cursor-pointer"
        const val DANGER_SM = "px-3 py-1.5 rounded-[8px] bg-transparent hover:bg-[#DC2626]/10 text-[#DC2626] border border-[#DC2626]/30 font-mono text-xs transition-colors cursor-pointer"
        const val PURPLE_SM = "min-h-[36px] text-xs font-mono text-[#3A241C] hover:bg-[#C56A3C]/10 bg-white border border-[#3A241C]/20 px-3 py-1.5 rounded-[8px] transition-colors cursor-pointer flex items-center gap-1.5"
        const val CARD_ACTION = "w-full min-h-[44px] py-2.5 px-4 rounded-[10px] bg-[#C56A3C] hover:bg-[#B35D32] text-white font-serif text-[16px] tracking-wide transition-all flex items-center justify-center gap-2 cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#C56A3C]"
        const val SUBMIT = "w-full min-h-[44px] py-2.5 rounded-[10px] bg-[#C56A3C] hover:bg-[#B35D32] text-white font-serif text-[17px] tracking-wide transition-all cursor-pointer focus:outline-none focus:ring-2 focus:ring-[#C56A3C]"
        const val DEMO_ADMIN = "flex-1 py-2 px-3 rounded-[8px] font-mono text-xs bg-[#F8F0E4] hover:bg-[#C56A3C]/15 text-[#3A241C] border border-[#3A241C]/20 transition-colors cursor-pointer"
        const val DEMO_CUSTOMER = "flex-1 py-2 px-3 rounded-[8px] font-mono text-xs bg-[#F8F0E4] hover:bg-[#C56A3C]/15 text-[#3A241C] border border-[#3A241C]/20 transition-colors cursor-pointer"
        const val SOCIAL = "flex items-center justify-center gap-2 min-h-[44px] py-2 px-4 rounded-[10px] bg-white hover:bg-[#F8F0E4] border border-[#3A241C]/20 font-serif text-sm text-[#3A241C] transition-colors cursor-pointer"
    }

    object Cards {
        const val BASE = "bg-white border border-[#3A241C]/15 rounded-[16px] shadow-none"
        const val PANEL = "hidden bg-white border border-[#3A241C]/20 rounded-[16px] p-6 space-y-5 transition-all"
        const val PRODUCT = "group bg-white border border-[#3A241C]/15 hover:border-[#C56A3C] rounded-[16px] p-5 transition-all flex flex-col justify-between"
        const val EMPTY = "col-span-full py-16 text-center bg-[#F8F0E4] border border-[#3A241C]/15 rounded-[16px]"
        const val AUDIT_EVENT = "p-3.5 rounded-[10px] bg-[#F8F0E4]/60 border border-[#3A241C]/15 hover:border-[#3A241C]/30 transition-colors text-xs space-y-1.5"
    }

    object Badges {
        const val SKU = "font-mono text-xs font-semibold px-2.5 py-1 rounded-[6px] bg-[#F8F0E4] text-[#3A241C] border border-[#3A241C]/20"
        const val ROLE = "font-mono text-[11px] font-bold px-2 py-0.5 rounded-[6px] bg-[#C56A3C]/15 text-[#C56A3C] border border-[#C56A3C]/30"

        fun status(status: ProductStatus): String = when (status) {
            ProductStatus.ACTIVE -> "px-2.5 py-0.5 text-xs font-serif font-bold rounded-[6px] bg-[#16A34A]/10 text-[#16A34A] border border-[#16A34A]/30"
            ProductStatus.DRAFT -> "px-2.5 py-0.5 text-xs font-serif font-bold rounded-[6px] bg-[#D97706]/10 text-[#D97706] border border-[#D97706]/30"
            ProductStatus.ARCHIVED -> "px-2.5 py-0.5 text-xs font-serif font-bold rounded-[6px] bg-[#6F5A4E]/10 text-[#6F5A4E] border border-[#6F5A4E]/30"
        }

        fun auditAction(action: String): String = when {
            action.contains("CREATE") -> "bg-[#16A34A]/10 text-[#16A34A] border border-[#16A34A]/30"
            action.contains("UPDATE") || action.contains("PATCH") -> "bg-[#C56A3C]/10 text-[#C56A3C] border border-[#C56A3C]/30"
            action.contains("DELETE") -> "bg-[#DC2626]/10 text-[#DC2626] border border-[#DC2626]/30"
            action.contains("AUTH") || action.contains("LOGIN") -> "bg-[#3A241C]/10 text-[#3A241C] border border-[#3A241C]/30"
            else -> "bg-[#6F5A4E]/10 text-[#6F5A4E] border border-[#6F5A4E]/30"
        }

        fun metricBorder(color: String): String = when (color) {
            "emerald" -> "hover:border-[#16A34A]/50"
            "rose" -> "hover:border-[#DC2626]/50"
            "purple" -> "hover:border-[#D97706]/50"
            else -> "hover:border-[#C56A3C]/50"
        }

        fun metricText(color: String): String = when (color) {
            "emerald" -> "text-[#16A34A]"
            "rose" -> "text-[#DC2626]"
            "purple" -> "text-[#D97706]"
            else -> "text-[#3A241C]"
        }
    }

    object Forms {
        const val INPUT = "w-full min-h-[44px] bg-white border border-[#3A241C]/25 rounded-[10px] px-3.5 py-2 text-[16px] text-[#111827] focus:outline-none focus:ring-2 focus:ring-[#C56A3C] focus:border-[#C56A3C]"
        const val INPUT_LG = "w-full min-h-[44px] bg-white border border-[#3A241C]/25 rounded-[10px] px-3.5 py-2.5 text-[16px] text-[#111827] focus:outline-none focus:ring-2 focus:ring-[#C56A3C] focus:border-[#C56A3C]"
        const val SELECT = "w-full min-h-[44px] bg-white border border-[#3A241C]/25 rounded-[10px] px-3.5 py-2 text-[15px] text-[#111827] focus:outline-none focus:ring-2 focus:ring-[#C56A3C]"
        const val SELECT_INLINE = "min-h-[44px] bg-white border border-[#3A241C]/25 text-[#3A241C] font-serif text-[15px] rounded-[10px] px-3 py-2 focus:outline-none focus:ring-2 focus:ring-[#C56A3C]"
        const val SEARCH_INPUT = "w-full min-h-[44px] pl-10 pr-10 py-2.5 bg-white border border-[#3A241C]/25 rounded-[10px] text-[16px] text-[#111827] placeholder-[#6F5A4E]/60 focus:outline-none focus:ring-2 focus:ring-[#C56A3C]"
        const val LABEL = "block text-sm font-serif font-medium text-[#3A241C] mb-1.5"
    }

    object Tables {
        const val TABLE = "w-full text-left border-collapse text-sm"
        const val THEAD = "bg-[#F8F0E4] border-b border-[#3A241C]/20 text-[#3A241C] font-serif font-semibold"
        const val TH = "py-3.5 px-4"
        const val TD = "py-3.5 px-4 border-b border-[#3A241C]/10 text-[#111827]"
        const val TD_RIGHT = "py-3.5 px-4 text-right border-b border-[#3A241C]/10"
        const val TR_HOVER = "hover:bg-[#F8F0E4]/60 transition-colors"
    }

    object Typography {
        const val GRADIENT_TEXT = "text-[#C56A3C]"
        const val PAGE_TITLE = "text-3xl sm:text-4xl font-serif text-[#3A241C] tracking-tight"
        const val SUBTITLE = "text-base text-[#6F5A4E] mt-1.5 font-serif"
        const val SECTION_TITLE = "text-2xl font-serif text-[#3A241C] flex items-center gap-2"
    }
}
