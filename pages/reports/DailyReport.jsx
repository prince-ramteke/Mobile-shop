import { useState, useEffect } from 'react';
import { Card, Typography, Row, Col, DatePicker, Table, Statistic, Button, Space } from 'antd';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import {
    BarChartOutlined,
    DollarOutlined,
    ShoppingCartOutlined,
    ToolOutlined,
    DownloadOutlined,
} from '@ant-design/icons';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    Legend,
} from 'recharts';
import reportService from '../../api/reportService';
import Loading from '../../components/common/Loading';

const { Title, Text } = Typography;

const DailyReport = () => {
    const [loading, setLoading] = useState(true);
    const [date, setDate] = useState(dayjs());
    const [report, setReport] = useState(null);

    useEffect(() => {
        loadReport();
    }, [date]);

    const loadReport = async () => {
        try {
            setLoading(true);
            const data = await reportService.getDailyReport(date.format('YYYY-MM-DD'));
            setReport(data);
        } catch (error) {
            console.error('Failed to load report:', error);
            // Demo data
            setReport({
                date: date.format('YYYY-MM-DD'),
                totalSales: 45600,
                totalRepairs: 12,
                totalRevenue: 58200,
                gstCollected: 8900,
                transactions: [
                    { id: 1, type: 'Sale', customer: 'John Doe', amount: 15000, time: '10:30 AM' },
                    { id: 2, type: 'Repair', customer: 'Jane Smith', amount: 3500, time: '11:45 AM' },
                    { id: 3, type: 'Sale', customer: 'Mike Johnson', amount: 8900, time: '02:15 PM' },
                ],
                hourlyData: Array.from({ length: 12 }, (_, i) => ({
                    hour: `${9 + i}:00`,
                    sales: Math.floor(Math.random() * 10000),
                    repairs: Math.floor(Math.random() * 3),
                })),
            });
        } finally {
            setLoading(false);
        }
    };

    const handleExportPdf = async () => {
        try {
            const blob = await reportService.exportPdf('daily', { date: date.format('YYYY-MM-DD') });
            reportService.downloadFile(blob, `Daily-Report-${date.format('YYYY-MM-DD')}.pdf`);
        } catch (error) {
            console.error('Failed to export:', error);
        }
    };

    const transactionColumns = [
        { title: 'Time', dataIndex: 'time', key: 'time' },
        {
            title: 'Type',
            dataIndex: 'type',
            key: 'type',
            render: (type) => (
                <span style={{ color: type === 'Sale' ? '#10b981' : '#3b82f6' }}>
                    {type}
                </span>
            ),
        },
        { title: 'Customer', dataIndex: 'customer', key: 'customer' },
        {
            title: 'Amount',
            dataIndex: 'amount',
            key: 'amount',
            render: (amount) => `₹${amount.toLocaleString()}`,
        },
    ];

    if (loading) {
        return <Loading fullScreen={false} text="Loading report..." />;
    }

    return (
        <div>
            {/* Page Header */}
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="page-header"
            >
                <Row justify="space-between" align="middle">
                    <Col>
                        <Title level={2} className="page-title">
                            <BarChartOutlined style={{ marginRight: 12 }} />
                            Daily Report
                        </Title>
                        <Text type="secondary">Sales and repair summary for the day</Text>
                    </Col>
                    <Col>
                        <Space>
                            <DatePicker
                                value={date}
                                onChange={(d) => setDate(d || dayjs())}
                                allowClear={false}
                            />
                            <Button icon={<DownloadOutlined />} onClick={handleExportPdf}>
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
                                title="Sales Amount"
                                value={report?.totalSales || 0}
                                prefix={<ShoppingCartOutlined />}
                                suffix="₹"
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
                        <Card>
                            <Statistic
                                title="Repairs Completed"
                                value={report?.totalRepairs || 0}
                                prefix={<ToolOutlined />}
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
                        <Card>
                            <Statistic
                                title="GST Collected"
                                value={report?.gstCollected || 0}
                                suffix="₹"
                                valueStyle={{ color: '#8b5cf6' }}
                            />
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Hourly Chart */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}>
                <Card title="Hourly Breakdown" style={{ marginBottom: 24 }}>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={report?.hourlyData || []}>
                            <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                            <XAxis dataKey="hour" stroke="var(--color-text-muted)" />
                            <YAxis stroke="var(--color-text-muted)" />
                            <Tooltip
                                contentStyle={{
                                    background: 'var(--color-bg-secondary)',
                                    border: '1px solid var(--color-border)',
                                    borderRadius: 8,
                                }}
                            />
                            <Legend />
                            <Bar dataKey="sales" name="Sales (₹)" fill="#00d4ff" radius={[4, 4, 0, 0]} />
                        </BarChart>
                    </ResponsiveContainer>
                </Card>
            </motion.div>

            {/* Transactions Table */}
            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6 }}>
                <Card title="Today's Transactions">
                    <Table
                        columns={transactionColumns}
                        dataSource={report?.transactions || []}
                        rowKey="id"
                        pagination={{ pageSize: 10 }}
                    />
                </Card>
            </motion.div>
        </div>
    );
};

export default DailyReport;
