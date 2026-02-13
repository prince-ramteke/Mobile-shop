import axiosClient from './axiosClient';

const productService = {
    getAll: async (params = {}) => {
        const response = await axiosClient.get('/api/products/search', { params });
        return response.data;
    },

    getById: async (id) => {
        const response = await axiosClient.get(`/api/products/${id}`);
        return response.data;
    },

    create: async (data) => {
        const response = await axiosClient.post('/api/products', data);
        return response.data;
    },

    update: async (id, data) => {
        const response = await axiosClient.put(`/api/products/${id}`, data);
        return response.data;
    },

    delete: async (id) => {
        const response = await axiosClient.delete(`/api/products/${id}`);
        return response.data;
    },

    search: async (query) => {
        const response = await axiosClient.get('/api/products/search', {
            params: { query }
        });
        return response.data;
    }
};

export default productService;
