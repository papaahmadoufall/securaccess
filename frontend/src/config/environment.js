// Environment Configuration for SecurAccess Enterprise

const environments = {
  development: {
    API_BASE_URL: 'http://localhost:8082/api',
    NODE_ENV: 'development',
    DEBUG: true
  },
  production: {
    API_BASE_URL: 'http://ec2-13-49-68-126.eu-north-1.compute.amazonaws.com/api',
    NODE_ENV: 'production', 
    DEBUG: false
  }
};

// Determine current environment
const currentEnv = process.env.NODE_ENV || 'development';

// Export current environment configuration
export const config = environments[currentEnv];
export default config;