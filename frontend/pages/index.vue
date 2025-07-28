<template>
  <div class="px-4 py-6 sm:px-0">
    <div class="text-center">
      <h1 class="text-4xl font-bold text-gray-900 dark:text-white mb-8">
        Bienvenue sur SecurAccess Enterprise
      </h1>
      
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto">
        <!-- Gestion des Événements -->
        <div class="card">
          <div class="text-center">
            <div class="w-12 h-12 mx-auto bg-primary-100 rounded-lg flex items-center justify-center mb-4">
              <svg class="w-6 h-6 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">
              Gestion des Événements
            </h3>
            <p class="text-gray-600 dark:text-gray-400 mb-4">
              Créez et gérez vos événements, invitez des participants et définissez les zones d'accès.
            </p>
            <NuxtLink to="/events" class="btn btn-primary">
              Gérer les événements
            </NuxtLink>
          </div>
        </div>

        <!-- Scanner QR Code -->
        <div class="card">
          <div class="text-center">
            <div class="w-12 h-12 mx-auto bg-green-100 rounded-lg flex items-center justify-center mb-4">
              <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">
              Scanner QR Code
            </h3>
            <p class="text-gray-600 dark:text-gray-400 mb-4">
              Scannez les QR codes pour valider les accès et enregistrer les entrées/sorties.
            </p>
            <NuxtLink to="/scanner" class="btn btn-primary">
              Ouvrir le scanner
            </NuxtLink>
          </div>
        </div>

        <!-- Rapports et Statistiques -->
        <div class="card">
          <div class="text-center">
            <div class="w-12 h-12 mx-auto bg-purple-100 rounded-lg flex items-center justify-center mb-4">
              <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">
              Rapports & Statistiques
            </h3>
            <p class="text-gray-600 dark:text-gray-400 mb-4">
              Consultez les rapports de fréquentation et les statistiques d'accès en temps réel.
            </p>
            <NuxtLink to="/reports" class="btn btn-primary">
              Voir les rapports
            </NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
useHead({
  title: 'Accueil - SecurAccess Enterprise'
})
</script>