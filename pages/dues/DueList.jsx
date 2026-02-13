import { useState, useEffect } from 'react';
import {
    Card,
    Table,
    Button,
    Typography,
    Row,
    Col,
    Tag,
    Space,
    Statistic,
    message,
    Tooltip,
    Modal,
} from 'antd';
import { motion } from 'framer-motion';
import {
    DollarOutlined,
    WhatsAppOutlined,
    ClockCircleOutlined,
    ExclamationCircleOutlined,
    SendOutlined,
} from '@ant-design/icons';
import reminderService from '../../api/reminderService';
import Loading from '../../components/common/Loading';
import EmptyState from '../../components/common/EmptyState';
import ReminderTrigger from '../../components/reminders/ReminderTrigger';

const { Title, Text } = Typography;

const DueList = () => {
    const [dues, setDues] = useState([]);
    const [loading, setLoading] = useState(true);
    const [summary, setSummary] = useState(null);
    const [sendingAll, setSendingAll] = useState(false);

    useEffect(() => {
        loadDues();
    }, []);

    const loadDues = async () => {
        try {
            setLoading(true);
            const [duesData, summaryData] = await Promise.all([
                reminderService.getDues(),
                reminderService.getDueSummary(),
            ]);
            setDues(duesData?.content || duesData || []);
            setSummary(summaryData);
        } catch (error) {
            console.error('Failed to load dues:', error);
            // Demo data
            setDues([
                { id: 1, customerName: 'John Doe', phone: '9876543210', amount: 5000, daysOverdue: 15, lastReminder: '2024-12-10' },
                { id: 2, customerName: 'Jane Smith', phone: '9876543211', amount: 12500, daysOverdue: 7, lastReminder: null },
                { id: 3, customerName: 'Mike Johnson', phone: '9876543212', amount: 3200, daysOverdue: 30, lastReminder: '2024-12-05' },
            ]);
            setSummary({ totalDue: 20700, customerCount: 3, overdueCount: 3 });
        } finally {
            setLoading(false);
        }
    };

    const handleSendAllReminders = async () => {
        Modal.confirm({
            title: 'Send Reminders to All',
            icon: <ExclamationCircleOutlined />,
            content: `This will send WhatsApp reminders to ${dues.length} customers with pending dues. Continue?`,
            okText: 'Send All',
            okButtonProps: { style: { background: '#25D366', borderColor: '#25D366' } },
            onOk: async () => {
                try {
                    setSendingAll(true);
                    await reminderService.sendDueReminders();
                    message.success('Reminders sent to all customers');
                    loadDues();
                } catch (error) {
                    console.error('Failed to send reminders:', error);
                    message.error('Failed to send reminders');
                } finally {
                    setSendingAll(false);
                }
            },
        });
    };

    const getOverdueColor = (days) => {
        if (days > 30) return '#ef4444';
        if (days > 14) return '#f59e0b';
        if (days > 7) return '#eab308';
        return '#10b981';
    };

    const columns = [
        {
            title: 'Customer',
            key: 'customer',
            render: (_, record) => (
                <div>
                    <Text strong>{record.customerName || record.customer?.name}</Text>
                    <br />
                    <Text type="secondary" style={{ fontSize: 12 }}>
                        {record.phone || record.customer?.phone}
                    </Text>
                </div>
            ),
        },
        {
            title: 'Pending Amount',
            dataIndex: 'amount',
            key: 'amount',
            render: (amount) => (
                <Text strong style={{ color: '#ef4444', fontSize: 16 }}>
                    ₹{(amount || 0).toLocaleString()}
                </Text>
            ),
            sorter: (a, b) => a.amount - b.amount,
        },
        {
            title: 'Overdue',
            dataIndex: 'daysOverdue',
            key: 'daysOverdue',
            render: (days) => (
                <Tag
                    icon={<ClockCircleOutlined />}
                    color={days > 30 ? 'red' : days > 14 ? 'orange' : 'gold'}
                >
                    {days} days
                </Tag>
            ),
            sorter: (a, b) => a.daysOverdue - b.daysOverdue,
        },
        {
            title: 'Last Reminder',
            dataIndex: 'lastReminder',
            key: 'lastReminder',
            render: (date) => date ? new Date(date).toLocaleDateString() : 'Never',
        },
        {
            title: 'Actions',
            key: 'actions',
            width: 180,
            render: (_, record) => (
                <ReminderTrigger
                    type="due"
                    targetId={record.id}
                    customerName={record.customerName || record.customer?.name}
                    amount={record.amount}
                    buttonText="Send Reminder"
                    onSuccess={loadDues}
                />
            ),
        },
    ];

    if (loading) {
        return <Loading fullScreen={false} text="Loading dues..." />;
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
                            <DollarOutlined style={{ marginRight: 12 }} />
                            Dues & Payments
                        </Title>
                        <Text type="secondary">Track and manage pending payments</Text>
                    </Col>
                    <Col>
                        <Button
                            icon={<SendOutlined />}
                            onClick={handleSendAllReminders}
                            loading={sendingAll}
                            style={{ background: '#25D366', borderColor: '#25D366', color: '#fff' }}
                            size="large"
                        >
                            Send All Reminders
                        </Button>
                    </Col>
                </Row>
            </motion.div>

            {/* Summary Cards */}
            <Row gutter={24} style={{ marginBottom: 24 }}>
                <Col xs={24} sm={8}>
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.1 }}
                    >
                        <Card>
                            <Statistic
                                title="Total Pending"
                                value={summary?.totalDue || 0}
                                prefix="₹"
                                valueStyle={{ color: '#ef4444' }}
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={8}>
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                    >
                        <Card>
                            <Statistic
                                title="Customers with Dues"
                                value={summary?.customerCount || dues.length}
                                valueStyle={{ color: '#f59e0b' }}
                            />
                        </Card>
                    </motion.div>
                </Col>
                <Col xs={24} sm={8}>
                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.3 }}
                    >
                        <Card>
                            <Statistic
                                title="Overdue (>7 days)"
                                value={summary?.overdueCount || dues.filter(d => d.daysOverdue > 7).length}
                                valueStyle={{ color: '#dc2626' }}
                            />
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Dues Table */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4 }}
            >
                <Card>
                    {dues.length > 0 ? (
                        <Table
                            columns={columns}
                            dataSource={dues}
                            rowKey="id"
                            pagination={{
                                pageSize: 10,
                                showTotal: (total) => `Total ${total} pending dues`,
                            }}
                        />
                    ) : (
                        <EmptyState
                            title="No Pending Dues"
                            description="All payments are up to date!"
                        />
                    )}
                </Card>
            </motion.div>
        </div>
    );
};

export default DueList;
