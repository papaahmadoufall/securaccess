#!/bin/bash

# SecurAccess Enterprise - AWS EC2 Deployment Script
# This script deploys the complete application to AWS EC2 free tier

set -e

echo "🚀 Starting SecurAccess Enterprise deployment to EC2..."

# EC2 connection details
EC2_HOST="ec2-13-49-68-126.eu-north-1.compute.amazonaws.com"
EC2_USER="ec2-user"
SSH_KEY="secure_access_aws.pem"

# Check if SSH key exists
if [ ! -f "$SSH_KEY" ]; then
    echo "❌ Error: SSH key file '$SSH_KEY' not found!"
    echo "Please ensure the SSH key file is in the current directory."
    exit 1
fi

# Set correct permissions for SSH key
chmod 400 "$SSH_KEY"

echo "📋 Step 1: Checking EC2 instance connection..."
ssh -o StrictHostKeyChecking=no -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "echo 'Connection successful!'"

echo "📋 Step 2: Installing Java 17 on EC2..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo yum update -y
    sudo yum install -y java-17-amazon-corretto-devel
    java -version
    echo 'JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto' | sudo tee -a /etc/environment
    export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto
"

echo "📋 Step 3: Installing Maven on EC2..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    cd /tmp
    wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
    sudo tar xzf apache-maven-3.9.6-bin.tar.gz -C /opt
    sudo ln -sf /opt/apache-maven-3.9.6 /opt/maven
    echo 'export PATH=/opt/maven/bin:\$PATH' | sudo tee -a /etc/environment
    export PATH=/opt/maven/bin:\$PATH
    mvn -version
"

echo "📋 Step 4: Installing Node.js 18 on EC2..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
    sudo yum install -y nodejs
    node --version
    npm --version
"

echo "📋 Step 5: Creating application directory..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    mkdir -p ~/securaccess-enterprise
    cd ~/securaccess-enterprise
    mkdir -p backend frontend logs
"

echo "📋 Step 6: Uploading backend files..."
scp -i "$SSH_KEY" -r backend/ "$EC2_USER@$EC2_HOST:~/securaccess-enterprise/"

echo "📋 Step 7: Uploading frontend files..."
scp -i "$SSH_KEY" -r frontend/ "$EC2_USER@$EC2_HOST:~/securaccess-enterprise/"

echo "📋 Step 8: Building backend on EC2..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    cd ~/securaccess-enterprise/backend
    export JAVA_HOME=/usr/lib/jvm/java-17-amazon-corretto
    export PATH=/opt/maven/bin:\$PATH
    mvn clean package -DskipTests
"

echo "📋 Step 9: Building frontend on EC2..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    cd ~/securaccess-enterprise/frontend
    npm install
    npm run build
"

echo "📋 Step 10: Creating production configuration..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    cd ~/securaccess-enterprise/backend/src/main/resources
    cp application.properties application-prod.properties
    
    # Update production config
    sed -i 's/server.port=8082/server.port=80/' application-prod.properties
    sed -i 's/logging.level.com.securaccess.enterprise=DEBUG/logging.level.com.securaccess.enterprise=INFO/' application-prod.properties
    sed -i 's/spring.jpa.show-sql=false/spring.jpa.show-sql=false/' application-prod.properties
"

echo "📋 Step 11: Creating systemd service for backend..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo tee /etc/systemd/system/securaccess-backend.service > /dev/null <<EOF
[Unit]
Description=SecurAccess Enterprise Backend
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/securaccess-enterprise/backend
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod target/securaccess-enterprise.war
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

    sudo systemctl daemon-reload
    sudo systemctl enable securaccess-backend
"

echo "📋 Step 12: Installing and configuring Nginx for frontend..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo yum install -y nginx
    
    sudo tee /etc/nginx/conf.d/securaccess.conf > /dev/null <<EOF
server {
    listen 3000;
    server_name _;
    root /home/ec2-user/securaccess-enterprise/frontend/dist;
    index index.html;
    
    location / {
        try_files \\\$uri \\\$uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:80;
        proxy_set_header Host \\\$host;
        proxy_set_header X-Real-IP \\\$remote_addr;
        proxy_set_header X-Forwarded-For \\\$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \\\$scheme;
    }
}
EOF

    sudo systemctl enable nginx
"

echo "📋 Step 13: Configuring security groups (manual step needed)..."
echo "Please ensure the following ports are open in your EC2 Security Group:"
echo "- Port 80 (HTTP) for backend API"
echo "- Port 3000 (HTTP) for frontend"
echo "- Port 22 (SSH) for administration"

echo "📋 Step 14: Starting services..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "
    sudo systemctl start securaccess-backend
    sudo systemctl start nginx
    
    # Check service status
    sudo systemctl status securaccess-backend --no-pager
    sudo systemctl status nginx --no-pager
"

echo "✅ Deployment completed!"
echo "🌐 Frontend: http://$EC2_HOST:3000"
echo "🔗 Backend API: http://$EC2_HOST:80/api"
echo "📊 H2 Console: http://$EC2_HOST:80/api/h2-console"

echo "🔍 To check logs:"
echo "Backend: ssh -i $SSH_KEY $EC2_USER@$EC2_HOST 'sudo journalctl -u securaccess-backend -f'"
echo "Nginx: ssh -i $SSH_KEY $EC2_USER@$EC2_HOST 'sudo tail -f /var/log/nginx/error.log'"