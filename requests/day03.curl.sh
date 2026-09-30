#!/usr/bin/env bash
# ==============================================================================
# ShopCraft Day 03: Validation, Error Handling & RFC 7807 cURL Verification
# ==============================================================================

set -euo pipefail

BASE_URL="http://localhost:8080/api/v1/products"

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_step() {
    echo -e "\n${BLUE}======================================================================${NC}"
    echo -e "${YELLOW}$1${NC}"
    echo -e "${BLUE}======================================================================${NC}"
}

print_step "1. POST /api/v1/products - Valid Product (Expected: 201 Created)"
curl -s -i -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-VAL-1001",
        "name": "Precision Gaming Mouse",
        "description": "26K DPI optical sensor with optical switches",
        "price": 89.99,
        "stockQuantity": 50,
        "status": "ACTIVE"
    }'

print_step "2. POST /api/v1/products - Validation Failure (Expected: 400 Bad Request RFC 7807)"
curl -s -i -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "lowercase-sku-wrong",
        "name": "",
        "price": -15.00,
        "stockQuantity": -2
    }'

print_step "3. POST /api/v1/products - Duplicate SKU Conflict (Expected: 409 Conflict RFC 7807)"
curl -s -i -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-VAL-1001",
        "name": "Duplicate Item",
        "price": 50.00,
        "stockQuantity": 10
    }'

print_step "4. GET /api/v1/products/999999 - Non-Existent ID (Expected: 404 Not Found RFC 7807)"
curl -s -i -X GET "$BASE_URL/999999"

print_step "5. POST /api/v1/products - Malformed JSON Body (Expected: 400 Bad Request RFC 7807)"
curl -s -i -X POST "$BASE_URL" \
    -H "Content-Type: application/json" \
    -d '{ "sku": "SKU-VAL-1002", "name": "Broken JSON", "price": '

echo -e "\n${GREEN}✔ Day 03 cURL test sequence completed.${NC}\n"
