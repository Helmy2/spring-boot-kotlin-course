package com.example.shopcraft.ui.view

import kotlinx.html.*

object Layout {

    fun render(
        title: String,
        activeNav: String = "storefront",
        content: MAIN.() -> Unit
    ): HTML.() -> Unit = {
        head {
            meta { charset = "UTF-8" }
            meta {
                name = "viewport"
                this.content = "width=device-width, initial-scale=1.0"
            }
            title { +"ShopCraft - $title" }

            // Google Fonts: DM Serif Display & JetBrains Mono
            link {
                rel = "preconnect"
                href = "https://fonts.googleapis.com"
            }
            link {
                rel = "preconnect"
                href = "https://fonts.gstatic.com"
                attributes["crossorigin"] = ""
            }
            link {
                rel = "stylesheet"
                href = "https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=JetBrains+Mono:wght@400;500;600&display=swap"
            }

            // Tailwind CSS 3 CDN
            script { src = "https://cdn.tailwindcss.com" }
            script {
                unsafe {
                    raw("""
                    tailwind.config = {
                        theme: {
                            extend: {
                                colors: {
                                    clay: '#C56A3C',
                                    'clay-hover': '#B35D32',
                                    'clay-soft': 'rgba(197, 106, 60, 0.14)',
                                    paper: '#F3E9D8',
                                    'paper-soft': '#F8F0E4',
                                    ink: '#111827',
                                    'ink-brown': '#3A241C',
                                    'ink-muted': '#6F5A4E',
                                    rule: 'rgba(58, 36, 28, 0.22)',
                                    surface: '#FFFFFF',
                                },
                                fontFamily: {
                                    serif: ['"DM Serif Display"', 'Georgia', 'serif'],
                                    mono: ['"JetBrains Mono"', 'monospace'],
                                    sans: ['"DM Serif Display"', 'Georgia', 'serif'],
                                },
                                borderRadius: {
                                    sm: '6px',
                                    md: '10px',
                                    lg: '16px',
                                    xl: '24px',
                                }
                            }
                        }
                    }
                    """.trimIndent())
                }
            }

            // HTMX 2.0.4 CDN
            script { src = "https://unpkg.com/htmx.org@2.0.4" }

            // Custom inline styles for subtle transitions and custom scrollbars
            style {
                unsafe {
                    raw("""
                    :root {
                        --clay: #C56A3C;
                        --clay-soft: rgba(197, 106, 60, 0.14);
                        --paper: #F3E9D8;
                        --paper-soft: #F8F0E4;
                        --ink: #111827;
                        --ink-brown: #3A241C;
                        --ink-muted: #6F5A4E;
                        --rule: rgba(58, 36, 28, 0.22);
                        --surface-card: #FFFFFF;
                        --focus-ring: #C56A3C;
                    }
                    body {
                        background-color: #F3E9D8;
                        color: #111827;
                        font-family: 'DM Serif Display', Georgia, serif;
                    }
                    .htmx-indicator {
                        opacity: 0;
                        transition: opacity 200ms ease-in-out;
                    }
                    .htmx-request .htmx-indicator, .htmx-request.htmx-indicator {
                        opacity: 1;
                    }
                    ::-webkit-scrollbar {
                        width: 8px;
                        height: 8px;
                    }
                    ::-webkit-scrollbar-track {
                        background: #F3E9D8;
                    }
                    ::-webkit-scrollbar-thumb {
                        background: rgba(58, 36, 28, 0.25);
                        border-radius: 4px;
                    }
                    ::-webkit-scrollbar-thumb:hover {
                        background: rgba(58, 36, 28, 0.45);
                    }
                    """.trimIndent())
                }
            }
        }

        body(classes = "bg-[#F3E9D8] text-[#111827] font-serif min-h-screen flex flex-col antialiased selection:bg-[#C56A3C] selection:text-white") {
            // Sticky Navbar
            renderNavbar(activeNav)

            // Main Content Area
            main(classes = "flex-grow") {
                content()
            }

            // Footer
            renderFooter()

            // Auth Modal
            renderAuthModal()

            // Toast Container
            div(classes = "fixed bottom-5 right-5 z-50 flex flex-col gap-2 pointer-events-none") {
                id = "toast-container"
            }

            // Client-side Script for Auth & HTMX & Toasts
            script {
                unsafe {
                    raw(authAndInteractivityScript())
                }
            }
        }
    }

