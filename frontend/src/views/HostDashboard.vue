<template>
  <div class="host-dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <div class="user-info">
        <div class="user-avatar">🏠</div>
        <div class="user-details">
          <h2>{{ user.name }}</h2>
          <p>Visiteur</p>
        </div>
      </div>
      <button @click="logout" class="logout-btn">🚪</button>
    </div>

    <!-- Access Card -->
    <div class="access-card">
      <div class="access-header">
        <h3>🎫 Mon Accès Temporaire</h3>
        <div class="access-status" :class="{ active: accessInfo.isActive, expired: !accessInfo.isActive }">
          {{ accessInfo.isActive ? '✅ Actif' : '❌ Expiré' }}
        </div>
      </div>
      
      <div class="access-details">
        <div class="detail-item">
          <span class="label">📍 Lieu d'accès:</span>
          <span class="value">{{ accessInfo.location }}</span>
        </div>
        <div class="detail-item">
          <span class="label">📅 Valide du:</span>
          <span class="value">{{ formatDate(accessInfo.startDate) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">📅 Valide jusqu'au:</span>
          <span class="value">{{ formatDate(accessInfo.endDate) }}</span>
        </div>
        <div class="detail-item">
          <span class="label">⏰ Temps restant:</span>
          <span class="value" :class="{ warning: timeRemaining.isLow, expired: !accessInfo.isActive }">
            {{ timeRemaining.text }}
          </span>
        </div>
      </div>
    </div>

    <!-- QR Code Section -->
    <div class="qr-section">
      <div class="qr-card">
        <h3>📱 Mon QR Code d'Accès</h3>
        <div class="qr-container">
          <div v-if="!qrCode.generated" class="qr-placeholder">
            <div class="qr-icon">🎫</div>
            <p>Générez votre QR code pour accéder à {{ accessInfo.location }}</p>
            <button 
              @click="generateQRCode" 
              class="generate-btn" 
              :disabled="isGenerating || !accessInfo.isActive"
            >
              {{ isGenerating ? 'Génération...' : '🔄 Générer QR Code' }}
            </button>
            <p v-if="!accessInfo.isActive" class="warning-text">
              Votre accès a expiré. Contactez votre hôte pour renouveler.
            </p>
          </div>
          <div v-else class="qr-display">
            <canvas ref="qrCanvas" class="qr-canvas"></canvas>
            <div class="qr-info">
              <p><strong>Code:</strong> {{ qrCode.code }}</p>
              <p><strong>Localisation:</strong> {{ accessInfo.location }}</p>
              <p><strong>Valide jusqu'à:</strong> {{ formatDateTime(qrCode.expiresAt) }}</p>
              <div class="qr-validity" :class="{ valid: qrCode.isValid, expired: !qrCode.isValid }">
                {{ qrCode.isValid ? '✅ Code valide' : '❌ Code expiré' }}
              </div>
            </div>
            <div class="qr-actions">
              <button @click="refreshQRCode" class="refresh-btn" :disabled="!accessInfo.isActive">
                🔄 Actualiser
              </button>
              <button @click="shareAccess" class="share-btn">
                📤 Partager
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Location Info -->
    <div class="location-section">
      <h3>📍 Informations de Localisation</h3>
      <div class="location-card">
        <div class="location-header">
          <div class="location-icon">🏢</div>
          <div class="location-name">{{ accessInfo.location }}</div>
        </div>
        <div class="location-details">
          <div class="location-item">
            <span class="location-label">🚪 Entrées autorisées:</span>
            <div class="entrance-list">
              <span 
                v-for="entrance in accessInfo.authorizedEntrances" 
                :key="entrance"
                class="entrance-tag"
              >
                {{ entrance }}
              </span>
            </div>
          </div>
          <div class="location-item">
            <span class="location-label">🕐 Horaires d'accès:</span>
            <span class="location-value">{{ accessInfo.accessHours }}</span>
          </div>
          <div class="location-item">
            <span class="location-label">📋 Instructions:</span>
            <p class="instructions">{{ accessInfo.instructions }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Access History -->
    <div class="history-section">
      <h3>📊 Historique de mes Accès</h3>
      <div class="history-list">
        <div v-if="accessHistory.length === 0" class="no-history">
          <p>Aucun accès enregistré pour le moment</p>
        </div>
        <div 
          v-for="entry in accessHistory" 
          :key="entry.id" 
          class="history-item"
          :class="{ entry: entry.type === 'entry', exit: entry.type === 'exit' }"
        >
          <div class="history-icon">
            {{ entry.type === 'entry' ? '🟢' : '🔴' }}
          </div>
          <div class="history-content">
            <div class="history-location">{{ entry.entrance }}</div>
            <div class="history-time">{{ formatDateTime(entry.timestamp) }}</div>
            <div class="history-type">
              {{ entry.type === 'entry' ? 'Entrée' : 'Sortie' }}
            </div>
          </div>
          <div class="history-status" :class="entry.status">
            {{ entry.status === 'success' ? '✅' : '❌' }}
          </div>
        </div>
      </div>
    </div>

    <!-- Emergency Contact -->
    <div class="emergency-section">
      <button @click="showEmergencyContact" class="emergency-btn">
        🚨 Contact d'Urgence
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
            <p><strong>Hôte responsable:</strong></p>
            <p>{{ accessInfo.hostContact.name }}</p>
            <p><a :href="`tel:${accessInfo.hostContact.phone}`">{{ formatPhone(accessInfo.hostContact.phone) }}</a></p>
            
            <hr>
            
            <p><strong>Sécurité de l'entreprise:</strong></p>
            <p><a href="tel:+221771234567">77 123 45 67</a></p>
            
            <p><strong>Urgences:</strong></p>
            <p><a href="tel:15">15 (SAMU)</a> | <a href="tel:18">18 (Pompiers)</a></p>
          </div>
        </div>
      </div>
    </div>

    <!-- Share Modal -->
    <div v-if="showShareModal" class="modal-overlay" @click="showShareModal = false">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h3>📤 Partager l'Accès</h3>
          <button @click="showShareModal = false" class="close-btn">✖</button>
        </div>
        <div class="modal-content">
          <div class="share-options">
            <p>Partagez vos informations d'accès avec votre hôte :</p>
            
            <div class="share-info">
              <p><strong>Nom:</strong> {{ user.name }}</p>
              <p><strong>Téléphone:</strong> {{ formatPhone(user.phone) }}</p>
              <p><strong>Lieu d'accès:</strong> {{ accessInfo.location }}</p>
              <p><strong>Code QR:</strong> {{ qrCode.code }}</p>
            </div>
            
            <div class="share-buttons">
              <button @click="shareViaText" class="share-btn-text">💬 SMS</button>
              <button @click="shareViaEmail" class="share-btn-email">📧 Email</button>
              <button @click="copyToClipboard" class="share-btn-copy">📋 Copier</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { formatSenegalPhone, formatSenegalDate, formatSenegalDateTime } from '@/utils/senegalFormat.js'

export default {
  name: 'HostDashboard',
  data() {
    return {
      user: {
        id: null,
        name: 'Hôte',
        phone: '',
        role: 'host'
      },
      showEmergency: false,
      showShareModal: false,
      isGenerating: false,
      qrCode: {
        generated: false,
        code: '',
        expiresAt: null,
        isValid: false
      },
      accessInfo: {
        location: 'Salle de Réunion A',
        startDate: new Date(),
        endDate: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000), // 3 days from now
        isActive: true,
        authorizedEntrances: ['Entrée Principale', 'Entrée Salle A', 'Ascenseur Nord'],
        accessHours: '08:00 - 18:00',
        instructions: 'Présentez votre QR code au scanner à l\'entrée. En cas de problème, contactez votre hôte ou la sécurité.',
        hostContact: {
          name: 'Marie Manager',
          phone: '771234567'
        }
      },
      accessHistory: []
    }
  },
  computed: {
    timeRemaining() {
      if (!this.accessInfo.isActive) {
        return { text: 'Expiré', isLow: false };
      }
      
      const now = new Date();
      const end = new Date(this.accessInfo.endDate);
      const diff = end - now;
      
      if (diff <= 0) {
        this.accessInfo.isActive = false;
        return { text: 'Expiré', isLow: false };
      }
      
      const hours = Math.floor(diff / (1000 * 60 * 60));
      const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      
      const isLow = hours < 24;
      
      if (hours > 24) {
        const days = Math.floor(hours / 24);
        return { text: `${days} jour${days > 1 ? 's' : ''}`, isLow: false };
      } else if (hours > 0) {
        return { text: `${hours}h ${minutes}min`, isLow };
      } else {
        return { text: `${minutes} minutes`, isLow: true };
      }
    }
  },
  async mounted() {
    this.loadUser();
    this.checkAccessValidity();
    await this.loadAccessHistory();
    // Update time remaining every minute
    setInterval(() => {
      this.checkAccessValidity();
    }, 60000);
  },
  methods: {
    loadUser() {
      const storedUser = localStorage.getItem('securaccess_user');
      if (storedUser) {
        try {
          const userData = JSON.parse(storedUser);
          this.user = {
            id: userData.id,
            name: userData.name || 'Hôte',
            phone: userData.phone || '',
            role: userData.role || 'host'
          };
          if (this.user.role !== 'host') {
            this.$router.push('/login');
          }
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
        const response = await this.$api.getHostAccessHistory(this.user.id, { limit: 50 });
        
        if (response.success) {
          this.accessHistory = response.history.map(entry => ({
            id: entry.id,
            type: entry.type,
            entrance: entry.entrance,
            timestamp: new Date(entry.timestamp),
            status: entry.success ? 'success' : 'failed'
          }));
        }
      } catch (error) {
        console.error('Error loading access history:', error);
        // Keep empty array as fallback
      }
    },
    
    checkAccessValidity() {
      const now = new Date();
      this.accessInfo.isActive = now >= new Date(this.accessInfo.startDate) && 
                                 now <= new Date(this.accessInfo.endDate);
      
      if (this.qrCode.generated && this.qrCode.expiresAt) {
        this.qrCode.isValid = now < new Date(this.qrCode.expiresAt) && this.accessInfo.isActive;
      }
    },
    
    async generateQRCode() {
      if (!this.accessInfo.isActive) return;
      
      this.isGenerating = true;
      
      try {
        const response = await this.$api.generateHostQRCode(this.user.id);
        
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
        const cellSize = size / 21;
        
        for (let i = 0; i < 21; i++) {
          for (let j = 0; j < 21; j++) {
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
      ctx.fillRect(x, y, 7 * cellSize, cellSize);
      ctx.fillRect(x, y, cellSize, 7 * cellSize);
      ctx.fillRect(x + 6 * cellSize, y, cellSize, 7 * cellSize);
      ctx.fillRect(x, y + 6 * cellSize, 7 * cellSize, cellSize);
      ctx.fillRect(x + 2 * cellSize, y + 2 * cellSize, 3 * cellSize, 3 * cellSize);
    },
    
    simpleHash(str) {
      let hash = 0;
      for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash;
      }
      return Math.abs(hash);
    },
    
    refreshQRCode() {
      this.qrCode.generated = false;
      this.generateQRCode();
    },
    
    shareAccess() {
      this.showShareModal = true;
    },
    
    shareViaText() {
      const text = `Bonjour, je suis ${this.user.name}. Mon code d'accès est ${this.qrCode.code} pour ${this.accessInfo.location}. Téléphone: ${this.formatPhone(this.user.phone)}`;
      const url = `sms:${this.accessInfo.hostContact.phone}?body=${encodeURIComponent(text)}`;
      window.open(url);
    },
    
    shareViaEmail() {
      const subject = `Informations d'accès - ${this.user.name}`;
      const body = `Bonjour,\n\nVoici mes informations d'accès :\n\nNom: ${this.user.name}\nTéléphone: ${this.formatPhone(this.user.phone)}\nLieu d'accès: ${this.accessInfo.location}\nCode QR: ${this.qrCode.code}\n\nCordialement,\n${this.user.name}`;
      const url = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
      window.open(url);
    },
    
    async copyToClipboard() {
      const text = `Nom: ${this.user.name}\nTéléphone: ${this.formatPhone(this.user.phone)}\nLieu d'accès: ${this.accessInfo.location}\nCode QR: ${this.qrCode.code}`;
      
      try {
        await navigator.clipboard.writeText(text);
        alert('Informations copiées dans le presse-papiers !');
      } catch (err) {
        console.error('Erreur lors de la copie:', err);
        alert('Impossible de copier automatiquement. Sélectionnez et copiez manuellement.');
      }
    },
    
    formatPhone(phone) {
      return formatSenegalPhone(phone);
    },
    
    formatDate(date) {
      return formatSenegalDate(date);
    },
    
    formatDateTime(date) {
      return formatSenegalDateTime(date);
    },
    
    showEmergencyContact() {
      this.showEmergency = true;
    },
    
    logout() {
      localStorage.removeItem('securaccess_user');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.host-dashboard {
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
  background: linear-gradient(135deg, #ed8936, #dd6b20);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
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

.access-card,
.qr-card,
.location-card {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.access-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.access-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.25rem;
}

.access-status {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-weight: 600;
  font-size: 0.9rem;
}

.access-status.active {
  background: #c6f6d5;
  color: #22543d;
}

.access-status.expired {
  background: #fed7d7;
  color: #c53030;
}

.access-details {
  display: grid;
  gap: 0.75rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f7fafc;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: 600;
  color: #4a5568;
}

.value {
  color: #2d3748;
  font-weight: 500;
}

.value.warning {
  color: #ed8936;
}

.value.expired {
  color: #e53e3e;
}

.qr-section h3,
.location-section h3,
.history-section h3 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.25rem;
  padding-left: 0.5rem;
}

.qr-card h3 {
  text-align: center;
  padding-left: 0;
}

.qr-placeholder {
  text-align: center;
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
  margin-bottom: 1rem;
}

.generate-btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.generate-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #a0aec0;
}

.warning-text {
  color: #e53e3e;
  font-size: 0.9rem;
  font-style: italic;
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
  text-align: center;
  width: 100%;
  max-width: 300px;
}

.qr-info p {
  margin: 0.25rem 0;
  font-size: 0.9rem;
  color: #4a5568;
}

.qr-validity {
  padding: 0.5rem;
  border-radius: 8px;
  font-weight: 600;
  margin-top: 0.5rem;
}

.qr-validity.valid {
  background: #c6f6d5;
  color: #22543d;
}

.qr-validity.expired {
  background: #fed7d7;
  color: #c53030;
}

.qr-actions {
  display: flex;
  gap: 0.75rem;
}

.refresh-btn,
.share-btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.refresh-btn {
  background: #4299e1;
  color: white;
}

.refresh-btn:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.share-btn {
  background: #48bb78;
  color: white;
}

.refresh-btn:hover:not(:disabled),
.share-btn:hover {
  transform: translateY(-1px);
}

.location-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.location-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  background: #edf2f7;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.location-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2d3748;
}

.location-details {
  display: grid;
  gap: 1rem;
}

.location-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.location-label {
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
}

.location-value {
  color: #2d3748;
}

.entrance-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.entrance-tag {
  background: #bee3f8;
  color: #2c5282;
  padding: 0.25rem 0.75rem;
  border-radius: 16px;
  font-size: 0.8rem;
  font-weight: 500;
}

.instructions {
  margin: 0;
  color: #4a5568;
  line-height: 1.5;
  font-size: 0.9rem;
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
}

.history-item:last-child {
  border-bottom: none;
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

.history-status {
  font-size: 1.25rem;
}

.emergency-section {
  text-align: center;
  margin-top: 1.5rem;
}

.emergency-btn {
  background: linear-gradient(135deg, #e53e3e, #c53030);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 1rem 2rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
  width: 100%;
  max-width: 300px;
}

.emergency-btn:hover {
  transform: translateY(-2px);
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

.emergency-contact hr {
  border: none;
  border-top: 1px solid #e2e8f0;
  margin: 1rem 0;
}

.share-info {
  background: #f7fafc;
  border-radius: 8px;
  padding: 1rem;
  margin: 1rem 0;
}

.share-info p {
  margin: 0.25rem 0;
  font-size: 0.9rem;
}

.share-buttons {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

.share-btn-text,
.share-btn-email,
.share-btn-copy {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.share-btn-text {
  background: #48bb78;
  color: white;
}

.share-btn-email {
  background: #4299e1;
  color: white;
}

.share-btn-copy {
  background: #ed8936;
  color: white;
}

.share-btn-text:hover,
.share-btn-email:hover,
.share-btn-copy:hover {
  transform: translateY(-1px);
}

/* Mobile Optimizations */
@media (max-width: 480px) {
  .host-dashboard {
    padding: 0.5rem;
  }
  
  .access-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }
  
  .location-header {
    flex-direction: column;
    text-align: center;
  }
  
  .location-item {
    gap: 0.25rem;
  }
  
  .entrance-list {
    justify-content: center;
  }
  
  .qr-actions {
    flex-direction: column;
    width: 100%;
  }
  
  .share-buttons {
    flex-direction: column;
  }
  
  .history-item {
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  
  .history-content {
    min-width: 200px;
  }
}
</style>