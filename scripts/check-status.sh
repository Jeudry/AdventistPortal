#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔍 AdventistPortal Services Status Check${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker is running${NC}"
echo ""

# Check if docker-compose services are running
echo -e "${YELLOW}📊 Docker Services Status:${NC}"
echo ""

# PostgreSQL
if docker ps | grep -q "adventistportal-postgres"; then
    if docker exec adventistportal-postgres pg_isready -U postgres -d adventistportal > /dev/null 2>&1; then
        echo -e "${GREEN}  ✅ PostgreSQL: Running (192.168.97.2:5432)${NC}"
    else
        echo -e "${YELLOW}  ⚠️  PostgreSQL: Container running but not ready${NC}"
    fi
else
    echo -e "${RED}  ❌ PostgreSQL: Not running${NC}"
fi

# Redis
if docker ps | grep -q "adventistportal-redis"; then
    if docker exec adventistportal-redis redis-cli -a adventistportal_redis_password ping 2>/dev/null | grep -q "PONG"; then
        echo -e "${GREEN}  ✅ Redis: Running (localhost:6379)${NC}"
    else
        echo -e "${YELLOW}  ⚠️  Redis: Container running but not ready${NC}"
    fi
else
    echo -e "${RED}  ❌ Redis: Not running${NC}"
fi

# RabbitMQ
if docker ps | grep -q "adventistportal-rabbitmq"; then
    if docker exec adventistportal-rabbitmq rabbitmq-diagnostics -q ping > /dev/null 2>&1; then
        echo -e "${GREEN}  ✅ RabbitMQ: Running (localhost:5672)${NC}"
        echo -e "${GREEN}     └─ Management UI: http://localhost:15672${NC}"
    else
        echo -e "${YELLOW}  ⚠️  RabbitMQ: Container running but not ready${NC}"
    fi
else
    echo -e "${RED}  ❌ RabbitMQ: Not running${NC}"
fi

echo ""

# Check if application is running
if lsof -i :8080 > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Application API: Running (http://localhost:8080)${NC}"
else
    echo -e "${YELLOW}⚠️  Application API: Not running (port 8080 is free)${NC}"
fi

echo ""

# Show docker-compose status
echo -e "${YELLOW}📋 Detailed Container Status:${NC}"
docker-compose -f docker-compose.orb.yml ps 2>/dev/null || docker ps --format "table {{.Names}}\t{{.Status}}" | grep adventistportal

echo ""
echo -e "${BLUE}💡 Quick Commands:${NC}"
echo -e "   Start Orb services:   ${GREEN}./reset-and-start-orb.sh${NC}"
echo -e "   Run application:      ${GREEN}./run-orb-app.sh${NC}"
echo -e "   Stop services:        ${GREEN}./stop-orb.sh${NC}"
echo -e "   View logs:            ${GREEN}docker-compose -f docker-compose.orb.yml logs -f${NC}"
echo ""