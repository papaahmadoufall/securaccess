<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Header -->
    <header class="bg-white shadow-sm border-b">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center py-4">
          <div class="flex items-center">
            <ShieldCheckIcon class="h-8 w-8 text-slate-600 mr-3" />
            <div>
              <h1 class="text-xl font-bold text-gray-900">Access Control Panel</h1>
              <p class="text-sm text-gray-600">{{ staffInfo.name }} - {{ staffInfo.staffId }}</p>
            </div>
          </div>
          <div class="flex items-center space-x-4">
            <div class="text-right">
              <p class="text-sm font-medium text-gray-900">{{ currentTime }}</p>
              <p class="text-xs text-gray-500">{{ currentDate }}</p>
            </div>
            <button
              @click="logout"
              class="bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition-colors text-sm font-medium"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </header>

    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        
        <!-- Left Column - Person Details -->
        <div class="space-y-6">
          <!-- Person Information Card -->
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="bg-gradient-to-r from-blue-600 to-indigo-600 px-6 py-4">
              <h2 class="text-xl font-bold text-white flex items-center">
                <UserIcon class="w-6 h-6 mr-2" />
                Person Details
              </h2>
            </div>
            
            <div class="p-6">
              <div v-if="personDetails" class="space-y-4">
                <!-- Profile Info -->
                <div class="flex items-center">
                  <div class="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center mr-4">
                    <UserIcon class="w-8 h-8 text-indigo-600" />
                  </div>
                  <div>
                    <h3 class="text-lg font-bold text-gray-900">{{ personDetails.name }}</h3>
                    <p class="text-gray-600 capitalize">{{ personDetails.role }}</p>
                    <p class="text-sm text-gray-500">{{ personDetails.department || personDetails.company }}</p>
                  </div>
                </div>

                <!-- Details Grid -->
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <p class="text-sm font-medium text-gray-700">Contact</p>
                    <p class="text-gray-900">{{ personDetails.phone }}</p>
                  </div>
                  <div>
                    <p class="text-sm font-medium text-gray-700">Access Zone</p>
                    <p class="text-gray-900">{{ personDetails.zone }}</p>
                  </div>
                  <div>
                    <p class="text-sm font-medium text-gray-700">Valid Period</p>
                    <p class="text-gray-900">{{ formatAccessPeriod }}</p>
                  </div>
                  <div>
                    <p class="text-sm font-medium text-gray-700">Status</p>
                    <span :class="statusClass" class="inline-flex px-2 py-1 text-xs font-semibold rounded-full">
                      {{ personDetails.status }}
                    </span>
                  </div>
                </div>

                <!-- Purpose (for hosts) -->
                <div v-if="personDetails.role === 'host' && personDetails.purpose" class="bg-blue-50 rounded-lg p-4">
                  <p class="text-sm font-medium text-blue-900">Purpose</p>
                  <p class="text-blue-800">{{ personDetails.purpose }}</p>
                </div>

                <!-- Last Access -->
                <div v-if="personDetails.lastAccess" class="bg-gray-50 rounded-lg p-4">
                  <p class="text-sm font-medium text-gray-700">Last Access</p>
                  <p class="text-gray-900">{{ formatLastAccess }}</p>
                </div>
              </div>

              <div v-else class="text-center py-8">
                <QrCodeIcon class="w-16 h-16 text-gray-300 mx-auto mb-4" />
                <p class="text-gray-500">Scan a QR code to view person details</p>
              </div>
            </div>
          </div>

          <!-- Recent Activity -->
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="bg-gradient-to-r from-green-600 to-emerald-600 px-6 py-4">
              <h2 class="text-xl font-bold text-white flex items-center">
                <ClockIcon class="w-6 h-6 mr-2" />
                Recent Activity
              </h2>
            </div>
            
            <div class="p-6">
              <div class="space-y-4 max-h-80 overflow-y-auto">
                <div v-for="entry in recentActivity" :key="entry.id" class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div class="flex items-center">
                    <div :class="entry.action === 'check-in' ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'" 
                         class="w-8 h-8 rounded-full flex items-center justify-center mr-3">
                      <ArrowRightOnRectangleIcon v-if="entry.action === 'check-in'" class="w-4 h-4" />
                      <ArrowLeftOnRectangleIcon v-else class="w-4 h-4" />
                    </div>
                    <div>
                      <p class="font-medium text-gray-900">{{ entry.name }}</p>
                      <p class="text-sm text-gray-600 capitalize">{{ entry.action.replace('-', ' ') }} - {{ entry.zone }}</p>
                    </div>
                  </div>
                  <div class="text-right">
                    <p class="text-sm font-medium text-gray-900">{{ formatTime(entry.timestamp) }}</p>
                    <p class="text-xs text-gray-500">{{ formatDate(entry.timestamp) }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Right Column - Access Control -->
        <div class="space-y-6">
          <!-- QR Scanner -->
          <div class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="bg-gradient-to-r from-purple-600 to-pink-600 px-6 py-4">
              <h2 class="text-xl font-bold text-white flex items-center">
                <QrCodeIcon class="w-6 h-6 mr-2" />
                QR Code Scanner
              </h2>
            </div>
            
            <div class="p-6">
              <div class="text-center mb-6">
                <div class="bg-gray-100 rounded-lg p-8 mb-4">
                  <QrCodeIcon class="w-24 h-24 text-gray-400 mx-auto mb-4" />
                  <p class="text-gray-600">Camera scanner would be here</p>
                  <p class="text-sm text-gray-500">Scan QR codes for access verification</p>
                </div>
                
                <!-- Manual QR Input -->
                <div class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Manual QR Code Entry</label>
                    <input
                      v-model="manualQrCode"
                      type="text"
                      placeholder="Enter QR code ID (e.g., QR001)"
                      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500"
                    />
                  </div>
                  <button
                    @click="loadQrCode"
                    :disabled="!manualQrCode"
                    class="w-full bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                  >
                    Load QR Code
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Access Actions -->
          <div v-if="personDetails" class="bg-white rounded-xl shadow-lg overflow-hidden">
            <div class="bg-gradient-to-r from-slate-600 to-gray-600 px-6 py-4">
              <h2 class="text-xl font-bold text-white flex items-center">
                <CogIcon class="w-6 h-6 mr-2" />
                Access Control Actions
              </h2>
            </div>
            
            <div class="p-6 space-y-4">
              <!-- Check-in Button -->
              <button
                @click="processAccess('check-in')"
                :disabled="!canCheckIn"
                class="w-full bg-green-600 text-white px-6 py-4 rounded-lg hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center text-lg font-semibold"
              >
                <ArrowRightOnRectangleIcon class="w-6 h-6 mr-2" />
                Check In (Entry)
              </button>

              <!-- Check-out Button -->
              <button
                @click="processAccess('check-out')"
                :disabled="!canCheckOut"
                class="w-full bg-red-600 text-white px-6 py-4 rounded-lg hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center text-lg font-semibold"
              >
                <ArrowLeftOnRectangleIcon class="w-6 h-6 mr-2" />
                Check Out (Exit)
              </button>

              <!-- Override Access -->
              <button
                @click="showOverrideModal = true"
                class="w-full bg-amber-600 text-white px-6 py-4 rounded-lg hover:bg-amber-700 transition-colors flex items-center justify-center font-semibold"
              >
                <ExclamationTriangleIcon class="w-6 h-6 mr-2" />
                Override Access
              </button>

              <!-- Notes -->
              <div class="space-y-2">
                <label class="block text-sm font-medium text-gray-700">Access Notes (Optional)</label>
                <textarea
                  v-model="accessNotes"
                  rows="3"
                  placeholder="Add any notes about this access..."
                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500"
                ></textarea>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Override Modal -->
    <div v-if="showOverrideModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg max-w-md w-full mx-4">
        <div class="px-6 py-4 border-b">
          <h3 class="text-lg font-semibold text-gray-900">Override Access Control</h3>
        </div>
        <div class="px-6 py-4">
          <p class="text-gray-600 mb-4">Are you sure you want to override the access control for this person?</p>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">Reason for Override</label>
              <textarea
                v-model="overrideReason"
                rows="3"
                placeholder="Explain why access is being overridden..."
                class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-amber-500"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="px-6 py-4 border-t flex justify-end space-x-3">
          <button
            @click="showOverrideModal = false"
            class="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
          >
            Cancel
          </button>
          <button
            @click="processOverride"
            :disabled="!overrideReason.trim()"
            class="px-4 py-2 bg-amber-600 text-white rounded-lg hover:bg-amber-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            Override Access
          </button>
        </div>
      </div>
    </div>

    <!-- Success/Error Messages -->
    <div v-if="message" :class="messageClass" class="fixed top-4 right-4 px-6 py-4 rounded-lg shadow-lg z-50">
      <div class="flex items-center">
        <CheckCircleIcon v-if="messageType === 'success'" class="w-5 h-5 mr-2" />
        <ExclamationTriangleIcon v-else class="w-5 h-5 mr-2" />
        <p class="font-medium">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ShieldCheckIcon,
  UserIcon,
  ClockIcon,
  QrCodeIcon,
  CogIcon,
  ArrowRightOnRectangleIcon,
  ArrowLeftOnRectangleIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const router = useRouter()

// Staff info
const staffInfo = ref({
  staffId: 'STAFF001',
  name: 'Security Officer',
  role: 'staff'
})

// Time tracking
const currentTime = ref('')
const currentDate = ref('')
let timeInterval = null

// QR Code and person details
const manualQrCode = ref(route.query.qr || '')
const personDetails = ref(null)
const accessNotes = ref('')

// Modal and override
const showOverrideModal = ref(false)
const overrideReason = ref('')

// Messages
const message = ref('')
const messageType = ref('success')

// Recent activity
const recentActivity = ref([
  {
    id: 1,
    name: 'Marie Martin',
    action: 'check-in',
    zone: 'Reception',
    timestamp: '2025-07-30T14:30:00'
  },
  {
    id: 2,
    name: 'Pierre Durand',
    action: 'check-out',
    zone: 'Building A',
    timestamp: '2025-07-30T14:25:00'
  },
  {
    id: 3,
    name: 'Sophie Dubois',
    action: 'check-in',
    zone: 'Meeting Room B',
    timestamp: '2025-07-30T14:15:00'
  }
])

// Computed properties
const statusClass = computed(() => {
  if (!personDetails.value) return ''
  switch (personDetails.value.status) {
    case 'valid': return 'bg-green-100 text-green-800'
    case 'expired': return 'bg-red-100 text-red-800'
    default: return 'bg-gray-100 text-gray-800'
  }
})

const formatAccessPeriod = computed(() => {
  if (!personDetails.value) return ''
  const from = new Date(personDetails.value.validFrom)
  const to = new Date(personDetails.value.validTo)
  return `${from.toLocaleDateString()} - ${to.toLocaleDateString()}`
})

const formatLastAccess = computed(() => {
  if (!personDetails.value?.lastAccess) return 'No recent access'
  return new Date(personDetails.value.lastAccess).toLocaleString()
})

const canCheckIn = computed(() => {
  return personDetails.value && personDetails.value.status === 'valid'
})

const canCheckOut = computed(() => {
  return personDetails.value && personDetails.value.status === 'valid'
})

const messageClass = computed(() => {
  return messageType.value === 'success' 
    ? 'bg-green-600 text-white' 
    : 'bg-red-600 text-white'
})

// Methods
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('fr-FR')
  currentDate.value = now.toLocaleDateString('fr-FR', { 
    weekday: 'long', 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  })
}

