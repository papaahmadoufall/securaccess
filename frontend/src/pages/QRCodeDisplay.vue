<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-2xl mx-auto px-4">
      
      <!-- Header -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">QR Code Display</h1>
        <p class="text-gray-600">Generated QR codes for access control</p>
      </div>

      <!-- QR Code Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        
        <!-- QR001 - Jean Dupont -->
        <div class="bg-white rounded-xl shadow-lg p-6 text-center">
          <div class="mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Jean Dupont</h3>
            <p class="text-sm text-gray-600">IT Department</p>
            <p class="text-xs text-gray-500">QR001</p>
          </div>
          
          <div class="mb-4">
            <img 
              :src="getQRCodeImageUrl('QR001')" 
              alt="QR Code for Jean Dupont"
              class="w-48 h-48 mx-auto border border-gray-200 rounded-lg"
              @error="handleImageError"
            />
          </div>
          
          <div class="space-y-2 text-sm text-gray-600">
            <p><strong>Location:</strong> Building A - Main Entrance</p>
            <p><strong>Zone:</strong> Reception Area</p>
            <p><strong>Valid:</strong> 7 days from creation</p>
          </div>
          
          <button 
            @click="testQRCode('QR001')"
            class="mt-4 w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors"
          >
            Test Access
          </button>
        </div>

        <!-- QR002 - Marie Martin -->
        <div class="bg-white rounded-xl shadow-lg p-6 text-center">
          <div class="mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Marie Martin</h3>
            <p class="text-sm text-gray-600">TechCorp Solutions</p>
            <p class="text-xs text-gray-500">QR002</p>
          </div>
          
          <div class="mb-4">
            <img 
              :src="getQRCodeImageUrl('QR002')" 
              alt="QR Code for Marie Martin"
              class="w-48 h-48 mx-auto border border-gray-200 rounded-lg"
              @error="handleImageError"
            />
          </div>
          
          <div class="space-y-2 text-sm text-gray-600">
            <p><strong>Location:</strong> Building A - Main Entrance</p>
            <p><strong>Zone:</strong> Reception Area</p>
            <p><strong>Valid:</strong> 7 days from creation</p>
          </div>
          
          <button 
            @click="testQRCode('QR002')"
            class="mt-4 w-full bg-green-600 text-white py-2 px-4 rounded-lg hover:bg-green-700 transition-colors"
          >
            Test Access
          </button>
        </div>

        <!-- QR003 - Pierre Durand -->
        <div class="bg-white rounded-xl shadow-lg p-6 text-center">
          <div class="mb-4">
            <h3 class="text-lg font-semibold text-gray-900">Pierre Durand</h3>
            <p class="text-sm text-gray-600">Security</p>
            <p class="text-xs text-gray-500">QR003</p>
          </div>
          
          <div class="mb-4">
            <img 
              :src="getQRCodeImageUrl('QR003')" 
              alt="QR Code for Pierre Durand"
              class="w-48 h-48 mx-auto border border-gray-200 rounded-lg"
              @error="handleImageError"
            />
          </div>
          
          <div class="space-y-2 text-sm text-gray-600">
            <p><strong>Location:</strong> Building A - Main Entrance</p>
            <p><strong>Zone:</strong> Reception Area</p>
            <p><strong>Valid:</strong> 7 days from creation</p>
          </div>
          
          <button 
            @click="testQRCode('QR003')"
            class="mt-4 w-full bg-purple-600 text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition-colors"
          >
            Test Access
          </button>
        </div>

        <!-- Generate New QR -->
        <div class="bg-gradient-to-br from-indigo-50 to-blue-50 rounded-xl border-2 border-dashed border-indigo-300 p-6 text-center">
          <div class="text-indigo-600 mb-4">
            <svg class="w-16 h-16 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path>
            </svg>
          </div>
          
          <h3 class="text-lg font-semibold text-gray-900 mb-2">Generate New QR Code</h3>
          <p class="text-sm text-gray-600 mb-4">Create QR codes for workers and visitors</p>
          
          <button 
            @click="navigateToGenerator"
            class="w-full bg-indigo-600 text-white py-2 px-4 rounded-lg hover:bg-indigo-700 transition-colors"
          >
            Create QR Code
          </button>
        </div>
      </div>

      <!-- Navigation -->
      <div class="mt-8 text-center space-x-4">
        <router-link 
          to="/staff/login" 
          class="inline-block bg-slate-600 text-white px-6 py-3 rounded-lg hover:bg-slate-700 transition-colors"
        >
          🛡️ Staff Scanner
        </router-link>
        
        <router-link 
          to="/" 
          class="inline-block bg-gray-600 text-white px-6 py-3 rounded-lg hover:bg-gray-700 transition-colors"
        >
          🏠 Home
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import config from '../config/environment.js'

const router = useRouter()

const getQRCodeImageUrl = (qrCodeId) => {
  return `${config.API_BASE_URL}/qrcode/${qrCodeId}/image`
}

const testQRCode = (qrCodeId) => {
  // Navigate to QR access page
  router.push(`/qr/${qrCodeId}`)
}

const navigateToGenerator = () => {
  // Navigate to QR generator (to be implemented)
  alert('QR Code generator will be implemented in future version')
}

const handleImageError = (event) => {
  console.error('Failed to load QR code image:', event)
  event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDIwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIyMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRjNGNEY2Ii8+Cjx0ZXh0IHg9IjEwMCIgeT0iMTAwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOUI5QkE2IiBmb250LXNpemU9IjE0cHgiPkVycm9yPC90ZXh0Pgo8L3N2Zz4K'
}
</script>