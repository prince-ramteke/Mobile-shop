import { useState, useEffect } from 'react';
import {
    Card,
    Table,
    Tag,
    Button,
    Space,
    Input,
    Row,
    Col,
    Typography,
    Badge,
    Tooltip,
    message,
} from 'antd';
import { motion } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';
import {
    PlusOutlined,
    SearchOutlined,
    EyeOutlined,
    EditOutlined,
    WhatsAppOutlined,
} from '@ant-design/icons';
import repairService from '../../api/repairService';
import Loading from '../../components/common/Loading';

const { Title, Text } = Typography;

const RepairsList = () => {
    const navigate = useNavigate();
    const [repairs, setRepairs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchText, setSearchText] = useState('');

    useEffect(() => {
        loadRepairs();
    }, []);

   const loadRepairs = async () => {
    try {
        setLoading(true);
        const data = await repairService.getAll();
        setRepairs(data || []);
    } catch (error) {
        console.error("Failed to load repairs", error);
        setRepairs([]);
    } finally {
        setLoading(false);
    }
};


    const handleSearch = (value) => {
        setSearchText(value.toLowerCase());
    };

    const filteredRepairs = repairs.filter(repair => 
        (repair.customerName?.toLowerCase() || '').includes(searchText) ||
        (repair.deviceBrand?.toLowerCase() || '').includes(searchText) ||
        (repair.deviceModel?.toLowerCase() || '').includes(searchText) ||
        (repair.jobNumber?.toLowerCase() || '').includes(searchText)
    );

    const getStatusColor = (status) => {
        const colors = {
            'PENDING': 'orange',
            'IN_PROGRESS': 'blue',
            'COMPLETED': 'green',
            'DELIVERED': 'purple',
            'CANCELLED': 'red'
        };
        return colors[status] || 'default';
    };

    const columns = [
        {
            title: 'Job #',
            dataIndex: 'jobNumber',
            key: 'jobNumber',
            width: 120,
            render: (text) => <Text strong>{text}</Text>,
        },
        {
            title: 'Customer',
            dataIndex: 'customerName',
            key: 'customerName',
            render: (text, record) => (
                <div>
                    <div>{text}</div>
                    <Text type="secondary" style={{ fontSize: 12 }}>{record.customerPhone}</Text>
                </div>
            ),
        },
        {
            title: 'Device',
            key: 'device',
            render: (_, record) => (
                <span>{record.deviceBrand} {record.deviceModel}</span>
            ),
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
            width: 130,
            render: (status) => (
                <Tag color={getStatusColor(status)} style={{ textTransform: 'capitalize' }}>
                    {status?.replace('_', ' ')}
                </Tag>
            ),
        },
        {
            title: 'Cost',
            key: 'cost',
            width: 120,
            render: (_, record) => (
                <div style={{ textAlign: 'right' }}>
                    <div>₹{Number(record.finalCost || 0).toLocaleString()}</div>
                    {record.pendingAmount > 0 && (
                        <Text type="danger" style={{ fontSize: 12 }}>
                            Pending: ₹{Number(record.pendingAmount).toLocaleString()}
                        </Text>
                    )}
                </div>
            ),
        },
        {
            title: 'Actions',
            key: 'actions',
            width: 150,
            render: (_, record) => (
                <Space>
                    <Tooltip title="View Details">
                        <Button 
                            icon={<EyeOutlined />} 
                            onClick={() => navigate(`/repairs/${record.id}`)}
                        />
                    </Tooltip>
                    <Tooltip title="Edit">
                        <Button 
                            icon={<EditOutlined />} 
                            onClick={() => navigate(`/repairs/edit/${record.id}`)}
                        />
                    </Tooltip>
                    <Tooltip title="Send WhatsApp">
                        <Button 
                            icon={<WhatsAppOutlined />} 
                            style={{ color: '#25D366' }}
                            onClick={() => handleWhatsApp(record)}
                        />
                    </Tooltip>
                </Space>
            ),
        },
    ];

    const handleWhatsApp = (repair) => {
        const phone = repair.customerPhone?.replace(/\D/g, '');
        if (phone) {
            const message = `Hi ${repair.customerName}, your ${repair.deviceBrand} ${repair.deviceModel} repair (Job: ${repair.jobNumber}) is currently ${repair.status}.`;
            window.open(`https://wa.me/91${phone}?text=${encodeURIComponent(message)}`, '_blank');
        }
    };

    if (loading) {
        return <Loading fullScreen={false} text="Loading repairs..." />;
    }

    return (
        <div>
            <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }}>
                <Row justify="space-between" align="middle" style={{ marginBottom: 24 }}>
                    <Col>
                        <Title level={2}>Repair Jobs</Title>
                    </Col>
                    <Col>
                        <Button 
                            type="primary" 
                            icon={<PlusOutlined />}
                            onClick={() => navigate('/repairs/create')}
                        >
                            New Repair
                        </Button>
                    </Col>
                </Row>
            </motion.div>

            <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                <Card>
                    <Row gutter={16} style={{ marginBottom: 16 }}>
                        <Col xs={24} sm={12} md={8}>
                            <Input.Search
                                placeholder="Search by customer, device, or job number"
                                allowClear
                                enterButton={<SearchOutlined />}
                                onSearch={handleSearch}
                                onChange={(e) => handleSearch(e.target.value)}
                            />
                        </Col>
                    </Row>

                    <Table
                        columns={columns}
                        dataSource={filteredRepairs}
                        rowKey="id"
                        pagination={{ pageSize: 10 }}
                        locale={{ emptyText: 'No repair jobs found' }}
                    />
                </Card>
            </motion.div>
        </div>
    );
};

export default RepairsList;