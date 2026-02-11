import { useState, useEffect } from 'react';
import {
    Card,
    Form,
    Input,
    InputNumber,
    Button,
    Select,
    Typography,
    Row,
    Col,
    message,
    Divider,
    Space,
    Table,
    Modal,
} from 'antd';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import {
    ShoppingCartOutlined,
    PlusOutlined,
    DeleteOutlined,
    SaveOutlined,
    UserAddOutlined,
} from '@ant-design/icons';
import CustomerAutocomplete from '../../components/customers/CustomerAutocomplete';
import saleService from '../../api/saleService';
import customerService from '../../api/customerService';

const { Title, Text } = Typography;
const { Option } = Select;

const CreateSale = () => {
    const navigate = useNavigate();
    const [saleForm] = Form.useForm();
    const [customerForm] = Form.useForm();
    
    const [loading, setLoading] = useState(false);
    const [items, setItems] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [customerModalVisible, setCustomerModalVisible] = useState(false);
    
    // Item form state
    const [itemType, setItemType] = useState('Accessory');
    const [itemName, setItemName] = useState('');
    const [itemQty, setItemQty] = useState(1);
    const [itemPrice, setItemPrice] = useState(null);
    
    const [gstPercentage] = useState(18);
    const [receivedAmount, setReceivedAmount] = useState(0);
    const [paymentMethod, setPaymentMethod] = useState('CASH');

    const [totals, setTotals] = useState({
        subtotal: 0,
        cgst: 0,
        sgst: 0,
        grandTotal: 0,
        pendingAmount: 0,
    });

    useEffect(() => {
        const subtotal = items.reduce(
            (sum, item) => sum + (item.price * item.quantity),
            0
        );
        const gstAmount = (subtotal * gstPercentage) / 100;
        const cgst = gstAmount / 2;
        const sgst = gstAmount / 2;
        const grandTotal = Math.round(subtotal + gstAmount);
        const pendingAmount = Math.max(0, grandTotal - receivedAmount);

        setTotals({
            subtotal,
            cgst,
            sgst,
            grandTotal,
            pendingAmount,
        });
    }, [items, receivedAmount, gstPercentage]);

    const handleCustomerSelect = (customerId, customer) => {
        setSelectedCustomer(customer);
    };

    const handleCreateNewCustomer = (name) => {
        customerForm.setFieldsValue({ name, phone: '' });
        setCustomerModalVisible(true);
    };

    const handleCreateCustomer = async (values) => {
        try {
            setLoading(true);
            const newCustomer = await customerService.create(values);
            setSelectedCustomer(newCustomer);
            setCustomerModalVisible(false);
            message.success('Customer created successfully');
        } catch (error) {
            console.error('Customer creation error:', error);
            message.error(error.response?.data?.message || 'Failed to create customer');
        } finally {
            setLoading(false);
        }
    };

    const handleAddItem = () => {
        if (!itemName || !itemName.trim()) {
            message.error('Please enter item name');
            return;
        }
        if (!itemPrice || itemPrice <= 0) {
            message.error('Please enter valid price');
            return;
        }
        if (!itemQty || itemQty <= 0) {
            message.error('Please enter valid quantity');
            return;
        }

        const newItem = {
            key: Date.now(),
            type: itemType,
            name: itemName.trim(),
            quantity: Number(itemQty),
            price: Number(itemPrice),
            total: Number(itemQty) * Number(itemPrice),
        };

        setItems([...items, newItem]);
        
        // Reset fields
        setItemName('');
        setItemPrice(null);
        setItemQty(1);
        setItemType('Accessory');
        
        message.success('Item added');
    };

    const handleRemoveItem = (key) => {
        setItems(items.filter(item => item.key !== key));
    };

    const handleSubmit = async (values) => {
        if (!selectedCustomer) {
            message.error('Please select a customer');
            return;
        }
        if (items.length === 0) {
            message.error('Please add at least one item');
            return;
        }

        try {
            setLoading(true);

            // Ensure all values are proper types
            const saleData = {
                customerId: Number(selectedCustomer.id),
                items: items.map(item => ({
                    itemName: String(item.name),
                    quantity: Number(item.quantity),
                    price: Number(item.price),
                    type: String(item.type),
                })),
                gstRate: Number(gstPercentage),
                gstType: 'CGST_SGST',
                amountReceived: Number(receivedAmount),
                paymentMode: String(paymentMethod),
                notes: values.notes ? String(values.notes) : '',
            };

            console.log('Submitting sale data:', saleData);
            
            const response = await saleService.create(saleData);
            console.log('Sale created:', response);
            
            message.success('Sale created successfully');
            navigate(`/sales/${response.id}`);
        } catch (error) {
            console.error('Sale creation error:', error);
            const errorMsg = error.response?.data?.message 
                || error.response?.data?.error 
                || 'Failed to create sale. Please check all fields.';
            message.error(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    const itemColumns = [
        { 
            title: 'Type', 
            dataIndex: 'type',
            width: 120,
            render: (type) => <span style={{ textTransform: 'capitalize' }}>{type}</span>
        },
        { title: 'Item Name', dataIndex: 'name', ellipsis: true },
        { 
            title: 'Qty', 
            dataIndex: 'quantity', 
            width: 80,
            align: 'center'
        },
        { 
            title: 'Price', 
            dataIndex: 'price',
            width: 120,
            render: (price) => `₹${Number(price)?.toLocaleString()}`
        },
        {
            title: 'Total',
            dataIndex: 'total',
            width: 120,
            render: (v) => <Text strong>₹{Number(v)?.toLocaleString()}</Text>,
        },
        {
            title: '',
            width: 60,
            render: (_, record) => (
                <Button
                    danger
                    type="text"
                    icon={<DeleteOutlined />}
                    onClick={() => handleRemoveItem(record.key)}
                />
            ),
        },
    ];

    return (
        <div>
            <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }}>
                <Title level={2}>
                    <ShoppingCartOutlined style={{ marginRight: 12 }} /> Create New Sale
                </Title>
            </motion.div>

            <Row gutter={24}>
                <Col xs={24} lg={16}>
                    <Form form={saleForm} onFinish={handleSubmit} layout="vertical">
                        {/* Customer Selection */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                            <Card title="Customer Details" style={{ marginBottom: 24 }}>
                                <Form.Item
                                    name="customerId"
                                    rules={[{ required: true, message: 'Please select a customer' }]}
                                    style={{ marginBottom: 0 }}
                                >
                                    <CustomerAutocomplete
                                        value={selectedCustomer?.id}
                                        onChange={handleCustomerSelect}
                                        onCreateNew={handleCreateNewCustomer}
                                    />
                                </Form.Item>
                                {selectedCustomer && (
                                    <div style={{ marginTop: 8, padding: '8px 12px', background: '#f6ffed', border: '1px solid #b7eb8f', borderRadius: 4 }}>
                                        <Text type="success" strong>{selectedCustomer.name}</Text>
                                        <Text type="secondary" style={{ marginLeft: 8 }}>{selectedCustomer.phone}</Text>
                                    </div>
                                )}
                            </Card>
                        </motion.div>

                        {/* Items Section */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
                            <Card title="Add Items" style={{ marginBottom: 24 }}>
                                <Row gutter={12} style={{ marginBottom: 16 }} align="middle">
                                    <Col xs={24} sm={6} md={5}>
                                        <Select 
                                            value={itemType} 
                                            onChange={setItemType}
                                            style={{ width: '100%' }}
                                        >
                                            <Option value="Accessory">Accessory</Option>
                                            <Option value="Service">Service</Option>
                                            <Option value="Repair">Repair</Option>
                                            <Option value="Mobile">Mobile</Option>
                                        </Select>
                                    </Col>
                                    <Col xs={24} sm={8} md={9}>
                                        <Input 
                                            placeholder="Item/Service name" 
                                            value={itemName}
                                            onChange={(e) => setItemName(e.target.value)}
                                            onPressEnter={handleAddItem}
                                        />
                                    </Col>
                                    <Col xs={12} sm={4} md={3}>
                                        <InputNumber 
                                            min={1} 
                                            placeholder="Qty"
                                            value={itemQty}
                                            onChange={(val) => setItemQty(val || 1)}
                                            style={{ width: '100%' }}
                                        />
                                    </Col>
                                    <Col xs={12} sm={4} md={4}>
                                        <InputNumber 
                                            min={0} 
                                            placeholder="Price"
                                            value={itemPrice}
                                            onChange={(val) => setItemPrice(val)}
                                            style={{ width: '100%' }}
                                            prefix="₹"
                                        />
                                    </Col>
                                    <Col xs={24} sm={2} md={3}>
                                        <Button 
                                            type="primary" 
                                            icon={<PlusOutlined />} 
                                            onClick={handleAddItem}
                                            block
                                        >
                                            Add
                                        </Button>
                                    </Col>
                                </Row>

                                <Table
                                    columns={itemColumns}
                                    dataSource={items}
                                    pagination={false}
                                    rowKey="key"
                                    size="small"
                                    summary={() => (
                                        <Table.Summary.Row>
                                            <Table.Summary.Cell colSpan={3} align="right">
                                                <Text strong>Subtotal:</Text>
                                            </Table.Summary.Cell>
                                            <Table.Summary.Cell colSpan={2}>
                                                <Text strong>₹{totals.subtotal.toLocaleString()}</Text>
                                            </Table.Summary.Cell>
                                        </Table.Summary.Row>
                                    )}
                                />
                            </Card>
                        </motion.div>

                        {/* Notes */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
                            <Card style={{ marginBottom: 24 }}>
                                <Form.Item name="notes" label="Additional Notes">
                                    <Input.TextArea rows={2} placeholder="Any special instructions..." />
                                </Form.Item>
                            </Card>
                        </motion.div>
                    </Form>
                </Col>

                <Col xs={24} lg={8}>
                    <motion.div 
                        initial={{ opacity: 0, x: 20 }} 
                        animate={{ opacity: 1, x: 0 }} 
                        transition={{ delay: 0.2 }}
                        style={{ position: 'sticky', top: 24 }}
                    >
                        <Card title="Order Summary">
                            <div style={{ marginBottom: 16 }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                    <Text type="secondary">Subtotal</Text>
                                    <Text>₹{totals.subtotal.toLocaleString()}</Text>
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                    <Text type="secondary">CGST ({gstPercentage/2}%)</Text>
                                    <Text>₹{totals.cgst.toLocaleString()}</Text>
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                    <Text type="secondary">SGST ({gstPercentage/2}%)</Text>
                                    <Text>₹{totals.sgst.toLocaleString()}</Text>
                                </div>
                                <Divider style={{ margin: '12px 0' }} />
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                                    <Text strong style={{ fontSize: 16 }}>Grand Total</Text>
                                    <Title level={3} style={{ margin: 0, color: '#00d4ff' }}>
                                        ₹{totals.grandTotal.toLocaleString()}
                                    </Title>
                                </div>
                            </div>

                            <div style={{ marginBottom: 16 }}>
                                <Text strong>Payment Details</Text>
                                <Select
                                    value={paymentMethod}
                                    onChange={setPaymentMethod}
                                    style={{ width: '100%', marginTop: 8, marginBottom: 12 }}
                                >
                                    <Option value="CASH">Cash</Option>
                                    <Option value="UPI">UPI</Option>
                                    <Option value="CARD">Card</Option>
                                    <Option value="BANK_TRANSFER">Bank Transfer</Option>
                                </Select>

                                <Text>Amount Received</Text>
                                <InputNumber
                                    value={receivedAmount}
                                    onChange={(val) => setReceivedAmount(Number(val) || 0)}
                                    max={totals.grandTotal}
                                    style={{ width: '100%', marginTop: 8 }}
                                    formatter={(value) => `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                                    parser={(value) => value.replace(/₹\s?|(,*)/g, '')}
                                />
                                
                                {totals.pendingAmount > 0 && (
                                    <div style={{ marginTop: 8, color: '#ef4444' }}>
                                        Pending: ₹{totals.pendingAmount.toLocaleString()}
                                    </div>
                                )}
                            </div>

                            <Button
                                type="primary"
                                icon={<SaveOutlined />}
                                block
                                loading={loading}
                                onClick={() => saleForm.submit()}
                                size="large"
                                disabled={items.length === 0 || !selectedCustomer}
                                style={{ height: 48 }}
                            >
                                Create Invoice
                            </Button>
                        </Card>
                    </motion.div>
                </Col>
            </Row>

            {/* Add Customer Modal */}
            <Modal
                title={<><UserAddOutlined /> Add New Customer</>}
                open={customerModalVisible}
                onCancel={() => setCustomerModalVisible(false)}
                footer={null}
                destroyOnClose
            >
                <Form 
                    form={customerForm} 
                    onFinish={handleCreateCustomer} 
                    layout="vertical"
                    requiredMark={false}
                >
                    <Form.Item 
                        name="name" 
                        label="Customer Name" 
                        rules={[{ required: true, message: 'Enter name' }]}
                    >
                        <Input placeholder="Full name" />
                    </Form.Item>
                    
                    <Form.Item 
                        name="phone" 
                        label="Phone Number" 
                        rules={[
                            { required: true, message: 'Enter phone' },
                            { pattern: /^\d{10}$/, message: 'Enter 10-digit number' }
                        ]}
                    >
                        <Input placeholder="10-digit mobile number" maxLength={10} />
                    </Form.Item>
                    
                    <Form.Item name="email" label="Email">
                        <Input type="email" placeholder="Optional" />
                    </Form.Item>
                    
                    <Form.Item name="address" label="Address">
                        <Input.TextArea rows={2} placeholder="Optional" />
                    </Form.Item>
                    
                    <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                        <Space>
                            <Button onClick={() => setCustomerModalVisible(false)}>Cancel</Button>
                            <Button type="primary" htmlType="submit" loading={loading}>Add Customer</Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default CreateSale;