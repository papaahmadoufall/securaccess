// Enhanced Mock API Server with Real MySQL Database Connection
// This bridges the frontend to the real database while we prepare the Java backend

const express = require('express');
const cors = require('cors');
const mysql = require('mysql2/promise');
const bcrypt = require('bcrypt');

const app = express();
const PORT = 8093;

// Database configuration
const dbConfig = {
    host: 'localhost',
    port: 3309,
    user: 'root',
    password: '',
    database: 'securaccess_db'
};

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Request logging
app.use((req, res, next) => {
    console.log(`${new Date().toISOString()} - ${req.method} ${req.path}`);
    next();
});

// Database connection
let db;

async function initDatabase() {
    try {
        db = await mysql.createConnection(dbConfig);
        console.log('✅ Connected to MySQL database');
        
        // Create tables if they don't exist
        await createTables();
        await seedInitialData();
        
    } catch (error) {
        console.error('❌ Database connection failed:', error.message);
        console.log('⚠️  Falling back to in-memory mode');
        db = null;
    }
}

async function createTables() {
    if (!db) return;
    
    try {
        // Workers table
        await db.execute(`
            CREATE TABLE IF NOT EXISTS workers (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                phone VARCHAR(10) UNIQUE NOT NULL,
                pin_hash VARCHAR(60) NOT NULL,
                department VARCHAR(50) NOT NULL,
                is_active BOOLEAN NOT NULL DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                last_access TIMESTAMP NULL
            ) ENGINE=InnoDB CHARACTER SET utf8mb4
        `);
        
        // Hosts table
        await db.execute(`
            CREATE TABLE IF NOT EXISTS hosts (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                phone VARCHAR(10) UNIQUE NOT NULL,
                pin_hash VARCHAR(60) NOT NULL,
                location VARCHAR(100) NOT NULL,
                access_start_date DATE NOT NULL,
                access_end_date DATE NOT NULL,
                is_active BOOLEAN NOT NULL DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB CHARACTER SET utf8mb4
        `);
        
        // Managers table
        await db.execute(`
            CREATE TABLE IF NOT EXISTS managers (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password_hash VARCHAR(100) NOT NULL,
                is_active BOOLEAN NOT NULL DEFAULT true,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            ) ENGINE=InnoDB CHARACTER SET utf8mb4
        `);
        
        // Access logs table
        await db.execute(`
            CREATE TABLE IF NOT EXISTS access_logs (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                worker_id BIGINT NULL,
                host_id BIGINT NULL,
                access_type VARCHAR(20) NOT NULL,
                location VARCHAR(100) NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                success BOOLEAN NOT NULL,
                qr_code VARCHAR(50),
                details VARCHAR(500),
                FOREIGN KEY (worker_id) REFERENCES workers(id) ON DELETE CASCADE,
                FOREIGN KEY (host_id) REFERENCES hosts(id) ON DELETE CASCADE
            ) ENGINE=InnoDB CHARACTER SET utf8mb4
        `);
        
        console.log('✅ Database tables created/verified');
    } catch (error) {
        console.error('❌ Error creating tables:', error.message);
    }
}

