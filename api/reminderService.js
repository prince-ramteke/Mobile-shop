import axiosClient from './axiosClient';

const reminderService = {

    getDues: async () => {
        const response = await axiosClient.get('/api/dues');
        return response.data;
    },

   markAsPaid: async (dueId, data) => {
    if (!dueId) throw new Error("dueId missing");

    const response = await axiosClient.post(
        `/api/dues/${dueId}/mark-paid`,
        data
    );
    return response.data;
},



    getDueSummary: async () => {
        const response = await axiosClient.get('/api/dues/summary');
        return response.data;
    },

 

sendWhatsAppReminder: async (customerId) => {
    const res = await axiosClient.post(`/api/dues/${customerId}/send-whatsapp`);
    return res.data;
},


    getPaymentHistory: async (customerId) => {
    const response = await axiosClient.get(`/api/payments/customer/${customerId}`);
    return response.data;
}

};

export default reminderService;
