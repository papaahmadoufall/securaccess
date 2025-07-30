<template>
  <div class="worker-dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <div class="user-info">
        <div class="user-avatar">{{ user.name ? user.name.charAt(0) : '👤' }}</div>
        <div class="user-details">
          <h2>{{ user.name || 'Utilisateur' }}</h2>
          <p>{{ user.phone ? formatPhone(user.phone) : '' }}</p>
        </div>
      </div>
      <button @click="logout" class="logout-btn">🚪</button>
    </div>

    <!-- QR Code Section -->
    <div class="qr-section">
      <div class="qr-card">
        <h3>🎫 Mon QR Code d'Accès</h3>
        <div class="qr-container">
          <div v-if="!qrCode.generated" class="qr-placeholder">
            <div class="qr-icon">📱</div>
            <p>Cliquez pour générer votre QR code</p>
            <button @click="generateQRCode" class="generate-btn" :disabled="isGenerating">
              {{ isGenerating ? 'Génération...' : '🔄 Générer QR Code' }}
            </button>
          </div>
          <div v-else class="qr-display">
            <canvas ref="qrCanvas" class="qr-canvas"></canvas>
            <div class="qr-info">
              <p><strong>Code:</strong> {{ qrCode.code }}</p>
              <p><strong>Valide jusqu'à:</strong> {{ formatDateTime(qrCode.expiresAt) }}</p>
              <div class="qr-status" :class="{ active: qrCode.isValid, expired: !qrCode.isValid }">
                {{ qrCode.isValid ? '✅ Valide' : '❌ Expiré' }}
              </div>
            </div>
            <button @click="refreshQRCode" class="refresh-btn">
              🔄 Actualiser
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Access History -->
    <div class="history-section">
      <h3>📊 Historique des Accès</h3>
      
      <!-- Filter -->
      <div class="filter-tabs">
        <button 
          v-for="filter in filters" 
          :key="filter.key"
          @click="activeFilter = filter.key"
          class="filter-tab"
          :class="{ active: activeFilter === filter.key }"
        >
          {{ filter.icon }} {{ filter.label }}
        </button>
      </div>

      <!-- History List -->
      <div class="history-list">
        <div v-if="filteredHistory.length === 0" class="no-history">
          <p>Aucun accès enregistré</p>
        </div>
        <div 
          v-for="entry in filteredHistory" 
          :key="entry.id" 
          class="history-item"
          :class="{ 'entry': entry.type === 'entry', 'exit': entry.type === 'exit' }"
        >
          <div class="history-icon">
            {{ entry.type === 'entry' ? '🟢' : '🔴' }}
          </div>
          <div class="history-content">
            <div class="history-location">{{ entry.location }}</div>
            <div class="history-time">{{ formatDateTime(entry.timestamp) }}</div>
            <div class="history-type">
              {{ entry.type === 'entry' ? 'Entrée' : 'Sortie' }}
            </div>
          </div>
          <div class="history-duration" v-if="entry.duration">
            {{ entry.duration }}
          </div>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
      <button @click="showEmergencyContact" class="action-btn emergency">
        🚨 Contact d'urgence
      </button>
      <button @click="showHelp" class="action-btn help">
        ❓ Aide
      </button>
    </div>

    <!-- Emergency Contact Modal -->
    <div v-if="showEmergency" class="modal-overlay" @click="showEmergency = false">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h3>🚨 Contact d'Urgence</h3>
          <button @click="showEmergency = false" class="close-btn">✖</button>
        </div>
        <div class="modal-content">
          <div class="emergency-contact">
            <p><strong>Sécurité:</strong> <a href="tel:+33123456789">01 23 45 67 89</a></p>
            <p><strong>Manager:</strong> <a href="tel:+33198765432">01 98 76 54 32</a></p>
            <p><strong>Urgences:</strong> <a href="tel:15">15</a></p>
          </div>
        </div>
      </div>
    </div>

    <!-- Help Modal -->
    <div v-if="showHelpModal" class="modal-overlay" @click="showHelpModal = false">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h3>❓ Aide</h3>
          <button @click="showHelpModal = false" class="close-btn">✖</button>
        </div>
        <div class="modal-content">
          <div class="help-content">
            <h4>🎫 Comment utiliser mon QR Code ?</h4>
            <ol>
              <li>Générez votre QR code en cliquant sur "Générer QR Code"</li>
              <li>Présentez votre téléphone au scanner d'accès</li>
              <li>Attendez la confirmation d'accès</li>
            </ol>
            
            <h4>🔄 Problèmes courants</h4>
            <ul>
              <li><strong>QR Code expiré:</strong> Cliquez sur "Actualiser"</li>
              <li><strong>Scanner ne répond pas:</strong> Vérifiez la luminosité de l'écran</li>
              <li><strong>Accès refusé:</strong> Contactez votre manager</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WorkerDashboard',
  data() {
    return {
      user: {
        id: null,
        name: 'Utilisateur',
        phone: '',
        role: 'worker'
      },
      qrCode: {
        generated: false,
        code: '',
        expiresAt: null,
        isValid: false
      },
      isGenerating: false,
      activeFilter: 'all',
      showEmergency: false,
      showHelpModal: false,
      filters: [
        { key: 'all', label: 'Tout', icon: '📋' },
        { key: 'today', label: 'Aujourd\'hui', icon: '📅' },
        { key: 'week', label: 'Semaine', icon: '📆' },
        { key: 'entry', label: 'Entrées', icon: '🟢' },
        { key: 'exit', label: 'Sorties', icon: '🔴' }
      ],
      accessHistory: []
    }
  },
  computed: {
    filteredHistory() {
      let filtered = this.accessHistory;
      
      switch (this.activeFilter) {
        case 'today':
          const today = new Date();
          today.setHours(0, 0, 0, 0);
          filtered = filtered.filter(entry => entry.timestamp >= today);
          break;
        case 'week':
          const weekAgo = new Date(Date.now() - 7 * 24 * 60 * 60 * 1000);
          filtered = filtered.filter(entry => entry.timestamp >= weekAgo);
          break;
        case 'entry':
          filtered = filtered.filter(entry => entry.type === 'entry');
          break;
        case 'exit':
          filtered = filtered.filter(entry => entry.type === 'exit');
          break;
      }
      
      return filtered.sort((a, b) => b.timestamp - a.timestamp);
    }
  },
  async mounted() {
    this.loadUser();
    this.checkQRCodeValidity();
    await this.loadAccessHistory();
  },
  methods: {
    loadUser() {
      const storedUser = localStorage.getItem('securaccess_user');
      if (storedUser) {
        try {
          const userData = JSON.parse(storedUser);
          this.user = {
            id: userData.id,
            name: userData.name || 'Utilisateur',
            phone: userData.phone || '',
            role: userData.role || 'worker'
          };
        } catch (error) {
          console.error('Error parsing user data:', error);
          this.$router.push('/login');
        }
      } else {
        this.$router.push('/login');
      }
    },
    
    async loadAccessHistory() {
      if (!this.user.id) return;
      
      try {
        const response = await this.$api.getWorkerAccessHistory(this.user.id, { limit: 50 });
        
        if (response.success) {
          this.accessHistory = response.history.map(entry => ({
            id: entry.id,
            type: entry.type,
            location: entry.location,
            timestamp: new Date(entry.timestamp),
            duration: null // Duration calculation could be added later
          }));
        }
      } catch (error) {
        console.error('Error loading access history:', error);
        // Keep empty array as fallback
      }
    },
    
    async generateQRCode() {
      this.isGenerating = true;
      
      try {
        const response = await this.$api.generateWorkerQRCode(this.user.id);
        
        if (response.success) {
          this.qrCode = {
            generated: true,
            code: response.qrCode.code,
            expiresAt: response.qrCode.expiresAt,
            isValid: response.qrCode.isValid
          };
          
          // Generate QR code canvas
          this.$nextTick(() => {
            this.drawQRCode(JSON.stringify(response.qrCode));
          });
        } else {
          alert('Erreur lors de la génération du QR code');
        }
      } catch (error) {
        console.error('Error generating QR code:', error);
        alert('Erreur de connexion lors de la génération du QR code');
      } finally {
        this.isGenerating = false;
      }
    },
    
    drawQRCode(data) {
      const canvas = this.$refs.qrCanvas;
      if (canvas) {
        const ctx = canvas.getContext('2d');
        const size = 200;
        canvas.width = size;
        canvas.height = size;
        
        // Simple QR code representation (mock)
        ctx.fillStyle = '#000';
        const cellSize = size / 21; // 21x21 grid for QR code
        
        // Create a simple pattern
        for (let i = 0; i < 21; i++) {
          for (let j = 0; j < 21; j++) {
            // Create a deterministic pattern based on data
            const hash = this.simpleHash(data + i + j);
            if (hash % 2 === 0) {
              ctx.fillRect(i * cellSize, j * cellSize, cellSize, cellSize);
            }
          }
        }
        
        // Add corner markers
        this.drawCornerMarker(ctx, 0, 0, cellSize);
        this.drawCornerMarker(ctx, 14 * cellSize, 0, cellSize);
        this.drawCornerMarker(ctx, 0, 14 * cellSize, cellSize);
      }
    },
    
    drawCornerMarker(ctx, x, y, cellSize) {
      ctx.fillStyle = '#000';
      // Outer square
      ctx.fillRect(x, y, 7 * cellSize, cellSize);
      ctx.fillRect(x, y, cellSize, 7 * cellSize);
      ctx.fillRect(x + 6 * cellSize, y, cellSize, 7 * cellSize);
      ctx.fillRect(x, y + 6 * cellSize, 7 * cellSize, cellSize);
      
      // Inner square
      ctx.fillRect(x + 2 * cellSize, y + 2 * cellSize, 3 * cellSize, 3 * cellSize);
    },
    
    simpleHash(str) {
      let hash = 0;
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // Convert to 32-bit integer
      }
      return Math.abs(hash);
    },
    
    refreshQRCode() {
      this.qrCode.generated = false;
      this.generateQRCode();
    },
    
    checkQRCodeValidity() {
      if (this.qrCode.generated && this.qrCode.expiresAt) {
        this.qrCode.isValid = new Date() < new Date(this.qrCode.expiresAt);
      }
    },
    
    formatPhone(phone) {
      if (!phone) return '';
      const cleaned = phone.replace(/\D/g, '');
      if (cleaned.length !== 10) return phone;
      return cleaned.replace(/(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/, '$1 $2 $3 $4 $5');
    },
    
    formatDateTime(date) {
      return new Date(date).toLocaleString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    
    showEmergencyContact() {
      this.showEmergency = true;
    },
    
    showHelp() {
      this.showHelpModal = true;
    },
    
    logout() {
      localStorage.removeItem('securaccess_user');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.worker-dashboard {
  min-height: 100vh;
  background: #f7fafc;
  padding: 1rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 1rem;
  border-radius: 16px;
  margin-bottom: 1rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4299e1, #3182ce);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
}

.user-details h2 {
  margin: 0;
  font-size: 1.1rem;
  color: #2d3748;
}

.user-details p {
  margin: 0;
  color: #718096;
  font-size: 0.9rem;
}

.logout-btn {
  background: #fed7d7;
  border: none;
  border-radius: 8px;
  padding: 0.5rem;
  font-size: 1.25rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background: #feb2b2;
}

.qr-section,
.history-section {
  margin-bottom: 1.5rem;
}

.qr-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.qr-card h3 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.25rem;
}

.qr-placeholder {
  padding: 2rem 1rem;
}

.qr-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.generate-btn {
  background: linear-gradient(135deg, #48bb78, #38a169);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.generate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.generate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.qr-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.qr-canvas {
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  max-width: 200px;
  width: 100%;
}

.qr-info {
  text-align: left;
  width: 100%;
  max-width: 300px;
}

.qr-info p {
  margin: 0.25rem 0;
  font-size: 0.9rem;
  color: #4a5568;
}

.qr-status {
  padding: 0.5rem;
  border-radius: 8px;
  font-weight: 600;
  text-align: center;
  margin-top: 0.5rem;
}

.qr-status.active {
  background: #c6f6d5;
  color: #22543d;
}

.qr-status.expired {
  background: #fed7d7;
  color: #c53030;
}

.refresh-btn {
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-size: 0.9rem;
}

.history-section h3 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.25rem;
  padding-left: 0.5rem;
}

.filter-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
}

.filter-tab {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  padding: 0.5rem 1rem;
  font-size: 0.8rem;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.filter-tab.active {
  background: #4299e1;
  color: white;
  border-color: #4299e1;
}

.history-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.no-history {
  padding: 2rem;
  text-align: center;
  color: #718096;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
  transition: background-color 0.3s ease;
}

.history-item:last-child {
  border-bottom: none;
}

.history-item:hover {
  background: #f7fafc;
}

.history-icon {
  font-size: 1.5rem;
  margin-right: 1rem;
}

.history-content {
  flex: 1;
}

.history-location {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.history-time {
  font-size: 0.8rem;
  color: #718096;
  margin-bottom: 0.25rem;
}

.history-type {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  display: inline-block;
}

.history-item.entry .history-type {
  background: #c6f6d5;
  color: #22543d;
}

.history-item.exit .history-type {
  background: #fed7d7;
  color: #c53030;
}

.history-duration {
  font-size: 0.8rem;
  color: #4a5568;
  font-weight: 600;
}

.quick-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.action-btn {
  flex: 1;
  padding: 1rem;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
}

.action-btn.emergency {
  background: #fed7d7;
  color: #c53030;
}

.action-btn.help {
  background: #bee3f8;
  color: #2c5282;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-card {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  color: #2d3748;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #718096;
}

.modal-content {
  padding: 1.5rem;
}

.emergency-contact p {
  margin: 0.5rem 0;
  font-size: 1rem;
}

.emergency-contact a {
  color: #4299e1;
  text-decoration: none;
  font-weight: 600;
}

.help-content h4 {
  color: #2d3748;
  margin: 1rem 0 0.5rem 0;
}

.help-content ol,
.help-content ul {
  padding-left: 1.5rem;
  color: #4a5568;
}

.help-content li {
  margin: 0.5rem 0;
}

/* Mobile Optimizations */
@media (max-width: 480px) {
  .worker-dashboard {
    padding: 0.5rem;
  }
  
  .dashboard-header {
    padding: 0.75rem;
    margin-bottom: 0.75rem;
  }
  
  .user-avatar {
    width: 40px;
    height: 40px;
    font-size: 1.25rem;
  }
  
  .qr-card {
    padding: 1rem;
  }
  
  .filter-tabs {
    gap: 0.25rem;
  }
  
  .filter-tab {
    padding: 0.375rem 0.75rem;
    font-size: 0.75rem;
  }
  
  .history-item {
    padding: 0.75rem;
  }
  
  .quick-actions {
    flex-direction: column;
    gap: 0.75rem;
  }
}
</style>