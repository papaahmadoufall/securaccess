@echo off
echo 🚀 Deploying SecurAccess Enterprise with Docker...
echo.

echo 📦 Building backend WAR file...
cd backend
call mvn clean package -q
if %ERRORLEVEL% neq 0 (
    echo ❌ Backend build failed!
    pause
    exit /b 1
)
cd ..

echo 🐳 Starting Docker containers...
docker-compose up --build -d

echo.
echo ✅ Deployment completed!
echo.
echo 🌐 Application URLs:
echo   Frontend:    http://localhost:3000
echo   Backend API: http://localhost:8080/securaccess-enterprise/api/test
echo   Database:    localhost:3308
echo.
echo 📋 To view logs: docker-compose logs -f
echo 🛑 To stop:     docker-compose down
echo.
pause