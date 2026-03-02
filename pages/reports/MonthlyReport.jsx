import { useState, useEffect } from 'react';
import { Card, Typography, Row, Col, DatePicker, Statistic, Button, Space, Table } from 'antd';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import {
    CalendarOutlined,
    DollarOutlined,
    RiseOutlined,
    DownloadOutlined,
} from '@ant-design/icons';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    Legend,
    AreaChart,
    Area,
} from 'recharts';
import reportService from '../../api/reportService';
import Loading from '../../components/common/Loading';

const { Title, Text } = Typography;

const MonthlyReport = () => {
    const [loading, setLoading] = useState(true);
    const [month, setMonth] = useState(dayjs());
    const [report, setReport] = useState(null);

    useEffect(() => {
        loadReport();
    }, [month]);

    const loadReport = async () => {
        try {
            setLoading(true);
            const data = await reportService.getMonthlyReport(
    month.year(),
    month.month() + 1
);

// Map backend response safely
const formattedData = {
    ...data,
    dailyData: (data.dailyData || data.dailyRevenue || []).map(item => ({
        day: item.day,
        sales: item.sales ?? item.revenue ?? 0,
    }))
};

setReport(formattedData);
        } catch (error) {
            console.error('Failed to load report:', error);
            // Demo data
            const daysInMonth = month.daysInMonth();
            setReport({
                month: month.format('MMMM YYYY'),
                totalRevenue: 523400,
                totalSales: 412300,
                totalRepairs: 45,
                averageDailySales: 16800,
                gstCollected: 79200,
                growth: 12.5,
                dailyData: Array.from({ length: daysInMonth }, (_, i) => ({
                    day: i + 1,
                    sales: Math.floor(Math.random() * 25000) + 5000,
                    repairs: Math.floor(Math.random() * 5),
                })),
                topCustomers: [
                    { name: 'John Doe', amount: 45000, visits: 8 },
                    { name: 'Jane Smith', amount: 32000, visits: 5 },
                    { name: 'Mike Johnson', amount: 28000, visits: 4 },
                ],
            });
        } finally {
            setLoading(false);
        }
    };

    const handleExport = async () => {
        try {
            const blob = await reportService.exportPdf('monthly', {
                year: month.year(),
                month: month.month() + 1
            });
            reportService.downloadFile(blob, `Monthly-Report-${month.format('YYYY-MM')}.pdf`);
        } catch (error) {
            console.error('Failed to export:', error);
        }
    };

    const topCustomerColumns = [
        { title: 'Customer', dataIndex: 'name', key: 'name' },
        {
            title: 'Total Spent',
            dataIndex: 'amount',
            key: 'amount',
            render: (amount) => `₹${amount.toLocaleString()}`,
        },
        { title: 'Visits', dataIndex: 'visits', key: 'visits' },
    ];

    if (loading) {
        return <Loading fullScreen={false} text="Loading report..." />;
    }

    return (
        <div>
            {/* Page Header */}
            <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} className="page-header">
                <Row justify="space-between" align="middle">
                    <Col>
                        <Title level={2} className="page-title">
                            <CalendarOutlined style={{ marginRight: 12 }} />
                            Monthly Report
                        </Title>
                        <Text type="secondary">Performance summary for {month.format('MMMM YYYY')}</Text>
                    </Col>
                    <Col>
                        <Space>
                            <DatePicker
                                value={month}
                                onChange={(m) => setMonth(m || dayjs())}
                                picker="month"
                                allowClear={false}
                            />
                            <Button icon={<DownloadOutlined />} onClick={handleExport}>
                                Export PDF
                            </Button>
                        </Space>
                    </Col>
                </Row>
            </motion.div>

            {/* Summary Cards */}
            <Row gutter={24} style={{ marginBottom: 24 }}>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                        <Card>
                            <Statistic
                                title="Total Revenue"
                                value={report?.totalRevenue || 0}
                                prefix={<DollarOutlined />}
                                suffix="₹"
                                valueStyle={{ color: '#10b981' }}
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
                        <Card>
                            <Statistic
                                title="Avg Daily Sales"
                                value={report?.averageDailySales || 0}
                                suffix="₹"
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
                        <Card>
                            <Statistic
                                title="Total Repairs"
                                value={report?.totalRepairs || 0}
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
                        <Card>
                            <Statistic
                                title="Growth"
                                value={report?.growth || 0}
                                prefix={<RiseOutlined />}
                                suffix="%"
                                valueStyle={{ color: report?.growth > 0 ? '#10b981' : '#ef4444' }}
                            />
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Revenue Trend */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}>
                <Card title="Daily Revenue Trend" style={{ marginBottom: 24 }}>
                    <ResponsiveContainer width="100%" height={300}>
                        <AreaChart data={report?.dailyData || []}>
                            <defs>
                                <linearGradient id="colorSales" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="5%" stopColor="#00d4ff" stopOpacity={0.3} />
                                    <stop offset="95%" stopColor="#00d4ff" stopOpacity={0} />
                                </linearGradient>
                            </defs>
                            <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                            <XAxis dataKey="day" stroke="var(--color-text-muted)" />
                            <YAxis stroke="var(--color-text-muted)" />
                            <Tooltip
                                contentStyle={{
                                    background: 'var(--color-bg-secondary)',
                                    border: '1px solid var(--color-border)',
                                    borderRadius: 8,
                                }}
                            />
                            <Area
                                type="monotone"
                                dataKey="sales"
                                name="Sales (₹)"
                                stroke="#00d4ff"
                                strokeWidth={2}
                                fill="url(#colorSales)"
                            />
                        </AreaChart>
                    </ResponsiveContainer>
                </Card>
            </motion.div>

            {/* Top Customers */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6 }}>
                <Card title="Top Customers This Month">
                    <Table
                        columns={topCustomerColumns}
                        dataSource={report?.topCustomers || []}
                        rowKey="name"
                        pagination={false}
                    />
                </Card>
            </motion.div>
        </div>
    );
};

export default MonthlyReport;
