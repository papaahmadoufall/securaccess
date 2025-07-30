import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'
import apiService from './services/api.js'

// Import views
import Login from './views/Login.vue'
import WorkerDashboard from './views/WorkerDashboard.vue'
import ManagerDashboard from './views/ManagerDashboard.vue'
import HostDashboard from './views/HostDashboard.vue'

// Import pages
import QRAccess from './pages/QRAccess.vue'
import StaffLogin from './pages/StaffLogin.vue'
import StaffScanner from './pages/StaffScanner.vue'

// Route guard to check authentication
const requireAuth = (to, from, next) => {
  const user = localStorage.getItem('securaccess_user');
  if (user) {
    next();
  } else {
    next('/login');
  }
};

// Role-based route guard
const requireRole = (roles) => (to, from, next) => {
  const user = JSON.parse(localStorage.getItem('securaccess_user') || '{}');
  if (user && roles.includes(user.role)) {
    next();
  } else {
    next('/login');
  }
};

// Staff authentication guard
const requireStaff = (to, from, next) => {
  const staffSession = localStorage.getItem('staff_session');
  if (staffSession) {
    next();
  } else {
    next('/staff/login');
  }
};

const routes = [
  { 
    path: '/', 
    redirect: '/login'
  },
  { 
    path: '/login', 
    component: Login,
    beforeEnter: (to, from, next) => {
      // Redirect if already logged in
      const user = localStorage.getItem('securaccess_user');
      if (user) {
        const userData = JSON.parse(user);
        if (userData.role === 'worker') {
          next('/worker');
        } else if (userData.role === 'manager') {
          next('/manager');
        } else if (userData.role === 'host') {
          next('/host');
        } else {
          next();
        }
      } else {
        next();
      }
    }
  },
  { 
    path: '/worker', 
    component: WorkerDashboard,
    beforeEnter: requireRole(['worker'])
  },
  { 
    path: '/manager', 
    component: ManagerDashboard,
    beforeEnter: requireRole(['manager'])
  },
  { 
    path: '/host', 
    component: HostDashboard,
    beforeEnter: requireRole(['host'])
  },
  // QR Code access routes
  {
    path: '/qr/:id',
    component: QRAccess,
    name: 'QRAccess'
  },
  // Staff routes
  {
    path: '/staff/login',
    component: StaffLogin,
    name: 'StaffLogin'
  },
  {
    path: '/staff/scanner',
    component: StaffScanner,
    name: 'StaffScanner',
    beforeEnter: requireStaff
  },
  {
    path: '/staff/dashboard',
    component: StaffScanner,
    name: 'StaffDashboard',
    beforeEnter: requireStaff
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)

// Make API service available globally
app.config.globalProperties.$api = apiService

app.mount('#app')