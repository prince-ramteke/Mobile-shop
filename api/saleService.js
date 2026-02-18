import axiosClient from './axiosClient';

const saleService = {
    // Get all sales with pagination and filters

    // Get all sales with pagination and filters
    getAll: async (params = {}) => {
        const response = await axiosClient.get('/api/sales', { params });
        return response.data;
    },

    // Get sale by ID
    getById: async (id) => {
        const response = await axiosClient.get(`/api/sales/${id}`);
        return response.data;
    },

    // Create new sale/invoice
    create: async (saleData) => {
        const response = await axiosClient.post('/api/sales', saleData);
        return response.data;
    },

    // Update sale
    update: async (id, saleData) => {
        const response = await axiosClient.put(`/api/sales/${id}`, saleData);
        return response.data;
    },

    // Delete sale
    delete: async (id) => {
        const response = await axiosClient.delete(`/api/sales/${id}`);
        return response.data;
    },

    // Get today's sales
    getTodaySales: async () => {
        const today = new Date().toISOString().split('T')[0];
        const response = await axiosClient.get('/api/sales', {
            params: { start: today, end: today }
        });
        return response.data;
    },

    // Get sales by date range
    getByDateRange: async (startDate, endDate) => {
        const response = await axiosClient.get('/api/sales', {
            params: { start: startDate, end: endDate },
        });
        return response.data;
    },

    // Add payment to sale
    addPayment: async (id, paymentData) => {
        // Backend expects { saleId, amount, paymentMode, referenceNote } at /api/payments
        const payload = { saleId: id, ...paymentData };
        const response = await axiosClient.post(`/api/payments`, payload);
        return response.data;
    },

    // Generate invoice PDF
    generateInvoice: async (id) => {
        const response = await axiosClient.get(`/api/pdf/sale/${id}`, {
            responseType: 'blob',
        });
        return response.data;
    },

    // Send invoice via WhatsApp (Backend sends automatically on create)
    sendInvoiceWhatsApp: async (id) => {
        // const response = await axiosClient.post(`/api/sales/${id}/send-whatsapp`);
        // return response.data;
        console.warn('sendInvoiceWhatsApp is handled automatically by backend on creation.');
        return { success: true };
    },

    // Get pending sales (with dues)
    getPendingSales: async () => {
        const response = await axiosClient.get('/api/sales', {
            params: { pending: true }
        });
        return response.data;
    },

    // Calculate totals for items
    calculateTotals: (items, gstPercentage = 18) => {
        const subtotal = items.reduce((sum, item) => {
            return sum + (item.quantity * item.price);
        }, 0);

        const gstAmount = (subtotal * gstPercentage) / 100;
        const cgst = gstAmount / 2;
        const sgst = gstAmount / 2;
        const grandTotal = subtotal + gstAmount;

        return {
            subtotal,
            cgst,
            sgst,
            igst: 0, // For inter-state
            gstAmount,
            grandTotal,
        };
    },
};

export default saleService;

// ================= MOBILE SALES (NEW PHONE MODULE) =================

// Create Mobile Sale
saleService.createMobileSale = async (data) => {
    const res = await axiosClient.post('/api/mobile-sales', data);
    return res.data;
};

// Update Mobile Sale
saleService.updateMobileSale = async (id, data) => {
    const res = await axiosClient.put(`/api/mobile-sales/${id}`, data);
    return res.data;
};


// Get All Mobile Sales
saleService.getMobileSales = async () => {
    const res = await axiosClient.get('/api/mobile-sales');
    return res.data;
};

// Get Mobile Sale By ID
saleService.getMobileSaleById = async (id) => {
    const res = await axiosClient.get(`/api/mobile-sales/${id}`);
    return res.data;
};

// Download Mobile Invoice PDF
saleService.downloadMobileInvoice = async (id) => {
    const res = await axiosClient.get(`/api/mobile-sales/${id}/invoice`, {
        responseType: 'blob',
    });
    return res.data;
};

// Recover Pending Payment (Mobile Sale)
saleService.recoverPayment = async (id, amount) => {
    const res = await axiosClient.put(`/api/mobile-sales/${id}/recover?amount=${amount}`);
    return res.data;
};


// Get Pending Mobile Sales
saleService.getPendingMobileSales = async () => {
  const res = await axiosClient.get('/api/mobile-sales/pending');
  return res.data;
};
// Recovery Ledger
saleService.getRecoveryLedger = async (id) => {
  const res = await axiosClient.get(`/api/mobile-sales/${id}/ledger`);
  return res.data;
};

// Send WhatsApp Reminder
saleService.sendReminder = async (id) => {
  const res = await axiosClient.post(`/api/mobile-sales/${id}/send-reminder`);
  return res.data;
};


