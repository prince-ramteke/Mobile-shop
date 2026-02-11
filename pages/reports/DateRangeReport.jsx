import { useState, useEffect } from 'react';
import { Card, Typography, Row, Col, DatePicker, Statistic, Button, Space, Table } from 'antd';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import {
    CalendarOutlined,
    DollarOutlined,
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
} from 'recharts';
import reportService from '../../api/reportService';
import Loading from '../../components/common/Loading';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

const DateRangeReport = () => {
    const [loading, setLoading] = useState(true);
    const [dateRange, setDateRange] = useState([dayjs().subtract(30, 'days'), dayjs()]);
    const [report, setReport] = useState(null);

    useEffect(() => {
        loadReport();
    }, [dateRange]);

    const loadReport = async () => {
        try {
            setLoading(true);
            const [start, end] = dateRange;
            const data = await reportService.getDateRangeReport(
                start.format('YYYY-MM-DD'),
                end.format('YYYY-MM-DD')
            );
            setReport(data);
        } catch (error) {
            console.error('Failed to load report:', error);
            // Demo data
            const days = dateRange[1].diff(dateRange[0], 'days') + 1;
            setReport({
                startDate: dateRange[0].format('YYYY-MM-DD'),
                endDate: dateRange[1].format('YYYY-MM-DD'),
                totalRevenue: 892400,
                totalSales: 723000,
                totalRepairs: 89,
                gstCollected: 135400,
                dailyData: Array.from({ length: Math.min(days, 30) }, (_, i) => ({
                    date: dateRange[0].add(i, 'days').format('MMM DD'),
                    sales: Math.floor(Math.random() * 30000) + 10000,
                    repairs: Math.floor(Math.random() * 5000) + 1000,
                })),
                salesBreakdown: [
                    { category: 'Mobiles', amount: 450000, percentage: 62 },
                    { category: 'Accessories', amount: 123000, percentage: 17 },
                    { category: 'Repairs', amount: 150000, percentage: 21 },
                ],
            });
        } finally {
            setLoading(false);
        }
    };

    const handleExport = async () => {
        try {
            const [start, end] = dateRange;
            const blob = await reportService.exportPdf('date-range', {
                startDate: start.format('YYYY-MM-DD'),
                endDate: end.format('YYYY-MM-DD'),
            });
            reportService.downloadFile(blob, `Report-${start.format('YYYY-MM-DD')}-to-${end.format('YYYY-MM-DD')}.pdf`);
        } catch (error) {
            console.error('Failed to export:', error);
        }
    };

    const breakdownColumns = [
        { title: 'Category', dataIndex: 'category', key: 'category' },
        {
            title: 'Amount',
            dataIndex: 'amount',
            key: 'amount',
            render: (amount) => `₹${amount.toLocaleString()}`,
        },
        {
            title: 'Share',
            dataIndex: 'percentage',
            key: 'percentage',
            render: (pct) => `${pct}%`,
        },
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
                            Date Range Report
                        </Title>
                        <Text type="secondary">
                            Custom period analysis: {dateRange[0].format('DD MMM')} - {dateRange[1].format('DD MMM YYYY')}
                        </Text>
                    </Col>
                    <Col>
                        <Space>
                            <RangePicker
                                value={dateRange}
                                onChange={(dates) => setDateRange(dates || [dayjs().subtract(30, 'days'), dayjs()])}
                                allowClear={false}
                            />
                            <Button icon={<DownloadOutlined />} onClick={handleExport}>
                                Export PDF
                            </Button>
                        </Space>
                    </Col>
                </Row>
            </motion.div>

            {/* Summary */}
            <Row gutter={24} style={{ marginBottom: 24 }}>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                        <Card>
                            <Statistic title="Total Revenue" value={report?.totalRevenue || 0} prefix="₹" valueStyle={{ color: '#10b981' }} />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
                        <Card>
                            <Statistic title="Sales Amount" value={report?.totalSales || 0} prefix="₹" />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
                        <Card>
                            <Statistic title="Total Repairs" value={report?.totalRepairs || 0} />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
                        <Card>
                            <Statistic title="GST Collected" value={report?.gstCollected || 0} prefix="₹" valueStyle={{ color: '#8b5cf6' }} />
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Trend Chart */}
            <Row gutter={24}>
                <Col xs={24} lg={16}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}>
                        <Card title="Revenue Trend" style={{ marginBottom: 24 }}>
                            <ResponsiveContainer width="100%" height={300}>
                                <LineChart data={report?.dailyData || []}>
                                    <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                                    <XAxis dataKey="date" stroke="var(--color-text-muted)" />
                                    <YAxis stroke="var(--color-text-muted)" />
                                    <Tooltip
                                        contentStyle={{
                                            background: 'var(--color-bg-secondary)',
                                            border: '1px solid var(--color-border)',
                                            borderRadius: 8,
                                        }}
                                    />
                                    <Legend />
                                    <Line type="monotone" dataKey="sales" name="Sales" stroke="#00d4ff" strokeWidth={2} />
                                    <Line type="monotone" dataKey="repairs" name="Repairs" stroke="#8b5cf6" strokeWidth={2} />
                                </LineChart>
                            </ResponsiveContainer>
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} lg={8}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6 }}>
                        <Card title="Sales Breakdown">
                            <Table
                                columns={breakdownColumns}
                                dataSource={report?.salesBreakdown || []}
                                rowKey="category"
                                pagination={false}
                                size="small"
                            />
                        </Card>
                    </motion.div>
                </Col>
            </Row>
        </div>
    );
};

export default DateRangeReport;
