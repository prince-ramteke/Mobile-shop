import axiosClient from './axiosClient';

/**
 * Repair Service - Fixed API integration
 * Properly handles status updates and stats
 */
const repairService = {
    // Get all repairs with pagination and filters
   getAll: async (params = {}) => {
    const queryParams = {
        query: params.query || '',
        page: params.page || 0,
        size: params.size || 1000  // ✅ INCREASE SIZE to get all repairs
    };
    const response = await axiosClient.get('/api/repairs/search', { params: queryParams });
    return response.data;
},

    // Get repair by ID
    getById: async (id) => {
        const response = await axiosClient.get(`/api/repairs/${id}`);
        return response.data;
    },

    // Create new repair job
    create: async (repairData) => {
        const response = await axiosClient.post('/api/repairs', repairData);
        return response.data;
    },

    // Update repair
    update: async (id, repairData) => {
        const response = await axiosClient.put(`/api/repairs/${id}`, repairData);
        return response.data;
    },

    // Delete repair
    delete: async (id) => {
        const response = await axiosClient.delete(`/api/repairs/${id}`);
        return response.data;
    },

    // Update repair status - uses PUT with full object
    updateStatus: async (id, status) => {
        // Get current repair data
        const { data: job } = await axiosClient.get(`/api/repairs/${id}`);
        
        // Update status
        const updatedJob = { 
            ...job, 
            status,
            customerId: job.customerId 
        };
        
        // PUT updated object
        const response = await axiosClient.put(`/api/repairs/${id}`, updatedJob);
        return response.data;
    },

    // Get repairs by status - client-side filter since backend doesn't support
    getByStatus: async (status) => {
        const response = await axiosClient.get('/api/repairs/search', {
            params: { query: '', page: 0, size: 1000 }
        });
        const repairs = response.data?.content || [];
        return repairs.filter(r => r.status === status);
    },

    // Get pending repairs - client-side filter
    getPending: async () => {
        const response = await axiosClient.get('/api/repairs/search', {
            params: { query: '', page: 0, size: 1000 }
        });
        const repairs = response.data?.content || [];
        return repairs.filter(r => r.pendingAmount > 0);
    },

    // Search repairs by IMEI or phone
    search: async (query) => {
        const response = await axiosClient.get('/api/repairs/search', {
            params: { query, page: 0, size: 20 },
        });
        return response.data;
    },

    // Send status update via WhatsApp
    sendStatusWhatsApp: async (id) => {
        const response = await axiosClient.post(`/api/whatsapp/repair/${id}`);
        return response.data;
    },

    // Get repair statistics - now from backend
    getStats: async () => {
        const response = await axiosClient.get('/api/dashboard/pending-counts');
        return response.data;
    },

    // Repair status options - matches backend enum
    statusOptions: [
        { value: 'PENDING', label: 'Pending', color: 'orange' },
        { value: 'IN_PROGRESS', label: 'In Progress', color: 'blue' },
        { value: 'PENDING_PARTS', label: 'Pending Parts', color: 'purple' },
        { value: 'COMPLETED', label: 'Completed', color: 'green' },
        { value: 'DELIVERED', label: 'Delivered', color: 'cyan' },
        { value: 'CANCELLED', label: 'Cancelled', color: 'red' },
    ],

    // Get status color
    getStatusColor: (status) => {
        const statusOption = repairService.statusOptions.find(s => s.value === status);
        return statusOption?.color || 'default';
    },

    // Get status label
    getStatusLabel: (status) => {
        const statusOption = repairService.statusOptions.find(s => s.value === status);
        return statusOption?.label || status;
    },
};

export default repairService;
