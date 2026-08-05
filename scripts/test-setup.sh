#!/bin/bash

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "🧪 Testing AdventistPortal Local Setup"
echo "=================================="
echo ""

ERRORS=0

# Test 1: Check if Docker is installed
echo -n "1. Docker installed... "
if command -v docker &> /dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - Docker not found"
    ERRORS=$((ERRORS + 1))
fi

# Test 2: Check if Docker is running
echo -n "2. Docker running... "
if docker info &> /dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - Docker not running"
    ERRORS=$((ERRORS + 1))
fi

# Test 3: Check if docker-compose is available
echo -n "3. Docker Compose available... "
if docker-compose version &> /dev/null || docker compose version &> /dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - Docker Compose not found"
    ERRORS=$((ERRORS + 1))
fi

# Test 4: Check if compose.infra.yaml exists
echo -n "4. compose.infra.yaml exists... "
if [ -f "compose.infra.yaml" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - File not found"
    ERRORS=$((ERRORS + 1))
fi

# Test 5: Check if compose.infra.yaml is valid
echo -n "5. compose.infra.yaml valid... "
if docker-compose config &> /dev/null; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - Invalid syntax"
    ERRORS=$((ERRORS + 1))
fi

# Test 6: Check if scripts exist
echo -n "6. Scripts exist... "
if [ -f "start-local.sh" ] && [ -f "stop-local.sh" ] && [ -f "check-status.sh" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - Scripts missing"
    ERRORS=$((ERRORS + 1))
fi

# Test 7: Check if scripts are executable
echo -n "7. Scripts executable... "
if [ -x "start-local.sh" ] && [ -x "stop-local.sh" ] && [ -x "check-status.sh" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - Scripts not executable"
    ERRORS=$((ERRORS + 1))
fi

# Test 8: Check if application-local.yml exists
echo -n "8. application-local.yml exists... "
if [ -f "app/src/main/resources/application-local.yml" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - File not found"
    ERRORS=$((ERRORS + 1))
fi

# Test 9: Check if Makefile exists
echo -n "9. Makefile exists... "
if [ -f "Makefile" ]; then
    echo -e "${GREEN}✓${NC}"
else
    echo -e "${RED}✗${NC} - File not found"
    ERRORS=$((ERRORS + 1))
fi

# Test 10: Check if required ports are free (optional)
echo -n "10. Checking ports... "
PORTS_IN_USE=0
for port in 5432 6379 5673 15673 8081; do
    if lsof -i :$port &> /dev/null; then
        PORTS_IN_USE=$((PORTS_IN_USE + 1))
    fi
done

if [ $PORTS_IN_USE -eq 0 ]; then
    echo -e "${GREEN}✓ All ports free${NC}"
else
    echo -e "${YELLOW}⚠ $PORTS_IN_USE port(s) in use${NC}"
fi

echo ""
echo "=================================="

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ All tests passed!${NC}"
    echo ""
    echo "You're ready to start:"
    echo "  ./start-local.sh && make run"
    exit 0
else
    echo -e "${RED}❌ $ERRORS test(s) failed${NC}"
    echo ""
    echo "Please fix the issues above before proceeding."
    exit 1
fi