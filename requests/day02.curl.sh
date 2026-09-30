#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE}  ShopCraft API - Day 02 Automated cURL Test Script   ${NC}"
echo -e "${BLUE}======================================================${NC}"

# Check server
echo -e "\n${YELLOW}Checking if server is running on ${BASE_URL}...${NC}"
if ! curl -s "${BASE_URL}/api/v1/products" > /dev/null 2>&1; then
    echo -e "${RED}[ERROR] ShopCraft server is not running on ${BASE_URL}.${NC}"
    echo -e "Please start the application with: ./gradlew bootRun"
    exit 1
fi
echo -e "${GREEN}[OK] Server is reachable.${NC}"

# 1. POST: Create Product
echo -e "\n${YELLOW}1. Creating Product (POST /api/v1/products)...${NC}"
CREATE_RESP=$(curl -s -i -X POST "${BASE_URL}/api/v1/products" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-MON-'"$(date +%s)"'",
        "name": "4K Ultra-Wide Monitor",
        "description": "34-inch curved display for developers",
        "price": 699.99,
        "stockQuantity": 15,
        "status": "ACTIVE"
    }')

HTTP_STATUS=$(echo "$CREATE_RESP" | grep -E "^HTTP/[0-9.]+" | awk '{print $2}' | tr -d '\r')
LOCATION_HEADER=$(echo "$CREATE_RESP" | grep -i "^Location:" | awk '{print $2}' | tr -d '\r' || true)
echo -e "HTTP Status: ${GREEN}${HTTP_STATUS}${NC}"
echo -e "Location: ${GREEN}${LOCATION_HEADER}${NC}"

PRODUCT_ID=$(echo "$LOCATION_HEADER" | awk -F'/' '{print $NF}')

# 2. PUT: Complete Replacement
echo -e "\n${YELLOW}2. Complete Resource Replacement (PUT /api/v1/products/${PRODUCT_ID})...${NC}"
PUT_RESP=$(curl -s -X PUT "${BASE_URL}/api/v1/products/${PRODUCT_ID}" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-MON-REPLACED",
        "name": "4K Ultra-Wide Monitor Pro Edition",
        "description": "34-inch curved 144Hz HDR display",
        "price": 799.99,
        "stockQuantity": 20,
        "status": "ACTIVE"
    }')
echo -e "Response: ${GREEN}${PUT_RESP}${NC}"

# 3. PATCH: Partial Modification
echo -e "\n${YELLOW}3. Selective Partial Modification (PATCH /api/v1/products/${PRODUCT_ID})...${NC}"
PATCH_RESP=$(curl -s -X PATCH "${BASE_URL}/api/v1/products/${PRODUCT_ID}" \
    -H "Content-Type: application/json" \
    -d '{
        "price": 749.99,
        "stockQuantity": 25
    }')
echo -e "Response: ${GREEN}${PATCH_RESP}${NC}"

# 4. GET: Paginated & Dynamic Filter Query
echo -e "\n${YELLOW}4. Paginated & Dynamic Query (GET /api/v1/products?search=Monitor&minPrice=500&page=0&size=5&sort=price,desc)...${NC}"
PAGED_RESP=$(curl -s -X GET "${BASE_URL}/api/v1/products?search=Monitor&minPrice=500&page=0&size=5&sort=price,desc" \
    -H "Accept: application/json")
echo -e "Response: ${GREEN}${PAGED_RESP}${NC}"

# 5. DELETE: Idempotent Resource Removal (204 No Content)
echo -e "\n${YELLOW}5. Deleting Product (DELETE /api/v1/products/${PRODUCT_ID})...${NC}"
DELETE_RESP=$(curl -s -i -X DELETE "${BASE_URL}/api/v1/products/${PRODUCT_ID}")
DEL_STATUS=$(echo "$DELETE_RESP" | grep -E "^HTTP/[0-9.]+" | awk '{print $2}' | tr -d '\r')
echo -e "HTTP Status: ${GREEN}${DEL_STATUS}${NC}"

if [ "$DEL_STATUS" != "204" ]; then
    echo -e "${RED}[FAIL] Expected HTTP 204 No Content, got: ${DEL_STATUS}${NC}"
    exit 1
fi
echo -e "${GREEN}[PASS] Resource deleted successfully with 204 No Content!${NC}"

echo -e "\n${GREEN}======================================================${NC}"
echo -e "${GREEN}  All Day 02 REST cURL tests completed successfully!  ${NC}"
echo -e "${GREEN}======================================================${NC}"
