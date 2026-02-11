import { useState, useEffect } from 'react';
import {
    Card,
    Table,
    Button,
    Input,
    Space,
    Tag,
    Typography,
    Row,
    Col,
    DatePicker,
    Select,
    Tooltip,
} from 'antd';
import { motion } from 'framer-motion';
import {
    PlusOutlined,
    SearchOutlined,
    EyeOutlined,
    PrinterOutlined,
    FilterOutlined,
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import saleService from '../../api/saleService';
import Loading from '../../components/common/Loading';
import EmptyState from '../../components/common/EmptyState';

const { Title, Text } = Typography;
const { RangePicker } = DatePicker;

const SaleList = () => {
    const [sales, setSales] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchText, setSearchText] = useState('');
    const [statusFilter, setStatusFilter] = useState('all');
    const navigate = useNavigate();

    useEffect(() => {
        loadSales();
    }, []);

    const loadSales = async () => {
        try {
            setLoading(true);
            const data = await saleService.getAll();
            setSales(data?.content || data || []);
        } catch (error) {
            console.error('Failed to load sales:', error);
        } finally {
            setLoading(false);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PAID': return 'green';
            case 'PARTIAL': return 'orange';
            case 'PENDING': return 'red';
            default: return 'default';
        }
    };

    const columns = [
        {
            title: 'Invoice #',
            dataIndex: 'invoiceNumber',
            key: 'invoiceNumber',
            render: (num, record) => (
                <Button type="link" onClick={() => navigate(`/sales/${record.id}`)}>
                    {num || `INV-${record.id}`}
                </Button>
            ),
        },
        {
            title: 'Customer',
            dataIndex: 'customerName',
            key: 'customerName',
            render: (name, record) => (
                <div>
                    <Text strong>{name || record.customer?.name}</Text>
                    <br />
                    <Text type="secondary" style={{ fontSize: 12 }}>
                        {record.customer?.phone}
                    </Text>
                </div>
            ),
        },
        {
            title: 'Date',
            dataIndex: 'createdAt',
            key: 'createdAt',
            render: (date) => new Date(date).toLocaleDateString(),
        },
        {
            title: 'Items',
            dataIndex: 'items',
            key: 'items',
            render: (items) => items?.length || 0,
        },
        {
            title: 'Total',
            dataIndex: 'grandTotal',
            key: 'grandTotal',
            render: (total) => (
                <Text strong>₹{(total || 0).toLocaleString()}</Text>
            ),
        },
        {
            title: 'Received',
            dataIndex: 'receivedAmount',
            key: 'receivedAmount',
            render: (amount) => `₹${(amount || 0).toLocaleString()}`,
        },
        {
            title: 'Pending',
            dataIndex: 'pendingAmount',
            key: 'pendingAmount',
            render: (amount) => (
                <Text style={{ color: amount > 0 ? '#ef4444' : '#10b981' }}>
                    ₹{(amount || 0).toLocaleString()}
                </Text>
            ),
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
            render: (status, record) => {
                const calculatedStatus = record.pendingAmount > 0
                    ? (record.receivedAmount > 0 ? 'PARTIAL' : 'PENDING')
                    : 'PAID';
                return (
                    <Tag color={getStatusColor(status || calculatedStatus)}>
                        {status || calculatedStatus}
                    </Tag>
                );
            },
        },
        {
            title: 'Actions',
            key: 'actions',
            width: 120,
            render: (_, record) => (
                <Space>
                    <Tooltip title="View Details">
                        <Button
                            type="text"
                            icon={<EyeOutlined />}
                            onClick={() => navigate(`/sales/${record.id}`)}
                        />
                    </Tooltip>
                    <Tooltip title="Print Invoice">
                        <Button type="text" icon={<PrinterOutlined />} />
                    </Tooltip>
                </Space>
            ),
        },
    ];

    if (loading) {
        return <Loading fullScreen={false} text="Loading sales..." />;
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
                        <Title level={2} className="page-title">Sales</Title>
                        <Text type="secondary">View and manage all sales invoices</Text>
                    </Col>
                    <Col>
                        <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            onClick={() => navigate('/sales/create')}
                            size="large"
                        >
                            New Sale
                        </Button>
                    </Col>
                </Row>
            </motion.div>

            {/* Filters and Table */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
            >
                <Card>
                    <Row gutter={16} style={{ marginBottom: 16 }}>
                        <Col flex="auto">
                            <Input
                                placeholder="Search by invoice number or customer..."
                                prefix={<SearchOutlined />}
                                value={searchText}
                                onChange={(e) => setSearchText(e.target.value)}
                                style={{ maxWidth: 400 }}
                                allowClear
                            />
                        </Col>
                        <Col>
                            <Select
                                value={statusFilter}
                                onChange={setStatusFilter}
                                style={{ width: 150 }}
                                placeholder="Filter by status"
                            >
                                <Select.Option value="all">All Status</Select.Option>
                                <Select.Option value="PAID">Paid</Select.Option>
                                <Select.Option value="PARTIAL">Partial</Select.Option>
                                <Select.Option value="PENDING">Pending</Select.Option>
                            </Select>
                        </Col>
                        <Col>
                            <RangePicker />
                        </Col>
                    </Row>

                    {sales.length > 0 ? (
                        <Table
                            columns={columns}
                            dataSource={sales.filter(sale => {
                                const matchesSearch = !searchText ||
                                    (sale.invoiceNumber?.toLowerCase().includes(searchText.toLowerCase())) ||
                                    (sale.customerName?.toLowerCase().includes(searchText.toLowerCase()));
                                const matchesStatus = statusFilter === 'all' || sale.status === statusFilter;
                                return matchesSearch && matchesStatus;
                            })}
                            rowKey="id"
                            pagination={{
                                pageSize: 10,
                                showSizeChanger: true,
                                showTotal: (total) => `Total ${total} sales`,
                            }}
                        />
                    ) : (
                        <EmptyState
                            title="No Sales Found"
                            description="Create your first sale to get started"
                            actionText="Create Sale"
                            onAction={() => navigate('/sales/create')}
                        />
                    )}
                </Card>
            </motion.div>
        </div>
    );
};

export default SaleList;
