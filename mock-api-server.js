// Mock API Server for SecurAccess Enterprise
// Run with: node mock-api-server.js

const express = require('express');
const cors = require('cors');
const app = express();
const PORT = 8090;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Request logging
app.use((req, res, next) => {
    console.log(`${new Date().toISOString()} - ${req.method} ${req.path}`);
    next();
});

// Mock data
const mockWorkers = {
    '0612345678:1234': {
        id: 1,
        name: 'Jean Dupont',
        phone: '0612345678',
        role: 'worker',
        department: 'IT',
        isActive: true
    },
    '0698765432:9876': {
        id: 2,
        name: 'Pierre Durand',
        phone: '0698765432',
        role: 'worker',
        department: 'Marketing',
        isActive: true
    }
};

const mockHosts = {
    '0687654321:5678': {
        id: 1,
        name: 'Marie Visiteur',
        phone: '0687654321',
        role: 'host',
        location: 'Salle de réunion A',
        isActive: true,
        accessStartDate: new Date().toISOString().split('T')[0],
        accessEndDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    }
};

const mockManagers = {
    'manager@entreprise.com:manager123': {
        id: 1,
        name: 'Responsable Manager',
        email: 'manager@entreprise.com',
        role: 'manager'
    }
};

// Helper functions
function validatePhone(phone) {
    return /^0[6-7][0-9]{8}$/.test(phone);
}

function validatePin(pin) {
    return /^[0-9]{4}$/.test(pin);
}

function validateEmail(email) {
    return /^[A-Za-z0-9+_.-]+@(.+)$/.test(email);
}

