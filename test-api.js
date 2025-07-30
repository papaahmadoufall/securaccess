// Simple Node.js script to test SecurAccess Enterprise API
const https = require('https');
const http = require('http');

const API_BASE_URL = 'http://localhost:8090/api';

// Colors for console output
const colors = {
    green: '\x1b[32m',
    red: '\x1b[31m',
    yellow: '\x1b[33m',
    blue: '\x1b[34m',
    reset: '\x1b[0m',
    bold: '\x1b[1m'
};

function log(message, color = 'reset') {
    console.log(`${colors[color]}${message}${colors.reset}`);
}

function makeRequest(method, path, data = null) {
    return new Promise((resolve, reject) => {
        const url = new URL(API_BASE_URL + path);
        const options = {
            hostname: url.hostname,
            port: url.port,
            path: url.pathname + url.search,
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            }
        };

        if (data) {
            const jsonData = JSON.stringify(data);
            options.headers['Content-Length'] = Buffer.byteLength(jsonData);
        }

        const req = http.request(options, (res) => {
            let responseData = '';
            
            res.on('data', (chunk) => {
                responseData += chunk;
            });
            
            res.on('end', () => {
                try {
                    const response = {
                        status: res.statusCode,
                        headers: res.headers,
                        data: responseData ? JSON.parse(responseData) : null
                    };
                    resolve(response);
                } catch (e) {
                    resolve({
                        status: res.statusCode,
                        headers: res.headers,
                        data: responseData
                    });
                }
            });
        });

        req.on('error', (error) => {
            reject(error);
        });

        if (data) {
            req.write(JSON.stringify(data));
        }
        
        req.end();
    });
}

async function testAPI() {
    log('🚀 Testing SecurAccess Enterprise API', 'bold');
    log('=' .repeat(50), 'blue');
    
    try {
        // Test 1: Health Check
        log('\n1️⃣  Testing Health Check...', 'yellow');
        try {
            const healthResponse = await makeRequest('GET', '/test/health');
            if (healthResponse.status === 200) {
                log('✅ Health check: OK', 'green');
            } else {
                log(`❌ Health check failed: ${healthResponse.status}`, 'red');
            }
        } catch (error) {
            log(`❌ Health check error: ${error.message}`, 'red');
            log('⚠️  Backend server might not be running on port 8090', 'yellow');
            log('💡 Try starting the backend server first', 'blue');
            return;
        }

        // Test 2: Worker Authentication
        log('\n2️⃣  Testing Worker Authentication...', 'yellow');
        const workerAuth = await makeRequest('POST', '/auth/login/worker', {
            phone: '0612345678',
            pin: '1234'
        });
        
        if (workerAuth.status === 200 && workerAuth.data.success) {
            log('✅ Worker login: SUCCESS', 'green');
            log(`   Token: ${workerAuth.data.token.substring(0, 30)}...`, 'blue');
        } else {
            log(`❌ Worker login failed: ${workerAuth.status}`, 'red');
            console.log('   Response:', workerAuth.data);
        }

        // Test 3: Host Authentication
        log('\n3️⃣  Testing Host Authentication...', 'yellow');
        const hostAuth = await makeRequest('POST', '/auth/login/host', {
            phone: '0687654321',
            pin: '5678'
        });
        
        if (hostAuth.status === 200 && hostAuth.data.success) {
            log('✅ Host login: SUCCESS', 'green');
            log(`   Token: ${hostAuth.data.token.substring(0, 30)}...`, 'blue');
        } else {
            log(`❌ Host login failed: ${hostAuth.status}`, 'red');
            console.log('   Response:', hostAuth.data);
        }

        // Test 4: Manager Authentication
        log('\n4️⃣  Testing Manager Authentication...', 'yellow');
        const managerAuth = await makeRequest('POST', '/auth/login/manager', {
            email: 'manager@entreprise.com',
            password: 'manager123'
        });
        
        if (managerAuth.status === 200 && managerAuth.data.success) {
            log('✅ Manager login: SUCCESS', 'green');
            log(`   Token: ${managerAuth.data.token.substring(0, 30)}...`, 'blue');
        } else {
            log(`❌ Manager login failed: ${managerAuth.status}`, 'red');
            console.log('   Response:', managerAuth.data);
        }

        // Test 5: Invalid Credentials
        log('\n5️⃣  Testing Invalid Credentials...', 'yellow');
        const invalidAuth = await makeRequest('POST', '/auth/login/worker', {
            phone: '0612345678',
            pin: '9999'
        });
        
        if (invalidAuth.status === 401) {
            log('✅ Invalid credentials rejected: SUCCESS', 'green');
        } else {
            log(`❌ Invalid credentials test failed: ${invalidAuth.status}`, 'red');
        }

        // Test 6: QR Code Generation
        log('\n6️⃣  Testing QR Code Generation...', 'yellow');
        const qrResponse = await makeRequest('GET', '/workers/1/qr-code/generate');
        
        if (qrResponse.status === 200 && qrResponse.data.success) {
            log('✅ QR Code generation: SUCCESS', 'green');
            log(`   Code: ${qrResponse.data.qrCode.code}`, 'blue');
        } else {
            log(`❌ QR Code generation failed: ${qrResponse.status}`, 'red');
        }

        // Test 7: Access History
        log('\n7️⃣  Testing Access History...', 'yellow');
        const historyResponse = await makeRequest('GET', '/workers/1/access-history?limit=5');
        
        if (historyResponse.status === 200 && historyResponse.data.success) {
            log('✅ Access history: SUCCESS', 'green');
            log(`   Records found: ${historyResponse.data.total}`, 'blue');
        } else {
            log(`❌ Access history failed: ${historyResponse.status}`, 'red');
        }

        // Test 8: Input Validation
        log('\n8️⃣  Testing Input Validation...', 'yellow');
        const validationTest = await makeRequest('POST', '/auth/login/worker', {
            phone: 'invalid-phone',
            pin: '123'
        });
        
        if (validationTest.status === 400) {
            log('✅ Input validation: SUCCESS', 'green');
        } else {
            log(`❌ Input validation failed: ${validationTest.status}`, 'red');
        }

        log('\n' + '=' .repeat(50), 'blue');
        log('🎉 API Testing Complete!', 'bold');
        
    } catch (error) {
        log(`\n💥 Test suite error: ${error.message}`, 'red');
    }
}