    private fun BODY.renderNavbar(activeNav: String) {
        nav(classes = "sticky top-0 z-40 bg-[#F3E9D8]/95 backdrop-blur-md border-b border-[#3A241C]/20 transition-all") {
            div(classes = "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8") {
                div(classes = "flex items-center justify-between h-20") {
                    // Left: Brand Logo & Links
                    div(classes = "flex items-center gap-10") {
                        a(href = "/", classes = "flex items-center gap-3.5 group") {
                            div(classes = "w-10 h-10 rounded-[10px] bg-[#C56A3C] flex items-center justify-center text-white group-hover:bg-[#B35D32] transition-colors") {
                                unsafe {
                                    raw("""<svg class="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/></svg>""")
                                }
                            }
                            div {
                                span(classes = "text-2xl font-serif font-bold tracking-normal text-[#3A241C]") {
                                    +"ShopCraft"
                                }
                                span(classes = "ml-2.5 px-2 py-0.5 text-xs font-mono font-semibold bg-[#F8F0E4] text-[#C56A3C] border border-[#3A241C]/20 rounded-[6px]") {
                                    +"Editorial Craft"
                                }
                            }
                        }

                        // Nav Links
                        div(classes = "hidden md:flex items-center gap-6") {
                            val storefrontClasses = if (activeNav == "storefront") {
                                "text-[#3A241C] border-b-2 border-[#C56A3C] font-serif font-bold text-base py-1"
                            } else {
                                "text-[#6F5A4E] hover:text-[#3A241C] font-serif font-medium text-base py-1 transition-colors"
                            }
                            a(href = "/", classes = storefrontClasses) {
                                +"Storefront"
                            }

                            val adminClasses = if (activeNav == "admin") {
                                "text-[#3A241C] border-b-2 border-[#C56A3C] font-serif font-bold text-base py-1"
                            } else {
                                "text-[#6F5A4E] hover:text-[#3A241C] font-serif font-medium text-base py-1 transition-colors"
                            }
                            a(href = "/ui/admin", classes = adminClasses) {
                                +"Admin Operations"
                            }

                            a(
                                href = "/h2-console",
                                classes = "text-[#6F5A4E] hover:text-[#3A241C] font-serif font-medium text-base py-1 transition-colors"
                            ) {
                                attributes["target"] = "_blank"
                                +"H2 Console ↗"
                            }
                        }
                    }

                    // Right: Auth Controls
                    div(classes = "flex items-center gap-3.5") {
                        // Unauthenticated State
                        div(classes = "flex items-center gap-3") {
                            id = "nav-auth-logged-out"

                            button(classes = UiStyles.Buttons.NAV_SIGN_IN) {
                                attributes["onclick"] = "openAuthModal()"
                                +"Sign In"
                            }

                            // Quick GitHub OAuth
                            div(classes = "hidden sm:flex items-center pl-3 border-l border-[#3A241C]/20") {
                                a(
                                    href = "/oauth2/authorization/github",
                                    classes = "p-2 rounded-[8px] bg-white hover:bg-[#F8F0E4] text-[#3A241C] border border-[#3A241C]/20 transition-colors"
                                ) {
                                    attributes["title"] = "Sign in with GitHub"
                                    unsafe {
                                        raw("""<svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/></svg>""")
                                    }
                                }
                            }
                        }

                        // Authenticated State (initially hidden)
                        div(classes = "hidden items-center gap-3") {
                            id = "nav-auth-logged-in"

                            div(classes = "flex items-center gap-2.5 px-3.5 py-1.5 rounded-[10px] bg-white border border-[#3A241C]/20") {
                                div(classes = "w-2 h-2 rounded-full bg-[#16A34A]")
                                span(classes = "text-xs font-mono text-[#3A241C]") {
                                    id = "nav-user-email"
                                    +"user"
                                }
                                roleBadge(role = "USER", id = "nav-user-role")
                                button(classes = "p-1 hover:text-[#C56A3C] text-[#6F5A4E] transition-colors cursor-pointer") {
                                    attributes["title"] = "Rotate Tokens via Refresh Mechanism"
                                    attributes["onclick"] = "manualRefreshToken()"
                                    unsafe {
                                        raw("""<svg class="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/></svg>""")
                                    }
                                }
                            }

                            button(classes = UiStyles.Buttons.SIGN_OUT) {
                                attributes["onclick"] = "logout()"
                                +"Sign Out"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun BODY.renderFooter() {
        footer(classes = "border-t border-[#3A241C]/20 bg-[#F8F0E4]/80 py-12 mt-20") {
            div(classes = "max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-6 text-sm font-serif text-[#6F5A4E]") {
                div(classes = "flex items-center gap-2.5") {
                    span(classes = "font-bold text-[#3A241C]") { +"ShopCraft" }
                    +"— Production E-Commerce Architecture in Spring Boot 4 & Kotlin"
                }
                div(classes = "flex items-center gap-3 font-mono text-xs") {
                    span(classes = "px-3 py-1 rounded-[6px] bg-white border border-[#3A241C]/15 text-[#3A241C]") {
                        +"kotlinx.html DSL"
                    }
                    span(classes = "px-3 py-1 rounded-[6px] bg-white border border-[#3A241C]/15 text-[#3A241C]") {
                        +"HTMX 2.0.4"
                    }
                    span(classes = "px-3 py-1 rounded-[6px] bg-white border border-[#3A241C]/15 text-[#C56A3C]") {
                        +"Terracotta Editorial"
                    }
                }
            }
        }
    }

    private fun BODY.renderAuthModal() {
        div(classes = "fixed inset-0 z-50 bg-[#3A241C]/50 backdrop-blur-sm hidden items-center justify-center p-4") {
            id = "auth-modal"

            div(classes = "bg-white border border-[#3A241C]/20 rounded-[16px] max-w-md w-full p-7 relative") {
                // Close button
                button(classes = "absolute top-5 right-5 text-[#6F5A4E] hover:text-[#3A241C] cursor-pointer") {
                    attributes["onclick"] = "closeAuthModal()"
                    unsafe {
                        raw("""<svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>""")
                    }
                }

                h2(classes = "text-2xl font-serif font-bold text-[#3A241C] tracking-tight") {
                    +"Sign in to ShopCraft"
                }
                p(classes = "text-sm font-serif text-[#6F5A4E] mt-1 mb-6") {
                    +"Authenticate to test role-based access control and live inventory mutations."
                }

                // Quick credential fill shortcuts
                div(classes = "mb-5 p-3.5 rounded-[10px] bg-[#F8F0E4] border border-[#3A241C]/20") {
                    p(classes = "text-xs font-mono font-semibold text-[#6F5A4E] mb-2 uppercase tracking-wider") {
                        +"Quick Demo Credentials"
                    }
                    div(classes = "flex gap-2.5") {
                        button(classes = UiStyles.Buttons.DEMO_ADMIN) {
                            attributes["onclick"] = "fillCredentials('admin@shopcraft.com', 'Admin123!')"
                            +"Fill Admin"
                        }
                        button(classes = UiStyles.Buttons.DEMO_CUSTOMER) {
                            attributes["onclick"] = "fillCredentials('customer@shopcraft.com', 'Customer123!')"
                            +"Fill Customer"
                        }
                    }
                }

                // Standard Login Form
                form(classes = "space-y-4") {
                    attributes["onsubmit"] = "performLogin(event)"

                    div {
                        label(classes = UiStyles.Forms.LABEL) { +"Email Address" }
                        input(type = InputType.email, classes = UiStyles.Forms.INPUT_LG) {
                            id = "auth-email"
                            required = true
                            placeholder = "you@example.com"
                        }
                    }

                    div {
                        label(classes = UiStyles.Forms.LABEL) { +"Password" }
                        input(type = InputType.password, classes = UiStyles.Forms.INPUT_LG) {
                            id = "auth-password"
                            required = true
                            placeholder = "••••••••"
                        }
                    }

                    button(type = ButtonType.submit, classes = UiStyles.Buttons.SUBMIT) {
                        id = "auth-submit-btn"
                        +"Sign In with Password"
                    }
                }

                // Social Login Divider
                div(classes = "relative my-6") {
                    div(classes = "absolute inset-0 flex items-center") {
                        div(classes = "w-full border-t border-[#3A241C]/20")
                    }
                    div(classes = "relative flex justify-center text-xs uppercase font-mono") {
                        span(classes = "bg-white px-3 text-[#6F5A4E] font-medium") {
                            +"Or social identity (Day 07)"
                        }
                    }
                }

                a(
                    href = "/oauth2/authorization/github",
                    classes = "${UiStyles.Buttons.SOCIAL} w-full"
                ) {
                    unsafe {
                        raw("""<svg class="w-4 h-4" viewBox="0 0 24 24" fill="currentColor"><path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/></svg>""")
                    }
                    +"Continue with GitHub"
                }

                // Active token telemetry inspect box
                div(classes = "mt-5 pt-4 border-t border-[#3A241C]/20 text-xs font-mono text-[#6F5A4E] flex items-center justify-between") {
                    span { id = "token-status-info"; +"No active session" }
                    button(classes = "text-[#C56A3C] hover:underline font-semibold cursor-pointer") {
                        attributes["onclick"] = "manualRefreshToken()"
                        +"Refresh Token"
                    }
                }
            }
        }
    }

    private fun authAndInteractivityScript(): String = """
        function showToast(message, type = 'info') {
            const container = document.getElementById('toast-container');
            if (!container) return;
            const toast = document.createElement('div');
            const bg = type === 'success' ? 'bg-white border-[#16A34A] text-[#16A34A]' :
                       type === 'error' ? 'bg-white border-[#DC2626] text-[#DC2626]' :
                       'bg-white border-[#C56A3C] text-[#3A241C]';
            toast.className = 'flex items-center gap-3 px-4 py-3 rounded-[10px] border-l-4 border-y border-r border-[#3A241C]/15 shadow-md text-sm font-serif pointer-events-auto transform transition-all duration-300 translate-y-2 opacity-0 ' + bg;
            toast.innerHTML = '<span>' + message + '</span>';
            container.appendChild(toast);
            requestAnimationFrame(() => {
                toast.classList.remove('translate-y-2', 'opacity-0');
            });
            setTimeout(() => {
                toast.classList.add('opacity-0', 'translate-y-2');
                setTimeout(() => toast.remove(), 300);
            }, 3500);
        }

        function openAuthModal() {
            const modal = document.getElementById('auth-modal');
            if (modal) {
                modal.classList.remove('hidden');
                modal.classList.add('flex');
            }
        }

        function closeAuthModal() {
            const modal = document.getElementById('auth-modal');
            if (modal) {
                modal.classList.add('hidden');
                modal.classList.remove('flex');
            }
        }

        function fillCredentials(email, password) {
            document.getElementById('auth-email').value = email;
            document.getElementById('auth-password').value = password;
        }

        async function performLogin(event) {
            event.preventDefault();
            const email = document.getElementById('auth-email').value;
            const password = document.getElementById('auth-password').value;
            const submitBtn = document.getElementById('auth-submit-btn');
            submitBtn.disabled = true;
            submitBtn.innerText = 'Authenticating...';

            try {
                const response = await fetch('/api/v1/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email, password })
                });
                if (!response.ok) {
                    const err = await response.json();
                    throw new Error(err.message || 'Login failed');
                }
                const data = await response.json();
                const token = data.token || data.accessToken;
                const refreshToken = data.refreshToken;
                localStorage.setItem('shopcraft_access_token', token);
                if (refreshToken) localStorage.setItem('shopcraft_refresh_token', refreshToken);
                const userEmail = (data.user && data.user.email) || data.email || email;
                showToast('Signed in successfully as ' + userEmail, 'success');
                closeAuthModal();
                updateAuthUI();
            } catch (err) {
                showToast(err.message, 'error');
            } finally {
                submitBtn.disabled = false;
                submitBtn.innerText = 'Sign In with Password';
            }
        }

        async function manualRefreshToken() {
            const refreshToken = localStorage.getItem('shopcraft_refresh_token');
            if (!refreshToken) {
                showToast('No refresh token found. Please sign in.', 'error');
                return;
            }
            try {
                const response = await fetch('/api/v1/auth/refresh', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ refreshToken })
                });
                if (!response.ok) {
                    throw new Error('Refresh token invalid or expired');
                }
                const data = await response.json();
                const token = data.token || data.accessToken;
                localStorage.setItem('shopcraft_access_token', token);
                if (data.refreshToken) localStorage.setItem('shopcraft_refresh_token', data.refreshToken);
                showToast('Tokens rotated successfully!', 'success');
                updateAuthUI();
            } catch (err) {
                logout();
                showToast('Session expired. Please sign in again.', 'error');
            }
        }

        function logout() {
            localStorage.removeItem('shopcraft_access_token');
            localStorage.removeItem('shopcraft_refresh_token');
            showToast('Signed out', 'info');
            updateAuthUI();
        }

        function parseJwt(token) {
            try {
                const base64Url = token.split('.')[1];
                const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
                const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                    return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
                }).join(''));
                return JSON.parse(jsonPayload);
            } catch (e) {
                return null;
            }
        }

        function updateAuthUI() {
            const token = localStorage.getItem('shopcraft_access_token');
            const loggedInSection = document.getElementById('nav-auth-logged-in');
            const loggedOutSection = document.getElementById('nav-auth-logged-out');
            const tokenStatusBox = document.getElementById('token-status-info');

            if (token) {
                const payload = parseJwt(token);
                if (payload && payload.exp * 1000 > Date.now()) {
                    if (loggedInSection) loggedInSection.classList.remove('hidden');
                    if (loggedInSection) loggedInSection.classList.add('flex');
                    if (loggedOutSection) loggedOutSection.classList.add('hidden');
                    const emailElem = document.getElementById('nav-user-email');
                    const roleElem = document.getElementById('nav-user-role');
                    if (emailElem) emailElem.innerText = payload.sub || 'User';
                    if (roleElem) roleElem.innerText = (payload.roles && payload.roles.length > 0) ? payload.roles[0] : 'USER';
                    if (tokenStatusBox) {
                        tokenStatusBox.innerText = 'Authenticated as ' + payload.sub + ' (' + (payload.roles || []).join(', ') + ')';
                    }
                    return;
                }
            }
            if (loggedInSection) {
                loggedInSection.classList.add('hidden');
                loggedInSection.classList.remove('flex');
            }
            if (loggedOutSection) loggedOutSection.classList.remove('hidden');
            if (tokenStatusBox) tokenStatusBox.innerText = 'No active session';
        }

        // Initialize HTMX and check OAuth2 redirect parameters
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.has('token')) {
                const token = urlParams.get('token');
                const refresh = urlParams.get('refreshToken');
                localStorage.setItem('shopcraft_access_token', token);
                if (refresh) localStorage.setItem('shopcraft_refresh_token', refresh);
                window.history.replaceState({}, document.title, window.location.pathname);
                showToast('OAuth2 social authentication completed!', 'success');
            }

            updateAuthUI();

            document.body.addEventListener('htmx:configRequest', function(evt) {
                const token = localStorage.getItem('shopcraft_access_token');
                if (token) {
                    evt.detail.headers['Authorization'] = 'Bearer ' + token;
                }
            });

            document.body.addEventListener('htmx:responseError', async function(evt) {
                if (evt.detail.xhr.status === 401) {
                    const refreshToken = localStorage.getItem('shopcraft_refresh_token');
                    if (refreshToken) {
                        try {
                            const res = await fetch('/api/v1/auth/refresh', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ refreshToken })
                            });
                            if (res.ok) {
                                const data = await res.json();
                                const token = data.token || data.accessToken;
                                localStorage.setItem('shopcraft_access_token', token);
                                if (data.refreshToken) localStorage.setItem('shopcraft_refresh_token', data.refreshToken);
                                showToast('Session refreshed automatically via token rotation!', 'success');
                                updateAuthUI();
                                return;
                            }
                        } catch(e) { console.error(e); }
                    }
                    showToast('Authentication required. Please sign in.', 'error');
                    openAuthModal();
                }
            });
        });
    """.trimIndent()
}
