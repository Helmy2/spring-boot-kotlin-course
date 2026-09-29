#!/usr/bin/env bash
set -euo pipefail

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}======================================================${NC}"
echo -e "${BLUE}  ShopCraft API - Day 01 Automated cURL Test Script   ${NC}"
echo -e "${BLUE}======================================================${NC}"

# Check server health
echo -e "\n${YELLOW}Step 0: Checking if server is running on ${BASE_URL}...${NC}"
if ! curl -s "${BASE_URL}/api/v1/products" > /dev/null 2>&1; then
    echo -e "${RED}[ERROR] ShopCraft server is not running on ${BASE_URL}.${NC}"
    echo -e "Please start the application with: ./gradlew bootRun"
    exit 1
fi
echo -e "${GREEN}[OK] Server is reachable.${NC}"

# Step 1: Create a Product (POST)
echo -e "\n${YELLOW}Step 1: Creating a new product (POST /api/v1/products)...${NC}"
CREATE_RESPONSE=$(curl -s -i -X POST "${BASE_URL}/api/v1/products" \
    -H "Content-Type: application/json" \
    -d '{
        "sku": "SKU-KEY-'"$(date +%s)"'",
        "name": "Mechanical Gaming Keyboard",
        "description": "RGB backlight with tactile blue mechanical switches",
        "price": 129.99,
        "stockQuantity": 50,
        "status": "ACTIVE"
    }')

HTTP_STATUS=$(echo "$CREATE_RESPONSE" | grep -E "^HTTP/[0-9.]+" | awk '{print $2}' | tr -d '\r')
LOCATION_HEADER=$(echo "$CREATE_RESPONSE" | grep -i "^Location:" | awk '{print $2}' | tr -d '\r' || true)

echo -e "HTTP Status: ${GREEN}${HTTP_STATUS}${NC}"
echo -e "Location Header: ${GREEN}${LOCATION_HEADER}${NC}"

if [ "$HTTP_STATUS" != "201" ]; then
    echo -e "${RED}[FAIL] Expected HTTP 201 Created, got: ${HTTP_STATUS}${NC}"
    exit 1
fi
echo -e "${GREEN}[PASS] Product created successfully with 201 Created & Location header!${NC}"

# Step 2: Get All Products (GET)
echo -e "\n${YELLOW}Step 2: Retrieving all products (GET /api/v1/products)...${NC}"
ALL_PRODUCTS=$(curl -s -X GET "${BASE_URL}/api/v1/products" -H "Accept: application/json")
echo -e "Response: ${GREEN}${ALL_PRODUCTS}${NC}"

# Step 3: Get Product By ID (GET)
if [ -n "$LOCATION_HEADER" ]; then
    echo -e "\n${YELLOW}Step 3: Fetching newly created product by Location URI (${LOCATION_HEADER})...${NC}"
    PRODUCT_BY_ID=$(curl -s -X GET "${BASE_URL}${LOCATION_HEADER}" -H "Accept: application/json")
    echo -e "Response: ${GREEN}${PRODUCT_BY_ID}${NC}"
fi

echo -e "\n${GREEN}======================================================${NC}"
echo -e "${GREEN}  All Day 01 cURL tests completed successfully!       ${NC}"
echo -e "${GREEN}======================================================${NC}"
