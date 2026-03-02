import axiosClient from './axiosClient';

/**
 * Dashboard Service - API integration for dashboard data
 * All endpoints now connected to real backend
 */
const dashboardService = {
    // Get dashboard stats
    getDashboard: async () => {
        const response = await axiosClient.get('/api/dashboard');
        return response.data;
    },

    // Get today's summary
    getTodaySummary: async () => {
        const response = await axiosClient.get('/api/dashboard/today-summary');
        return response.data;
    },

    // Get recent activities (now from backend)
    getRecentActivities: async (limit = 10) => {
        const response = await axiosClient.get('/api/sales', {
            params: { page: 0, size: limit }
        });
        // Transform sales data to activities
        const sales = response.data?.content || [];
        return sales.map(sale => ({
            id: sale.id,
            type: 'sale',
            customerName: sale.customerName || 'Unknown',
            amount: sale.grandTotal,
            time: sale.createdAt
        }));
    },

    // Get quick stats
    getQuickStats: async () => {
        const response = await axiosClient.get('/api/dashboard/quick-stats');
        return response.data;
    },

    // Get revenue chart data
    getRevenueChartData: async (period = 'week') => {
    const response = await axiosClient.get('/api/dashboard/revenue-chart', {
        params: { period }
    });

    const data = response.data || [];

    return data.map(item => ({
        name: item.name,
        sales: item.value || 0
    }));

    },

    // Get repair status distribution
   getRepairStatusDistribution: async () => {
    const response = await axiosClient.get('/api/dashboard/repair-status');
    return response.data;
},

    // Get top customers
    getTopCustomers: async (limit = 5) => {
        const response = await axiosClient.get('/api/customers/search', {
            params: { query: '', page: 0, size: limit }
        });
        return response.data?.content || [];
    },

    // Get pending items count
    getPendingCounts: async () => {
        const response = await axiosClient.get('/api/dashboard/pending-counts');
        return response.data;
    },
};

export default dashboardService;
