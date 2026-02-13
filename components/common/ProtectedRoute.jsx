import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Loading from './Loading';

const ProtectedRoute = ({ children, requiredRole = null, adminOnly = false }) => {
    const { isAuthenticated, loading, hasRole, isAdmin } = useAuth();
    const location = useLocation();

    // Show loading while checking auth status
    if (loading) {
        return <Loading fullScreen text="Authenticating..." />;
    }

    // Redirect to login if not authenticated
    if (!isAuthenticated) {
        return <Navigate to="/login" state={{ from: location }} replace />;
    }

    // Check admin-only access
    if (adminOnly && !isAdmin()) {
        return (
            <div
                style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    minHeight: '60vh',
                    padding: 40,
                    textAlign: 'center',
                }}
            >
                <h2 style={{ color: 'var(--color-error)', marginBottom: 16 }}>
                    Access Denied
                </h2>
                <p style={{ color: 'var(--color-text-secondary)', maxWidth: 400 }}>
                    You don't have permission to access this page.
                    This area is restricted to administrators only.
                </p>
                <button
                    onClick={() => window.history.back()}
                    style={{
                        marginTop: 24,
                        padding: '10px 24px',
                        background: 'var(--color-primary)',
                        color: '#fff',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                    }}
                >
                    Go Back
                </button>
            </div>
        );
    }

    // Check required role
    if (requiredRole && !hasRole(requiredRole)) {
        return (
            <div
                style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    minHeight: '60vh',
                    padding: 40,
                    textAlign: 'center',
                }}
            >
                <h2 style={{ color: 'var(--color-error)', marginBottom: 16 }}>
                    Access Denied
                </h2>
                <p style={{ color: 'var(--color-text-secondary)', maxWidth: 400 }}>
                    You don't have the required permissions to access this page.
                </p>
                <button
                    onClick={() => window.history.back()}
                    style={{
                        marginTop: 24,
                        padding: '10px 24px',
                        background: 'var(--color-primary)',
                        color: '#fff',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                    }}
                >
                    Go Back
                </button>
            </div>
        );
    }

    // Render children if all checks pass
    return children;
};

export default ProtectedRoute;
