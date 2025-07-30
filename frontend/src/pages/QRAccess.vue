<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 py-8">
    <div class="max-w-2xl mx-auto px-4">
      <!-- Header -->
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Access Request</h1>
        <p class="text-gray-600">Please verify the access details below</p>
      </div>

      <!-- QR Code Details Card -->
      <div class="bg-white rounded-2xl shadow-xl overflow-hidden">
        <!-- Status Banner -->
        <div :class="statusBannerClass" class="px-6 py-4">
          <div class="flex items-center">
            <div :class="statusIconClass" class="w-8 h-8 rounded-full flex items-center justify-center mr-3">
              <CheckCircleIcon v-if="accessDetails.status === 'valid'" class="w-5 h-5" />
              <ExclamationTriangleIcon v-else-if="accessDetails.status === 'expired'" class="w-5 h-5" />
              <XCircleIcon v-else class="w-5 h-5" />
            </div>
            <div>
              <p class="font-semibold text-white">{{ statusMessage }}</p>
              <p class="text-sm opacity-90">{{ statusDescription }}</p>
            </div>
          </div>
        </div>

        <!-- Person Details -->
        <div class="px-6 py-6">
          <div class="flex items-center mb-6">
            <div class="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center mr-4">
              <UserIcon class="w-8 h-8 text-indigo-600" />
            </div>
            <div>
              <h2 class="text-xl font-bold text-gray-900">{{ accessDetails.name }}</h2>
              <p class="text-gray-600 capitalize">{{ accessDetails.role }}</p>
              <p class="text-sm text-gray-500">{{ accessDetails.department || accessDetails.company }}</p>
            </div>
          </div>

          <!-- Access Information -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
            <!-- Location -->
            <div class="flex items-start">
              <MapPinIcon class="w-5 h-5 text-gray-400 mt-1 mr-3" />
              <div>
                <p class="font-medium text-gray-900">Location</p>
                <p class="text-gray-600">{{ accessDetails.location }}</p>
                <p class="text-sm text-gray-500">{{ accessDetails.zone }}</p>
              </div>
            </div>

            <!-- Access Period -->
            <div class="flex items-start">
              <ClockIcon class="w-5 h-5 text-gray-400 mt-1 mr-3" />
              <div>
                <p class="font-medium text-gray-900">Access Period</p>
                <p class="text-gray-600">{{ formatDateRange }}</p>
                <p class="text-sm text-gray-500">{{ formatTimeRange }}</p>
              </div>
            </div>

            <!-- Purpose (for hosts) -->
            <div v-if="accessDetails.role === 'host'" class="flex items-start">
              <DocumentTextIcon class="w-5 h-5 text-gray-400 mt-1 mr-3" />
              <div>
                <p class="font-medium text-gray-900">Purpose</p>
                <p class="text-gray-600">{{ accessDetails.purpose }}</p>
                <p class="text-sm text-gray-500">{{ accessDetails.description }}</p>
              </div>
            </div>

            <!-- Contact -->
            <div class="flex items-start">
              <PhoneIcon class="w-5 h-5 text-gray-400 mt-1 mr-3" />
              <div>
                <p class="font-medium text-gray-900">Contact</p>
                <p class="text-gray-600">{{ accessDetails.phone }}</p>
                <p v-if="accessDetails.email" class="text-sm text-gray-500">{{ accessDetails.email }}</p>
              </div>
            </div>
          </div>

          <!-- Special Instructions -->
          <div v-if="accessDetails.instructions" class="bg-amber-50 border border-amber-200 rounded-lg p-4 mb-6">
            <div class="flex items-start">
              <InformationCircleIcon class="w-5 h-5 text-amber-600 mt-0.5 mr-3" />
              <div>
                <p class="font-medium text-amber-900">Special Instructions</p>
                <p class="text-amber-800">{{ accessDetails.instructions }}</p>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex flex-col sm:flex-row gap-3">
            <button 
              v-if="accessDetails.status === 'valid'" 
              @click="proceedToStaffLogin"
              class="flex-1 bg-indigo-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-indigo-700 transition-colors flex items-center justify-center"
            >
              <QrCodeIcon class="w-5 h-5 mr-2" />
              Proceed to Access Control
            </button>
            
            <button 
              v-else
              disabled
              class="flex-1 bg-gray-300 text-gray-500 px-6 py-3 rounded-lg font-semibold cursor-not-allowed flex items-center justify-center"
            >
              <XCircleIcon class="w-5 h-5 mr-2" />
              Access Not Available
            </button>

            <button 
              @click="goBack"
              class="flex-1 bg-gray-100 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-200 transition-colors"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>

      <!-- QR Code Display -->
      <div class="mt-8 bg-white rounded-lg shadow-lg p-6 text-center">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">QR Code</h3>
        <div class="flex justify-center">
          <div class="bg-gray-100 p-4 rounded-lg">
            <canvas ref="qrCanvas" class="w-48 h-48"></canvas>
          </div>
        </div>
        <p class="text-sm text-gray-500 mt-4">ID: {{ accessDetails.qrCodeId }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  UserIcon, 
  MapPinIcon, 
  ClockIcon, 
  PhoneIcon, 
  DocumentTextIcon,
  InformationCircleIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  QrCodeIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const router = useRouter()

// Mock data - in real app, fetch from API using QR code ID
const accessDetails = ref({
  qrCodeId: route.params.id || 'QR001',
  name: 'Jean Dupont',
  role: 'host', // 'worker' or 'host'
  department: 'IT Department',
  company: 'TechCorp Solutions',
  phone: '+33 6 12 34 56 78',
  email: 'jean.dupont@techcorp.com',
  location: 'Building A - Main Entrance',
  zone: 'Reception Area',
  purpose: 'Client Meeting',
  description: 'Meeting with development team regarding new project specifications',
  instructions: 'Please escort to meeting room B-205. Valid ID required.',
  validFrom: '2025-07-30T08:00:00',
  validTo: '2025-07-30T18:00:00',
  accessHours: '08:00 - 18:00',
  status: 'valid', // 'valid', 'expired', 'invalid'
  createdAt: '2025-07-30T06:30:00',
  lastAccess: null
})

const qrCanvas = ref(null)

const statusBannerClass = computed(() => {
  switch (accessDetails.value.status) {
    case 'valid': return 'bg-green-600'
    case 'expired': return 'bg-amber-600'
    default: return 'bg-red-600'
  }
})

const statusIconClass = computed(() => {
  switch (accessDetails.value.status) {
    case 'valid': return 'bg-green-100 text-green-600'
    case 'expired': return 'bg-amber-100 text-amber-600'
    default: return 'bg-red-100 text-red-600'
  }
})

const statusMessage = computed(() => {
  switch (accessDetails.value.status) {
    case 'valid': return 'Access Authorized'
    case 'expired': return 'Access Expired'
    default: return 'Access Denied'
  }
})

const statusDescription = computed(() => {
  switch (accessDetails.value.status) {
    case 'valid': return 'This QR code is valid and ready for use'
    case 'expired': return 'This QR code has expired and is no longer valid'
    default: return 'This QR code is invalid or has been revoked'
  }
})

const formatDateRange = computed(() => {
  const from = new Date(accessDetails.value.validFrom)
  const to = new Date(accessDetails.value.validTo)
  
  if (from.toDateString() === to.toDateString()) {
    return from.toLocaleDateString('fr-FR', { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    })
  }
  
  return `${from.toLocaleDateString('fr-FR')} - ${to.toLocaleDateString('fr-FR')}`
})

const formatTimeRange = computed(() => {
  const from = new Date(accessDetails.value.validFrom)
  const to = new Date(accessDetails.value.validTo)
  
  return `${from.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })} - ${to.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })}`
})

const proceedToStaffLogin = () => {
  // Redirect to staff login with QR code ID
  router.push(`/staff/login?qr=${accessDetails.value.qrCodeId}`)
}

const goBack = () => {
  router.push('/')
}

onMounted(() => {
  // Generate QR code (mock implementation)
  if (qrCanvas.value) {
    const ctx = qrCanvas.value.getContext('2d')
    ctx.fillStyle = '#000'
    ctx.fillRect(0, 0, 192, 192)
    ctx.fillStyle = '#fff'
    ctx.font = '12px Arial'
    ctx.textAlign = 'center'
    ctx.fillText('QR CODE', 96, 100)
    ctx.fillText(accessDetails.value.qrCodeId, 96, 120)
  }
})
</script>