function sanitizeInput(input) {
    if (!input) return null;
    return input.toString().trim().replace(/[<>"'&]/g, '');
}

function generateMockToken(user) {
    return `MOCK_TOKEN_${user.role}_${Date.now()}`;
}

function createErrorResponse(message, status = 400) {
    return {
        success: false,
        error: message,
        timestamp: Date.now()
    };
}

// API Endpoints

// Health check
app.get('/api/test/health', (req, res) => {
    res.json({
        success: true,
        message: 'SecurAccess Enterprise API is running',
        timestamp: Date.now(),
        version: '1.0.0'
    });
});

// Authentication endpoints
app.post('/api/auth/login/worker', (req, res) => {
    try {
        const { phone, pin } = req.body;
        
        const cleanPhone = sanitizeInput(phone);
        const cleanPin = sanitizeInput(pin);
        
        if (!validatePhone(cleanPhone)) {
            return res.status(400).json(createErrorResponse('Numéro de téléphone invalide'));
        }
        
        if (!validatePin(cleanPin)) {
            return res.status(400).json(createErrorResponse('Code PIN invalide'));
        }
        
        const key = `${cleanPhone}:${cleanPin}`;
        const worker = mockWorkers[key];
        
        if (worker) {
            res.json({
                success: true,
                user: worker,
                token: generateMockToken(worker),
                expiresIn: 28800
            });
        } else {
            res.status(401).json(createErrorResponse('Numéro de téléphone ou code PIN incorrect'));
        }
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur du serveur'));
    }
});

app.post('/api/auth/login/host', (req, res) => {
    try {
        const { phone, pin } = req.body;
        
        const cleanPhone = sanitizeInput(phone);
        const cleanPin = sanitizeInput(pin);
        
        if (!validatePhone(cleanPhone)) {
            return res.status(400).json(createErrorResponse('Numéro de téléphone invalide'));
        }
        
        if (!validatePin(cleanPin)) {
            return res.status(400).json(createErrorResponse('Code PIN invalide'));
        }
        
        const key = `${cleanPhone}:${cleanPin}`;
        const host = mockHosts[key];
        
        if (host) {
            res.json({
                success: true,
                user: host,
                token: generateMockToken(host),
                expiresIn: 28800
            });
        } else {
            res.status(401).json(createErrorResponse('Numéro de téléphone ou code PIN incorrect'));
        }
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur du serveur'));
    }
});

app.post('/api/auth/login/manager', (req, res) => {
    try {
        const { email, password } = req.body;
        
        const cleanEmail = sanitizeInput(email);
        const cleanPassword = sanitizeInput(password);
        
        if (!validateEmail(cleanEmail)) {
            return res.status(400).json(createErrorResponse('Email invalide'));
        }
        
        if (!cleanPassword || cleanPassword.length < 6) {
            return res.status(400).json(createErrorResponse('Mot de passe invalide'));
        }
        
        const key = `${cleanEmail}:${cleanPassword}`;
        const manager = mockManagers[key];
        
        if (manager) {
            res.json({
                success: true,
                user: manager,
                token: generateMockToken(manager),
                expiresIn: 28800
            });
        } else {
            res.status(401).json(createErrorResponse('Email ou mot de passe incorrect'));
        }
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur du serveur'));
    }
});

// QR Code endpoints
app.get('/api/workers/:workerId/qr-code/generate', (req, res) => {
    try {
        const { workerId } = req.params;
        
        const qrCode = {
            id: `qr_${Date.now()}`,
            code: `WKR-${Date.now().toString(36).toUpperCase()}`,
            workerId: workerId,
            generatedAt: new Date().toISOString(),
            expiresAt: new Date(Date.now() + 8 * 60 * 60 * 1000).toISOString(),
            isValid: true,
            imageBase64: 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='
        };
        
        res.json({
            success: true,
            qrCode: qrCode
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la génération du QR code'));
    }
});

// Access history endpoint
app.get('/api/workers/:workerId/access-history', (req, res) => {
    try {
        const { workerId } = req.params;
        const { limit = 50, type } = req.query;
        
        // Generate mock access history
        const history = [];
        for (let i = 0; i < Math.min(parseInt(limit), 10); i++) {
            const accessType = i % 2 === 0 ? 'entry' : 'exit';
            if (!type || type === accessType) {
                history.push({
                    id: `access_${Date.now()}_${i}`,
                    type: accessType,
                    location: ['Entrée Principale', 'Salle de Réunion A', 'Cafétéria'][i % 3],
                    timestamp: new Date(Date.now() - i * 2 * 60 * 60 * 1000).toISOString(),
                    success: true,
                    qrCode: `WKR-${Date.now() - i * 1000}`
                });
            }
        }
        
        res.json({
            success: true,
            history: history,
            total: history.length
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération de l\'historique'));
    }
});

// Worker profile endpoint
app.get('/api/workers/:workerId/profile', (req, res) => {
    try {
        const { workerId } = req.params;
        
        // Find worker by ID
        const worker = Object.values(mockWorkers).find(w => w.id == workerId);
        
        if (worker) {
            res.json({
                success: true,
                worker: {
                    ...worker,
                    createdAt: '2024-01-15T10:30:00',
                    lastAccess: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
                }
            });
        } else {
            res.status(404).json(createErrorResponse('Employé non trouvé'));
        }
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération du profil'));
    }
});

// Access logging endpoint
app.post('/api/workers/:workerId/access-log', (req, res) => {
    try {
        const { workerId } = req.params;
        const accessData = req.body;
        
        const accessLog = {
            id: `log_${Date.now()}`,
            workerId: workerId,
            type: accessData.type,
            location: accessData.location,
            timestamp: new Date().toISOString(),
            qrCode: accessData.qrCode,
            success: true
        };
        
        res.json({
            success: true,
            accessLog: accessLog,
            message: 'Accès enregistré avec succès'
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de l\'enregistrement de l\'accès'));
    }
});

// Token validation endpoint
app.get('/api/auth/validate-token', (req, res) => {
    try {
        const authHeader = req.headers.authorization;
        
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json(createErrorResponse('Token manquant'));
        }
        
        const token = authHeader.substring(7);
        
        if (token.startsWith('MOCK_TOKEN_')) {
            res.json({
                valid: true,
                message: 'Token valide'
            });
        } else {
            res.status(401).json(createErrorResponse('Token invalide'));
        }
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur de validation du token'));
    }
});

// Logout endpoint
app.post('/api/auth/logout', (req, res) => {
    res.json({
        success: true,
        message: 'Déconnexion réussie'
    });
});

// Host QR Code endpoints
app.get('/api/hosts/:hostId/qr-code/generate', (req, res) => {
    try {
        const { hostId } = req.params;
        
        const qrCode = {
            id: `qr_host_${Date.now()}`,
            code: `HST-${Date.now().toString(36).toUpperCase()}`,
            hostId: hostId,
            generatedAt: new Date().toISOString(),
            expiresAt: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(), // 4 hours
            isValid: true,
            imageBase64: 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='
        };
        
        res.json({
            success: true,
            qrCode: qrCode
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la génération du QR code'));
    }
});

// Host access history endpoint
app.get('/api/hosts/:hostId/access-history', (req, res) => {
    try {
        const { hostId } = req.params;
        const { limit = 50, type } = req.query;
        
        // Generate mock access history for hosts
        const history = [];
        for (let i = 0; i < Math.min(parseInt(limit), 8); i++) {
            const accessType = i % 2 === 0 ? 'entry' : 'exit';
            if (!type || type === accessType) {
                history.push({
                    id: `host_access_${Date.now()}_${i}`,
                    type: accessType,
                    entrance: ['Entrée Principale', 'Salle de Réunion A', 'Ascenseur Nord'][i % 3],
                    timestamp: new Date(Date.now() - i * 2 * 60 * 60 * 1000).toISOString(),
                    success: true,
                    qrCode: `HST-${Date.now() - i * 1000}`
                });
            }
        }
        
        res.json({
            success: true,
            history: history,
            total: history.length
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération de l\'historique'));
    }
});

// Host profile endpoint
app.get('/api/hosts/:hostId/profile', (req, res) => {
    try {
        const { hostId } = req.params;
        
        // Find host by ID
        const host = Object.values(mockHosts).find(h => h.id == hostId);
        
        if (host) {
            res.json({
                success: true,
                host: {
                    ...host,
                    createdAt: '2024-01-20T14:30:00',
                    lastAccess: new Date(Date.now() - 1 * 60 * 60 * 1000).toISOString()
                }
            });
        } else {
            res.status(404).json(createErrorResponse('Hôte non trouvé'));
        }
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération du profil'));
    }
});

// Manager - Get all workers
app.get('/api/workers', (req, res) => {
    try {
        const workers = Object.values(mockWorkers).map(worker => ({
            ...worker,
            createdAt: '2024-01-15T10:30:00',
            lastAccess: new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000).toISOString()
        }));
        
        res.json({
            success: true,
            workers: workers,
            total: workers.length
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération des employés'));
    }
});

// Manager - Get all hosts
app.get('/api/hosts', (req, res) => {
    try {
        const hosts = Object.values(mockHosts).map(host => ({
            ...host,
            createdAt: '2024-01-20T14:30:00',
            lastAccess: new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000).toISOString()
        }));
        
        res.json({
            success: true,
            hosts: hosts,
            total: hosts.length
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération des hôtes'));
    }
});

// Dashboard statistics
app.get('/api/stats/dashboard', (req, res) => {
    try {
        const stats = {
            totalWorkers: Object.keys(mockWorkers).length,
            totalHosts: Object.keys(mockHosts).length,
            activeAccess: Math.floor(Math.random() * 20) + 10,
            todayAccess: Math.floor(Math.random() * 50) + 20
        };
        
        res.json({
            success: true,
            stats: stats
        });
    } catch (error) {
        res.status(500).json(createErrorResponse('Erreur lors de la récupération des statistiques'));
    }
});

// 404 handler
app.use((req, res) => {
    res.status(404).json(createErrorResponse('Endpoint non trouvé', 404));
});

// Error handler
app.use((error, req, res, next) => {
    console.error('Server error:', error);
    res.status(500).json(createErrorResponse('Erreur interne du serveur'));
});

// Start server
app.listen(PORT, () => {
    console.log('🚀 SecurAccess Enterprise Mock API Server');
    console.log('=' .repeat(50));
    console.log(`📡 Server running on: http://localhost:${PORT}`);
    console.log(`🔗 API Base URL: http://localhost:${PORT}/api`);
    console.log('\n📋 Available endpoints:');
    console.log('  GET  /api/test/health');
    console.log('  POST /api/auth/login/worker');
    console.log('  POST /api/auth/login/host');
    console.log('  POST /api/auth/login/manager');
    console.log('  GET  /api/workers/:id/qr-code/generate');
    console.log('  GET  /api/workers/:id/access-history');
    console.log('  GET  /api/workers/:id/profile');
    console.log('  POST /api/workers/:id/access-log');
    console.log('  GET  /api/auth/validate-token');
    console.log('  POST /api/auth/logout');
    console.log('\n🧪 Run tests with: node test-api.js');
    console.log('=' .repeat(50));
});

module.exports = app;