import axiosClient from './axiosClient';

/**
 * Reminder Service - API integration for dues and WhatsApp reminders
 * All endpoints now connected to real backend
 */
const reminderService = {
    // Get all dues with pagination
    getDues: async (params = {}) => {
        const response = await axiosClient.get('/api/dues', { 
            params: {
                page: params.page || 0,
                size: params.size || 20
            }
        });
        return response.data;
    },

    // Get overdue customers
    getOverdue: async (days = 7) => {
        const response = await axiosClient.get('/api/dues/overdue', {
            params: { days },
        });
        return response.data;
    },

    // Send due reminders (trigger backend scheduler)
    sendDueReminders: async () => {
        const response = await axiosClient.post('/api/test/send-due-reminders');
        return response.data;
    },

    // Send reminder to specific customer
    sendReminderToCustomer: async (customerId) => {
        const response = await axiosClient.post(`/api/dues/${customerId}/send-reminder`);
        return response.data;
    },

    // Get reminder history for customer
    getReminderHistory: async (customerId) => {
        const response = await axiosClient.get(`/api/dues/${customerId}/reminder-history`);
        return response.data;
    },

    // Mark due as paid
    markAsPaid: async (dueId, paymentData) => {
        const response = await axiosClient.post(`/api/dues/${dueId}/mark-paid`, paymentData);
        return response.data;
    },

    // Get due summary
    getDueSummary: async () => {
        const response = await axiosClient.get('/api/dues/summary');
        return response.data;
    },

    // Send WhatsApp message for invoice
    sendInvoiceWhatsApp: async (saleId) => {
        const response = await axiosClient.post(`/api/whatsapp/invoice/${saleId}`);
        return response.data;
    },

    // Send WhatsApp message for repair status
    sendRepairStatusWhatsApp: async (repairId) => {
        const response = await axiosClient.post(`/api/whatsapp/repair/${repairId}`);
        return response.data;
    },

    // Preview WhatsApp message
    previewMessage: async (type, id) => {
        const response = await axiosClient.get(`/api/whatsapp/preview/${type}/${id}`);
        return response.data;
    },

    // Get WhatsApp message templates
    getTemplates: async () => {
        const response = await axiosClient.get('/api/whatsapp/templates');
        return response.data;
    },

    // Update WhatsApp template
    updateTemplate: async (templateId, templateData) => {
        const response = await axiosClient.put(`/api/whatsapp/templates/${templateId}`, templateData);
        return response.data;
    },
};

export default reminderService;
