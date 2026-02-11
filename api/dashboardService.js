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
        const response = await axiosClient.get('/api/reports/revenue-trend', {
            params: { days: period === 'week' ? 7 : period === 'month' ? 30 : 365 }
        });
        const data = response.data || [];
        // Transform to chart format
        return data.map(item => ({
            name: new Date(item.date).toLocaleDateString('en-US', { weekday: 'short' }),
            value: item.amount || 0
        }));
    },

    // Get repair status distribution
    getRepairStatusDistribution: async () => {
        const response = await axiosClient.get('/api/repairs/search', {
            params: { query: '', page: 0, size: 1000 }
        });
        const repairs = response.data?.content || [];
        
        // Count by status
        const counts = repairs.reduce((acc, repair) => {
            acc[repair.status] = (acc[repair.status] || 0) + 1;
            return acc;
        }, {});
        
        return [
            { name: 'Pending', value: counts.PENDING || 0 },
            { name: 'In Progress', value: counts.IN_PROGRESS || 0 },
            { name: 'Completed', value: counts.COMPLETED || 0 },
        ];
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
