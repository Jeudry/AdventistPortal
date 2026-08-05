#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Starting AdventistPortal Local Development Environment${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# One infrastructure file for both runtimes. There used to be two that differed in
# credentials and ports, which is two files to keep in step and nobody does.
COMPOSE_FILE="compose.infra.yaml"
if docker info 2>/dev/null | grep -q "orbstack"; then
    echo -e "${BLUE}🔮 Detected Orb Stack${NC}"
    USE_ORB=true
else
    echo -e "${BLUE}🐳 Using Docker Desktop${NC}"
    USE_ORB=false
fi

# Start Docker Compose services
echo -e "${YELLOW}📦 Starting Docker services...${NC}"
docker-compose -f "$COMPOSE_FILE" up -d

# Wait for services to be healthy
echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
sleep 5

# Check PostgreSQL
echo -e "${YELLOW}🔍 Checking PostgreSQL...${NC}"
until docker exec adventistportal-postgres pg_isready -U postgres -d adventistportal > /dev/null 2>&1; do
    echo -e "${YELLOW}   Waiting for PostgreSQL...${NC}"
    sleep 2
done
echo -e "${GREEN}   ✓ PostgreSQL is ready${NC}"

# Check Redis
echo -e "${YELLOW}🔍 Checking Redis...${NC}"
until docker exec adventistportal-redis redis-cli -a adventistportal_redis_password ping > /dev/null 2>&1; do
    echo -e "${YELLOW}   Waiting for Redis...${NC}"
    sleep 2
done
echo -e "${GREEN}   ✓ Redis is ready${NC}"

# Check RabbitMQ
echo -e "${YELLOW}🔍 Checking RabbitMQ...${NC}"
until docker exec adventistportal-rabbitmq rabbitmq-diagnostics -q ping > /dev/null 2>&1; do
    echo -e "${YELLOW}   Waiting for RabbitMQ...${NC}"
    sleep 2
done
echo -e "${GREEN}   ✓ RabbitMQ is ready${NC}"

echo ""
echo -e "${GREEN}✅ All services are up and running!${NC}"
echo ""
echo -e "${YELLOW}📊 Service Information:${NC}"
echo -e "   PostgreSQL:  localhost:5432"
echo -e "   Redis:       localhost:6379"
echo -e "   RabbitMQ:    localhost:5672"
echo -e "   RabbitMQ UI: http://localhost:15672"
echo ""
if [ "$USE_ORB" = true ]; then
    echo -e "${BLUE}🔮 Orb Stack Domains (also accessible):${NC}"
    echo -e "   postgres.adventistportal.orb.local"
    echo -e "   redis.adventistportal.orb.local"
    echo -e "   rabbitmq.adventistportal.orb.local"
    echo ""
fi
echo -e "${YELLOW}🎯 To run the application:${NC}"
echo -e "   Option 1 - Using Script:"
echo -e "      ./run-orb-app.sh"
echo ""
echo -e "   Option 2 - Using Gradle:"
echo -e "      ./gradlew :app:bootRun --args='--spring.profiles.active=orb'"
echo ""
echo -e "   Option 3 - From IntelliJ:"
echo -e "      Run Configuration: 'AdventistPortalApi [ORB]'"
echo ""
echo -e "${YELLOW}📋 View logs:${NC}"
echo -e "   docker-compose -f $COMPOSE_FILE logs -f"
echo ""
echo -e "${YELLOW}🛑 To stop services:${NC}"
echo -e "   ./stop-local.sh"
echo -e "   or: docker-compose -f $COMPOSE_FILE down"
echo ""