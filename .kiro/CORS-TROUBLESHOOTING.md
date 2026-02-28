# CORS Troubleshooting Guide

## Problem
Frontend at http://localhost:3000 cannot connect to backend at http://localhost:8080 due to CORS errors.

## Solution Options

### Option 1: Restart Backend (Recommended)
The CORS configuration has been updated in `backend/src/main/resources/application.properties`. You need to restart the backend for changes to take effect.

**If running with docker-compose:**
```bash
docker-compose down
docker-compose up --build backend
```

**If running backend locally:**
```bash
cd backend
./mvnw quarkus:dev
```

### Option 2: Verify Backend is Running
Check if the backend is actually running and accessible:

```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{"status":"UP"}
```

If this fails, the backend is not running or not accessible on port 8080.

### Option 3: Check Browser Console
Open your browser's developer console (F12) and look for the exact CORS error message. It should tell you:
- What origin is being blocked
- What headers are missing
- What method is being used

### Option 4: Test Backend Directly
Test the backend API directly to ensure it's working:

```bash
# Test GET
curl http://localhost:8080/api/urls

# Test POST
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com"}'
```

### Option 5: Run Everything in Docker
If you want to avoid CORS issues entirely, run both frontend and backend in Docker:

```bash
docker-compose up --build
```

Then access the frontend at http://localhost:3000

## Current CORS Configuration

The backend is configured to allow:
- Origins: `http://localhost:3000`, `http://frontend:3000`, `http://127.0.0.1:3000`
- Methods: `GET`, `POST`, `PATCH`, `DELETE`, `OPTIONS`, `PUT`
- Headers: `accept`, `authorization`, `content-type`, `x-requested-with`, `origin`

## Common Issues

### Issue: "No 'Access-Control-Allow-Origin' header"
**Cause:** Backend CORS not configured or backend not running
**Fix:** Restart backend after CORS configuration update

### Issue: "CORS preflight request failed"
**Cause:** OPTIONS method not allowed
**Fix:** Already included in configuration, restart backend

### Issue: "Network error" or "Failed to fetch"
**Cause:** Backend not running or wrong URL
**Fix:** Verify backend is running on port 8080

### Issue: Frontend shows "Connecting..." forever
**Cause:** Backend not responding
**Fix:** Check backend logs and ensure it started successfully

## Verification Steps

1. **Check backend is running:**
   ```bash
   curl http://localhost:8080/api/health
   ```

2. **Check CORS headers are present:**
   ```bash
   curl -H "Origin: http://localhost:3000" \
        -H "Access-Control-Request-Method: GET" \
        -H "Access-Control-Request-Headers: Content-Type" \
        -X OPTIONS \
        -v \
        http://localhost:8080/api/urls
   ```
   
   Look for these headers in the response:
   - `Access-Control-Allow-Origin: http://localhost:3000`
   - `Access-Control-Allow-Methods: GET,POST,PATCH,DELETE,OPTIONS,PUT`

3. **Test the actual API call:**
   ```bash
   curl -H "Origin: http://localhost:3000" \
        -H "Content-Type: application/json" \
        http://localhost:8080/api/urls
   ```

## Next Steps

After restarting the backend:
1. Refresh your browser (Ctrl+Shift+R or Cmd+Shift+R)
2. Open the browser console to check for errors
3. Try saving a URL in the frontend
4. If still failing, share the exact error message from the console
