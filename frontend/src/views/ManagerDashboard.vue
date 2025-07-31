<template>
  <div class="manager-dashboard">
    <!-- Header -->
    <div class="dashboard-header">
      <div class="user-info">
        <div class="user-avatar">👔</div>
        <div class="user-details">
          <h2>{{ user.name }}</h2>
          <p>Manager</p>
        </div>
      </div>
      <button @click="logout" class="logout-btn">🚪</button>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalWorkers }}</div>
          <div class="stat-label">Employés</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">🏠</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalHosts }}</div>
          <div class="stat-label">Hôtes</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">✅</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.activeAccess }}</div>
          <div class="stat-label">Accès actifs</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📅</div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.todayAccess }}</div>
          <div class="stat-label">Aujourd'hui</div>
        </div>
      </div>
    </div>

    <!-- Action Buttons -->
    <div class="action-buttons">
      <button @click="showAddWorker = true" class="action-btn primary">
        ➕ Ajouter Employé
      </button>
      <button @click="showImportCSV = true" class="action-btn secondary">
        📋 Importer CSV
      </button>
      <button @click="showAddHost = true" class="action-btn tertiary">
        🏠 Ajouter Hôte
      </button>
    </div>

    <!-- Workers List -->
    <div class="section">
      <div class="section-header">
        <h3>👥 Gestion des Employés</h3>
        <div class="search-box">
          <input 
            v-model="searchWorker" 
            type="text" 
            placeholder="🔍 Rechercher..."
            class="search-input"
          >
        </div>
      </div>
      
      <div class="workers-list">
        <div 
          v-for="worker in filteredWorkers" 
          :key="worker.id"
          class="worker-item"
          :class="{ active: worker.isActive }"
        >
          <div class="worker-avatar">{{ worker.name.charAt(0) }}</div>
          <div class="worker-info">
            <div class="worker-name">{{ worker.name }}</div>
            <div class="worker-phone">{{ formatPhone(worker.phone) }}</div>
            <div class="worker-department">{{ worker.department }}</div>
          </div>
          <div class="worker-status">
            <span class="status-badge" :class="{ active: worker.isActive }">
              {{ worker.isActive ? '✅ Actif' : '❌ Inactif' }}
            </span>
          </div>
          <div class="worker-actions">
            <button @click="editWorker(worker)" class="btn-icon">✏️</button>
            <button @click="toggleWorkerStatus(worker)" class="btn-icon">
              {{ worker.isActive ? '🔒' : '🔓' }}
            </button>
            <button @click="deleteWorker(worker)" class="btn-icon danger">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Hosts List -->
    <div class="section">
      <div class="section-header">
        <h3>🏠 Gestion des Hôtes</h3>
        <div class="search-box">
          <input 
            v-model="searchHost" 
            type="text" 
            placeholder="🔍 Rechercher..."
            class="search-input"
          >
        </div>
      </div>
      
      <div class="hosts-list">
        <div 
          v-for="host in filteredHosts" 
          :key="host.id"
          class="host-item"
          :class="{ active: host.isActive }"
        >
          <div class="host-avatar">🏠</div>
          <div class="host-info">
            <div class="host-name">{{ host.name }}</div>
            <div class="host-phone">{{ formatPhone(host.phone) }}</div>
            <div class="host-location">{{ host.location }}</div>
          </div>
          <div class="host-access">
            <div class="access-info">
              <span class="access-date">Du {{ formatDate(host.accessStart) }}</span>
              <span class="access-date">Au {{ formatDate(host.accessEnd) }}</span>
            </div>
          </div>
          <div class="host-actions">
            <button @click="editHost(host)" class="btn-icon">✏️</button>
            <button @click="toggleHostStatus(host)" class="btn-icon">
              {{ host.isActive ? '🔒' : '🔓' }}
            </button>
            <button @click="deleteHost(host)" class="btn-icon danger">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Worker Modal -->
    <div v-if="showAddWorker" class="modal-overlay" @click="closeAddWorker">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h3>➕ {{ editingWorker ? 'Modifier' : 'Ajouter' }} Employé</h3>
          <button @click="closeAddWorker" class="close-btn">✖</button>
        </div>
        <form @submit.prevent="saveWorker" class="modal-form">
          <div class="form-group">
            <label>👤 Nom complet</label>
            <input 
              v-model="workerForm.name" 
              type="text" 
              class="form-input"
              placeholder="Jean Dupont"
              required
            >
          </div>
          <div class="form-group">
            <label>📱 Téléphone</label>
            <input 
              v-model="workerForm.phone" 
              type="tel" 
              class="form-input"
              placeholder="77 123 45 67"
              @input="formatWorkerPhone"
              required
            >
          </div>
          <div class="form-group">
            <label>🔢 Code PIN (4 chiffres)</label>
            <input 
              v-model="workerForm.pin" 
              type="password" 
              class="form-input"
              placeholder="1234"
              maxlength="4"
              pattern="[0-9]{4}"
              required
            >
          </div>
          <div class="form-group">
            <label>🏢 Département</label>
            <select v-model="workerForm.department" class="form-input" required>
              <option value="">Sélectionner...</option>
              <option value="IT">Informatique</option>
              <option value="HR">Ressources Humaines</option>
              <option value="Marketing">Marketing</option>
              <option value="Sales">Ventes</option>
              <option value="Finance">Finance</option>
              <option value="Operations">Opérations</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeAddWorker" class="btn-secondary">
              Annuler
            </button>
            <button type="submit" class="btn-primary" :disabled="isLoading">
              {{ isLoading ? 'Sauvegarde...' : (editingWorker ? 'Modifier' : 'Ajouter') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Add Host Modal -->
    <div v-if="showAddHost" class="modal-overlay" @click="closeAddHost">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h3>🏠 {{ editingHost ? 'Modifier' : 'Ajouter' }} Hôte</h3>
          <button @click="closeAddHost" class="close-btn">✖</button>
        </div>
        <form @submit.prevent="saveHost" class="modal-form">
          <div class="form-group">
            <label>👤 Nom complet</label>
            <input 
              v-model="hostForm.name" 
              type="text" 
              class="form-input"
              placeholder="Marie Visiteur"
              required
            >
          </div>
          <div class="form-group">
            <label>📱 Téléphone</label>
            <input 
              v-model="hostForm.phone" 
              type="tel" 
              class="form-input"
              placeholder="78 987 65 43"
              @input="formatHostPhone"
              required
            >
          </div>
          <div class="form-group">
            <label>🔢 Code PIN (4 chiffres)</label>
            <input 
              v-model="hostForm.pin" 
              type="password" 
              class="form-input"
              placeholder="5678"
              maxlength="4"
              pattern="[0-9]{4}"
              required
            >
          </div>
          <div class="form-group">
            <label>📍 Lieu d'accès</label>
            <input 
              v-model="hostForm.location" 
              type="text" 
              class="form-input"
              placeholder="Salle de réunion A"
              required
            >
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>📅 Date début</label>
              <input 
                v-model="hostForm.accessStart" 
                type="date" 
                class="form-input"
                required
              >
            </div>
            <div class="form-group">
              <label>📅 Date fin</label>
              <input 
                v-model="hostForm.accessEnd" 
                type="date" 
                class="form-input"
                required
              >
            </div>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeAddHost" class="btn-secondary">
              Annuler
            </button>
            <button type="submit" class="btn-primary" :disabled="isLoading">
              {{ isLoading ? 'Sauvegarde...' : (editingHost ? 'Modifier' : 'Ajouter') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- CSV Import Modal -->
    <div v-if="showImportCSV" class="modal-overlay" @click="closeImportCSV">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h3>📋 Importer des Employés (CSV)</h3>
          <button @click="closeImportCSV" class="close-btn">✖</button>
        </div>
        <div class="modal-content">
          <div class="csv-instructions">
            <h4>Format requis:</h4>
            <code>nom,telephone,pin,departement</code>
            <p>Exemple:</p>
            <code>Jean Dupont,771234567,1234,IT</code>
          </div>
          
          <div class="file-upload">
            <input 
              ref="csvFile"
              type="file" 
              accept=".csv"
              @change="handleCSVFile"
              class="file-input"
            >
            <button @click="$refs.csvFile.click()" class="upload-btn">
              📁 Choisir fichier CSV
            </button>
          </div>
          
          <div v-if="csvPreview.length > 0" class="csv-preview">
            <h4>Aperçu ({{ csvPreview.length }} entrées):</h4>
            <div class="preview-table">
              <div class="preview-header">
                <span>Nom</span>
                <span>Téléphone</span>
                <span>Département</span>
              </div>
              <div 
                v-for="(row, index) in csvPreview.slice(0, 5)" 
                :key="index"
                class="preview-row"
              >
                <span>{{ row.name }}</span>
                <span>{{ formatPhone(row.phone) }}</span>
                <span>{{ row.department }}</span>
              </div>
              <div v-if="csvPreview.length > 5" class="preview-more">
                ... et {{ csvPreview.length - 5 }} autres
              </div>
            </div>
          </div>
          
          <div class="modal-actions">
            <button @click="closeImportCSV" class="btn-secondary">
              Annuler
            </button>
            <button 
              @click="importCSV" 
              class="btn-primary" 
              :disabled="csvPreview.length === 0 || isLoading"
            >
              {{ isLoading ? 'Import...' : `Importer ${csvPreview.length} employés` }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { formatSenegalPhone, formatPhoneInput, formatSenegalDate } from '@/utils/senegalFormat.js'

export default {
  name: 'ManagerDashboard',
  data() {
    return {
      user: {
        name: 'Manager',
        email: '',
        role: 'manager'
      },
      searchWorker: '',
      searchHost: '',
      showAddWorker: false,
      showAddHost: false,
      showImportCSV: false,
      editingWorker: null,
      editingHost: null,
      isLoading: false,
      csvPreview: [],
      stats: {
        totalWorkers: 0,
        totalHosts: 0,
        activeAccess: 0,
        todayAccess: 0
      },
      workerForm: {
        name: '',
        phone: '',
        pin: '',
        department: ''
      },
      hostForm: {
        name: '',
        phone: '',
        pin: '',
        location: '',
        accessStart: '',
        accessEnd: ''
      },
      workers: [],
      hosts: []
    }
  },
  computed: {
    filteredWorkers() {
      return this.workers.filter(worker =>
        worker.name.toLowerCase().includes(this.searchWorker.toLowerCase()) ||
        worker.phone.includes(this.searchWorker) ||
        worker.department.toLowerCase().includes(this.searchWorker.toLowerCase())
      );
    },
    filteredHosts() {
      return this.hosts.filter(host =>
        host.name.toLowerCase().includes(this.searchHost.toLowerCase()) ||
        host.phone.includes(this.searchHost) ||
        host.location.toLowerCase().includes(this.searchHost.toLowerCase())
      );
    }
  },
  async mounted() {
    this.loadUser();
    await this.loadDashboardData();
  },
  methods: {
    loadUser() {
      const storedUser = localStorage.getItem('securaccess_user');
      if (storedUser) {
        try {
          const userData = JSON.parse(storedUser);
          this.user = {
            name: userData.name || 'Manager',
            email: userData.email || '',
            role: userData.role || 'manager'
          };
          if (this.user.role !== 'manager') {
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
    
    async loadDashboardData() {
      try {
        // Load statistics
        const statsResponse = await this.$api.getDashboardStats();
        if (statsResponse.success) {
          this.stats = statsResponse.stats;
        }
        
        // Load workers
        const workersResponse = await this.$api.getAllWorkers();
        if (workersResponse.success) {
          this.workers = workersResponse.workers;
        }
        
        // Load hosts
        const hostsResponse = await this.$api.getAllHosts();
        if (hostsResponse.success) {
          this.hosts = hostsResponse.hosts.map(host => ({
            ...host,
            accessStart: new Date(host.accessStartDate),
            accessEnd: new Date(host.accessEndDate)
          }));
        }
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      }
    },
    
    formatPhone(phone) {
      return formatSenegalPhone(phone);
    },
    
    formatDate(date) {
      return formatSenegalDate(date);
    },
    
    formatWorkerPhone() {
      this.workerForm.phone = formatPhoneInput(this.workerForm.phone);
    },
    
    formatHostPhone() {
      this.hostForm.phone = formatPhoneInput(this.hostForm.phone);
    },
    
    async saveWorker() {
      this.isLoading = true;
      try {
        const cleanPhone = this.workerForm.phone.replace(/\s/g, '');
        const workerData = {
          name: this.workerForm.name,
          phone: cleanPhone,
          pin: this.workerForm.pin,
          department: this.workerForm.department
        };
        
        if (this.editingWorker) {
          const response = await this.$api.updateWorker(this.editingWorker.id, workerData);
          if (response.success) {
            const index = this.workers.findIndex(w => w.id === this.editingWorker.id);
            this.workers[index] = { ...this.editingWorker, ...workerData };
          }
        } else {
          const response = await this.$api.createWorker(workerData);
          if (response.success) {
            this.workers.push({
              id: response.worker.id,
              ...workerData,
              isActive: true,
              createdAt: new Date()
            });
            this.stats.totalWorkers++;
          }
        }
        
        this.closeAddWorker();
      } catch (error) {
        console.error('Error saving worker:', error);
        alert('Erreur lors de la sauvegarde de l\'employé');
      } finally {
        this.isLoading = false;
      }
    },
    
    async saveHost() {
      this.isLoading = true;
      try {
        const cleanPhone = this.hostForm.phone.replace(/\s/g, '');
        const hostData = {
          name: this.hostForm.name,
          phone: cleanPhone,
          pin: this.hostForm.pin,
          location: this.hostForm.location,
          accessStartDate: this.hostForm.accessStart,
          accessEndDate: this.hostForm.accessEnd
        };
        
        if (this.editingHost) {
          const response = await this.$api.updateHost(this.editingHost.id, hostData);
          if (response.success) {
            const index = this.hosts.findIndex(h => h.id === this.editingHost.id);
            this.hosts[index] = {
              ...this.editingHost,
              ...hostData,
              accessStart: new Date(this.hostForm.accessStart),
              accessEnd: new Date(this.hostForm.accessEnd)
            };
          }
        } else {
          const response = await this.$api.createHost(hostData);
          if (response.success) {
            this.hosts.push({
              id: response.host.id,
              ...hostData,
              accessStart: new Date(this.hostForm.accessStart),
              accessEnd: new Date(this.hostForm.accessEnd),
              isActive: true,
              createdAt: new Date()
            });
            this.stats.totalHosts++;
          }
        }
        
        this.closeAddHost();
      } catch (error) {
        console.error('Error saving host:', error);
        alert('Erreur lors de la sauvegarde de l\'hôte');
      } finally {
        this.isLoading = false;
      }
    },
    
    editWorker(worker) {
      this.editingWorker = worker;
      this.workerForm = {
        name: worker.name,
        phone: this.formatPhone(worker.phone),
        pin: worker.pin,
        department: worker.department
      };
      this.showAddWorker = true;
    },
    
    editHost(host) {
      this.editingHost = host;
      this.hostForm = {
        name: host.name,
        phone: this.formatPhone(host.phone),
        pin: host.pin,
        location: host.location,
        accessStart: host.accessStart.toISOString().split('T')[0],
        accessEnd: host.accessEnd.toISOString().split('T')[0]
      };
      this.showAddHost = true;
    },
    
    async toggleWorkerStatus(worker) {
      try {
        const newStatus = !worker.isActive;
        const response = await this.$api.toggleWorkerStatus(worker.id, newStatus);
        if (response.success) {
          worker.isActive = newStatus;
          this.stats.activeAccess += newStatus ? 1 : -1;
        }
      } catch (error) {
        console.error('Error toggling worker status:', error);
        alert('Erreur lors de la modification du statut');
      }
    },
    
    async toggleHostStatus(host) {
      try {
        const newStatus = !host.isActive;
        const response = await this.$api.toggleHostStatus(host.id, newStatus);
        if (response.success) {
          host.isActive = newStatus;
          this.stats.activeAccess += newStatus ? 1 : -1;
        }
      } catch (error) {
        console.error('Error toggling host status:', error);
        alert('Erreur lors de la modification du statut');
      }
    },
    
    async deleteWorker(worker) {
      if (confirm(`Êtes-vous sûr de vouloir supprimer ${worker.name} ?`)) {
        try {
          const response = await this.$api.deleteWorker(worker.id);
          if (response.success) {
            const index = this.workers.findIndex(w => w.id === worker.id);
            this.workers.splice(index, 1);
            this.stats.totalWorkers--;
            if (worker.isActive) this.stats.activeAccess--;
          }
        } catch (error) {
          console.error('Error deleting worker:', error);
          alert('Erreur lors de la suppression de l\'employé');
        }
      }
    },
    
    async deleteHost(host) {
      if (confirm(`Êtes-vous sûr de vouloir supprimer ${host.name} ?`)) {
        try {
          const response = await this.$api.deleteHost(host.id);
          if (response.success) {
            const index = this.hosts.findIndex(h => h.id === host.id);
            this.hosts.splice(index, 1);
            this.stats.totalHosts--;
            if (host.isActive) this.stats.activeAccess--;
          }
        } catch (error) {
          console.error('Error deleting host:', error);
          alert('Erreur lors de la suppression de l\'hôte');
        }
      }
    },
    
    handleCSVFile(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.parseCSV(e.target.result);
        };
        reader.readAsText(file);
      }
    },
    
    parseCSV(csvText) {
      const lines = csvText.split('\n').filter(line => line.trim());
      const preview = [];
      
      // Skip header if exists
      const startIndex = lines[0].toLowerCase().includes('nom') ? 1 : 0;
      
      for (let i = startIndex; i < lines.length; i++) {
        const [name, phone, pin, department] = lines[i].split(',').map(field => field.trim());
        
        if (name && phone && pin && department) {
          preview.push({
            name,
            phone: phone.replace(/\D/g, ''),
            pin,
            department
          });
        }
      }
      
      this.csvPreview = preview;
    },
    
    async importCSV() {
      this.isLoading = true;
      try {
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        let imported = 0;
        for (const row of this.csvPreview) {
          // Check if worker already exists
          const exists = this.workers.some(w => w.phone === row.phone);
          if (!exists) {
            this.workers.push({
              id: Date.now() + imported,
              name: row.name,
              phone: row.phone,
              pin: row.pin,
              department: row.department,
              isActive: true,
              createdAt: new Date()
            });
            imported++;
          }
        }
        
        this.stats.totalWorkers += imported;
        this.stats.activeAccess += imported;
        
        alert(`${imported} employés importés avec succès !`);
        this.closeImportCSV();
        
      } finally {
        this.isLoading = false;
      }
    },
    
    closeAddWorker() {
      this.showAddWorker = false;
      this.editingWorker = null;
      this.workerForm = { name: '', phone: '', pin: '', department: '' };
    },
    
    closeAddHost() {
      this.showAddHost = false;
      this.editingHost = null;
      this.hostForm = { name: '', phone: '', pin: '', location: '', accessStart: '', accessEnd: '' };
    },
    
    closeImportCSV() {
      this.showImportCSV = false;
      this.csvPreview = [];
      if (this.$refs.csvFile) {
        this.$refs.csvFile.value = '';
      }
    },
    
    logout() {
      localStorage.removeItem('securaccess_user');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.manager-dashboard {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 2rem;
  width: 50px;
  height: 50px;
  background: #f7fafc;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2d3748;
}

.stat-label {
  font-size: 0.8rem;
  color: #718096;
}

.action-buttons {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.action-btn {
  padding: 0.75rem 1rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
  flex: 1;
  min-width: 150px;
}

.action-btn:hover {
  transform: translateY(-2px);
}

.action-btn.primary {
  background: linear-gradient(135deg, #48bb78, #38a169);
  color: white;
}

.action-btn.secondary {
  background: linear-gradient(135deg, #4299e1, #3182ce);
  color: white;
}

.action-btn.tertiary {
  background: linear-gradient(135deg, #ed8936, #dd6b20);
  color: white;
}

.section {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.25rem;
}

.search-box {
  flex: 1;
  max-width: 300px;
}

.search-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
}

.workers-list,
.hosts-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.worker-item,
.host-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.worker-item:hover,
.host-item:hover {
  border-color: #cbd5e0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.worker-item.active,
.host-item.active {
  border-color: #48bb78;
  background: #f0fff4;
}

.worker-avatar,
.host-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #4299e1;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.1rem;
}

.host-avatar {
  background: #ed8936;
  font-size: 1.25rem;
}

.worker-info,
.host-info {
  flex: 1;
}

.worker-name,
.host-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.worker-phone,
.host-phone {
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 0.25rem;
}

.worker-department,
.host-location {
  font-size: 0.8rem;
  color: #4a5568;
  padding: 0.25rem 0.5rem;
  background: #edf2f7;
  border-radius: 12px;
  display: inline-block;
}

.worker-status {
  display: flex;
  align-items: center;
}

.status-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-badge.active {
  background: #c6f6d5;
  color: #22543d;
}

.status-badge:not(.active) {
  background: #fed7d7;
  color: #c53030;
}

.host-access {
  text-align: center;
}

.access-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.access-date {
  font-size: 0.8rem;
  color: #4a5568;
}

.worker-actions,
.host-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: none;
  border: none;
  font-size: 1.1rem;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.btn-icon:hover {
  background: #edf2f7;
}

.btn-icon.danger:hover {
  background: #fed7d7;
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
  max-width: 500px;
  max-height: 90vh;
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

.modal-form,
.modal-content {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.modal-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.btn-secondary,
.btn-primary {
  flex: 1;
  padding: 0.75rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-secondary {
  background: #f7fafc;
  border: 1px solid #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #edf2f7;
}

.btn-primary {
  background: #4299e1;
  border: 1px solid #4299e1;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #3182ce;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* CSV Import Styles */
.csv-instructions {
  background: #edf2f7;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
}

.csv-instructions h4 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
}

.csv-instructions code {
  background: #e2e8f0;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-family: monospace;
  display: block;
  margin: 0.5rem 0;
}

.file-upload {
  text-align: center;
  margin-bottom: 1rem;
}

.file-input {
  display: none;
}

.upload-btn {
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0.75rem 1.5rem;
  cursor: pointer;
  font-weight: 600;
}

.csv-preview {
  margin-bottom: 1rem;
}

.csv-preview h4 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
}

.preview-table {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.preview-header,
.preview-row {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 1rem;
  padding: 0.5rem;
}

.preview-header {
  background: #f7fafc;
  font-weight: 600;
  color: #2d3748;
}

.preview-row {
  border-top: 1px solid #e2e8f0;
  font-size: 0.9rem;
}

.preview-more {
  padding: 0.5rem;
  text-align: center;
  color: #718096;
  font-style: italic;
  border-top: 1px solid #e2e8f0;
}

/* Mobile Optimizations */
@media (max-width: 768px) {
  .manager-dashboard {
    padding: 0.5rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-btn {
    min-width: auto;
  }
  
  .section-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    max-width: none;
  }
  
  .worker-item,
  .host-item {
    flex-wrap: wrap;
  }
  
  .worker-actions,
  .host-actions {
    width: 100%;
    justify-content: center;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .preview-header,
  .preview-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
}
</style>