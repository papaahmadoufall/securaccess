// Test script for API endpoints
const API_BASE_URL = 'http://localhost:8090/api';

async function testEndpoint(method, endpoint, data = null) {
    try {
        const config = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };
        
        if (data) {
            config.body = JSON.stringify(data);
        }
        
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        const result = await response.json();
        
        console.log(`${method} ${endpoint}:`, response.status, result);
        return result;
    } catch (error) {
        console.error(`Error testing ${method} ${endpoint}:`, error.message);
        return null;
    }
}

async function runTests() {
    console.log('Testing API endpoints...\n');
    
    // Test health check
    await testEndpoint('GET', '/test/health');
    
    // Test worker login
    const workerLogin = await testEndpoint('POST', '/auth/login/worker', {
        phone: '0612345678',
        pin: '1234'
    });
    
    // Test manager login
    const managerLogin = await testEndpoint('POST', '/auth/login/manager', {
        email: 'manager@entreprise.com',
        password: 'manager123'
    });
    
    // Test workers list
    await testEndpoint('GET', '/workers');
    
    // Test hosts list  
    await testEndpoint('GET', '/hosts');
    
    // Test dashboard stats
    await testEndpoint('GET', '/stats/dashboard');
    
    // Test worker QR generation
    if (workerLogin && workerLogin.success) {
        await testEndpoint('GET', `/workers/${workerLogin.user.id}/qr-code/generate`);
        await testEndpoint('GET', `/workers/${workerLogin.user.id}/access-history`);
    }
    
    console.log('\nTests completed!');
}

runTests().catch(console.error);