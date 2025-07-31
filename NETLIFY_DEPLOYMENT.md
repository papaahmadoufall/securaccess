# Netlify Deployment Guide for SecurAccess Enterprise

This guide will help you deploy the SecurAccess Enterprise frontend to Netlify.

## Prerequisites

1. A Netlify account (free at https://netlify.com)
2. GitHub account (to connect your repository)

## Deployment Steps

### Option 1: Deploy via GitHub (Recommended)

1. **Push your code to GitHub:**
   ```bash
   git add .
   git commit -m "Add Netlify configuration for frontend deployment"
   git push origin main
   ```

2. **Connect to Netlify:**
   - Go to https://app.netlify.com
   - Click "New site from Git"
   - Choose GitHub and authorize Netlify
   - Select your repository
   - Netlify will automatically detect the `netlify.toml` configuration file
   - Verify the settings show:
     - **Base directory:** `frontend`
     - **Build command:** `npm install && npm run build`
     - **Publish directory:** `frontend/dist`
   - Click "Deploy site"

### Option 2: Drag and Drop Deployment

1. **Build the frontend locally:**
   ```bash
   cd frontend
   npm install
   npm run build
   ```

2. **Deploy to Netlify:**
   - Go to https://app.netlify.com
   - Drag and drop the `frontend/dist` folder to the deploy area
   - Your site will be deployed instantly

## Configuration Files Created

- `frontend/netlify.toml` - Netlify configuration with redirects and headers
- `frontend/.nvmrc` - Node.js version specification

## Post-Deployment Steps

### 1. Update Backend CORS Configuration

After deployment, you'll get a Netlify URL like `https://amazing-name-123456.netlify.app`

Update the backend CORS configuration:

```properties
# In backend/src/main/resources/application-prod.properties
app.cors.allowed-origins=https://your-netlify-app.netlify.app,http://localhost:3000,http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com
```

### 2. Redeploy Backend

After updating CORS settings:

```bash
./manual-deploy.sh
```

### 3. Configure Custom Domain (Optional)

In Netlify dashboard:
- Go to Site settings > Domain management
- Add custom domain
- Update DNS settings as instructed

## Environment Variables

The frontend automatically uses production environment when NODE_ENV=production, which points to:
- API Base URL: `http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api`

## Security Features

The Netlify deployment includes:
- SPA routing support (all routes redirect to index.html)
- Security headers (CSP, XSS protection, etc.)
- Asset caching optimization
- HTTPS by default

## Testing the Deployment

1. Visit your Netlify URL
2. Try logging in with:
   - Phone: `06 12 34 56 78`
   - PIN: `1234`
3. Verify all features work (QR code generation, camera access, etc.)

## Troubleshooting

### CORS Errors
If you see CORS errors:
1. Check that the Netlify URL is added to backend CORS configuration
2. Redeploy the backend
3. Clear browser cache

### Build Failures
If build fails on Netlify:
1. Check build logs in Netlify dashboard
2. Ensure all dependencies are in package.json
3. Verify Node.js version compatibility

### Camera Access Issues
HTTPS is required for camera access. Netlify provides HTTPS by default, so camera features should work.

## Live URLs

After deployment, you'll have:
- **Frontend:** `https://your-app-name.netlify.app`
- **Backend API:** `http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api`

## Continuous Deployment

With GitHub integration, Netlify will automatically:
- Rebuild and redeploy when you push changes
- Show deploy previews for pull requests
- Maintain deploy history and rollback capability