async function seedInitialData() {
    if (!db) return;
    
    try {
        // Check if data already exists
        const [workerRows] = await db.execute('SELECT COUNT(*) as count FROM workers');
        if (workerRows[0].count > 0) {
            console.log('✅ Database already has data, skipping seed');
            return;
        }
        
        // Insert sample workers
        const hashedPin1 = await bcrypt.hash('1234', 10);
        const hashedPin2 = await bcrypt.hash('9876', 10);
        
        await db.execute(`
            INSERT INTO workers (name, phone, pin_hash, department) VALUES 
            ('Jean Dupont', '0612345678', ?, 'IT'),
            ('Pierre Durand', '0698765432', ?, 'Marketing')
        `, [hashedPin1, hashedPin2]);
        
        // Insert sample hosts
        const hashedPin3 = await bcrypt.hash('5678', 10);
        
        await db.execute(`
            INSERT INTO hosts (name, phone, pin_hash, location, access_start_date, access_end_date) VALUES 
            ('Marie Visiteur', '0687654321', ?, 'Salle de réunion A', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY))
        `, [hashedPin3]);
        
        // Insert sample manager
        const hashedPassword = await bcrypt.hash('manager123', 10);
        
        await db.execute(`
            INSERT INTO managers (name, email, password_hash) VALUES 
            ('Responsable Manager', 'manager@entreprise.com', ?)
        `, [hashedPassword]);
        
        console.log('✅ Initial data seeded');
    } catch (error) {
        console.error('❌ Error seeding data:', error.message);
    }
}

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
    return input.toString().trim().replace(/[<>\"'&]/g, '');
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
        database: db ? 'Connected' : 'Disconnected',
        timestamp: Date.now(),
        version: '1.0.0'
    });
});

// Worker login with database
app.post('/api/auth/login/worker', async (req, res) => {
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
        
        if (db) {
            // Real database authentication
            const [rows] = await db.execute(
                'SELECT * FROM workers WHERE phone = ? AND is_active = true',
                [cleanPhone]
            );
            
            if (rows.length > 0) {
                const worker = rows[0];
                const pinMatch = await bcrypt.compare(cleanPin, worker.pin_hash);
                
                if (pinMatch) {
                    // Update last access
                    await db.execute(
                        'UPDATE workers SET last_access = NOW() WHERE id = ?',
                        [worker.id]
                    );
                    
                    res.json({
                        success: true,
                        user: {
                            id: worker.id,
                            name: worker.name,
                            phone: worker.phone,
                            role: 'worker',
                            department: worker.department,
                            isActive: worker.is_active
                        },
                        token: `DB_TOKEN_worker_${Date.now()}`,
                        expiresIn: 28800
                    });
                    return;
                }
            }
            
            return res.status(401).json(createErrorResponse('Numéro de téléphone ou code PIN incorrect'));
        } else {
            // Fallback to mock data
            return res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
        
    } catch (error) {
        console.error('Worker login error:', error);
        res.status(500).json(createErrorResponse('Erreur du serveur'));
    }
});

// Manager login with database
app.post('/api/auth/login/manager', async (req, res) => {
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
        
        if (db) {
            // Real database authentication
            const [rows] = await db.execute(
                'SELECT * FROM managers WHERE email = ? AND is_active = true',
                [cleanEmail]
            );
            
            if (rows.length > 0) {
                const manager = rows[0];
                const passwordMatch = await bcrypt.compare(cleanPassword, manager.password_hash);
                
                if (passwordMatch) {
                    res.json({
                        success: true,
                        user: {
                            id: manager.id,
                            name: manager.name,
                            email: manager.email,
                            role: 'manager'
                        },
                        token: `DB_TOKEN_manager_${Date.now()}`,
                        expiresIn: 28800
                    });
                    return;
                }
            }
            
            return res.status(401).json(createErrorResponse('Email ou mot de passe incorrect'));
        } else {
            return res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
        
    } catch (error) {
        console.error('Manager login error:', error);
        res.status(500).json(createErrorResponse('Erreur du serveur'));
    }
});