const loadQrCode = async () => {
  if (!manualQrCode.value) return
  
  try {
    const response = await apiService.getQRCodeDetails(manualQrCode.value)
    
    if (response.success) {
      personDetails.value = response.data
      showMessage('QR Code loaded successfully', 'success')
    } else {
      showMessage(response.message || 'Failed to load QR code', 'error')
    }
  } catch (error) {
    showMessage('Error loading QR code: ' + error.message, 'error')
  }
}

const processAccess = async (action) => {
  if (!personDetails.value) return

  try {
    // Prepare access log data
    const accessData = {
      qrCodeId: personDetails.value.qrCodeId,
      personName: personDetails.value.name,
      action: action,
      zone: personDetails.value.zone,
      staffId: staffInfo.value.staffId,
      notes: accessNotes.value
    }

    // Log access via API
    const response = await apiService.logAccess(accessData)

    if (response.success) {
      // Create local log entry for immediate display
      const logEntry = {
        id: Date.now(),
        name: personDetails.value.name,
        action: action,
        zone: personDetails.value.zone,
        timestamp: new Date().toISOString(),
        notes: accessNotes.value,
        staffId: staffInfo.value.staffId
      }

      // Add to recent activity
      recentActivity.value.unshift(logEntry)
      if (recentActivity.value.length > 10) {
        recentActivity.value.pop()
      }

      // Update last access
      personDetails.value.lastAccess = logEntry.timestamp

      // Clear notes
      accessNotes.value = ''

      // Show success message
      const actionText = action === 'check-in' ? 'Check-in' : 'Check-out'
      showMessage(`${actionText} recorded successfully for ${personDetails.value.name}`, 'success')
    } else {
      showMessage(response.message || 'Failed to record access', 'error')
    }
  } catch (error) {
    showMessage('Error recording access: ' + error.message, 'error')
  }
}

