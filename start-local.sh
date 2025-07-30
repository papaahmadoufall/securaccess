#!/bin/bash

# Start SecurAccess Enterprise locally for development
echo "🚀 Starting SecurAccess Enterprise locally..."

# Check if ports are available
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
    echo "⚠️  Port 8080 is already in use (backend)"
fi

if lsof -Pi :3000 -sTCP:LISTEN -t >/dev/null ; then
    echo "⚠️  Port 3000 is already in use (frontend)"
fi

echo ""
echo "📋 Starting services..."

# Start backend in background
echo "🔧 Starting Spring Boot backend on port 8080..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!

# Wait for backend to start
echo "⏳ Waiting for backend to initialize..."
sleep 10

# Start frontend
echo "🎨 Starting Vue.js frontend on port 3000..."
cd ../frontend
npm install
npm run dev &
FRONTEND_PID=$!

echo ""
echo "🎉 SecurAccess Enterprise is starting locally!"
echo ""
echo "📱 Frontend: http://localhost:3000"
echo "🔗 Backend API: http://localhost:8080/api"
echo "📊 Health Check: http://localhost:8080/api/test/health"
echo "🔲 QR Display: http://localhost:3000/qr-display"
echo "🛡️  Staff Scanner: http://localhost:3000/staff/login"
echo ""
echo "✅ Camera access will work on localhost!"
echo "🔑 Staff credentials: STAFF001 / 1234"
echo ""
echo "Press Ctrl+C to stop both services"

# Wait for interrupt
trap "echo ''; echo '🛑 Stopping services...'; kill $BACKEND_PID $FRONTEND_PID; exit" INT
wait