#!/bin/bash
# Setup Git hooks for Classroom Guard

echo "Setting up Git hooks..."

# Make the pre-commit hook executable
chmod +x .githooks/pre-commit

# Configure Git to use our hooks directory
git config core.hooksPath .githooks

echo "✅ Git hooks configured successfully!"
echo ""
echo "The pre-commit hook will now run:"
echo "  - Frontend: ESLint + TypeScript checks"
echo "  - Backend: Maven tests"
echo ""
echo "To skip hooks (not recommended): git commit --no-verify"
