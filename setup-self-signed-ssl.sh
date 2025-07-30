#!/bin/bash

# Quick self-signed SSL setup for immediate mobile testing
echo "🔐 Setting up self-signed SSL for mobile camera access..."

EC2_HOST="ec2-13-49-68-126.eu-north-1.compute.amazonaws.com"
EC2_USER="ec2-user"
SSH_KEY="secure_access_aws.pem"

echo "🔗 Connecting to EC2 to setup self-signed SSL..."

ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" << 'ENDSSH'

echo "📜 Creating self-signed SSL certificate..."

# Create SSL directory
sudo mkdir -p /etc/nginx/ssl

# Generate self-signed certificate
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/ssl/securaccess.key \
    -out /etc/nginx/ssl/securaccess.crt \
    -subj "/C=US/ST=State/L=City/O=SecurAccess/CN=ec2-13-49-68-126.eu-north-1.compute.amazonaws.com"

echo "🔧 Updating Nginx configuration for HTTPS..."

# Backup current nginx config
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup

# Create new nginx config with HTTPS
sudo tee /etc/nginx/nginx.conf > /dev/null << 'EOF'
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;

    sendfile            on;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 4096;
    client_max_body_size 50M;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    # HTTP Server (keep for compatibility)
    server {
        listen       80;
        server_name  ec2-13-49-68-126.eu-north-1.compute.amazonaws.com;

        # Frontend (Vue.js)
        location / {
            root   /home/ec2-user/securaccess-enterprise/frontend/dist;
            index  index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # Backend API (Spring Boot)
        location /api/ {
            proxy_pass http://localhost:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }

    # HTTPS Server
    server {
        listen       443 ssl;
        server_name  ec2-13-49-68-126.eu-north-1.compute.amazonaws.com;
        
        # SSL Certificate
        ssl_certificate     /etc/nginx/ssl/securaccess.crt;
        ssl_certificate_key /etc/nginx/ssl/securaccess.key;
        
        # SSL Configuration
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers on;

        # Frontend (Vue.js)
        location / {
            root   /home/ec2-user/securaccess-enterprise/frontend/dist;
            index  index.html index.htm;
            try_files $uri $uri/ /index.html;
        }

        # Backend API (Spring Boot)
        location /api/ {
            proxy_pass http://localhost:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto https;
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }
    }
}
EOF

echo "🔧 Testing Nginx configuration..."
sudo nginx -t

if [ $? -eq 0 ]; then
    echo "✅ Nginx configuration is valid"
    sudo systemctl reload nginx
    echo "✅ Nginx reloaded with HTTPS support"
else
    echo "❌ Nginx configuration error, reverting..."
    sudo cp /etc/nginx/nginx.conf.backup /etc/nginx/nginx.conf
    sudo systemctl reload nginx
fi

ENDSSH

echo ""
echo "🔍 Testing HTTPS access..."
sleep 3

if curl -k -I https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/ >/dev/null 2>&1; then
    echo "✅ HTTPS is working!"
    echo ""
    echo "📱 Mobile Access URLs (HTTPS):"
    echo "   🏠 Frontend: https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/"
    echo "   🔲 QR Display: https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/qr-display"
    echo "   🛡️  Staff Scanner: https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/staff/login"
    echo ""
    echo "⚠️  Note: You'll see a 'Not Secure' warning because it's self-signed"
    echo "📱 On mobile: Tap 'Advanced' → 'Proceed to site' to accept the certificate"
    echo "🎉 Camera will work after accepting the certificate!"
else
    echo "⚠️  HTTPS may need more time. Try the HTTPS URL manually."
fi

echo ""
echo "📋 Fallback URLs (HTTP - no camera):"
echo "   🏠 Frontend: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/"