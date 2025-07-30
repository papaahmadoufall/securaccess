<template>
  <div class="container">
    <div class="header">
      <h1 class="title">📅 Gestion des Événements</h1>
      <button class="btn btn-primary" @click="showCreateModal = true">
        ➕ Nouvel Événement
      </button>
    </div>

    <!-- Events List -->
    <div class="events-grid">
      <div v-for="event in events" :key="event.id" class="event-card">
        <div class="event-header">
          <h3 class="event-title">{{ event.nom }}</h3>
          <span class="event-status" :class="{ active: event.actif, inactive: !event.actif }">
            {{ event.actif ? 'Actif' : 'Inactif' }}
          </span>
        </div>
        
        <p class="event-description">{{ event.description }}</p>
        
        <div class="event-details">
          <div class="detail-item">
            <span class="label">📍 Lieu:</span>
            <span>{{ event.lieu || 'Non spécifié' }}</span>
          </div>
          <div class="detail-item">
            <span class="label">🕐 Début:</span>
            <span>{{ formatDate(event.dateDebut) }}</span>
          </div>
          <div class="detail-item">
            <span class="label">🕐 Fin:</span>
            <span>{{ formatDate(event.dateFin) }}</span>
          </div>
        </div>
        
        <div class="event-actions">
          <button class="btn btn-secondary" @click="editEvent(event)">
            ✏️ Modifier
          </button>
          <button class="btn btn-primary" @click="generateQRCodes(event)">
            📱 QR Codes
          </button>
        </div>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModals">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>{{ showCreateModal ? 'Nouvel Événement' : 'Modifier Événement' }}</h2>
          <button class="close-btn" @click="closeModals">✖</button>
        </div>
        
        <form @submit.prevent="saveEvent" class="modal-form">
          <div class="form-group">
            <label>Nom de l'événement *</label>
            <input 
              v-model="currentEvent.nom" 
              type="text" 
              required 
              class="form-input"
              placeholder="Ex: Réunion équipe marketing"
            >
          </div>
          
          <div class="form-group">
            <label>Description</label>
            <textarea 
              v-model="currentEvent.description" 
              class="form-textarea"
              placeholder="Description détaillée de l'événement..."
              rows="3"
            ></textarea>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Date de début *</label>
              <input 
                v-model="currentEvent.dateDebut" 
                type="datetime-local" 
                required 
                class="form-input"
              >
            </div>
            
            <div class="form-group">
              <label>Date de fin *</label>
              <input 
                v-model="currentEvent.dateFin" 
                type="datetime-local" 
                required 
                class="form-input"
              >
            </div>
          </div>
          
          <div class="form-group">
            <label>Lieu</label>
            <input 
              v-model="currentEvent.lieu" 
              type="text" 
              class="form-input"
              placeholder="Ex: Salle de conférence A"
            >
          </div>
          
          <div class="modal-actions">
            <button type="button" class="btn btn-secondary" @click="closeModals">
              Annuler
            </button>
            <button type="submit" class="btn btn-primary">
              {{ showCreateModal ? 'Créer' : 'Sauvegarder' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Events',
  data() {
    return {
      events: [
        {
          id: 1,
          nom: 'Réunion Équipe Marketing',
          description: 'Présentation des nouvelles stratégies marketing pour Q2',
          dateDebut: '2024-02-15T09:00:00',
          dateFin: '2024-02-15T11:00:00',
          lieu: 'Salle de conférence A',
          actif: true
        },
        {
          id: 2,
          nom: 'Formation Sécurité',
          description: 'Formation obligatoire sur les nouvelles procédures de sécurité',
          dateDebut: '2024-02-20T14:00:00',
          dateFin: '2024-02-20T16:00:00',
          lieu: 'Auditorium',
          actif: true
        },
        {
          id: 3,
          nom: 'Journée Portes Ouvertes',
          description: 'Présentation de l\'entreprise aux visiteurs',
          dateDebut: '2024-02-25T10:00:00',
          dateFin: '2024-02-25T17:00:00',
          lieu: 'Hall d\'accueil',
          actif: false
        }
      ],
      showCreateModal: false,
      showEditModal: false,
      currentEvent: {
        nom: '',
        description: '',
        dateDebut: '',
        dateFin: '',
        lieu: ''
      }
    }
  },
  methods: {
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString('fr-FR');
    },
    
    editEvent(event) {
      this.currentEvent = { ...event };
      this.showEditModal = true;
    },
    
    closeModals() {
      this.showCreateModal = false;
      this.showEditModal = false;
      this.currentEvent = {
        nom: '',
        description: '',
        dateDebut: '',
        dateFin: '',
        lieu: ''
      };
    },
    
    saveEvent() {
      if (this.showCreateModal) {
        // Create new event
        const newEvent = {
          ...this.currentEvent,
          id: Date.now(),
          actif: true
        };
        this.events.push(newEvent);
      } else {
        // Update existing event
        const index = this.events.findIndex(e => e.id === this.currentEvent.id);
        if (index !== -1) {
          this.events[index] = { ...this.currentEvent };
        }
      }
      this.closeModals();
    },
    
    generateQRCodes(event) {
      alert(`Génération des QR codes pour l'événement: ${event.nom}`);
      // TODO: Implement QR code generation
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

.events-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 1.5rem;
}

.event-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.event-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.event-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.event-status {
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.event-status.active {
  background: #dcfce7;
  color: #15803d;
}

.event-status.inactive {
  background: #fee2e2;
  color: #dc2626;
}

.event-description {
  color: #6b7280;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.event-details {
  margin-bottom: 1.5rem;
}

.detail-item {
  display: flex;
  margin-bottom: 0.5rem;
}

.label {
  font-weight: 500;
  margin-right: 0.5rem;
  min-width: 80px;
}

.event-actions {
  display: flex;
  gap: 0.75rem;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
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
}

.modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
}

.modal-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 1rem;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .events-grid {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .event-actions {
    flex-direction: column;
  }
}
</style>