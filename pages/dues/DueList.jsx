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
    const [payModalOpen, setPayModalOpen] = useState(false);
const [selectedCustomer, setSelectedCustomer] = useState(null);
const [payAmount, setPayAmount] = useState(0);
const [historyOpen, setHistoryOpen] = useState(false);
const [paymentHistory, setPaymentHistory] = useState([]);



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
            setDues([]);
setSummary(null);

        } finally {
            setLoading(false);
        }
    };

    const loadPaymentHistory = async (customerId) => {
    try {
        const res = await reminderService.getPaymentHistory(customerId);
        setPaymentHistory(res || []);
        setHistoryOpen(true);
    } catch (e) {
        message.error("Failed to load payment history");
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
                    <Text
    strong
    style={{ cursor: "pointer", color: "#1677ff" }}
    onClick={() => loadPaymentHistory(record.customerId)}
>
    {record.name}
</Text>


                    <br />
                    <Text type="secondary" style={{ fontSize: 12 }}>
                        {record.phone}
                    </Text>
                </div>
            ),
        },
        {
            title: 'Pending Amount',
            dataIndex: 'totalPending',
            key: 'amount',
            render: (amount) => (
                <Text strong style={{ color: '#ef4444', fontSize: 16 }}>
                    ₹{(amount || 0).toLocaleString()}
                </Text>
            ),
sorter: (a, b) => a.totalPending - b.totalPending,
        },
        {
            title: 'Overdue',
            dataIndex: 'overdueDays',
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
    width: 220,
    render: (_, record) => (
        <Space>
            <Button
    icon={<WhatsAppOutlined />}
    style={{ background: "#25D366", color: "#fff", borderColor: "#25D366" }}
    size="small"
    onClick={async () => {
        try {
            await reminderService.sendWhatsAppReminder(record.customerId);
            message.success("WhatsApp reminder sent");
        } catch (e) {
            console.error(e);
            message.error("Failed to send WhatsApp");
        }
    }}
>
    Send Reminder
</Button>


            <Button
                type="primary"
                size="small"
                onClick={() => {
                    setSelectedCustomer(record);
                    setPayModalOpen(true);
                }}
            >
                Mark Paid
            </Button>
        </Space>
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
                                value={summary?.overdueCount || dues.filter(d => d.overdueDays > 7).length}
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
                            rowKey="customerId"
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

            <Modal
    open={payModalOpen}
    title={`Mark Payment - ${selectedCustomer?.name || ''}`}
    onCancel={() => setPayModalOpen(false)}
    onOk={async () => {
        if (!payAmount || payAmount <= 0) {
            message.error("Enter valid amount");
            return;
        }

        try {
           await reminderService.markAsPaid(
    selectedCustomer.customerId,
    { amount: payAmount, note: "Manual payment" }
);


            message.success("Payment recorded");
            await loadDues();   // IMPORTANT await
            setPayModalOpen(false);
            setPayAmount(0);
        
        } catch (e) {
            console.error(e);
            message.error("Payment failed");
        }
    }}
>
    <input
        type="number"
        placeholder="Enter amount"
        style={{ width: "100%", padding: 8 }}
        onChange={(e) => setPayAmount(Number(e.target.value))}
    />
</Modal>
<Modal
    open={historyOpen}
    title="Payment History"
    footer={null}
    onCancel={() => setHistoryOpen(false)}
>
    {paymentHistory.length === 0 ? (
        <Text type="secondary">No payments yet</Text>
    ) : (
        <Table
            size="small"
            pagination={false}
            rowKey="id"
            dataSource={paymentHistory}
            columns={[
                {
                    title: "Date",
                    dataIndex: "paidAt",
                    render: (d) => new Date(d).toLocaleString(),
                },
                {
                    title: "Amount",
                    dataIndex: "amount",
                    render: (a) => `₹${a}`,
                },
                {
                    title: "Note",
                    dataIndex: "note",
                },
            ]}
        />
    )}
</Modal>

        </div>
    );
};

export default DueList;
