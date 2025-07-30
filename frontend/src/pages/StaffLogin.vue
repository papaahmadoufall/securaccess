<template>
  <div class="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <!-- Header -->
      <div class="text-center">
        <div class="mx-auto h-16 w-16 bg-slate-600 rounded-full flex items-center justify-center">
          <ShieldCheckIcon class="h-8 w-8 text-white" />
        </div>
        <h2 class="mt-6 text-3xl font-bold text-gray-900">
          Staff Access Control
        </h2>
        <p class="mt-2 text-sm text-gray-600">
          Scan and verify access permissions
        </p>
        <div v-if="qrCodeId" class="mt-4 bg-blue-50 border border-blue-200 rounded-lg p-3">
          <p class="text-sm text-blue-800">
            <strong>QR Code:</strong> {{ qrCodeId }}
          </p>
        </div>
      </div>

      <!-- Login Form -->
      <form @submit.prevent="handleLogin" class="mt-8 space-y-6">
        <div class="bg-white rounded-lg shadow-lg px-6 py-8">
          <div class="space-y-6">
            <!-- Staff ID -->
            <div>
              <label for="staffId" class="block text-sm font-medium text-gray-700 mb-2">
                Staff ID
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center">
                  <IdentificationIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="staffId"
                  v-model="form.staffId"
                  type="text"
                  required
                  class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                  placeholder="Enter your staff ID"
                />
              </div>
            </div>

            <!-- PIN -->
            <div>
              <label for="pin" class="block text-sm font-medium text-gray-700 mb-2">
                PIN Code
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center">
                  <LockClosedIcon class="h-5 w-5 text-gray-400" />
                </div>
                <input
                  id="pin"
                  v-model="form.pin"
                  type="password"
                  required
                  class="block w-full pl-10 pr-3 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500 focus:border-transparent"
                  placeholder="Enter your PIN"
                  maxlength="6"
                />
              </div>
            </div>

            <!-- Login Button -->
            <button
              type="submit"
              :disabled="loading"
              class="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-slate-600 hover:bg-slate-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-slate-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              <span v-if="!loading" class="flex items-center">
                <ShieldCheckIcon class="w-5 h-5 mr-2" />
                Access Control Panel
              </span>
              <span v-else class="flex items-center">
                <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
                Authenticating...
              </span>
            </button>
          </div>
        </div>

        <!-- Test Credentials -->
        <div class="bg-gray-50 rounded-lg p-4">
          <h3 class="text-sm font-medium text-gray-700 mb-2">Test Credentials:</h3>
          <div class="text-xs text-gray-600 space-y-1">
            <p><strong>Staff ID:</strong> STAFF001</p>
            <p><strong>PIN:</strong> 1234</p>
          </div>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
          <div class="flex">
            <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
            <div class="ml-3">
              <h3 class="text-sm font-medium text-red-800">
                Authentication Failed
              </h3>
              <p class="mt-1 text-sm text-red-700">{{ error }}</p>
            </div>
          </div>
        </div>
      </form>

      <!-- Back Link -->
      <div class="text-center">
        <button
          @click="goBack"
          class="text-sm text-slate-600 hover:text-slate-800 transition-colors"
        >
          ← Back to Access Request
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ShieldCheckIcon,
  IdentificationIcon,
  LockClosedIcon,
  ExclamationTriangleIcon
} from '@heroicons/vue/24/outline'
import apiService from '../services/api.js'

const route = useRoute()
const router = useRouter()

const qrCodeId = ref(route.query.qr || null)
const loading = ref(false)
const error = ref('')

const form = ref({
  staffId: '',
  pin: ''
})

const handleLogin = async () => {
  loading.value = true
  error.value = ''

  try {
    // Real staff authentication via API
    const response = await apiService.loginStaff(form.value.staffId, form.value.pin)
    
    if (response.success) {
      // Store staff session
      localStorage.setItem('staff_session', JSON.stringify({
        ...response.staff,
        token: response.token,
        loginTime: new Date().toISOString()
      }))

      // Redirect to staff dashboard with QR code
      if (qrCodeId.value) {
        router.push(`/staff/scanner?qr=${qrCodeId.value}`)
      } else {
        router.push('/staff/dashboard')
      }
    } else {
      throw new Error(response.message || 'Authentication failed')
    }
  } catch (err) {
    error.value = err.message || 'Authentication failed. Please try again.'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  if (qrCodeId.value) {
    router.push(`/qr/${qrCodeId.value}`)
  } else {
    router.push('/')
  }
}

onMounted(() => {
  // Clear any existing error when component mounts
  error.value = ''
})
</script>