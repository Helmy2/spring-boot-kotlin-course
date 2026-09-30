#!/usr/bin/env bash
# ==============================================================================
# ShopCraft Day 04: AOP & Audit Logging cURL Verification
# ==============================================================================

set -euo pipefail

BASE_URL="http://localhost:8080/api/v1"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_step() {
    echo -e "\n${BLUE}======================================================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${BLUE}======================================================================${NC}"
}

print_step "1. POST /api/v1/products - Create Product (Triggers @AuditLog PRODUCT_CREATED)"
curl -s -i -X POST "$BASE_URL/products" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-AOP-9999",
        "name": "AOP Audited Headphones",
        "description": "Spatial audio with active noise cancellation",
        "price": 299.99,
        "stockQuantity": 15,
        "status": "ACTIVE"
    }'

print_step "2. GET /api/v1/products - Fetch Products (Triggers @TrackExecutionTime)"
curl -s -i -X GET "$BASE_URL/products?page=0&size=5"

print_step "3. GET /api/v1/admin/audit-logs - Query Audit Trail (Expected: 200 OK PagedResponse)"
curl -s -i -X GET "$BASE_URL/admin/audit-logs?page=0&size=10"

echo -e "\n${GREEN}✔ Day 04 cURL audit sequence completed.${NC}\n"
