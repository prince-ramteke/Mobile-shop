import { useState, useEffect } from 'react';
import { Layout, Menu, Avatar, Typography, Tooltip } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
    DashboardOutlined,
    ShoppingCartOutlined,
    ToolOutlined,
    UserOutlined,
    DollarOutlined,
    BarChartOutlined,
    SettingOutlined,
    MenuFoldOutlined,
    MenuUnfoldOutlined,
    FileTextOutlined,
    BellOutlined,
    MobileOutlined,
} from '@ant-design/icons';
import { useAuth } from '../../context/AuthContext';

const { Sider } = Layout;
const { Text } = Typography;

const Sidebar = ({ collapsed, onCollapse }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const { isAdmin, user } = useAuth();
    const [selectedKey, setSelectedKey] = useState('dashboard');

    // Menu items configuration
    const menuItems = [
        {
            key: 'dashboard',
            icon: <DashboardOutlined />,
            label: 'Dashboard',
            path: '/',
        },
        {
            key: 'sales',
            icon: <ShoppingCartOutlined />,
            label: 'Sales',
            children: [
                { key: 'create-sale', label: 'New Sale', path: '/sales/create' },
                { key: 'sale-list', label: 'All Sales', path: '/sales' },
            ],
        },
        {
            key: 'repairs',
            icon: <ToolOutlined />,
            label: 'Repairs',
            children: [
                { key: 'create-repair', label: 'New Repair', path: '/repairs/create' },
                { key: 'repair-list', label: 'All Repairs', path: '/repairs' },
            ],
        },
        {
            key: 'customers',
            icon: <UserOutlined />,
            label: 'Customers',
            path: '/customers',
        },
        {
            key: 'dues',
            icon: <DollarOutlined />,
            label: 'Dues & Payments',
            path: '/dues',
        },
        {
            key: 'reports',
            icon: <BarChartOutlined />,
            label: 'Reports',
            children: [
                { key: 'daily-report', label: 'Daily Report', path: '/reports/daily' },
                { key: 'monthly-report', label: 'Monthly Report', path: '/reports/monthly' },
                { key: 'date-range-report', label: 'Date Range', path: '/reports/date-range' },
                { key: 'gst-report', label: 'GST Report', path: '/reports/gst' },
            ],
        },
    ];

    // Admin-only items
    const adminItems = [
        {
            key: 'settings',
            icon: <SettingOutlined />,
            label: 'Settings',
            path: '/settings',
        },
    ];

    // Combine menu items based on role
    const allMenuItems = isAdmin() ? [...menuItems, ...adminItems] : menuItems;

    // Update selected key based on current path
    useEffect(() => {
        const path = location.pathname;

        // Find matching menu item
        const findKey = (items) => {
            for (const item of items) {
                if (item.path === path) return item.key;
                if (item.children) {
                    const childKey = item.children.find(c => c.path === path)?.key;
                    if (childKey) return childKey;
                }
            }
            return null;
        };

        const key = findKey(allMenuItems);
        if (key) setSelectedKey(key);
    }, [location.pathname, allMenuItems]);

    // Handle menu click
    const handleMenuClick = ({ key }) => {
        setSelectedKey(key);

        // Find path for the key
        const findPath = (items) => {
            for (const item of items) {
                if (item.key === key) return item.path;
                if (item.children) {
                    const child = item.children.find(c => c.key === key);
                    if (child) return child.path;
                }
            }
            return null;
        };

        const path = findPath(allMenuItems);
        if (path) navigate(path);
    };

    // Convert menu items to Ant Design format
    const getAntMenuItems = (items) => {
        return items.map(item => ({
            key: item.key,
            icon: item.icon,
            label: item.label,
            children: item.children?.map(child => ({
                key: child.key,
                label: child.label,
            })),
        }));
    };

    return (
        <Sider
            collapsible
            collapsed={collapsed}
            onCollapse={onCollapse}
            trigger={null}
            width={260}
            collapsedWidth={80}
            style={{
                background: 'var(--sidebar-bg)',
                minHeight: '100vh',
                position: 'fixed',
                left: 0,
                top: 0,
                bottom: 0,
                zIndex: 100,
                boxShadow: '4px 0 20px rgba(0, 0, 0, 0.15)',
            }}
        >
            {/* Logo Section */}
            <motion.div
                initial={false}
                animate={{ padding: collapsed ? '16px 8px' : '20px 16px' }}
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: collapsed ? 'center' : 'flex-start',
                    gap: '12px',
                    borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
                    marginBottom: '8px',
                }}
            >
                <motion.div
                    whileHover={{ scale: 1.05, rotate: 5 }}
                    whileTap={{ scale: 0.95 }}
                    style={{
                        width: 44,
                        height: 44,
                        borderRadius: 12,
                        background: 'linear-gradient(135deg, #00d4ff 0%, #1e3a5f 100%)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 4px 15px rgba(0, 212, 255, 0.3)',
                    }}
                >
                    <MobileOutlined style={{ fontSize: 24, color: '#fff' }} />
                </motion.div>

                <AnimatePresence>
                    {!collapsed && (
                        <motion.div
                            initial={{ opacity: 0, x: -10 }}
                            animate={{ opacity: 1, x: 0 }}
                            exit={{ opacity: 0, x: -10 }}
                            transition={{ duration: 0.2 }}
                        >
                            <Text
                                strong
                                style={{
                                    color: '#fff',
                                    fontSize: 16,
                                    whiteSpace: 'nowrap',
                                    display: 'block',
                                }}
                            >
                                Saurabh Mobile
                            </Text>
                            <Text
                                style={{
                                    color: 'rgba(255, 255, 255, 0.6)',
                                    fontSize: 12,
                                }}
                            >
                                Shop Management
                            </Text>
                        </motion.div>
                    )}
                </AnimatePresence>
            </motion.div>

            {/* Collapse Toggle Button */}
            <motion.div
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => onCollapse(!collapsed)}
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    padding: '12px',
                    margin: collapsed ? '8px 8px 16px' : '8px 16px 16px',
                    cursor: 'pointer',
                    borderRadius: 8,
                    background: 'rgba(255, 255, 255, 0.05)',
                    color: '#fff',
                    transition: 'background 0.3s',
                }}
                onMouseEnter={(e) => e.currentTarget.style.background = 'rgba(255, 255, 255, 0.1)'}
                onMouseLeave={(e) => e.currentTarget.style.background = 'rgba(255, 255, 255, 0.05)'}
            >
                {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
                {!collapsed && <Text style={{ color: '#fff', marginLeft: 12 }}>Collapse Menu</Text>}
            </motion.div>

            {/* Navigation Menu */}
            <Menu
                mode="inline"
                selectedKeys={[selectedKey]}
                defaultOpenKeys={collapsed ? [] : ['sales', 'repairs', 'reports']}
                items={getAntMenuItems(allMenuItems)}
                onClick={handleMenuClick}
                style={{
                    background: 'transparent',
                    borderRight: 'none',
                }}
            />

            {/* User Info at Bottom */}
            <AnimatePresence>
                {!collapsed && (
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        exit={{ opacity: 0, y: 20 }}
                        style={{
                            position: 'absolute',
                            bottom: 0,
                            left: 0,
                            right: 0,
                            padding: '16px',
                            borderTop: '1px solid rgba(255, 255, 255, 0.1)',
                            background: 'rgba(0, 0, 0, 0.2)',
                        }}
                    >
                        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                            <Avatar
                                size={40}
                                style={{
                                    background: 'linear-gradient(135deg, #00d4ff 0%, #1e3a5f 100%)',
                                    boxShadow: '0 2px 8px rgba(0, 212, 255, 0.3)',
                                }}
                            >
                                {user?.username?.charAt(0).toUpperCase() || 'U'}
                            </Avatar>
                            <div>
                                <Text strong style={{ color: '#fff', display: 'block' }}>
                                    {user?.username || 'User'}
                                </Text>
                                <Text style={{ color: 'var(--color-accent)', fontSize: 12 }}>
                                    {user?.role || 'Staff'}
                                </Text>
                            </div>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </Sider>
    );
};

export default Sidebar;
