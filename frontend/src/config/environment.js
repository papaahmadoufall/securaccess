// Environment Configuration for SecurAccess Enterprise

const environments = {
  development: {
    API_BASE_URL: 'http://localhost:8082/api',
    NODE_ENV: 'development',
    DEBUG: true
  },
  production: {
    API_BASE_URL: '/api',
    NODE_ENV: 'production', 
    DEBUG: false
  }
};

// Determine current environment
const currentEnv = process.env.NODE_ENV || 'development';
console.log('Current environment:', currentEnv);
console.log('API Base URL:', environments[currentEnv].API_BASE_URL);

// Export current environment configuration
export const config = environments[currentEnv];
export default config;