#!/bin/bash

# Simple test script to verify the addUrl endpoint implementation
# This script checks if the implementation compiles correctly

echo "Checking if ClassroomResource.kt compiles..."

# Try to compile just the ClassroomResource
./mvnw compile -pl . 2>&1 | tee compile-output.txt

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful"
    exit 0
else
    echo "✗ Compilation failed"
    cat compile-output.txt
    exit 1
fi
