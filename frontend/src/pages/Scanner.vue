<template>
  <div class="container">
    <div class="header">
      <h1 class="title">📱 Scanner QR Code</h1>
      <div class="status-indicator" :class="{ active: isScanning, inactive: !isScanning }">
        {{ isScanning ? '🟢 Scanner actif' : '🔴 Scanner inactif' }}
      </div>
    </div>

    <!-- Scanner Section -->
    <div class="scanner-section">
      <div class="scanner-container">
        <div v-if="!isScanning" class="scanner-placeholder">
          <div class="scanner-icon">📱</div>
          <h3>Scanner QR Code</h3>
          <p>Cliquez sur "Démarrer le scanner" pour commencer la lecture des QR codes</p>
          <button class="btn btn-primary large" @click="startScanner">
            🚀 Démarrer le scanner
          </button>
        </div>
        
        <div v-else class="scanner-active">
          <div class="camera-view">
            <div class="scanning-overlay">
              <div class="scan-area">
                <div class="scan-corners">
                  <div class="corner top-left"></div>
                  <div class="corner top-right"></div>
                  <div class="corner bottom-left"></div>
                  <div class="corner bottom-right"></div>
                </div>
                <div class="scan-line"></div>
              </div>
            </div>
            <div class="camera-placeholder">
              <p>📷 Caméra en cours d'initialisation...</p>
              <div class="loading-spinner"></div>
            </div>
          </div>
          
          <div class="scanner-controls">
            <button class="btn btn-secondary" @click="stopScanner">
              ⏹️ Arrêter
            </button>
            <button class="btn btn-primary" @click="simulateScan">
              🧪 Test Scan
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Scan Results -->
    <div class="results-section">
      <h2>📋 Résultats des scans</h2>
      
      <div v-if="scanResults.length === 0" class="no-results">
        <p>Aucun scan effectué pour le moment</p>
      </div>
      
      <div v-else class="results-list">
        <div 
          v-for="result in scanResults" 
          :key="result.id" 
          class="result-item"
          :class="{ success: result.valid, error: !result.valid }"
        >
          <div class="result-header">
            <div class="result-status">
              <span class="status-icon">{{ result.valid ? '✅' : '❌' }}</span>
              <span class="status-text">{{ result.valid ? 'Accès autorisé' : 'Accès refusé' }}</span>
            </div>
            <div class="result-time">{{ formatTime(result.timestamp) }}</div>
          </div>
          
          <div class="result-details">
            <div class="detail-row">
              <span class="label">QR Code:</span>
              <span class="value">{{ result.code }}</span>
            </div>
            <div class="detail-row">
              <span class="label">Utilisateur:</span>
              <span class="value">{{ result.user || 'Inconnu' }}</span>
            </div>
            <div class="detail-row">
              <span class="label">Événement:</span>
              <span class="value">{{ result.event || 'Général' }}</span>
            </div>
            <div v-if="!result.valid" class="detail-row">
              <span class="label">Raison:</span>
              <span class="value error-reason">{{ result.reason }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Statistics -->
    <div class="stats-section">
      <h2>📊 Statistiques</h2>
      
      <div class="stats-grid">
        <div class="stat-card success">
          <div class="stat-number">{{ stats.successful }}</div>
          <div class="stat-label">Accès autorisés</div>
        </div>
        
        <div class="stat-card error">
          <div class="stat-number">{{ stats.failed }}</div>
          <div class="stat-label">Accès refusés</div>
        </div>
        
        <div class="stat-card total">
          <div class="stat-number">{{ stats.total }}</div>
          <div class="stat-label">Total scans</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Scanner',
  data() {
    return {
      isScanning: false,
      scanResults: [],
      stats: {
        successful: 0,
        failed: 0,
        total: 0
      }
    }
  },
  methods: {
    startScanner() {
      this.isScanning = true;
      // TODO: Initialize camera and QR code scanner
      console.log('Scanner started');
    },
    
    stopScanner() {
      this.isScanning = false;
      // TODO: Stop camera and scanner
      console.log('Scanner stopped');
    },
    
    simulateScan() {
      // Simulate a QR code scan for testing
      const testCodes = [
        { code: 'QR123456', user: 'Jean Dupont', event: 'Réunion Marketing', valid: true },
        { code: 'QR789012', user: 'Marie Martin', event: 'Formation Sécurité', valid: true },
        { code: 'QREXPIRED', user: 'Pierre Durand', event: 'Événement expiré', valid: false, reason: 'QR Code expiré' },
        { code: 'QRINVALID', user: null, event: null, valid: false, reason: 'QR Code invalide' }
      ];
      
      const randomCode = testCodes[Math.floor(Math.random() * testCodes.length)];
      
      const result = {
        id: Date.now(),
        timestamp: new Date(),
        ...randomCode
      };
      
      this.scanResults.unshift(result);
      this.updateStats();
      
      // Show notification
      if (result.valid) {
        this.showNotification('Accès autorisé', 'success');
      } else {
        this.showNotification('Accès refusé: ' + result.reason, 'error');
      }
    },
    
    updateStats() {
      this.stats.total = this.scanResults.length;
      this.stats.successful = this.scanResults.filter(r => r.valid).length;
      this.stats.failed = this.scanResults.filter(r => !r.valid).length;
    },
    
    formatTime(date) {
      return date.toLocaleTimeString('fr-FR');
    },
    
    showNotification(message, type) {
      // Simple alert for now, could be replaced with a toast notification
      alert(message);
    }
  }
}
</script>

