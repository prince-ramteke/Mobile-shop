import axiosClient from './axiosClient';

/**
 * Settings Service - API integration for shop settings
 * Now connected to real backend instead of localStorage
 */
const settingsService = {
    // Get shop settings from backend
    getSettings: async () => {
        const response = await axiosClient.get('/api/settings');
        return response.data;
    },

    // Update shop settings
    updateSettings: async (settingsData) => {
        const response = await axiosClient.put('/api/settings', settingsData);
        return response.data;
    },

    // Get GST settings
    getGstSettings: async () => {
        const response = await axiosClient.get('/api/settings/gst');
        return response.data;
    },

    // Update GST settings
    updateGstSettings: async (gstData) => {
        const response = await axiosClient.put('/api/settings/gst', gstData);
        return response.data;
    },

    // Get WhatsApp settings
    getWhatsAppSettings: async () => {
        const response = await axiosClient.get('/api/settings/whatsapp');
        return response.data;
    },

    // Update WhatsApp settings
    updateWhatsAppSettings: async (whatsappData) => {
        const response = await axiosClient.put('/api/settings/whatsapp', whatsappData);
        return response.data;
    },

    // Get invoice template settings
    getInvoiceSettings: async () => {
        const response = await axiosClient.get('/api/settings');
        return response.data;
    },

    // Update invoice template settings
    updateInvoiceSettings: async (invoiceData) => {
        const response = await axiosClient.put('/api/settings', invoiceData);
        return response.data;
    },

    // Test WhatsApp connection
    testWhatsAppConnection: async () => {
        const response = await axiosClient.post('/api/settings/whatsapp/test');
        return response.data;
    },

    // Get backup settings
    getBackupSettings: async () => {
        const response = await axiosClient.get('/api/settings');
        return { lastBackup: response.data?.lastBackup };
    },

    // Trigger manual backup
    triggerBackup: async () => {
        const response = await axiosClient.post('/api/settings/backup');
        return response.data;
    },
};

export default settingsService;
