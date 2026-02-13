import { useState, useEffect } from 'react';
import {
    Card,
    Table,
    Button,
    Input,
    Space,
    Tag,
    Typography,
    Modal,
    Form,
    message,
    Popconfirm,
    Empty,
    Row,
    Col,
} from 'antd';
import {
    PlusOutlined,
    SearchOutlined,
    EditOutlined,
    DeleteOutlined,
    MobileOutlined,
    UserOutlined,
    SaveOutlined,
} from '@ant-design/icons';
import { motion } from 'framer-motion';
import customerService from '../../api/customerService';

const { Title, Text } = Typography;

const CustomerList = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchText, setSearchText] = useState('');
    const [modalVisible, setModalVisible] = useState(false);
    const [editingCustomer, setEditingCustomer] = useState(null);
    const [form] = Form.useForm();
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        loadCustomers();
    }, []);

    const loadCustomers = async () => {
        try {
            setLoading(true);
            const data = await customerService.getAll();
            setCustomers(data?.content || data || []);
        } catch (error) {
            console.error('Failed to load customers:', error);
            message.error(error.response?.data?.message || 'Failed to load customers');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (value) => {
        setSearchText(value);
        if (value.length >= 2) {
            try {
                const results = await customerService.search(value);
                setCustomers(results || []);
            } catch (error) {
                console.error('Search failed:', error);
                message.error(error.response?.data?.message || 'Search failed');
            }
        } else if (value === '') {
            loadCustomers();
        }
    };

    const handleAdd = () => {
        setEditingCustomer(null);
        form.resetFields();
        setModalVisible(true);
    };

    const handleEdit = (customer) => {
        setEditingCustomer(customer);
        form.setFieldsValue(customer);
        setModalVisible(true);
    };

    const handleDelete = async (id) => {
        try {
            await customerService.delete(id);
            message.success('Customer deleted successfully');
            loadCustomers();
        } catch (error) {
            console.error('Failed to delete customer:', error);
            message.error(error.response?.data?.message || 'Failed to delete customer');
        }
    };

    const handleSubmit = async (values) => {
        try {
            setSubmitting(true);
            if (editingCustomer) {
                await customerService.update(editingCustomer.id, values);
                message.success('Customer updated successfully');
            } else {
                await customerService.create(values);
                message.success('Customer created successfully');
            }
            setModalVisible(false);
            loadCustomers();
        } catch (error) {
            console.error('Failed to save customer:', error);
            message.error(error.response?.data?.message || 'Failed to save customer');
        } finally {
            setSubmitting(false);
        }
    };

    const columns = [
        {
            title: 'Name',
            dataIndex: 'name',
            key: 'name',
            render: (text) => <Text strong>{text}</Text>,
        },
        {
            title: 'Phone',
            dataIndex: 'phone',
            key: 'phone',
        },
        {
            title: 'Email',
            dataIndex: 'email',
            key: 'email',
            render: (text) => text || '-',
        },
        {
            title: 'Due Amount',
            dataIndex: 'dueAmount',
            key: 'dueAmount',
            render: (amount) => (
                <Tag color={amount > 0 ? 'red' : 'green'}>
                    ₹{amount?.toLocaleString() || 0}
                </Tag>
            ),
        },
        {
            title: 'Actions',
            key: 'action',
            render: (_, record) => (
                <Space>
                    <Button
                        type="text"
                        icon={<EditOutlined style={{ color: 'var(--color-primary)' }} />}
                        onClick={() => handleEdit(record)}
                    />
                    <Popconfirm
                        title="Delete Customer"
                        description="Are you sure you want to delete this customer?"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Yes"
                        cancelText="No"
                    >
                        <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined />}
                        />
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <div>
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="page-header"
            >
                <Row justify="space-between" align="middle">
                    <Col>
                        <Title level={2}>
                            <UserOutlined style={{ marginRight: 12 }} />
                            Customers
                        </Title>
                        <Text type="secondary">Manage your customer database</Text>
                    </Col>
                    <Col>
                        <Button
                            type="primary"
                            icon={<PlusOutlined />}
                            size="large"
                            onClick={handleAdd}
                        >
                            Add Customer
                        </Button>
                    </Col>
                </Row>
            </motion.div>

            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
            >
                <Card style={{ marginBottom: 24 }}>
                    <Input
                        prefix={<SearchOutlined style={{ color: 'rgba(0,0,0,0.25)' }} />}
                        placeholder="Search by name or phone..."
                        size="large"
                        onChange={(e) => handleSearch(e.target.value)}
                        style={{ maxWidth: 400 }}
                    />
                </Card>

                <Card>
                    <Table
                        columns={columns}
                        dataSource={customers}
                        loading={loading}
                        rowKey="id"
                        locale={{
                            emptyText: <Empty description="No Customers Found" />
                        }}
                    />
                </Card>
            </motion.div>

            <Modal
                title={editingCustomer ? "Edit Customer" : "Add New Customer"}
                open={modalVisible}
                onCancel={() => setModalVisible(false)}
                footer={null}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                    requiredMark={false}
                >
                    <Form.Item
                        name="name"
                        label="Customer Name"
                        rules={[{ required: true, message: 'Please enter customer name' }]}
                    >
                        <Input placeholder="Enter full name" />
                    </Form.Item>

                    <Form.Item
                        name="phone"
                        label="Phone Number"
                        rules={[
                            { required: true, message: 'Please enter phone number' },
                            { pattern: /^\d{10}$/, message: 'Please enter a valid 10-digit number' }
                        ]}
                    >
                        <Input placeholder="Enter 10-digit mobile number" maxLength={10} />
                    </Form.Item>

                    <Form.Item
                        name="email"
                        label="Email Address"
                        rules={[{ type: 'email', message: 'Please enter a valid email' }]}
                    >
                        <Input placeholder="Enter email address (optional)" />
                    </Form.Item>

                    <Form.Item
                        name="address"
                        label="Address"
                    >
                        <Input.TextArea rows={3} placeholder="Enter customer address" />
                    </Form.Item>

                    <Form.Item
                        name="gstNumber"
                        label="GST Number"
                    >
                        <Input placeholder="Enter GSTIN (optional)" />
                    </Form.Item>

                    <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                        <Space>
                            <Button onClick={() => setModalVisible(false)}>
                                Cancel
                            </Button>
                            <Button
                                type="primary"
                                htmlType="submit"
                                loading={submitting}
                                icon={<SaveOutlined />}
                            >
                                {editingCustomer ? 'Update Customer' : 'Save Customer'}
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default CustomerList;
