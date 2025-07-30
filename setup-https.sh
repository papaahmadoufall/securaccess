#!/bin/bash

# Setup HTTPS for SecurAccess Enterprise on EC2
echo "🔐 Setting up HTTPS for mobile camera access..."

EC2_HOST="ec2-13-49-68-126.eu-north-1.compute.amazonaws.com"
EC2_USER="ec2-user"
SSH_KEY="secure_access_aws.pem"

echo "🔗 Connecting to EC2 to setup SSL certificates..."

ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" << 'ENDSSH'

echo "📦 Installing Certbot for Let's Encrypt SSL..."
sudo yum update -y
sudo yum install -y certbot python3-certbot-nginx

echo "🔧 Updating Nginx configuration for HTTPS..."

# Backup current nginx config
sudo cp /etc/nginx/nginx.conf /etc/nginx/nginx.conf.backup

# Create new nginx config with HTTP and HTTPS
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

    # HTTP Server (redirects to HTTPS)
    server {
        listen       80;
        server_name  ec2-13-49-68-126.eu-north-1.compute.amazonaws.com;
        
        # Redirect all HTTP requests to HTTPS
        return 301 https://$server_name$request_uri;
    }

    # HTTPS Server
    server {
        listen       443 ssl http2;
        server_name  ec2-13-49-68-126.eu-north-1.compute.amazonaws.com;
        
        # SSL Certificate paths (will be configured by certbot)
        ssl_certificate     /etc/letsencrypt/live/ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/fullchain.pem;
        ssl_certificate_key /etc/letsencrypt/live/ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/privkey.pem;
        
        # SSL Configuration
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        
        # Security headers
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
        add_header X-Frame-Options DENY;
        add_header X-Content-Type-Options nosniff;
        add_header X-XSS-Protection "1; mode=block";

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
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }

        # Health check
        location /health {
            proxy_pass http://localhost:8080/api/test/health;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
EOF

echo "🔧 Testing Nginx configuration..."
sudo nginx -t

if [ $? -eq 0 ]; then
    echo "✅ Nginx configuration is valid"
    
    echo "🔐 Obtaining SSL certificate from Let's Encrypt..."
    sudo certbot --nginx -d ec2-13-49-68-126.eu-north-1.compute.amazonaws.com --non-interactive --agree-tos --email admin@securaccess.com
    
    if [ $? -eq 0 ]; then
        echo "✅ SSL certificate obtained successfully!"
        
        # Restart nginx
        sudo systemctl reload nginx
        sudo systemctl restart nginx
        
        echo "🔄 Setting up auto-renewal for SSL certificate..."
        sudo systemctl enable certbot-renew.timer
        sudo systemctl start certbot-renew.timer
        
        echo ""
        echo "🎉 HTTPS setup completed successfully!"
        echo ""
        echo "🌐 Your application is now available at:"
        echo "   https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/"
        echo ""
        echo "📱 Mobile camera access will now work!"
        echo "🔐 SSL certificate will auto-renew every 90 days"
        
    else
        echo "❌ Failed to obtain SSL certificate"
        echo "🔧 Reverting to HTTP-only configuration..."
        sudo cp /etc/nginx/nginx.conf.backup /etc/nginx/nginx.conf
        sudo systemctl reload nginx
    fi
else
    echo "❌ Nginx configuration error"
    echo "🔧 Reverting to backup configuration..."
    sudo cp /etc/nginx/nginx.conf.backup /etc/nginx/nginx.conf
fi

ENDSSH

echo ""
echo "🔍 Testing HTTPS access..."
sleep 5

if curl -k -I https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/ >/dev/null 2>&1; then
    echo "✅ HTTPS is working!"
    echo ""
    echo "📱 Mobile Access URLs:"
    echo "   🏠 Frontend: https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/"
    echo "   🔲 QR Display: https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/qr-display"
    echo "   🛡️  Staff Scanner: https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/staff/login"
    echo ""
    echo "🎉 Camera scanning will now work on mobile devices!"
else
    echo "⚠️  HTTPS setup may need more time to propagate"
    echo "🔧 Try accessing https://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/ in a few minutes"
fi