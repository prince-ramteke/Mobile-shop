import { useState, useEffect } from 'react';
import { Row, Col, Card, Statistic, Typography, Table, Tag, Space, Button, Tooltip } from 'antd';
import { motion } from 'framer-motion';
import {
    DollarOutlined,
    ShoppingCartOutlined,
    ToolOutlined,
    UserOutlined,
    ArrowUpOutlined,
    ArrowDownOutlined,
    RiseOutlined,
    EyeOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import {
    AreaChart,
    Area,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip as RechartsTooltip,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
} from 'recharts';
import dashboardService from '../../api/dashboardService';
import Loading from '../../components/common/Loading';
import EmptyState from '../../components/common/EmptyState';

const { Title, Text } = Typography;

const Dashboard = () => {
    const [loading, setLoading] = useState(true);
    const [stats, setStats] = useState(null);
    const [revenueData, setRevenueData] = useState([]);
    const [repairStatusData, setRepairStatusData] = useState([]);
    const [recentActivities, setRecentActivities] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            setLoading(true);
            const [dashboardData, revenue, activities] = await Promise.all([
                dashboardService.getDashboard(),
                dashboardService.getRevenueChartData('week'),
                dashboardService.getRecentActivities(5),
            ]);

            setStats(dashboardData);
            setRevenueData(revenue || generateDemoRevenueData());
            setRecentActivities(activities || []);

            // Set repair status data from dashboard
            if (dashboardData?.repairStats) {
                setRepairStatusData([
                    { name: 'Pending', value: dashboardData.repairStats.pending || 0, color: '#f59e0b' },
                    { name: 'In Progress', value: dashboardData.repairStats.inProgress || 0, color: '#3b82f6' },
                    { name: 'Completed', value: dashboardData.repairStats.completed || 0, color: '#10b981' },
                ]);
            }
        } catch (error) {
            console.error('Failed to load dashboard data:', error);
            // Set demo data for display
            setStats({
                todaySales: 15420,
                todayRepairs: 8,
                totalCustomers: 1250,
                pendingDues: 45680,
                salesGrowth: 12.5,
                repairsGrowth: -5.2,
            });
            setRevenueData(generateDemoRevenueData());
            setRepairStatusData([
                { name: 'Pending', value: 12, color: '#f59e0b' },
                { name: 'In Progress', value: 8, color: '#3b82f6' },
                { name: 'Completed', value: 25, color: '#10b981' },
            ]);
        } finally {
            setLoading(false);
        }
    };

    // Generate demo revenue data
    const generateDemoRevenueData = () => {
        const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
        return days.map(day => ({
            name: day,
            sales: Math.floor(Math.random() * 20000) + 5000,
            repairs: Math.floor(Math.random() * 10) + 2,
        }));
    };

    // Stat card component
    const StatCard = ({ title, value, prefix, suffix, icon, trend, trendValue, color, onClick }) => (
        <motion.div
            whileHover={{ y: -4, boxShadow: '0 12px 40px -10px var(--color-shadow)' }}
            transition={{ type: 'spring', stiffness: 300 }}
        >
            <Card
                style={{ cursor: onClick ? 'pointer' : 'default' }}
                onClick={onClick}
            >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <div>
                        <Text type="secondary" style={{ fontSize: 14 }}>{title}</Text>
                        <div style={{ marginTop: 8 }}>
                            <Statistic
                                value={value}
                                prefix={prefix}
                                suffix={suffix}
                                valueStyle={{ fontSize: 28, fontWeight: 700, color: 'var(--color-text-primary)' }}
                            />
                        </div>
                        {trend !== undefined && (
                            <div style={{ marginTop: 8, display: 'flex', alignItems: 'center', gap: 4 }}>
                                {trend >= 0 ? (
                                    <ArrowUpOutlined style={{ color: '#10b981', fontSize: 12 }} />
                                ) : (
                                    <ArrowDownOutlined style={{ color: '#ef4444', fontSize: 12 }} />
                                )}
                                <Text style={{ color: trend >= 0 ? '#10b981' : '#ef4444', fontSize: 13 }}>
                                    {Math.abs(trend)}%
                                </Text>
                                <Text type="secondary" style={{ fontSize: 12 }}>vs last week</Text>
                            </div>
                        )}
                    </div>
                    <div
                        style={{
                            width: 56,
                            height: 56,
                            borderRadius: 16,
                            background: `linear-gradient(135deg, ${color}20 0%, ${color}40 100%)`,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                        }}
                    >
                        {icon}
                    </div>
                </div>
            </Card>
        </motion.div>
    );

    // Activity table columns
    const activityColumns = [
        {
            title: 'Type',
            dataIndex: 'type',
            key: 'type',
            render: (type) => (
                <Tag color={type === 'sale' ? 'green' : type === 'repair' ? 'blue' : 'orange'}>
                    {type?.toUpperCase()}
                </Tag>
            ),
        },
        {
            title: 'Customer',
            dataIndex: 'customerName',
            key: 'customerName',
        },
        {
            title: 'Amount',
            dataIndex: 'amount',
            key: 'amount',
            render: (amount) => `₹${amount?.toLocaleString() || 0}`,
        },
        {
            title: 'Time',
            dataIndex: 'time',
            key: 'time',
        },
        {
            title: 'Action',
            key: 'action',
            render: (_, record) => (
                <Tooltip title="View Details">
                    <Button type="text" icon={<EyeOutlined />} size="small" />
                </Tooltip>
            ),
        },
    ];

    if (loading) {
        return <Loading fullScreen={false} text="Loading dashboard..." />;
    }

    return (
        <div>
            {/* Page Header */}
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="page-header"
            >
                <Title level={2} className="page-title">Dashboard</Title>
                <Text type="secondary">Welcome back! Here's what's happening today.</Text>
            </motion.div>

            {/* Stats Cards */}
            <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard
                        title="Today's Sales"
                        value={stats?.todaySales || 0}
                        prefix="₹"
                        icon={<DollarOutlined style={{ fontSize: 24, color: '#10b981' }} />}
                        trend={stats?.salesGrowth}
                        color="#10b981"
                        onClick={() => navigate('/sales')}
                    />
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard
                        title="Repairs Today"
                        value={stats?.todayRepairs || 0}
                        icon={<ToolOutlined style={{ fontSize: 24, color: '#3b82f6' }} />}
                        trend={stats?.repairsGrowth}
                        color="#3b82f6"
                        onClick={() => navigate('/repairs')}
                    />
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard
                        title="Total Customers"
                        value={stats?.totalCustomers || 0}
                        icon={<UserOutlined style={{ fontSize: 24, color: '#8b5cf6' }} />}
                        color="#8b5cf6"
                        onClick={() => navigate('/customers')}
                    />
                </Col>
                <Col xs={24} sm={12} lg={6}>
                    <StatCard
                        title="Pending Dues"
                        value={stats?.pendingDues || 0}
                        prefix="₹"
                        icon={<RiseOutlined style={{ fontSize: 24, color: '#f59e0b' }} />}
                        color="#f59e0b"
                        onClick={() => navigate('/dues')}
                    />
                </Col>
            </Row>

            {/* Charts Section */}
            <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
                {/* Revenue Chart */}
                <Col xs={24} lg={16}>
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                    >
                        <Card title="Revenue Overview" extra={<Button type="link">View Report</Button>}>
                            <ResponsiveContainer width="100%" height={300}>
                                <AreaChart data={revenueData}>
                                    <defs>
                                        <linearGradient id="salesGradient" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="#00d4ff" stopOpacity={0.3} />
                                            <stop offset="95%" stopColor="#00d4ff" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                                    <XAxis dataKey="name" stroke="var(--color-text-muted)" />
                                    <YAxis stroke="var(--color-text-muted)" />
                                    <RechartsTooltip
                                        contentStyle={{
                                            background: 'var(--color-bg-secondary)',
                                            border: '1px solid var(--color-border)',
                                            borderRadius: 8,
                                        }}
                                    />
                                    <Area
                                        type="monotone"
                                        dataKey="sales"
                                        stroke="#00d4ff"
                                        strokeWidth={3}
                                        fill="url(#salesGradient)"
                                    />
                                </AreaChart>
                            </ResponsiveContainer>
                        </Card>
                    </motion.div>
                </Col>

                {/* Repair Status Pie Chart */}
                <Col xs={24} lg={8}>
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.3 }}
                    >
                        <Card title="Repair Status">
                            <ResponsiveContainer width="100%" height={300}>
                                <PieChart>
                                    <Pie
                                        data={repairStatusData}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={100}
                                        paddingAngle={5}
                                        dataKey="value"
                                    >
                                        {repairStatusData.map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={entry.color} />
                                        ))}
                                    </Pie>
                                    <RechartsTooltip />
                                </PieChart>
                            </ResponsiveContainer>
                            <div style={{ display: 'flex', justifyContent: 'center', gap: 16 }}>
                                {repairStatusData.map((item, index) => (
                                    <div key={index} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                        <div
                                            style={{
                                                width: 12,
                                                height: 12,
                                                borderRadius: 3,
                                                background: item.color,
                                            }}
                                        />
                                        <Text type="secondary" style={{ fontSize: 12 }}>
                                            {item.name}: {item.value}
                                        </Text>
                                    </div>
                                ))}
                            </div>
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Recent Activity */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4 }}
            >
                <Card
                    title="Recent Activity"
                    extra={
                        <Space>
                            <Button onClick={() => navigate('/sales/create')}>New Sale</Button>
                            <Button type="primary" onClick={() => navigate('/repairs/create')}>
                                New Repair
                            </Button>
                        </Space>
                    }
                >
                    {recentActivities.length > 0 ? (
                        <Table
                            columns={activityColumns}
                            dataSource={recentActivities}
                            rowKey="id"
                            pagination={false}
                        />
                    ) : (
                        <EmptyState
                            title="No Recent Activity"
                            description="Start by creating a new sale or repair job."
                            actionText="Create Sale"
                            onAction={() => navigate('/sales/create')}
                            size="small"
                        />
                    )}
                </Card>
            </motion.div>
        </div>
    );
};

export default Dashboard;
