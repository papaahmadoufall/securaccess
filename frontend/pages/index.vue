<template>
  <div>
    <h1 style="color: red; font-size: 48px; text-align: center; margin: 50px 0;">
      🎯 SecurAccess Enterprise - WORKING! 🎯
    </h1>
    <div style="max-width: 800px; margin: 0 auto; padding: 20px; background: #f3f4f6; border-radius: 8px;">
      <p style="font-size: 18px; text-align: center; margin-bottom: 30px;">
        Test de fonctionnement de l'application Nuxt.js
      </p>
      
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px;">
        <div style="background: white; padding: 20px; border-radius: 8px; text-align: center;">
          <h3 style="font-size: 20px; margin-bottom: 10px;">📅 Gestion des Événements</h3>
          <p>Créez et gérez vos événements</p>
        </div>
        
        <div style="background: white; padding: 20px; border-radius: 8px; text-align: center;">
          <h3 style="font-size: 20px; margin-bottom: 10px;">📱 Scanner QR Code</h3>
          <p>Scannez les QR codes d'accès</p>
        </div>
        
        <div style="background: white; padding: 20px; border-radius: 8px; text-align: center;">
          <h3 style="font-size: 20px; margin-bottom: 10px;">📊 Rapports</h3>
          <p>Consultez les statistiques</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
useHead({
  title: 'SecurAccess Enterprise - Test'
})
</script>