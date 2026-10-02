# Day 08: Full-Stack Web UI with Kotlin HTML DSL (`kotlinx.html`), Tailwind CSS & HTMX

Welcome to **Day 08** of the ShopCraft Backend Master Course. In Days 01 through 07, you engineered a production-grade enterprise backend featuring:
- Dynamic specifications & advanced JPA filtering (Day 02 & 03)
- Global exception handling and bean validation (Day 04)
- Observability via Spring AOP execution logging & security audit trails (Day 05)
- Stateless JWT authentication and persistent Refresh Token Rotation (Day 06)
- Third-party social identity federation via OAuth 2.0 (Day 07)

In **Day 08**, you will construct a **modern, rich, and reactive full-stack web interface** directly in Kotlin using **Kotlin HTML DSL (`kotlinx.html`)**, **Tailwind CSS**, and **HTMX**—eliminating the complexity, build overhead, and context switching of separate JavaScript frameworks.

---

## 🎯 Learning Objectives

By the end of this module, you will master:
1. **Server-Side Kotlin HTML DSL (`kotlinx.html-jvm`)**: Building 100% type-safe, compile-time checked HTML templates natively in Kotlin without external template engines or Node.js/npm toolchains.
2. **Server-Driven Hypermedia Architecture (HTMX 2.0.4)**: Creating responsive Single Page Application (SPA) experiences by swapping HTML fragments over HTTP (`hx-get`, `hx-target`, `hx-trigger`, `hx-indicator`, `hx-include`).
3. **Modern Design Systems with Tailwind CSS**: Utilizing curated dark mode aesthetics, glassmorphic navigation bars, vibrant gradients, and Google Fonts (`Plus Jakarta Sans`) without static assets bundling.
4. **Full-Stack Security Integration**:
   - Connecting the UI with the Day 06 JWT & Refresh Token mechanisms.
   - Intercepting HTMX requests to inject `Authorization: Bearer <token>` headers.
   - Automatically recovering from `401 Unauthorized` responses via client-side Refresh Token Rotation.
   - Consuming OAuth 2.0 social login redirects from Day 07 (`/oauth2/authorization/github`).
5. **Real-Time Operations & Observability**:
   - Building an operations dashboard displaying live catalog metrics, inventory management tables, and dynamic product creation drawers.
   - Streaming live system audit logs intercepted by the Day 05 `@AuditLog` aspect.

---

## 🔬 Architecture & Data Flow

### 1. Server-Driven Hypermedia with HTMX & Kotlin HTML DSL

```mermaid
sequenceDiagram
    autonumber
    actor User as Browser User
    participant HTMX as HTMX Engine (Client)
    participant Ctrl as UiController (Spring MVC)
    participant DSL as Kotlin HTML DSL (kotlinx.html)
    participant Svc as ProductService & AuditService

    Note over User,HTMX: Full Page Load
    User->>Ctrl: GET / (Accept: text/html)
    Ctrl->>Svc: getAllProducts()
    Svc-->>Ctrl: List<ProductResponse>
    Ctrl->>DSL: Layout.render(CatalogView.renderCatalogPage(products))
    DSL-->>Ctrl: Complete HTML5 Document (Tailwind + HTMX)
    Ctrl-->>User: 200 OK text/html;charset=UTF-8

    Note over User,HTMX: Live Reactive Search (Debounced 300ms)
    User->>HTMX: Types "Key" in #catalog-search
    HTMX->>Ctrl: GET /ui/products?search=Key&status=ACTIVE
    Ctrl->>Svc: getProducts(ProductFilterCriteria("Key", ACTIVE), Pageable)
    Svc-->>Ctrl: PagedResponse<ProductResponse>
    Ctrl->>DSL: CatalogView.renderProductGridFragment(paged.content)
    DSL-->>Ctrl: HTML Fragment (<div class="group...">...</div>)
    Ctrl-->>HTMX: 200 OK text/html;charset=UTF-8
    HTMX->>HTMX: Swap innerHTML of #product-grid (DOM updated without full reload)
```

---

## 🛠️ Implementation Steps

### Step 1: HTML Response Helper & HTMX DSL Extensions
Define standard utilities to stream HTML pages and partial fragments:
- `HtmlResponseHelper.page`: Generates complete `<!DOCTYPE html>` documents with `text/html;charset=UTF-8`.
- `HtmlResponseHelper.fragment`: Streams partial HTML snippets for HTMX target swaps.
- Custom HTMX attribute helpers on `Tag`: `hxGet`, `hxPost`, `hxDelete`, `hxTarget`, `hxTrigger`, `hxSwap`, `hxInclude`, `hxIndicator`.

### Step 2: Modern Layout Master Template (`Layout.kt`)
Construct the reusable application shell:
- **Head**: Includes Plus Jakarta Sans typography, Tailwind CSS 3 CDN, and HTMX 2.0.4.
- **Glassmorphic Navbar**: Features brand identity, navigation routes (`Storefront`, `Admin Operations`, `H2 Console`), and dynamic authentication badges (`ROLE_ADMIN` / `ROLE_USER`).
- **Auth Modal**: Supports quick demo credential autofill (`admin@shopcraft.com`, `customer@shopcraft.com`) and social OAuth2 logins.
- **Interactivity Script**: Intercepts `htmx:configRequest` to inject JWT tokens and `htmx:responseError` to perform automatic token rotation on 401s.

### Step 3: Interactive Storefront Catalog (`CatalogView.kt`)
Develop the public storefront:
- **Hero Banner**: Atmospheric violet ambient glow and reactive feature badges.
- **Controls Bar**: Instant live search input with debounced keyup triggers, loading spinners, and status filter selectors.
- **Product Grid**: Responsive 4-column card layout displaying SKUs, status badges, formatted prices, live stock levels, and interactive "Add to Cart" toast notifications.
- **Empty State**: Friendly visual guidance when no inventory matches search queries.

### Step 4: Operations Control Center (`AdminView.kt`) & Controller (`UiController.kt`)
Implement the administrative dashboard:
- **Metrics Bar**: 4-card KPI summary (Total Products, Active SKUs, Low Stock alerts, Total Audits).
- **Product Drawer**: Collapsible creation panel enforcing `@ValidSku` formats (`SKU-XXX-0000`).
- **Inventory Table**: Complete table with single-click authenticated deletion actions.
- **Security & Telemetry Feed**: Real-time event stream intercepted via Day 05 `@AuditLog` and Spring Security authentication listeners.
- **UiController**: Expose `@GetMapping` endpoints for `/`, `/ui/products`, `/ui/admin`, and `/ui/audit`.

---

## 🧪 Verification & Testing

### Automated Slice Tests
Run the complete UI slice test suite:
```bash
./gradlew test --tests "com.example.shopcraft.ui.controller.UiControllerTest"
```

### Full Test Suite Rerun
Verify that all 114 tests across Days 01 through 08 pass:
```bash
./gradlew test --rerun-tasks
```

### Manual Browser Verification
1. Launch the Spring Boot application:
   ```bash
   ./gradlew bootRun
   ```
2. Navigate to `http://localhost:8080/` in your browser.
3. Type in the search box to observe instant HTMX product filtering.
4. Click **Sign In**, select **Fill Admin**, and authenticate.
5. Navigate to **Admin Operations** (`http://localhost:8080/ui/admin`) to inspect metrics and audit telemetry.
