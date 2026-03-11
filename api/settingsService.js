import axiosClient from './axiosClient';

const settingsService = {

    getSettings: async () => {
        const response = await axiosClient.get('/api/settings');
        return response.data;
    },

    updateSettings: async (settingsData) => {
        const response = await axiosClient.put('/api/settings', settingsData);
        return response.data;
    },
};

export default settingsService;