const processOverride = async () => {
  if (!overrideReason.value.trim()) return

  try {
    // Prepare override access data
    const accessData = {
      qrCodeId: personDetails.value.qrCodeId,
      personName: personDetails.value.name,
      action: 'override',
      zone: personDetails.value.zone,
      staffId: staffInfo.value.staffId,
      overrideReason: overrideReason.value,
      notes: `Override: ${overrideReason.value}`
    }

    // Log override via API
    const response = await apiService.logAccess(accessData)

    if (response.success) {
      const logEntry = {
        id: Date.now(),
        name: personDetails.value.name,
        action: 'override',
        zone: personDetails.value.zone,
        timestamp: new Date().toISOString(),
        notes: `Override: ${overrideReason.value}`,
        staffId: staffInfo.value.staffId
      }

      recentActivity.value.unshift(logEntry)
      showMessage('Access override recorded successfully', 'success')
    } else {
      showMessage(response.message || 'Failed to record override', 'error')
    }
  } catch (error) {
    showMessage('Error recording override: ' + error.message, 'error')
  }
  
  showOverrideModal.value = false
  overrideReason.value = ''
}

const showMessage = (text, type = 'success') => {
  message.value = text
  messageType.value = type
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

const formatTime = (timestamp) => {
  return new Date(timestamp).toLocaleTimeString('fr-FR', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const formatDate = (timestamp) => {
  return new Date(timestamp).toLocaleDateString('fr-FR')
}

const logout = () => {
  localStorage.removeItem('staff_session')
  router.push('/')
}

// Lifecycle
onMounted(() => {
  // Check staff authentication
  const staffSession = localStorage.getItem('staff_session')
  if (!staffSession) {
    router.push('/staff/login')
    return
  }

  // Load staff info
  const session = JSON.parse(staffSession)
  staffInfo.value = session

  // Start time updates
  updateTime()
  timeInterval = setInterval(updateTime, 1000)

  // Load QR code if provided
  if (manualQrCode.value) {
    loadQrCode()
  }
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})
</script>