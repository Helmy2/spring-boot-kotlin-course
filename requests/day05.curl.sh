#!/usr/bin/env bash
# ==============================================================================
# ShopCraft Day 05: Unit & Integration Testing Pyramid cURL Verification
# ==============================================================================

set -euo pipefail

BASE_URL="http://localhost:8080/api/v1"

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

print_step() {
    echo -e "\n${BLUE}======================================================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${BLUE}======================================================================${NC}"
}

print_step "1. POST /api/v1/products - Create Product (Expect: 201 Created & Location header)"
curl -s -i -X POST "$BASE_URL/products" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-PYR-9001",
        "name": "Pyramid Verified Keyboard",
        "description": "Tested across unit, slice, and integration test tiers",
        "price": 189.99,
        "stockQuantity": 25,
        "status": "ACTIVE"
    }'

print_step "2. POST /api/v1/products - Invalid Payload (Expect: 400 Bad Request RFC 7807)"
curl -s -i -X POST "$BASE_URL/products" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-PYR-9002",
        "name": "",
        "price": -10.00,
        "stockQuantity": -5,
        "status": "ACTIVE"
    }'

print_step "3. GET /api/v1/products/99999 - Non-Existent Product (Expect: 404 Not Found ProblemDetail)"
curl -s -i -X GET "$BASE_URL/products/99999"

print_step "4. GET /api/v1/products - Filter Products (Expect: 200 OK PagedResponse)"
curl -s -i -X GET "$BASE_URL/products?minPrice=100.00&maxPrice=250.00&status=ACTIVE&page=0&size=5"

print_step "5. GET /api/v1/admin/audit-logs - Check Audit Trail (Expect: 200 OK)"
curl -s -i -X GET "$BASE_URL/admin/audit-logs?page=0&size=10"

echo -e "\n${GREEN}✔ Day 05 cURL test verification sequence completed.${NC}\n"
