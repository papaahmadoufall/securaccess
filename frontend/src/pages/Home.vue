<template>
  <div class="container">
    <h1 class="title">
      🎯 SecurAccess Enterprise 🎯
    </h1>
    
    <div class="cards-container">
      <!-- Gestion des Événements -->
      <div class="card">
        <div class="card-icon blue">📅</div>
        <h3 class="card-title">Gestion des Événements</h3>
        <p class="card-description">
          Créez et gérez vos événements, invitez des participants et définissez les zones d'accès.
        </p>
        <button class="btn btn-blue">Gérer les événements</button>
      </div>

      <!-- Scanner QR Code -->
      <div class="card">
        <div class="card-icon green">📱</div>
        <h3 class="card-title">Scanner QR Code</h3>
        <p class="card-description">
          Scannez les QR codes pour valider les accès et enregistrer les entrées/sorties.
        </p>
        <button class="btn btn-green">Ouvrir le scanner</button>
      </div>

      <!-- Rapports et Statistiques -->
      <div class="card">
        <div class="card-icon purple">📊</div>
        <h3 class="card-title">Rapports & Statistiques</h3>
        <p class="card-description">
          Consultez les rapports de fréquentation et les statistiques d'accès en temps réel.
        </p>
        <button class="btn btn-purple">Voir les rapports</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.container {
  padding: 2rem 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

.title {
  font-size: 3rem;
  font-weight: bold;
  text-align: center;
  color: #1f2937;
  margin-bottom: 3rem;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  max-width: 900px;
  margin: 0 auto;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px -3px rgba(0, 0, 0, 0.1);
}

.card-icon {
  font-size: 3rem;
  width: 80px;
  height: 80px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}

.card-icon.blue {
  background: #dbeafe;
}

.card-icon.green {
  background: #dcfce7;
}

.card-icon.purple {
  background: #e9d5ff;
}

.card-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 1rem;
}

.card-description {
  color: #6b7280;
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: white;
}

.btn-blue {
  background: #2563eb;
}

.btn-blue:hover {
  background: #1d4ed8;
}

.btn-green {
  background: #16a34a;
}

.btn-green:hover {
  background: #15803d;
}

.btn-purple {
  background: #9333ea;
}

.btn-purple:hover {
  background: #7c3aed;
}

@media (max-width: 768px) {
  .title {
    font-size: 2rem;
  }
  
  .cards-container {
    grid-template-columns: 1fr;
  }
}
</style>