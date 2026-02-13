import { useState, useEffect } from 'react';
import { Card, Typography, Row, Col, DatePicker, Statistic, Button, Space, Table, Divider } from 'antd';
import { motion } from 'framer-motion';
import dayjs from 'dayjs';
import {
    FileTextOutlined,
    DownloadOutlined,
} from '@ant-design/icons';
import {
    PieChart,
    Pie,
    Cell,
    ResponsiveContainer,
    Legend,
    Tooltip,
} from 'recharts';
import reportService from '../../api/reportService';
import Loading from '../../components/common/Loading';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

const GstReport = () => {
    const [loading, setLoading] = useState(true);
    const [dateRange, setDateRange] = useState([dayjs().startOf('month'), dayjs()]);
    const [report, setReport] = useState(null);

    useEffect(() => {
        loadReport();
    }, [dateRange]);

    const loadReport = async () => {
        try {
            setLoading(true);
            const [start, end] = dateRange;
            const data = await reportService.getGstReport(
                start.format('YYYY-MM-DD'),
                end.format('YYYY-MM-DD')
            );
            setReport(data);
        } catch (error) {
            console.error('Failed to load report:', error);
            // Demo data
            setReport({
                totalSales: 892400,
                taxableSales: 756271,
                totalGst: 136129,
                cgst: 68065,
                sgst: 68065,
                igst: 0,
                gstBreakdown: [
                    { name: 'CGST', value: 68065, color: '#00d4ff' },
                    { name: 'SGST', value: 68065, color: '#8b5cf6' },
                ],
                monthlyGst: [
                    { month: 'Oct 2024', sales: 320000, cgst: 28800, sgst: 28800 },
                    { month: 'Nov 2024', sales: 285000, cgst: 25650, sgst: 25650 },
                    { month: 'Dec 2024', sales: 287400, cgst: 25866, sgst: 25866 },
                ],
            });
        } finally {
            setLoading(false);
        }
    };

    const handleExport = async () => {
        try {
            const [start, end] = dateRange;
            const blob = await reportService.exportPdf('gst', {
                startDate: start.format('YYYY-MM-DD'),
                endDate: end.format('YYYY-MM-DD'),
            });
            reportService.downloadFile(blob, `GST-Report-${start.format('YYYY-MM-DD')}-to-${end.format('YYYY-MM-DD')}.pdf`);
        } catch (error) {
            console.error('Failed to export:', error);
        }
    };

    const gstColumns = [
        { title: 'Period', dataIndex: 'month', key: 'month' },
        {
            title: 'Taxable Sales',
            dataIndex: 'sales',
            key: 'sales',
            render: (val) => `₹${val.toLocaleString()}`,
        },
        {
            title: 'CGST',
            dataIndex: 'cgst',
            key: 'cgst',
            render: (val) => `₹${val.toLocaleString()}`,
        },
        {
            title: 'SGST',
            dataIndex: 'sgst',
            key: 'sgst',
            render: (val) => `₹${val.toLocaleString()}`,
        },
        {
            title: 'Total GST',
            key: 'total',
            render: (_, record) => `₹${(record.cgst + record.sgst).toLocaleString()}`,
        },
    ];

    if (loading) {
        return <Loading fullScreen={false} text="Loading GST report..." />;
    }

    return (
        <div>
            {/* Page Header */}
            <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} className="page-header">
                <Row justify="space-between" align="middle">
                    <Col>
                        <Title level={2} className="page-title">
                            <FileTextOutlined style={{ marginRight: 12 }} />
                            GST Report
                        </Title>
                        <Text type="secondary">Tax summary and breakdown</Text>
                    </Col>
                    <Col>
                        <Space>
                            <RangePicker
                                value={dateRange}
                                onChange={(dates) => setDateRange(dates || [dayjs().startOf('month'), dayjs()])}
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
                            <Statistic title="Total Sales" value={report?.totalSales || 0} prefix="₹" />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
                        <Card>
                            <Statistic title="Taxable Amount" value={report?.taxableSales || 0} prefix="₹" />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
                        <Card>
                            <Statistic title="Total GST" value={report?.totalGst || 0} prefix="₹" valueStyle={{ color: '#10b981' }} />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={6}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
                        <Card>
                            <Row gutter={16}>
                                <Col span={12}>
                                    <Statistic title="CGST" value={report?.cgst || 0} prefix="₹" valueStyle={{ fontSize: 18 }} />
                                </Col>
                                <Col span={12}>
                                    <Statistic title="SGST" value={report?.sgst || 0} prefix="₹" valueStyle={{ fontSize: 18 }} />
                                </Col>
                            </Row>
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Chart and Table */}
            <Row gutter={24}>
                <Col xs={24} lg={8}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.5 }}>
                        <Card title="GST Distribution">
                            <ResponsiveContainer width="100%" height={250}>
                                <PieChart>
                                    <Pie
                                        data={report?.gstBreakdown || []}
                                        cx="50%"
                                        cy="50%"
                                        innerRadius={60}
                                        outerRadius={90}
                                        paddingAngle={5}
                                        dataKey="value"
                                        label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                                    >
                                        {(report?.gstBreakdown || []).map((entry, index) => (
                                            <Cell key={`cell-${index}`} fill={entry.color} />
                                        ))}
                                    </Pie>
                                    <Tooltip formatter={(value) => `₹${value.toLocaleString()}`} />
                                </PieChart>
                            </ResponsiveContainer>
                            <Divider />
                            <div style={{ display: 'flex', justifyContent: 'space-around' }}>
                                <div style={{ textAlign: 'center' }}>
                                    <div style={{ width: 12, height: 12, background: '#00d4ff', borderRadius: 2, margin: '0 auto 4px' }} />
                                    <Text type="secondary">CGST</Text>
                                    <br />
                                    <Text strong>₹{(report?.cgst || 0).toLocaleString()}</Text>
                                </div>
                                <div style={{ textAlign: 'center' }}>
                                    <div style={{ width: 12, height: 12, background: '#8b5cf6', borderRadius: 2, margin: '0 auto 4px' }} />
                                    <Text type="secondary">SGST</Text>
                                    <br />
                                    <Text strong>₹{(report?.sgst || 0).toLocaleString()}</Text>
                                </div>
                            </div>
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} lg={16}>
                    <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.6 }}>
                        <Card title="Monthly GST Summary">
                            <Table
                                columns={gstColumns}
                                dataSource={report?.monthlyGst || []}
                                rowKey="month"
                                pagination={false}
                                summary={(data) => {
                                    const totalSales = data.reduce((sum, r) => sum + r.sales, 0);
                                    const totalCgst = data.reduce((sum, r) => sum + r.cgst, 0);
                                    const totalSgst = data.reduce((sum, r) => sum + r.sgst, 0);
                                    return (
                                        <Table.Summary.Row>
                                            <Table.Summary.Cell><Text strong>Total</Text></Table.Summary.Cell>
                                            <Table.Summary.Cell><Text strong>₹{totalSales.toLocaleString()}</Text></Table.Summary.Cell>
                                            <Table.Summary.Cell><Text strong>₹{totalCgst.toLocaleString()}</Text></Table.Summary.Cell>
                                            <Table.Summary.Cell><Text strong>₹{totalSgst.toLocaleString()}</Text></Table.Summary.Cell>
                                            <Table.Summary.Cell><Text strong style={{ color: '#10b981' }}>₹{(totalCgst + totalSgst).toLocaleString()}</Text></Table.Summary.Cell>
                                        </Table.Summary.Row>
                                    );
                                }}
                            />
                        </Card>
                    </motion.div>
                </Col>
            </Row>
        </div>
    );
};

export default GstReport;
