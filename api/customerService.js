import axiosClient from './axiosClient';

/**
 * Customer Service - Fixed API integration
 * Properly handles Spring Page responses
 */
const customerService = {
    // Get all customers with pagination
    getAll: async (params = {}) => {
        const queryParams = {
            query: '',
            page: params.page || 0,
            size: params.size || 10
        };
        const response = await axiosClient.get('/api/customers/search', { params: queryParams });
        // Handle Spring Page response
        return response.data?.content || response.data || [];
    },

    // Get customer by ID
    getById: async (id) => {
        const response = await axiosClient.get(`/api/customers/${id}`);
        return response.data;
    },

    // Create new customer
    create: async (customerData) => {
        const response = await axiosClient.post('/api/customers', customerData);
        return response.data;
    },

    // Update customer
    update: async (id, customerData) => {
        const response = await axiosClient.put(`/api/customers/${id}`, customerData);
        return response.data;
    },

    // Delete customer
    delete: async (id) => {
        const response = await axiosClient.delete(`/api/customers/${id}`);
        return response.data;
    },

    // Search customers by name or phone
    search: async (query, params = {}) => {
        const response = await axiosClient.get('/api/customers/search', {
            params: { 
                query,
                page: params.page || 0,
                size: params.size || 10
            },
        });
        // Handle Spring Page response
        return response.data?.content || response.data || [];
    },

    // Get customers with pending dues
    getWithDues: async () => {
        const response = await axiosClient.get('/api/dues');
        return response.data?.content || response.data || [];
    },

    // Get customer purchase history
    getPurchaseHistory: async (id) => {
        const response = await axiosClient.get('/api/sales', {
            params: { customerId: id }
        });
        return response.data?.content || response.data || [];
    },

    // Get customer repair history
    getRepairHistory: async (id) => {
        const response = await axiosClient.get('/api/repairs/search', {
            params: { query: '', customerId: id, page: 0, size: 100 }
        });
        return response.data?.content || response.data || [];
    },
};

export default customerService;

// ================= CUSTOMER LEDGER =================
export const getCustomerLedger = (customerId) => {
  return axiosClient.get(`/customers/${customerId}/ledger`);
};

