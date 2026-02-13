import axiosClient from './axiosClient';

const authService = {
    login: async (username, password) => {
        const { data } = await axiosClient.post('/api/auth/login', {
            username,
            password,
        });

        localStorage.setItem('token', data.token);
        localStorage.setItem(
            'user',
            JSON.stringify({ username: data.username, role: data.role })
        );

        return data;
    },

    logout: () => {
        localStorage.clear();
    },

    getCurrentUser: () => {
        const raw = localStorage.getItem('user');
        return raw ? JSON.parse(raw) : null;
    },

    isAuthenticated: () => !!localStorage.getItem('token'),
};

export default authService;
