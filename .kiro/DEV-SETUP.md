# Development Setup - URL Dashboard

## Quick Start

### Option 1: Automated Script (Recommended)
```bash
chmod +x start-dev.sh
./start-dev.sh
```

This will start both backend and frontend automatically.

### Option 2: Manual Start (Two Terminals)

**Terminal 1 - Backend:**
```bash
cd backend
./mvnw quarkus:dev
```

Wait for the message: `Quarkus X.X.X started in X.XXXs`

**Terminal 2 - Frontend:**
```bash
cd frontend
npm run dev
```

Wait for the message: `Ready on http://localhost:3000`

## Verify Everything Works

### 1. Check Backend Health
```bash
curl http://localhost:8080/api/health
```

Expected: `{"status":"UP"}`

### 2. Test CORS
```bash
curl -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -X OPTIONS \
     -v \
     http://localhost:8080/api/urls 2>&1 | grep -i "access-control"
```

Expected to see:
```
< Access-Control-Allow-Origin: *
< Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH
```

### 3. Test API Endpoints

**Get all URLs (should be empty initially):**
```bash
curl http://localhost:8080/api/urls
```

Expected: `[]`

**Create a URL:**
```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com"}'
```

Expected: `{"id":1,"url":"https://example.com","createdAt":"2026-02-28T..."}`

**Get all URLs again:**
```bash
curl http://localhost:8080/api/urls
```

Expected: `[{"id":1,"url":"https://example.com","createdAt":"2026-02-28T..."}]`

### 4. Test Frontend

1. Open http://localhost:3000 in your browser
2. Open browser console (F12) - should see no CORS errors
3. Enter a URL in the input field
4. Click "Save URL"
5. The URL should appear in the list below
6. Refresh the page - the URL should still be there

## CORS Fix Applied

A `CorsFilter.kt` has been added to the backend that explicitly sets CORS headers on all responses. This ensures:
- All origins are allowed (`Access-Control-Allow-Origin: *`)
- All necessary methods are allowed (GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH)
- All necessary headers are allowed

## Troubleshooting

### Backend won't start
- Check if port 8080 is already in use: `lsof -i :8080`
- Check backend logs for errors
- Ensure PostgreSQL Dev Services can start (Docker must be running)

### Frontend won't start
- Check if port 3000 is already in use: `lsof -i :3000`
- Run `npm install` in the frontend directory
- Check frontend logs for errors

### CORS errors persist
1. Make sure backend is fully started before testing frontend
2. Hard refresh browser (Ctrl+Shift+R or Cmd+Shift+R)
3. Clear browser cache
4. Check browser console for the exact error message
5. Verify CORS headers are present (see "Test CORS" above)

### Database errors
- Quarkus Dev Services will automatically start a PostgreSQL container
- Ensure Docker is running
- Check backend logs for database connection errors

## What's Running

When both services are started:

**Backend (Quarkus):**
- Port: 8080
- Health: http://localhost:8080/api/health
- API: http://localhost:8080/api/urls
- Database: Automatic PostgreSQL container (Dev Services)
- Hot reload: Enabled (changes auto-reload)

**Frontend (Next.js):**
- Port: 3000
- URL: http://localhost:3000
- Hot reload: Enabled (changes auto-reload)

## Stopping Services

**If using start-dev.sh:**
- Press Ctrl+C in the terminal

**If using manual start:**
- Press Ctrl+C in each terminal
- Or: `pkill -f "quarkus:dev"` and `pkill -f "next-dev"`

## Next Steps

Once everything is working:
1. Try adding multiple URLs
2. Refresh the page to verify persistence
3. Check the database by looking at backend logs (SQL statements are logged)
4. Experiment with the API using curl or Postman
