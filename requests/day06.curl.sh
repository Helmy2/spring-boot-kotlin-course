#!/usr/bin/env bash
# ==============================================================================
# ShopCraft Day 06: JWT Authentication & RBAC Verification Script
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

print_step "1. Public Access: GET /api/v1/products (Expect: 200 OK without token)"
curl -s -i -X GET "$BASE_URL/products?page=0&size=2"

print_step "2. Anonymous Access Attempt: POST /api/v1/products (Expect: 401 Unauthorized ProblemDetail)"
curl -s -i -X POST "$BASE_URL/products" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-TST-9001",
        "name": "Unauthorized Product",
        "price": 49.99,
        "stockQuantity": 10,
        "status": "ACTIVE"
    }'

print_step "3. Customer Login: POST /api/v1/auth/login"
CUSTOMER_LOGIN_RESP=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"email":"customer@shopcraft.com","password":"Customer123!"}')
echo "$CUSTOMER_LOGIN_RESP"

CUSTOMER_TOKEN=$(echo "$CUSTOMER_LOGIN_RESP" | grep -o '"token":"[^"]*' | cut -d'"' -f4 || true)

print_step "4. Authenticated Profile: GET /api/v1/auth/me (Customer Token - Expect: 200 OK)"
if [ -n "$CUSTOMER_TOKEN" ]; then
    curl -s -i -X GET "$BASE_URL/auth/me" \
        -H "Authorization: Bearer $CUSTOMER_TOKEN"
fi

print_step "5. RBAC Violation: Customer POST /api/v1/products (Expect: 403 Forbidden ProblemDetail)"
if [ -n "$CUSTOMER_TOKEN" ]; then
    curl -s -i -X POST "$BASE_URL/products" \
        -H "Authorization: Bearer $CUSTOMER_TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "sku": "SKU-TST-9002",
            "name": "Customer Attempt Product",
            "price": 79.99,
            "stockQuantity": 5,
            "status": "ACTIVE"
        }'
fi

print_step "6. Admin Login: POST /api/v1/auth/login"
ADMIN_LOGIN_RESP=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@shopcraft.com","password":"Admin123!"}')
echo "$ADMIN_LOGIN_RESP"

ADMIN_TOKEN=$(echo "$ADMIN_LOGIN_RESP" | grep -o '"token":"[^"]*' | cut -d'"' -f4 || true)

print_step "7. Admin Authorized Mutation: POST /api/v1/products (Admin Token - Expect: 201 Created)"
if [ -n "$ADMIN_TOKEN" ]; then
    curl -s -i -X POST "$BASE_URL/products" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -H "Content-Type: application/json" \
        -d '{
            "sku": "SKU-ELC-9003",
            "name": "Admin RGB Mechanical Keyboard",
            "description": "Authorized product creation with ROLE_ADMIN",
            "price": 169.99,
            "stockQuantity": 25,
            "status": "ACTIVE"
        }'
fi

print_step "8. Admin Audit Trail Query: GET /api/v1/admin/audit-logs (Admin Token - Expect: 200 OK)"
if [ -n "$ADMIN_TOKEN" ]; then
    curl -s -i -X GET "$BASE_URL/admin/audit-logs?page=0&size=5" \
        -H "Authorization: Bearer $ADMIN_TOKEN"
fi

echo -e "\n${GREEN}✔ Day 06 JWT & RBAC verification sequence completed.${NC}\n"
