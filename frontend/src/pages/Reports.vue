<template>
  <div class="container">
    <div class="header">
      <h1 class="title">📊 Rapports & Statistiques</h1>
      <div class="date-filter">
        <select v-model="selectedPeriod" class="period-select" @change="updateData">
          <option value="today">Aujourd'hui</option>
          <option value="week">Cette semaine</option>
          <option value="month">Ce mois</option>
          <option value="year">Cette année</option>
        </select>
      </div>
    </div>

    <!-- Key Metrics -->
    <div class="metrics-section">
      <h2>📈 Métriques Principales</h2>
      
      <div class="metrics-grid">
        <div class="metric-card primary">
          <div class="metric-icon">👥</div>
          <div class="metric-content">
            <div class="metric-number">{{ metrics.totalAccess }}</div>
            <div class="metric-label">Accès totaux</div>
            <div class="metric-change positive">+{{ metrics.accessGrowth }}%</div>
          </div>
        </div>
        
        <div class="metric-card success">
          <div class="metric-icon">✅</div>
          <div class="metric-content">
            <div class="metric-number">{{ metrics.successfulAccess }}</div>
            <div class="metric-label">Accès autorisés</div>
            <div class="metric-change positive">{{ (metrics.successfulAccess / metrics.totalAccess * 100).toFixed(1) }}%</div>
          </div>
        </div>
        
        <div class="metric-card error">
          <div class="metric-icon">❌</div>
          <div class="metric-content">
            <div class="metric-number">{{ metrics.deniedAccess }}</div>
            <div class="metric-label">Accès refusés</div>
            <div class="metric-change negative">{{ (metrics.deniedAccess / metrics.totalAccess * 100).toFixed(1) }}%</div>
          </div>
        </div>
        
        <div class="metric-card info">
          <div class="metric-icon">📅</div>
          <div class="metric-content">
            <div class="metric-number">{{ metrics.activeEvents }}</div>
            <div class="metric-label">Événements actifs</div>
            <div class="metric-change neutral">{{ metrics.eventGrowth > 0 ? '+' : '' }}{{ metrics.eventGrowth }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Access by Type Chart -->
    <div class="chart-section">
      <h2>📊 Répartition des Accès par Type</h2>
      
      <div class="chart-container">
        <div class="chart-legend">
          <div v-for="item in accessByType" :key="item.type" class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: item.color }"></div>
            <span class="legend-label">{{ item.type }}</span>
            <span class="legend-value">{{ item.count }}</span>
          </div>
        </div>
        
        <div class="pie-chart">
          <svg viewBox="0 0 200 200" class="pie-svg">
            <circle cx="100" cy="100" r="80" fill="none" stroke="#e5e7eb" stroke-width="4"/>
            <circle 
              v-for="(segment, index) in pieSegments" 
              :key="index"
              cx="100" 
              cy="100" 
              r="80" 
              fill="none" 
              :stroke="segment.color" 
              stroke-width="20"
              :stroke-dasharray="segment.dashArray"
              :stroke-dashoffset="segment.dashOffset"
              transform="rotate(-90 100 100)"
            />
          </svg>
          <div class="pie-center">
            <div class="pie-total">{{ metrics.totalAccess }}</div>
            <div class="pie-label">Total</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="activity-section">
      <h2>🕐 Activité Récente</h2>
      
      <div class="activity-list">
        <div v-for="activity in recentActivity" :key="activity.id" class="activity-item">
          <div class="activity-icon" :class="activity.type">
            {{ activity.icon }}
          </div>
          <div class="activity-content">
            <div class="activity-title">{{ activity.title }}</div>
            <div class="activity-description">{{ activity.description }}</div>
            <div class="activity-time">{{ formatTime(activity.timestamp) }}</div>
          </div>
          <div class="activity-status" :class="activity.status">
            {{ activity.statusText }}
          </div>
        </div>
      </div>
    </div>

    <!-- Top Users -->
    <div class="users-section">
      <h2>👑 Utilisateurs les Plus Actifs</h2>
      
      <div class="users-table">
        <div class="table-header">
          <div class="header-cell">Utilisateur</div>
          <div class="header-cell">Type</div>
          <div class="header-cell">Accès</div>
          <div class="header-cell">Dernière activité</div>
        </div>
        
        <div v-for="user in topUsers" :key="user.id" class="table-row">
          <div class="cell user-cell">
            <div class="user-avatar">{{ user.nom.charAt(0) }}</div>
            <div class="user-info">
              <div class="user-name">{{ user.nom }} {{ user.prenom }}</div>
              <div class="user-email">{{ user.email }}</div>
            </div>
          </div>
          <div class="cell">
            <span class="user-type" :class="user.type.toLowerCase()">{{ user.type }}</span>
          </div>
          <div class="cell">
            <div class="access-count">{{ user.accessCount }}</div>
          </div>
          <div class="cell">
            <div class="last-access">{{ formatDate(user.lastAccess) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Export Section -->
    <div class="export-section">
      <h2>📁 Exportation des Données</h2>
      
      <div class="export-options">
        <button class="btn btn-secondary" @click="exportData('csv')">
          📊 Exporter CSV
        </button>
        <button class="btn btn-secondary" @click="exportData('pdf')">
          📄 Exporter PDF
        </button>
        <button class="btn btn-secondary" @click="exportData('excel')">
          📈 Exporter Excel
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Reports',
  data() {
    return {
      selectedPeriod: 'week',
      metrics: {
        totalAccess: 1247,
        successfulAccess: 1156,
        deniedAccess: 91,
        activeEvents: 8,
        accessGrowth: 12.5,
        eventGrowth: 2
      },
      accessByType: [
        { type: 'Employés', count: 856, color: '#2563eb' },
        { type: 'Visiteurs', count: 234, color: '#10b981' },
        { type: 'Prestataires', count: 157, color: '#f59e0b' }
      ],
      recentActivity: [
        {
          id: 1,
          icon: '✅',
          type: 'success',
          title: 'Accès autorisé',
          description: 'Jean Dupont - Salle de conférence A',
          timestamp: new Date(Date.now() - 300000),
          status: 'success',
          statusText: 'Autorisé'
        },
        {
          id: 2,
          icon: '❌',
          type: 'error',
          title: 'Accès refusé',
          description: 'QR Code expiré - Événement Marketing',
          timestamp: new Date(Date.now() - 900000),
          status: 'error',
          statusText: 'Refusé'
        },
        {
          id: 3,
          icon: '📅',
          type: 'info',
          title: 'Nouvel événement',
          description: 'Formation Sécurité créée par Admin',
          timestamp: new Date(Date.now() - 1800000),
          status: 'info',
          statusText: 'Créé'
        },
        {
          id: 4,
          icon: '👥',
          type: 'success',
          title: 'Utilisateur enregistré',
          description: 'Marie Martin - Type: Employé',
          timestamp: new Date(Date.now() - 3600000),
          status: 'success',
          statusText: 'Actif'
        }
      ],
      topUsers: [
        {
          id: 1,
          nom: 'Dupont',
          prenom: 'Jean',
          email: 'jean.dupont@entreprise.com',
          type: 'EMPLOYE',
          accessCount: 45,
          lastAccess: new Date(Date.now() - 1800000)
        },
        {
          id: 2,
          nom: 'Martin',
          prenom: 'Marie',
          email: 'marie.martin@entreprise.com',
          type: 'ADMIN',
          accessCount: 38,
          lastAccess: new Date(Date.now() - 3600000)
        },
        {
          id: 3,
          nom: 'Durand',
          prenom: 'Pierre',
          email: 'pierre.durand@visiteur.com',
          type: 'VISITEUR',
          accessCount: 12,
          lastAccess: new Date(Date.now() - 7200000)
        }
      ]
    }
  },
  computed: {
    pieSegments() {
      const total = this.accessByType.reduce((sum, item) => sum + item.count, 0);
      const circumference = 2 * Math.PI * 80;
      let cumulativeOffset = 0;
      
      return this.accessByType.map(item => {
        const percentage = item.count / total;
        const dashLength = percentage * circumference;
        const segment = {
          color: item.color,
          dashArray: `${dashLength} ${circumference - dashLength}`,
          dashOffset: -cumulativeOffset
        };
        cumulativeOffset += dashLength;
        return segment;
      });
    }
  },
  methods: {
    updateData() {
      // TODO: Fetch data based on selected period
      console.log('Updating data for period:', this.selectedPeriod);
    },
    
    formatTime(date) {
      return date.toLocaleTimeString('fr-FR');
    },
    
    formatDate(date) {
      return date.toLocaleDateString('fr-FR');
    },
    
    exportData(format) {
      alert(`Exportation des données au format ${format.toUpperCase()}`);
      // TODO: Implement actual data export
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

.date-filter {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.period-select {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  background: white;
  font-size: 1rem;
}

.metrics-section,
.chart-section,
.activity-section,
.users-section,
.export-section {
  margin-bottom: 3rem;
}

.metrics-section h2,
.chart-section h2,
.activity-section h2,
.users-section h2,
.export-section h2 {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1f2937;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
  border-top: 4px solid;
}

.metric-card.primary { border-top-color: #2563eb; }
.metric-card.success { border-top-color: #10b981; }
.metric-card.error { border-top-color: #ef4444; }
.metric-card.info { border-top-color: #f59e0b; }

.metric-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: #f8fafc;
}

.metric-content {
  flex: 1;
}

.metric-number {
  font-size: 2rem;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 0.25rem;
}

.metric-label {
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.metric-change {
  font-size: 0.875rem;
  font-weight: 500;
}

.metric-change.positive { color: #10b981; }
.metric-change.negative { color: #ef4444; }
.metric-change.neutral { color: #6b7280; }

.chart-container {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 3rem;
}

.chart-legend {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.legend-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
}

.legend-label {
  flex: 1;
  font-weight: 500;
}

.legend-value {
  font-weight: bold;
  color: #1f2937;
}

.pie-chart {
  position: relative;
  width: 200px;
  height: 200px;
}

.pie-svg {
  width: 100%;
  height: 100%;
}

.pie-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.pie-total {
  font-size: 1.5rem;
  font-weight: bold;
  color: #1f2937;
}

.pie-label {
  color: #6b7280;
  font-size: 0.875rem;
}

.activity-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
}

.activity-icon.success { background: #dcfce7; }
.activity-icon.error { background: #fee2e2; }
.activity-icon.info { background: #dbeafe; }

.activity-content {
  flex: 1;
}

.activity-title {
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.25rem;
}

.activity-description {
  color: #6b7280;
  font-size: 0.875rem;
  margin-bottom: 0.25rem;
}

.activity-time {
  color: #9ca3af;
  font-size: 0.75rem;
}

.activity-status {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.activity-status.success {
  background: #dcfce7;
  color: #15803d;
}

.activity-status.error {
  background: #fee2e2;
  color: #dc2626;
}

.activity-status.info {
  background: #dbeafe;
  color: #1d4ed8;
}

.users-table {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-bottom: 1px solid #e5e7eb;
}

.header-cell {
  font-weight: 600;
  color: #374151;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
  align-items: center;
}

.table-row:last-child {
  border-bottom: none;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #2563eb;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.user-name {
  font-weight: 500;
  color: #1f2937;
}

.user-email {
  font-size: 0.875rem;
  color: #6b7280;
}

.user-type {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.user-type.employe {
  background: #dbeafe;
  color: #1d4ed8;
}

.user-type.admin {
  background: #fef3c7;
  color: #d97706;
}

.user-type.visiteur {
  background: #dcfce7;
  color: #15803d;
}

.access-count {
  font-weight: bold;
  color: #1f2937;
}

.last-access {
  color: #6b7280;
  font-size: 0.875rem;
}

.export-options {
  display: flex;
  gap: 1rem;
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
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
  
  .chart-container {
    flex-direction: column;
    gap: 2rem;
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
  }
  
  .export-options {
    flex-direction: column;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>