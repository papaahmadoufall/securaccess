# SecurAccess Enterprise - AWS EC2 Deployment Guide

## Prerequisites
1. AWS EC2 free tier instance (t2.micro) running Amazon Linux 2
2. SSH key file (`secure_access_aws.pem`) for the EC2 instance
3. Security Group configured with the following inbound rules:
   - SSH (Port 22) - Your IP only
   - HTTP (Port 80) - Anywhere (0.0.0.0/0)
   - Custom TCP (Port 3000) - Anywhere (0.0.0.0/0)

## Deployment Steps

### 1. Prepare SSH Key
Place the `secure_access_aws.pem` file in the project root directory and set correct permissions:
```bash
chmod 400 secure_access_aws.pem
```

### 2. Test Connection
```bash
ssh -i "secure_access_aws.pem" ec2-user@ec2-13-49-68-126.eu-north-1.compute.amazonaws.com
```

### 3. Automated Deployment
Run the deployment script:
```bash
bash deploy-to-ec2.sh
```

### 4. Manual Steps (if script fails)

#### Install Java 17
```bash
ssh -i "secure_access_aws.pem" ec2-user@ec2-13-49-68-126.eu-north-1.compute.amazonaws.com
sudo yum update -y
sudo yum install -y java-17-amazon-corretto-devel
java -version
```

#### Install Maven
```bash
cd /tmp
wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar xzf apache-maven-3.9.6-bin.tar.gz -C /opt
sudo ln -sf /opt/apache-maven-3.9.6 /opt/maven
export PATH=/opt/maven/bin:$PATH
mvn -version
```

#### Install Node.js
```bash
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs
node --version
npm --version
```

#### Upload and Build Application
```bash
# From local machine
scp -i "secure_access_aws.pem" -r backend/ ec2-user@ec2-13-49-68-126.eu-north-1.compute.amazonaws.com:~/
scp -i "secure_access_aws.pem" -r frontend/ ec2-user@ec2-13-49-68-126.eu-north-1.compute.amazonaws.com:~/

# On EC2 instance
cd ~/backend
mvn clean package -DskipTests

cd ~/frontend  
npm install
npm run build
```

#### Start Services
```bash
# Start backend
cd ~/backend
java -jar -Dspring.profiles.active=prod target/securaccess-enterprise.war &

# Install and start Nginx for frontend
sudo yum install -y nginx
# Configure nginx (see deploy-to-ec2.sh for config)
sudo systemctl start nginx
```

## Access URLs
- Frontend: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com:3000
- Backend API: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api
- H2 Console: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api/h2-console

## Test Credentials
- Worker: Phone: 0612345678, PIN: 1234
- Host: Phone: 0687654321, PIN: 5678  
- Manager: Email: manager@securaccess.com, Password: admin123

## Troubleshooting
- Check backend logs: `sudo journalctl -u securaccess-backend -f`
- Check nginx logs: `sudo tail -f /var/log/nginx/error.log`
- Verify services: `sudo systemctl status securaccess-backend nginx`