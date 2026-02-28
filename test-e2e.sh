#!/bin/bash
set -e

echo "🧪 Running E2E Tests for Classroom Guard"
echo "=========================================="

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Cleanup function
cleanup() {
    echo -e "\n${YELLOW}Cleaning up...${NC}"
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null || true
        echo "Stopped backend (PID: $BACKEND_PID)"
    fi
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null || true
        echo "Stopped frontend (PID: $FRONTEND_PID)"
    fi
}

trap cleanup EXIT

# Check if backend is already running
if curl -s http://localhost:8080/q/health/ready > /dev/null 2>&1; then
    echo -e "${YELLOW}Backend already running on port 8080${NC}"
    BACKEND_RUNNING=true
else
    echo -e "${GREEN}Starting backend...${NC}"
    cd backend
    ./mvnw quarkus:dev -Dquarkus.http.port=8080 > ../backend-e2e.log 2>&1 &
    BACKEND_PID=$!
    cd ..
    echo "Backend PID: $BACKEND_PID"
    BACKEND_RUNNING=false
fi

# Check if frontend is already running
if curl -s http://localhost:3000 > /dev/null 2>&1; then
    echo -e "${YELLOW}Frontend already running on port 3000${NC}"
    FRONTEND_RUNNING=true
else
    echo -e "${GREEN}Starting frontend...${NC}"
    cd frontend
    npm run build > /dev/null 2>&1
    npm start > ../frontend-e2e.log 2>&1 &
    FRONTEND_PID=$!
    cd ..
    echo "Frontend PID: $FRONTEND_PID"
    FRONTEND_RUNNING=false
fi

# Wait for services to be ready
echo -e "${GREEN}Waiting for services to be ready...${NC}"
MAX_WAIT=60
WAITED=0

while [ $WAITED -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8080/q/health/ready > /dev/null 2>&1 && \
       curl -s http://localhost:3000 > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Services are ready!${NC}"
        break
    fi
    sleep 2
    WAITED=$((WAITED + 2))
    echo "Waiting... ($WAITED/$MAX_WAIT seconds)"
done

if [ $WAITED -ge $MAX_WAIT ]; then
    echo -e "${RED}✗ Services did not start in time${NC}"
    echo "Check logs:"
    echo "  Backend: backend-e2e.log"
    echo "  Frontend: frontend-e2e.log"
    exit 1
fi

# Run E2E tests
echo -e "${GREEN}Running Playwright E2E tests...${NC}"
cd frontend
npm run test:e2e

echo -e "${GREEN}✓ E2E tests completed!${NC}"
