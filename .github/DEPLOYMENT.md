# 🚀 GitHub Actions CI/CD Setup for SecurAccess Enterprise

This guide will help you set up automated deployment to AWS EC2 using GitHub Actions.

## 📋 Prerequisites

1. **GitHub Repository** with your SecurAccess Enterprise code
2. **AWS EC2 Instance** (already configured: `ec2-13-49-68-126.eu-north-1.compute.amazonaws.com`)
3. **SSH Key** for EC2 access (`secure_access_aws.pem`)

## 🔧 Setup Instructions

### Step 1: Repository Setup

1. **Create a GitHub repository** (if not already done):
   ```bash
   git init
   git add .
   git commit -m "Initial commit: SecurAccess Enterprise"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/securaccess-enterprise.git
   git push -u origin main
   ```

### Step 2: Configure GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add the following secrets:

#### Required Secrets:
- **`EC2_PRIVATE_KEY`**: Content of your `secure_access_aws.pem` file
  ```bash
  # Copy the entire content of your SSH key file
  cat secure_access_aws.pem
  ```

#### Optional Secrets (for future enhancements):
- **`EC2_HOST`**: `ec2-13-49-68-126.eu-north-1.compute.amazonaws.com`
- **`EC2_USER`**: `ec2-user`
- **`JWT_SECRET`**: Your production JWT secret
- **`DATABASE_URL`**: Production database URL (if switching from H2)

### Step 3: Workflow Files Created

The following workflow files have been created:

1. **`.github/workflows/deploy.yml`** - Main deployment pipeline
2. **`.github/workflows/test.yml`** - Testing pipeline

### Step 4: How the CI/CD Works

#### On Every Push/PR:
- ✅ Runs tests for both frontend and backend
- ✅ Builds both applications
- ✅ Performs security scans
- ✅ Generates test reports

#### On Push to `main`/`master`:
- 🚀 **Deploys to EC2** automatically
- 📦 Builds optimized production versions
- 🔄 Stops running services
- ⬆️ Uploads new versions
- 🔄 Restarts services
- ✅ Verifies deployment health

## 📁 Project Structure Expected

```
securaccess-enterprise/
├── .github/
│   └── workflows/
│       ├── deploy.yml
│       └── test.yml
├── backend/
│   ├── src/
│   ├── target/
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── dist/
│   ├── package.json
│   └── package-lock.json
└── README.md
```

## 🔄 Deployment Process

### Automatic Deployment
1. **Push code** to `main` or `master` branch
2. **GitHub Actions** automatically:
   - Runs all tests
   - Builds applications
   - Creates backup on EC2
   - Deploys new version
   - Restarts services
   - Verifies deployment

### Manual Deployment
You can also trigger deployment manually:
1. Go to **Actions** tab in your GitHub repository
2. Select **"Deploy SecurAccess Enterprise to AWS EC2"**
3. Click **"Run workflow"**
4. Select branch and click **"Run workflow"**

## 🛡️ Security Features

- ✅ **SSH Key Encryption**: Private key stored as GitHub secret
- ✅ **Host Verification**: SSH host key verification
- ✅ **Backup Creation**: Automatic backup before deployment
- ✅ **Rollback Capability**: Previous versions preserved
- ✅ **Health Checks**: Deployment verification
- ✅ **Security Scanning**: OWASP dependency check

## 📊 Monitoring Deployment

### GitHub Actions Dashboard
- View deployment status in **Actions** tab
- See detailed logs for each step
- Monitor test results and coverage

### EC2 Monitoring
- **Application Logs**: `/home/ec2-user/securaccess-enterprise/logs/backend.log`
- **Nginx Logs**: `/var/log/nginx/error.log`
- **Service Status**: `systemctl status nginx`

## 🚨 Troubleshooting

### Common Issues:

1. **SSH Connection Failed**
   - Verify `EC2_PRIVATE_KEY` secret is correctly set
   - Check EC2 Security Group allows SSH (port 22)

2. **Deployment Failed**
   - Check EC2 instance is running
   - Verify disk space: `df -h`
   - Check service logs

3. **Health Check Failed**
   - Verify backend is starting: `ps aux | grep java`
   - Check application logs for errors
   - Ensure ports 8080 and 80 are available

### Manual Rollback:
```bash
ssh -i secure_access_aws.pem ec2-user@ec2-13-49-68-126.eu-north-1.compute.amazonaws.com
sudo cp -r /home/ec2-user/securaccess-enterprise_backup_YYYYMMDD_HHMMSS/* /home/ec2-user/securaccess-enterprise/
sudo systemctl restart nginx
```

## 🎯 Next Steps

1. **Set up staging environment** for testing
2. **Add database migrations** for production
3. **Configure SSL/HTTPS** with Let's Encrypt
4. **Add monitoring** with CloudWatch
5. **Set up notifications** for deployment status

## 📞 Support

- **Application URL**: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com
- **API Documentation**: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api
- **H2 Console**: http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api/h2-console

---

## 🚀 Quick Start Checklist

- [ ] Create GitHub repository
- [ ] Add `EC2_PRIVATE_KEY` secret
- [ ] Push code to trigger first deployment
- [ ] Verify deployment at application URL
- [ ] Monitor logs for any issues
- [ ] Test all application features