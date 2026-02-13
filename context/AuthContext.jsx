import { createContext, useContext, useState, useCallback } from 'react';
import { message } from 'antd';
import authService from '../api/authService';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {

    // Load user from localStorage at startup
    const [user, setUser] = useState(() => {
        try {
            return authService.getCurrentUser();
        } catch (e) {
            console.error("Auth initialization error", e);
            return null;
        }
    });

    const [loading, setLoading] = useState(false);

    // 🔑 FIXED LOGIN FUNCTION
    const login = useCallback(async (username, password) => {
        try {
            setLoading(true);

            const data = await authService.login(username, password);

            // Backend returns: username, token, role
            if (!data || !data.token) {
                throw new Error("Token missing in response");
            }

            const loggedUser = {
                username: data.username,
                role: data.role
            };

            setUser(loggedUser);

            message.success('Login successful');

            return { success: true };

        } catch (error) {
            console.error('Login error:', error);
            message.error(error.response?.data?.message || 'Invalid credentials');
            return { success: false };
        } finally {
            setLoading(false);
        }
    }, []);

    const logout = useCallback(() => {
        authService.logout();
        setUser(null);
        window.location.href = '/login';
    }, []);

    const hasRole = (role) => {
        if (!user || !user.role) return false;
        const userRole = String(user.role).replace('ROLE_', '');
        return userRole === role;
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                loading,
                isAuthenticated: !!user,
                login,
                logout,
                hasRole,
                isAdmin: () => hasRole('ADMIN'),
                isStaff: () => hasRole('STAFF'),
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
