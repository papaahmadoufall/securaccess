// API Service for SecurAccess Enterprise
import config from '../config/environment.js';
const API_BASE_URL = config.API_BASE_URL;

class ApiService {
    constructor() {
        this.baseURL = API_BASE_URL;
        this.token = localStorage.getItem('securaccess_token');
    }

    // Set authentication token
    setToken(token) {
        this.token = token;
        if (token) {
            localStorage.setItem('securaccess_token', token);
        } else {
            localStorage.removeItem('securaccess_token');
        }
    }

    // Get authentication headers
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        };
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        
        return headers;
    }

    // Generic API request method
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        
        const config = {
            headers: this.getHeaders(),
            ...options,
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.error || `HTTP ${response.status}: ${response.statusText}`);
            }

            return data;
        } catch (error) {
            console.error('API Request Error:', error);
            throw error;
        }
    }

    // Authentication Methods
    async loginWorker(phone, pin) {
        const response = await this.request('/auth/login/worker', {
            method: 'POST',
            body: JSON.stringify({ phone, pin }),
        });
        
        if (response.success && response.token) {
            this.setToken(response.token);
        }
        
        return response;
    }

    async loginHost(phone, pin) {
        const response = await this.request('/auth/login/host', {
            method: 'POST',
            body: JSON.stringify({ phone, pin }),
        });
        
        if (response.success && response.token) {
            this.setToken(response.token);
        }
        
        return response;
    }

    async loginManager(email, password) {
        const response = await this.request('/auth/login/manager', {
            method: 'POST',
            body: JSON.stringify({ email, password }),
        });
        
        if (response.success && response.token) {
            this.setToken(response.token);
        }
        
        return response;
    }

    async logout() {
        try {
            await this.request('/auth/logout', { method: 'POST' });
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            this.setToken(null);
        }
    }

    async validateToken() {
        return await this.request('/auth/validate-token');
    }

    // Worker Methods
    async generateWorkerQRCode(workerId) {
        return await this.request(`/workers/${workerId}/qr-code/generate`);
    }

    async getWorkerAccessHistory(workerId, options = {}) {
        const params = new URLSearchParams();
        if (options.limit) params.append('limit', options.limit);
        if (options.type) params.append('type', options.type);
        
        const queryString = params.toString();
        const endpoint = `/workers/${workerId}/access-history${queryString ? '?' + queryString : ''}`;
        
        return await this.request(endpoint);
    }

    async getWorkerProfile(workerId) {
        return await this.request(`/workers/${workerId}/profile`);
    }

    async logWorkerAccess(workerId, accessData) {
        return await this.request(`/workers/${workerId}/access-log`, {
            method: 'POST',
            body: JSON.stringify(accessData),
        });
    }

    async updateWorkerLastAccess(workerId) {
        return await this.request(`/workers/${workerId}/last-access`, {
            method: 'PUT',
        });
    }

    // Host Methods
    async generateHostQRCode(hostId) {
        return await this.request(`/hosts/${hostId}/qr-code/generate`);
    }

    async getHostAccessHistory(hostId, options = {}) {
        const params = new URLSearchParams();
        if (options.limit) params.append('limit', options.limit);
        if (options.type) params.append('type', options.type);
        
        const queryString = params.toString();
        const endpoint = `/hosts/${hostId}/access-history${queryString ? '?' + queryString : ''}`;
        
        return await this.request(endpoint);
    }

    async getHostProfile(hostId) {
        return await this.request(`/hosts/${hostId}/profile`);
    }

    // Manager Methods
    async getAllWorkers() {
        return await this.request('/workers');
    }

    async createWorker(workerData) {
        return await this.request('/workers', {
            method: 'POST',
            body: JSON.stringify(workerData),
        });
    }

    async updateWorker(workerId, workerData) {
        return await this.request(`/workers/${workerId}`, {
            method: 'PUT',
            body: JSON.stringify(workerData),
        });
    }

    async deleteWorker(workerId) {
        return await this.request(`/workers/${workerId}`, {
            method: 'DELETE',
        });
    }

    async toggleWorkerStatus(workerId, isActive) {
        return await this.request(`/workers/${workerId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ isActive }),
        });
    }

    async getAllHosts() {
        return await this.request('/hosts');
    }

    async createHost(hostData) {
        return await this.request('/hosts', {
            method: 'POST',
            body: JSON.stringify(hostData),
        });
    }

    async updateHost(hostId, hostData) {
        return await this.request(`/hosts/${hostId}`, {
            method: 'PUT',
            body: JSON.stringify(hostData),
        });
    }

    async deleteHost(hostId) {
        return await this.request(`/hosts/${hostId}`, {
            method: 'DELETE',
        });
    }

    async toggleHostStatus(hostId, isActive) {
        return await this.request(`/hosts/${hostId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ isActive }),
        });
    }

    // Statistics Methods
    async getDashboardStats() {
        return await this.request('/stats/dashboard');
    }

    async getAccessStats(period = 'week') {
        return await this.request(`/stats/access?period=${period}`);
    }

    // Health Check
    async healthCheck() {
        return await this.request('/test/health');
    }

    // Staff Methods
    async loginStaff(staffId, pin) {
        const response = await this.request('/staff/login', {
            method: 'POST',
            body: JSON.stringify({ staffId, pin }),
        });
        
        if (response.success && response.token) {
            this.setToken(response.token);
        }
        
        return response;
    }

    async logoutStaff() {
        try {
            await this.request('/staff/logout', { method: 'POST' });
        } catch (error) {
            console.error('Staff logout error:', error);
        } finally {
            this.setToken(null);
        }
    }

    async getQRCodeDetails(qrCodeId) {
        return await this.request(`/staff/qr/${qrCodeId}`);
    }

    async logAccess(accessData) {
        return await this.request('/staff/access/log', {
            method: 'POST',
            body: JSON.stringify(accessData),
        });
    }

    async getAccessLogs(params = {}) {
        const queryParams = new URLSearchParams();
        
        if (params.page !== undefined) queryParams.append('page', params.page);
        if (params.size !== undefined) queryParams.append('size', params.size);
        if (params.staffId) queryParams.append('staffId', params.staffId);
        if (params.action) queryParams.append('action', params.action);
        if (params.date) queryParams.append('date', params.date);
        
        const queryString = queryParams.toString();
        const endpoint = `/staff/access/logs${queryString ? '?' + queryString : ''}`;
        
        return await this.request(endpoint);
    }

    async getStaffDashboardStats() {
        return await this.request('/staff/dashboard/stats');
    }

    // Manager API methods
    async getAllManagers() {
        return await this.request('/managers');
    }

    async createManager(managerData) {
        return await this.request('/managers', {
            method: 'POST',
            body: JSON.stringify(managerData),
        });
    }

    async getManager(managerId) {
        return await this.request(`/managers/${managerId}`);
    }

    async updateManager(managerId, managerData) {
        return await this.request(`/managers/${managerId}`, {
            method: 'PUT',
            body: JSON.stringify(managerData),
        });
    }

    async deleteManager(managerId) {
        return await this.request(`/managers/${managerId}`, {
            method: 'DELETE',
        });
    }

    async getManagerProfile(managerId) {
        return await this.request(`/managers/${managerId}/profile`);
    }

    // Utility method to handle API errors
    handleApiError(error) {
        if (error.message.includes('401') || error.message.includes('Token')) {
            // Token expired or invalid
            this.setToken(null);
            localStorage.removeItem('securaccess_user');
            window.location.href = '/login';
            return;
        }
        
        throw error;
    }
}

// Create singleton instance
const apiService = new ApiService();

export default apiService;