<style scoped>
.container {
  padding: 2rem 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.title {
  font-size: 2.5rem;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.status-indicator {
  padding: 0.5rem 1rem;
  border-radius: 9999px;
  font-weight: 500;
}

.status-indicator.active {
  background: #dcfce7;
  color: #15803d;
}

.status-indicator.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.scanner-section {
  margin-bottom: 3rem;
}

.scanner-container {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.scanner-placeholder {
  text-align: center;
  padding: 3rem 1rem;
}

.scanner-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.scanner-placeholder h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #1f2937;
}

.scanner-placeholder p {
  color: #6b7280;
  margin-bottom: 2rem;
}

.scanner-active {
  text-align: center;
}

.camera-view {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: 300px;
  margin: 0 auto 2rem;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.camera-placeholder {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  text-align: center;
}

.scanning-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scan-area {
  position: relative;
  width: 200px;
  height: 200px;
}

.scan-corners {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.corner {
  position: absolute;
  width: 30px;
  height: 30px;
  border: 3px solid #2563eb;
}

.corner.top-left {
  top: 0;
  left: 0;
  border-right: none;
  border-bottom: none;
}

.corner.top-right {
  top: 0;
  right: 0;
  border-left: none;
  border-bottom: none;
}

.corner.bottom-left {
  bottom: 0;
  left: 0;
  border-right: none;
  border-top: none;
}

.corner.bottom-right {
  bottom: 0;
  right: 0;
  border-left: none;
  border-top: none;
}

.scan-line {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 2px;
  background: #2563eb;
  animation: scan 2s linear infinite;
}

@keyframes scan {
  0% { transform: translateY(-100px); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translateY(100px); opacity: 0; }
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #374151;
  border-top: 4px solid #2563eb;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 1rem auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.scanner-controls {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.results-section,
.stats-section {
  margin-bottom: 3rem;
}

.results-section h2,
.stats-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1f2937;
}

.no-results {
  text-align: center;
  padding: 2rem;
  color: #6b7280;
  background: white;
  border-radius: 8px;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.result-item {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px -1px rgba(0, 0, 0, 0.1);
  border-left: 4px solid;
}

.result-item.success {
  border-left-color: #10b981;
}

.result-item.error {
  border-left-color: #ef4444;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.result-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-text {
  font-weight: 600;
}

.result-item.success .status-text {
  color: #10b981;
}

.result-item.error .status-text {
  color: #ef4444;
}

.result-time {
  color: #6b7280;
  font-size: 0.875rem;
}

.result-details {
  display: grid;
  gap: 0.5rem;
}

.detail-row {
  display: flex;
}

.label {
  font-weight: 500;
  margin-right: 1rem;
  min-width: 100px;
  color: #374151;
}

.value {
  color: #6b7280;
}

.error-reason {
  color: #ef4444;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: 0 2px 4px -1px rgba(0, 0, 0, 0.1);
  border-top: 4px solid;
}

.stat-card.success {
  border-top-color: #10b981;
}

.stat-card.error {
  border-top-color: #ef4444;
}

.stat-card.total {
  border-top-color: #2563eb;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.stat-card.success .stat-number {
  color: #10b981;
}

.stat-card.error .stat-number {
  color: #ef4444;
}

.stat-card.total .stat-number {
  color: #2563eb;
}

.stat-label {
  color: #6b7280;
  font-weight: 500;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
}

.btn.large {
  padding: 1rem 2rem;
  font-size: 1.1rem;
}

.btn-primary {
  background: #2563eb;
  color: white;
}

.btn-primary:hover {
  background: #1d4ed8;
}

.btn-secondary {
  background: #e5e7eb;
  color: #374151;
}

.btn-secondary:hover {
  background: #d1d5db;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .scanner-controls {
    flex-direction: column;
    align-items: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .result-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }
}
</style>