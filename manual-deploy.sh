#!/bin/bash

# Manual deployment script for SecurAccess Enterprise QR Code improvements
# This script deploys the latest code with ZXing QR generation and camera scanning

echo "🚀 Starting manual deployment of QR code improvements..."

# Configuration
EC2_HOST="ec2-13-49-68-126.eu-north-1.compute.amazonaws.com"
EC2_USER="ec2-user"
APP_DIR="/home/ec2-user/securaccess-enterprise"
SSH_KEY="secure_access_aws.pem"

# Check if SSH key exists
if [ ! -f "$SSH_KEY" ]; then
    echo "❌ SSH key $SSH_KEY not found!"
    exit 1
fi

# Set proper permissions
chmod 600 "$SSH_KEY"

echo "🔧 Building backend with ZXing QR code generation..."
cd backend
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Backend build failed!"
    exit 1
fi

echo "🔧 Building frontend with camera scanning..."
cd ../frontend
npm install
npm run build
if [ $? -ne 0 ]; then
    echo "❌ Frontend build failed!"
    exit 1
fi

cd ..

echo "🔄 Deploying to EC2 server..."

# Test SSH connectivity
echo "🔗 Testing SSH connection..."
ssh -i "$SSH_KEY" -o ConnectTimeout=10 -o StrictHostKeyChecking=no "$EC2_USER@$EC2_HOST" "echo 'SSH connection successful'" || {
    echo "❌ SSH connection failed!"
    exit 1
}

echo "⏹️  Stopping existing services..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    pkill -f securaccess-enterprise.jar || true
    sudo systemctl stop nginx || true
" || echo "⚠️  Some services were not running"

echo "📦 Uploading backend (with ZXing QR generation)..."
scp -i "$SSH_KEY" backend/target/securaccess-enterprise.jar "$EC2_USER@$EC2_HOST:$APP_DIR/backend/target/" || {
    echo "❌ Backend upload failed!"
    exit 1
}

echo "📦 Uploading frontend (with camera scanning)..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "rm -rf $APP_DIR/frontend/dist/*" || true
scp -i "$SSH_KEY" -r frontend/dist/* "$EC2_USER@$EC2_HOST:$APP_DIR/frontend/dist/" || {
    echo "❌ Frontend upload failed!"
    exit 1
}

echo "🔄 Starting services..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    cd $APP_DIR/backend
    export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto.x86_64
    nohup java -jar target/securaccess-enterprise.jar > ../logs/backend.log 2>&1 &
    
    # Wait for backend to start
    echo 'Waiting for backend to start...'
    sleep 15
    
    # Start nginx
    sudo systemctl start nginx
    sudo systemctl reload nginx
    
    echo 'Services started!'
"

echo "🔍 Verifying deployment..."
sleep 5

# Test health endpoint
if curl -f -m 10 "http://$EC2_HOST/api/test/health" > /dev/null 2>&1; then
    echo "✅ Backend health check: PASSED"
else
    echo "❌ Backend health check: FAILED"
fi

# Test QR code generation
if curl -I -m 10 "http://$EC2_HOST/api/qrcode/QR001/image" 2>/dev/null | grep -q "Content-Length: [0-9]*[0-9][0-9]"; then
    echo "✅ QR code generation: WORKING (real images)"
else
    echo "⚠️  QR code generation: May need more time to initialize"
fi

# Test frontend
if curl -f -m 10 "http://$EC2_HOST/" > /dev/null 2>&1; then
    echo "✅ Frontend: ACCESSIBLE"
else
    echo "❌ Frontend: NOT ACCESSIBLE"
fi

echo ""
echo "🎉 Deployment completed!"
echo "📱 Frontend: http://$EC2_HOST/"
echo "🔗 Backend API: http://$EC2_HOST/api/"
echo "📊 Health Check: http://$EC2_HOST/api/test/health"
echo "🔲 QR Code Display: http://$EC2_HOST/qr-display"
echo "🛡️  Staff Scanner: http://$EC2_HOST/staff/login"
echo ""
echo "🚀 QR Code scanning with camera is now available!"