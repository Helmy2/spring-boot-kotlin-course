package com.example.shopcraft.ui.view

import kotlinx.html.HTML
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

object HtmlResponseHelper {
    val UTF8_HTML: MediaType = MediaType("text", "html", java.nio.charset.StandardCharsets.UTF_8)

    fun page(block: HTML.() -> Unit): ResponseEntity<String> {
        val content = "<!DOCTYPE html>\n" + createHTML(prettyPrint = false).html(block = block)
        return ResponseEntity.ok().contentType(UTF8_HTML).body(content)
    }

    fun fragment(block: TagConsumer<StringBuilder>.() -> Unit): ResponseEntity<String> {
        val content = buildString {
            appendHTML(prettyPrint = false).apply(block)
        }
        return ResponseEntity.ok().contentType(UTF8_HTML).body(content)
    }
}

// HTMX DSL Helpers
fun Tag.hxGet(url: String) { attributes["hx-get"] = url }
fun Tag.hxPost(url: String) { attributes["hx-post"] = url }
fun Tag.hxDelete(url: String) { attributes["hx-delete"] = url }
fun Tag.hxTarget(target: String) { attributes["hx-target"] = target }
fun Tag.hxTrigger(trigger: String) { attributes["hx-trigger"] = trigger }
fun Tag.hxSwap(swap: String) { attributes["hx-swap"] = swap }
fun Tag.hxInclude(include: String) { attributes["hx-include"] = include }
fun Tag.hxIndicator(indicator: String) { attributes["hx-indicator"] = indicator }
