import { useState } from 'react';
import { Layout, Input, Badge, Avatar, Dropdown, Switch, Space, Typography, Button } from 'antd';
import { motion } from 'framer-motion';
import {
    SearchOutlined,
    BellOutlined,
    UserOutlined,
    LogoutOutlined,
    SettingOutlined,
    SunOutlined,
    MoonOutlined,
    MenuOutlined,
} from '@ant-design/icons';
import { useAuth } from '../../context/AuthContext';
import { useTheme } from '../../context/ThemeContext';
import { useNavigate } from 'react-router-dom';

const { Header } = Layout;
const { Text } = Typography;

const Topbar = ({ collapsed, onMenuClick }) => {
    const { user, logout, isAdmin } = useAuth();
    const { theme, toggleTheme, isDark } = useTheme();
    const navigate = useNavigate();
    const [searchValue, setSearchValue] = useState('');

    // Notification items (would come from API)
    const notifications = [
        { id: 1, title: 'New repair request', time: '5 min ago' },
        { id: 2, title: 'Payment received', time: '1 hour ago' },
        { id: 3, title: 'Due reminder sent', time: '2 hours ago' },
    ];

    // User dropdown menu items
    const userMenuItems = [
        {
            key: 'profile',
            icon: <UserOutlined />,
            label: 'Profile',
            onClick: () => navigate('/profile'),
        },
        ...(isAdmin() ? [{
            key: 'settings',
            icon: <SettingOutlined />,
            label: 'Settings',
            onClick: () => navigate('/settings'),
        }] : []),
        {
            type: 'divider',
        },
        {
            key: 'logout',
            icon: <LogoutOutlined />,
            label: 'Logout',
            danger: true,
            onClick: logout,
        },
    ];

    // Notification dropdown content
    const notificationMenu = {
        items: notifications.map(notif => ({
            key: notif.id,
            label: (
                <div style={{ padding: '8px 0' }}>
                    <Text strong style={{ display: 'block' }}>{notif.title}</Text>
                    <Text type="secondary" style={{ fontSize: 12 }}>{notif.time}</Text>
                </div>
            ),
        })),
    };

    // Handle search
    const handleSearch = (value) => {
        if (value.trim()) {
            navigate(`/search?q=${encodeURIComponent(value)}`);
        }
    };

    return (
        <Header
            style={{
                background: 'var(--color-bg-secondary)',
                padding: '0 24px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                height: 64,
                position: 'sticky',
                top: 0,
                zIndex: 99,
                borderBottom: '1px solid var(--color-border)',
                boxShadow: '0 2px 8px var(--color-shadow)',
                marginLeft: collapsed ? 80 : 260,
                transition: 'margin-left 0.3s ease',
            }}
        >
            {/* Left Section - Mobile Menu & Search */}
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
                {/* Mobile Menu Toggle */}
                <Button
                    type="text"
                    icon={<MenuOutlined />}
                    onClick={onMenuClick}
                    style={{
                        display: 'none',
                        '@media (max-width: 768px)': { display: 'flex' }
                    }}
                    className="mobile-menu-btn"
                />

                {/* Search Bar */}
                <motion.div
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: 0.1 }}
                >
                    <Input
                        placeholder="Search customers, sales, repairs..."
                        prefix={<SearchOutlined style={{ color: 'var(--color-text-muted)' }} />}
                        value={searchValue}
                        onChange={(e) => setSearchValue(e.target.value)}
                        onPressEnter={(e) => handleSearch(e.target.value)}
                        style={{
                            width: 320,
                            borderRadius: 8,
                            background: 'var(--color-bg-tertiary)',
                        }}
                        allowClear
                    />
                </motion.div>
            </div>

            {/* Right Section - Actions */}
            <Space size={16} align="center">
                {/* Theme Toggle */}
                <motion.div
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                >
                    <Button
                        type="text"
                        icon={isDark ? <SunOutlined /> : <MoonOutlined />}
                        onClick={toggleTheme}
                        style={{
                            width: 40,
                            height: 40,
                            borderRadius: 8,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            background: 'var(--color-bg-tertiary)',
                            color: isDark ? '#faad14' : '#1e3a5f',
                        }}
                    />
                </motion.div>

                {/* Notifications */}
                <Dropdown menu={notificationMenu} placement="bottomRight" trigger={['click']}>
                    <motion.div
                        whileHover={{ scale: 1.05 }}
                        whileTap={{ scale: 0.95 }}
                    >
                        <Badge count={notifications.length} size="small">
                            <Button
                                type="text"
                                icon={<BellOutlined />}
                                style={{
                                    width: 40,
                                    height: 40,
                                    borderRadius: 8,
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    background: 'var(--color-bg-tertiary)',
                                }}
                            />
                        </Badge>
                    </motion.div>
                </Dropdown>

                {/* User Avatar & Dropdown */}
                <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" trigger={['click']}>
                    <motion.div
                        whileHover={{ scale: 1.02 }}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: 12,
                            padding: '6px 12px',
                            borderRadius: 8,
                            cursor: 'pointer',
                            background: 'var(--color-bg-tertiary)',
                        }}
                    >
                        <Avatar
                            size={36}
                            style={{
                                background: 'linear-gradient(135deg, #00d4ff 0%, #1e3a5f 100%)',
                            }}
                        >
                            {user?.username?.charAt(0).toUpperCase() || 'U'}
                        </Avatar>
                        <div style={{ lineHeight: 1.2 }}>
                            <Text strong style={{ display: 'block', fontSize: 14 }}>
                                {user?.username || 'User'}
                            </Text>
                            <Text type="secondary" style={{ fontSize: 12 }}>
                                {user?.role || 'Staff'}
                            </Text>
                        </div>
                    </motion.div>
                </Dropdown>
            </Space>
        </Header>
    );
};

export default Topbar;
