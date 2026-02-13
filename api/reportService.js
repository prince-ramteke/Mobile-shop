import axiosClient from './axiosClient';

/**
 * Report Service - API integration for reports
 * All endpoints now connected to real backend
 */
const reportService = {
    // Get daily report
    getDailyReport: async (date) => {
        const response = await axiosClient.get('/api/reports/daily', {
            params: { date },
        });
        return response.data;
    },

    // Get monthly report
    getMonthlyReport: async (year, month) => {
        const response = await axiosClient.get('/api/reports/monthly', {
            params: { year, month },
        });
        return response.data;
    },

    // Get date range report
    getDateRangeReport: async (startDate, endDate) => {
        const response = await axiosClient.get('/api/reports/date-range', {
            params: { startDate, endDate },
        });
        return response.data;
    },

    // Get GST report
    getGstReport: async (startDate, endDate) => {
        const response = await axiosClient.get('/api/reports/gst', {
            params: { startDate, endDate },
        });
        return response.data;
    },

    // Get dashboard stats
    getDashboardStats: async () => {
        const response = await axiosClient.get('/api/dashboard');
        return response.data;
    },

    // Get sales summary
    getSalesSummary: async (period = 'week') => {
        const response = await axiosClient.get('/api/reports/sales-summary', {
            params: { period },
        });
        return response.data;
    },

    // Get revenue trend
    getRevenueTrend: async (days = 30) => {
        const response = await axiosClient.get('/api/reports/revenue-trend', {
            params: { days },
        });
        return response.data;
    },

    // Export report as PDF - now uses real backend
    exportPdf: async (reportType, params = {}) => {
        let endpoint;
        switch (reportType) {
            case 'daily':
                endpoint = '/api/reports/daily/pdf';
                break;
            case 'monthly':
                endpoint = '/api/reports/monthly/pdf';
                break;
            case 'gst':
                endpoint = '/api/reports/gst/pdf';
                break;
            default:
                throw new Error('Unknown report type');
        }
        
        const response = await axiosClient.get(endpoint, {
            params,
            responseType: 'blob',
        });
        return response.data;
    },

    // Export report as Excel - now uses real backend
    exportExcel: async (reportType, params = {}) => {
        let endpoint;
        switch (reportType) {
            case 'daily':
                endpoint = '/api/reports/daily/excel';
                break;
            case 'monthly':
                endpoint = '/api/reports/monthly/excel';
                break;
            case 'gst':
                endpoint = '/api/reports/gst/excel';
                break;
            default:
                throw new Error('Unknown report type');
        }
        
        const response = await axiosClient.get(endpoint, {
            params,
            responseType: 'blob',
        });
        return response.data;
    },

    // Download blob as file
    downloadFile: (blob, filename) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
    },
};

export default reportService;