// CURL command examples
function showCurlExamples() {
    log('\n📝 CURL Examples for Manual Testing:', 'bold');
    log('=' .repeat(50), 'blue');
    
    const examples = [
        {
            title: 'Health Check',
            command: `curl -X GET "${API_BASE_URL}/test/health"`
        },
        {
            title: 'Worker Login',
            command: `curl -X POST "${API_BASE_URL}/auth/login/worker" \\
  -H "Content-Type: application/json" \\
  -d '{"phone":"0612345678","pin":"1234"}'`
        },
        {
            title: 'Host Login',
            command: `curl -X POST "${API_BASE_URL}/auth/login/host" \\
  -H "Content-Type: application/json" \\
  -d '{"phone":"0687654321","pin":"5678"}'`
        },
        {
            title: 'Manager Login',
            command: `curl -X POST "${API_BASE_URL}/auth/login/manager" \\
  -H "Content-Type: application/json" \\
  -d '{"email":"manager@entreprise.com","password":"manager123"}'`
        },
        {
            title: 'Generate QR Code',
            command: `curl -X GET "${API_BASE_URL}/workers/1/qr-code/generate"`
        },
        {
            title: 'Get Access History',
            command: `curl -X GET "${API_BASE_URL}/workers/1/access-history?limit=10"`
        }
    ];
    
    examples.forEach((example, index) => {
        log(`\n${index + 1}. ${example.title}:`, 'yellow');
        log(example.command, 'blue');
    });
    
    log('\n💡 Tip: Add -v flag to curl for verbose output', 'green');
    log('💡 Tip: Add | jq for pretty JSON formatting', 'green');
}

// Run tests
if (require.main === module) {
    testAPI().then(() => {
        showCurlExamples();
    });
}

module.exports = { testAPI, makeRequest };