// Get all workers (for manager dashboard)
app.get('/api/workers', async (req, res) => {
    try {
        if (db) {
            const [rows] = await db.execute(`
                SELECT id, name, phone, department, is_active, created_at, last_access 
                FROM workers ORDER BY created_at DESC
            `);
            
            res.json({
                success: true,
                workers: rows.map(worker => ({
                    id: worker.id,
                    name: worker.name,
                    phone: worker.phone,
                    department: worker.department,
                    isActive: worker.is_active,
                    createdAt: worker.created_at,
                    lastAccess: worker.last_access
                })),
                total: rows.length
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('Get workers error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la récupération des employés'));
    }
});

// Dashboard statistics
app.get('/api/stats/dashboard', async (req, res) => {
    try {
        if (db) {
            const [workerCount] = await db.execute('SELECT COUNT(*) as count FROM workers WHERE is_active = true');
            const [hostCount] = await db.execute('SELECT COUNT(*) as count FROM hosts WHERE is_active = true');
            const [todayAccess] = await db.execute('SELECT COUNT(*) as count FROM access_logs WHERE DATE(timestamp) = CURDATE()');
            
            res.json({
                success: true,
                stats: {
                    totalWorkers: workerCount[0].count,
                    totalHosts: hostCount[0].count,
                    activeAccess: Math.floor(Math.random() * 20) + 10,
                    todayAccess: todayAccess[0].count
                }
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('Dashboard stats error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la récupération des statistiques'));
    }
});

// Worker QR Code generation
app.get('/api/workers/:workerId/qr-code/generate', async (req, res) => {
    try {
        const { workerId } = req.params;
        
        if (db) {
            // Verify worker exists
            const [workerRows] = await db.execute('SELECT * FROM workers WHERE id = ? AND is_active = true', [workerId]);
            
            if (workerRows.length === 0) {
                return res.status(404).json(createErrorResponse('Employé non trouvé'));
            }
            
            const qrCode = {
                id: `qr_${Date.now()}`,
                code: `WKR-${Date.now().toString(36).toUpperCase()}`,
                workerId: workerId,
                generatedAt: new Date().toISOString(),
                expiresAt: new Date(Date.now() + 8 * 60 * 60 * 1000).toISOString(), // 8 hours
                isValid: true,
                imageBase64: 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='
            };
            
            // Log QR code generation in access_logs
            await db.execute(`
                INSERT INTO access_logs (worker_id, access_type, location, success, qr_code, details) 
                VALUES (?, 'qr_generation', 'Mobile App', true, ?, 'QR Code generated')
            `, [workerId, qrCode.code]);
            
            res.json({
                success: true,
                qrCode: qrCode
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('QR generation error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la génération du QR code'));
    }
});

// Worker access history
app.get('/api/workers/:workerId/access-history', async (req, res) => {
    try {
        const { workerId } = req.params;
        const { limit = 50, type } = req.query;
        
        if (db) {
            // Verify worker exists
            const [workerRows] = await db.execute('SELECT * FROM workers WHERE id = ? AND is_active = true', [workerId]);
            
            if (workerRows.length === 0) {
                return res.status(404).json(createErrorResponse('Employé non trouvé'));
            }
            
            let query = 'SELECT * FROM access_logs WHERE worker_id = ?';
            let params = [workerId];
            
            if (type && (type === 'entry' || type === 'exit')) {
                query += ' AND access_type = ?';
                params.push(type);
            }
            
            query += ` ORDER BY timestamp DESC LIMIT ${parseInt(limit)}`;
            
            const [accessRows] = await db.execute(query, params);
            
            const history = accessRows.map(row => ({
                id: row.id,
                type: row.access_type,
                location: row.location,
                timestamp: row.timestamp,
                success: row.success,
                qrCode: row.qr_code
            }));
            
            res.json({
                success: true,
                history: history,
                total: history.length
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('Access history error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la récupération de l\'historique'));
    }
});

// Host QR Code generation
app.get('/api/hosts/:hostId/qr-code/generate', async (req, res) => {
    try {
        const { hostId } = req.params;
        
        if (db) {
            // Verify host exists
            const [hostRows] = await db.execute('SELECT * FROM hosts WHERE id = ? AND is_active = true', [hostId]);
            
            if (hostRows.length === 0) {
                return res.status(404).json(createErrorResponse('Hôte non trouvé'));
            }
            
            const qrCode = {
                id: `qr_host_${Date.now()}`,
                code: `HST-${Date.now().toString(36).toUpperCase()}`,
                hostId: hostId,
                generatedAt: new Date().toISOString(),
                expiresAt: new Date(Date.now() + 4 * 60 * 60 * 1000).toISOString(), // 4 hours
                isValid: true,
                imageBase64: 'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=='
            };
            
            // Log QR code generation in access_logs
            await db.execute(`
                INSERT INTO access_logs (host_id, access_type, location, success, qr_code, details) 
                VALUES (?, 'qr_generation', 'Mobile App', true, ?, 'Host QR Code generated')
            `, [hostId, qrCode.code]);
            
            res.json({
                success: true,
                qrCode: qrCode
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('Host QR generation error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la génération du QR code'));
    }
});

// Host access history
app.get('/api/hosts/:hostId/access-history', async (req, res) => {
    try {
        const { hostId } = req.params;
        const { limit = 50, type } = req.query;
        
        if (db) {
            // Verify host exists
            const [hostRows] = await db.execute('SELECT * FROM hosts WHERE id = ? AND is_active = true', [hostId]);
            
            if (hostRows.length === 0) {
                return res.status(404).json(createErrorResponse('Hôte non trouvé'));
            }
            
            let query = 'SELECT * FROM access_logs WHERE host_id = ?';
            let params = [hostId];
            
            if (type && (type === 'entry' || type === 'exit')) {
                query += ' AND access_type = ?';
                params.push(type);
            }
            
            query += ` ORDER BY timestamp DESC LIMIT ${parseInt(limit)}`;
            
            const [accessRows] = await db.execute(query, params);
            
            const history = accessRows.map(row => ({
                id: row.id,
                type: row.access_type,
                entrance: row.location, // Use 'entrance' for hosts
                timestamp: row.timestamp,
                success: row.success,
                qrCode: row.qr_code
            }));
            
            res.json({
                success: true,
                history: history,
                total: history.length
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('Host access history error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la récupération de l\'historique'));
    }
});

// Get all hosts (for manager dashboard)
app.get('/api/hosts', async (req, res) => {
    try {
        if (db) {
            const [rows] = await db.execute(`
                SELECT id, name, phone, location, access_start_date, access_end_date, is_active, created_at 
                FROM hosts ORDER BY created_at DESC
            `);
            
            res.json({
                success: true,
                hosts: rows.map(host => ({
                    id: host.id,
                    name: host.name,
                    phone: host.phone,
                    location: host.location,
                    accessStartDate: host.access_start_date,
                    accessEndDate: host.access_end_date,
                    isActive: host.is_active,
                    createdAt: host.created_at
                })),
                total: rows.length
            });
        } else {
            res.status(503).json(createErrorResponse('Service de base de données non disponible'));
        }
    } catch (error) {
        console.error('Get hosts error:', error);
        res.status(500).json(createErrorResponse('Erreur lors de la récupération des hôtes'));
    }
});

// 404 handler
app.use((req, res) => {
    res.status(404).json(createErrorResponse('Endpoint non trouvé', 404));
});

// Start server
async function startServer() {
    await initDatabase();
    
    app.listen(PORT, () => {
        console.log('🚀 SecurAccess Enterprise Database-Connected API Server');
        console.log('=' .repeat(60));
        console.log(`📡 Server running on: http://localhost:${PORT}`);
        console.log(`🔗 API Base URL: http://localhost:${PORT}/api`);
        console.log(`💾 Database: ${db ? '✅ Connected to MySQL' : '❌ Not Connected'}`);
        console.log('\\n📋 Available endpoints:');
        console.log('  GET  /api/test/health');
        console.log('  POST /api/auth/login/worker');
        console.log('  POST /api/auth/login/manager'); 
        console.log('  GET  /api/workers');
        console.log('  GET  /api/stats/dashboard');
        console.log('=' .repeat(60));
    });
}

startServer().catch(console.error);