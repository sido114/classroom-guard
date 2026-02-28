#!/bin/bash
# Automated test runner for Classroom Guard

set -e

echo "==================================="
echo "Classroom Guard Test Runner"
echo "==================================="
echo ""

# Backend Tests
echo "1. Running Backend Tests..."
echo "-----------------------------------"
cd backend
sh mvnw clean test -q
BACKEND_EXIT=$?
cd ..

if [ $BACKEND_EXIT -eq 0 ]; then
    echo "✓ Backend tests passed"
else
    echo "✗ Backend tests failed"
    echo "Check backend/target/surefire-reports/ for details"
    exit 1
fi

echo ""

# Frontend Type Check
echo "2. Running Frontend Type Check..."
echo "-----------------------------------"
cd frontend
npx tsc --noEmit
FRONTEND_EXIT=$?
cd ..

if [ $FRONTEND_EXIT -eq 0 ]; then
    echo "✓ Frontend type check passed"
else
    echo "✗ Frontend type check failed"
    exit 1
fi

echo ""
echo "==================================="
echo "✓ All tests passed!"
echo "==================================="
