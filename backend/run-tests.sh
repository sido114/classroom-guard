#!/bin/bash
# Simple test runner script

cd "$(dirname "$0")"

echo "Running Quarkus tests..."
echo "========================"

# Run Maven test
sh mvnw clean test

EXIT_CODE=$?

if [ $EXIT_CODE -eq 0 ]; then
    echo ""
    echo "✓ All tests passed!"
    exit 0
else
    echo ""
    echo "✗ Tests failed with exit code: $EXIT_CODE"
    echo "Check target/surefire-reports/ for details"
    exit $EXIT_CODE
fi
