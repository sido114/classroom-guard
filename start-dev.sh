#!/bin/bash
# Development startup script for Classroom Guard

set -e

echo "=========================================="
echo "Classroom Guard - Development Mode"
echo "=========================================="
echo ""

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    echo "Error: Must run from project root directory"
    exit 1
fi

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "Shutting down services..."
    pkill -P $$ || true
    exit 0
}

trap cleanup SIGINT SIGTERM

# Start backend in background
echo "Starting backend (Quarkus)..."
echo "-------------------------------------------"
cd backend
./mvnw quarkus:dev > ../backend.log 2>&1 &
BACKEND_PID=$!
cd ..

# Wait for backend to start
echo "Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
        echo "✓ Backend is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "✗ Backend failed to start. Check backend.log for details."
        cat backend.log
        kill $BACKEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 2
done

echo ""

# Start frontend in background
echo "Starting frontend (Next.js)..."
echo "-------------------------------------------"
cd frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
cd ..

# Wait for frontend to start
echo "Waiting for frontend to start..."
for i in {1..30}; do
    if curl -s http://localhost:3000 > /dev/null 2>&1; then
        echo "✓ Frontend is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "✗ Frontend failed to start. Check frontend.log for details."
        cat frontend.log
        kill $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 2
done

echo ""
echo "=========================================="
echo "✓ All services are running!"
echo "=========================================="
echo ""
echo "Frontend: http://localhost:3000"
echo "Backend:  http://localhost:8080"
echo "Health:   http://localhost:8080/api/health"
echo ""
echo "Logs:"
echo "  Backend:  tail -f backend.log"
echo "  Frontend: tail -f frontend.log"
echo ""
echo "Press Ctrl+C to stop all services"
echo "=========================================="

# Wait for user to stop
wait
