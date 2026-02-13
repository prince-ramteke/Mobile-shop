import { useState, useEffect } from 'react';
import { Layout } from 'antd';
import { motion, AnimatePresence } from 'framer-motion';
import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';

const { Content } = Layout;

const AppLayout = () => {
    const [collapsed, setCollapsed] = useState(false);
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const location = useLocation();

    // Close mobile menu on route change
    useEffect(() => {
        setMobileMenuOpen(false);
    }, [location.pathname]);

    // Handle window resize
    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth < 768) {
                setCollapsed(true);
            }
        };

        handleResize();
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    // Page transition variants
    const pageVariants = {
        initial: {
            opacity: 0,
            y: 20,
        },
        animate: {
            opacity: 1,
            y: 0,
            transition: {
                duration: 0.3,
                ease: 'easeOut',
            },
        },
        exit: {
            opacity: 0,
            y: -20,
            transition: {
                duration: 0.2,
            },
        },
    };

    return (
        <Layout style={{ minHeight: '100vh' }}>
            {/* Sidebar */}
            <Sidebar collapsed={collapsed} onCollapse={setCollapsed} />

            {/* Mobile Overlay */}
            <AnimatePresence>
                {mobileMenuOpen && (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        onClick={() => setMobileMenuOpen(false)}
                        style={{
                            position: 'fixed',
                            top: 0,
                            left: 0,
                            right: 0,
                            bottom: 0,
                            background: 'rgba(0, 0, 0, 0.5)',
                            zIndex: 99,
                            display: 'none',
                        }}
                        className="mobile-overlay"
                    />
                )}
            </AnimatePresence>

            {/* Main Content Area */}
            <Layout
                style={{
                    marginLeft: collapsed ? 80 : 260,
                    transition: 'margin-left 0.3s ease',
                    minHeight: '100vh',
                }}
            >
                {/* Topbar */}
                <Topbar
                    collapsed={collapsed}
                    onMenuClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                />

                {/* Page Content */}
                <Content
                    style={{
                        padding: '24px',
                        minHeight: 'calc(100vh - 64px)',
                        background: 'var(--color-bg-primary)',
                    }}
                >
                    <AnimatePresence mode="wait">
                        <motion.div
                            key={location.pathname}
                            variants={pageVariants}
                            initial="initial"
                            animate="animate"
                            exit="exit"
                            style={{ minHeight: '100%' }}
                        >
                            <Outlet />
                        </motion.div>
                    </AnimatePresence>
                </Content>
            </Layout>

            {/* Global Styles for Responsive */}
            <style>{`
        @media (max-width: 768px) {
          .mobile-menu-btn {
            display: flex !important;
          }
          .mobile-overlay {
            display: block !important;
          }
        }
      `}</style>
        </Layout>
    );
};

export default AppLayout;
