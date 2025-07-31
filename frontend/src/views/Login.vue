<template>
  <div class="login-container">
    <div class="login-card">
      <!-- App Header -->
      <div class="app-header">
        <div class="app-logo">🔐</div>
        <h1 class="app-title">SecurAccess</h1>
        <p class="app-subtitle">Enterprise Mobile Access</p>
      </div>

      <!-- Login Form -->
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="phone" class="form-label">📱 Numéro de téléphone</label>
          <input 
            id="phone"
            v-model="loginForm.phone" 
            type="tel" 
            class="form-input"
            placeholder="77 123 45 67"
            maxlength="14"
            required
            @input="formatPhoneNumber"
          >
        </div>

        <div class="form-group">
          <label for="pin" class="form-label">🔢 Code PIN (4 chiffres)</label>
          <input 
            id="pin"
            v-model="loginForm.pin" 
            type="password" 
            class="form-input pin-input"
            placeholder="••••"
            maxlength="4"
            pattern="[0-9]{4}"
            required
            @input="formatPin"
          >
        </div>

        <button type="submit" class="login-btn" :disabled="!isFormValid || isLoading">
          <span v-if="isLoading" class="loading-spinner"></span>
          <span v-else>🚀</span>
          {{ isLoading ? 'Connexion...' : 'Se connecter' }}
        </button>

        <!-- Error Message -->
        <div v-if="errorMessage" class="error-message">
          ❌ {{ errorMessage }}
        </div>
      </form>

      <!-- Manager Access Button -->
      <div class="manager-access">
        <button @click="showManagerLogin = true" class="manager-btn">
          👔 Accès Manager
        </button>
      </div>

      <!-- Footer -->
      <div class="login-footer">
        <p>Première connexion ? Contactez votre manager</p>
        <p class="version">v1.0.0</p>
      </div>
    </div>

    <!-- Manager Login Modal -->
    <div v-if="showManagerLogin" class="modal-overlay" @click="closeManagerModal">
      <div class="modal-card" @click.stop>
        <div class="modal-header">
          <h2>👔 Connexion Manager</h2>
          <button @click="closeManagerModal" class="close-btn">✖</button>
        </div>
        
        <form @submit.prevent="handleManagerLogin" class="modal-form">
          <div class="form-group">
            <label>📧 Email</label>
            <input 
              v-model="managerForm.email" 
              type="email" 
              class="form-input"
              placeholder="manager@entreprise.com"
              required
            >
          </div>
          
          <div class="form-group">
            <label>🔑 Mot de passe</label>
            <input 
              v-model="managerForm.password" 
              type="password" 
              class="form-input"
              placeholder="••••••••"
              required
            >
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeManagerModal" class="btn-secondary">
              Annuler
            </button>
            <button type="submit" class="btn-primary" :disabled="isManagerLoading">
              {{ isManagerLoading ? 'Connexion...' : 'Se connecter' }}
            </button>
          </div>
          
          <div v-if="managerError" class="error-message">
            ❌ {{ managerError }}
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { formatPhoneInput } from '@/utils/senegalFormat.js'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        phone: '',
        pin: ''
      },
      managerForm: {
        email: '',
        password: ''
      },
      showManagerLogin: false,
      isLoading: false,
      isManagerLoading: false,
      errorMessage: '',
      managerError: ''
    }
  },
  computed: {
    isFormValid() {
      return this.loginForm.phone.replace(/\s/g, '').length >= 9 && 
             this.loginForm.pin.length === 4;
    }
  },
  methods: {
    formatPhoneNumber() {
      this.loginForm.phone = formatPhoneInput(this.loginForm.phone);
    },
    
    formatPin(event) {
      // Only allow digits
      const value = event.target.value.replace(/\D/g, '');
      this.loginForm.pin = value.substring(0, 4);
    },
    
    async handleLogin() {
      this.isLoading = true;
      this.errorMessage = '';
      
      try {
        // Clean phone number (remove spaces)
        const cleanPhone = this.loginForm.phone.replace(/\s/g, '');
        
        // Call API for authentication
        const response = await this.$api.loginWorker(cleanPhone, this.loginForm.pin);
        
        if (response.success) {
          // Store user session
          localStorage.setItem('securaccess_user', JSON.stringify({
            ...response.user,
            loginTime: new Date().toISOString()
          }));
          
          // Redirect based on role
          if (response.user.role === 'worker') {
            this.$router.push('/worker');
          } else if (response.user.role === 'host') {
            this.$router.push('/host');
          }
        } else {
          this.errorMessage = response.error || 'Erreur de connexion';
        }
      } catch (error) {
        console.error('Login error:', error);
        // Try host login if worker login fails
        try {
          const cleanPhone = this.loginForm.phone.replace(/\s/g, '');
          const hostResponse = await this.$api.loginHost(cleanPhone, this.loginForm.pin);
          
          if (hostResponse.success) {
            localStorage.setItem('securaccess_user', JSON.stringify({
              ...hostResponse.user,
              loginTime: new Date().toISOString()
            }));
            
            this.$router.push('/host');
            return;
          }
        } catch (hostError) {
          console.error('Host login error:', hostError);
        }
        
        this.errorMessage = 'Numéro de téléphone ou code PIN incorrect';
      } finally {
        this.isLoading = false;
      }
    },
    
    async handleManagerLogin() {
      this.isManagerLoading = true;
      this.managerError = '';
      
      try {
        const response = await this.$api.loginManager(this.managerForm.email, this.managerForm.password);
        
        if (response.success) {
          localStorage.setItem('securaccess_user', JSON.stringify({
            ...response.user,
            loginTime: new Date().toISOString()
          }));
          
          this.$router.push('/manager');
        } else {
          this.managerError = response.error || 'Email ou mot de passe incorrect';
        }
      } catch (error) {
        console.error('Manager login error:', error);
        this.managerError = 'Email ou mot de passe incorrect';
      } finally {
        this.isManagerLoading = false;
      }
    },
    
    closeManagerModal() {
      this.showManagerLogin = false;
      this.managerForm = { email: '', password: '' };
      this.managerError = '';
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.login-card {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.app-header {
  text-align: center;
  margin-bottom: 2rem;
}

.app-logo {
  font-size: 4rem;
  margin-bottom: 0.5rem;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.app-title {
  font-size: 2rem;
  font-weight: bold;
  color: #2d3748;
  margin: 0 0 0.25rem 0;
}

.app-subtitle {
  color: #718096;
  margin: 0;
  font-size: 0.9rem;
}

.login-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.pin-input {
  text-align: center;
  font-size: 1.5rem;
  letter-spacing: 0.5rem;
  font-weight: bold;
}

.login-btn {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, #4299e1, #3182ce);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(66, 153, 225, 0.3);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  width: 18px;
  height: 18px;
  border: 2px solid #ffffff40;
  border-top: 2px solid #ffffff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fed7d7;
  border: 1px solid #feb2b2;
  border-radius: 8px;
  color: #c53030;
  font-size: 0.9rem;
  text-align: center;
}

.manager-access {
  text-align: center;
  margin-bottom: 1.5rem;
}

.manager-btn {
  background: #f7fafc;
  border: 2px solid #e2e8f0;
  color: #4a5568;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.manager-btn:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.login-footer {
  text-align: center;
  color: #718096;
  font-size: 0.8rem;
}

.login-footer p {
  margin: 0.25rem 0;
}

.version {
  font-weight: bold;
  color: #a0aec0;
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
  max-width: 400px;
  animation: modalSlide 0.3s ease-out;
}

@keyframes modalSlide {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #2d3748;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  color: #718096;
  padding: 0.25rem;
}

.modal-form {
  padding: 1.5rem;
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
  font-weight: 500;
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

/* Mobile Optimizations */
@media (max-width: 480px) {
  .login-container {
    padding: 0.5rem;
  }
  
  .login-card {
    padding: 1.5rem;
    border-radius: 16px;
  }
  
  .app-logo {
    font-size: 3rem;
  }
  
  .app-title {
    font-size: 1.75rem;
  }
  
  .form-input {
    padding: 0.875rem;
    font-size: 1rem;
  }
  
  .pin-input {
    font-size: 1.25rem;
    letter-spacing: 0.25rem;
  }
  
  .modal-card {
    margin: 1rem;
  }
}

/* PWA Touch Optimizations */
.form-input,
.login-btn,
.manager-btn,
.btn-primary,
.btn-secondary {
  -webkit-tap-highlight-color: transparent;
  touch-action: manipulation;
}

/* Focus visible for accessibility */
.form-input:focus-visible,
.login-btn:focus-visible,
.manager-btn:focus-visible {
  outline: 2px solid #4299e1;
  outline-offset: 2px;
}
</style>