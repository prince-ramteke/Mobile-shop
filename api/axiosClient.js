import axios from 'axios';

const axiosClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 30000,
});

// Request interceptor - Add JWT token
axiosClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor - Handle errors
axiosClient.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        const { response } = error;
        const errorMessage = response?.data?.message || 'Something went wrong';

        if (response) {
            switch (response.status) {
                case 401:
                    // Only redirect if not already on login page
                    if (!window.location.pathname.includes('/login')) {
                        localStorage.removeItem('token');
                        localStorage.removeItem('user');
                        window.location.href = '/login';
                    }
                    break;
                case 403:
                    console.error('Access forbidden:', errorMessage);
                    break;
                case 404:
                    console.error('Resource not found:', errorMessage);
                    break;
                case 500:
                    console.error('Server error:', errorMessage);
                    break;
                default:
                    console.error('An error occurred:', errorMessage);
            }
        } else if (error.request) {
            console.error('Network error - please check your connection');
        }

        return Promise.reject(error);
    }
);

export default axiosClient;
