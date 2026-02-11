import { useState } from 'react';
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
    Space,
    Modal,
} from 'antd';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import {
    ToolOutlined,
    SaveOutlined,
    UserAddOutlined,
} from '@ant-design/icons';
import CustomerAutocomplete from '../../components/customers/CustomerAutocomplete';
import repairService from '../../api/repairService';
import customerService from '../../api/customerService';

const { Title, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;

const CreateRepair = () => {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [customerForm] = Form.useForm();
    
    const [loading, setLoading] = useState(false);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [customerModalVisible, setCustomerModalVisible] = useState(false);

    const mobileBrands = [
        'Apple', 'Samsung', 'OnePlus', 'Xiaomi', 'Realme', 'Oppo', 'Vivo',
        'Motorola', 'Nokia', 'Google', 'Poco', 'Redmi', 'iQOO', 'Other'
    ];

    const handleCustomerSelect = (customerId, customer) => {
        setSelectedCustomer(customer);
        if (customer) {
            form.setFieldValue('customerId', customerId);
        }
    };

    const handleCreateNewCustomer = (name) => {
        customerForm.setFieldsValue({ name: name || '', phone: '' });
        setCustomerModalVisible(true);
    };

    const handleCreateCustomer = async (values) => {
        try {
            setLoading(true);
            const newCustomer = await customerService.create(values);
            setSelectedCustomer(newCustomer);
            setCustomerModalVisible(false);
            message.success('Customer created successfully');
            form.setFieldValue('customerId', newCustomer.id);
        } catch (error) {
            console.error('Customer creation error:', error);
            message.error(error.response?.data?.message || 'Failed to create customer');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (values) => {
        if (!selectedCustomer) {
            message.error('Please select a customer');
            return;
        }

        try {
            setLoading(true);

            // Build description with all details
            const details = [
                values.issueDescription,
                values.deviceColor ? `Color: ${values.deviceColor}` : null,
                values.devicePassword ? `Pwd: ${values.devicePassword}` : null,
                values.notes ? `Notes: ${values.notes}` : null
            ].filter(Boolean).join(' | ');

            const repairData = {
                customerId: Number(selectedCustomer.id),
                deviceBrand: String(values.deviceBrand),
                deviceModel: String(values.deviceModel),
                imei: values.imei ? String(values.imei) : '',
                issueDescription: String(details),
                estimatedCost: Number(values.estimatedCost || 0),
                finalCost: Number(values.finalCost || values.estimatedCost || 0),
                advancePaid: Number(values.advancePaid || 0),
                status: values.status || 'PENDING'
            };

            console.log('Sending repair data:', repairData);
            
            const response = await repairService.create(repairData);
            console.log('Repair created:', response);
            
            message.success('Repair job created successfully');
            navigate('/repairs');
        } catch (error) {
            console.error('Repair creation error:', error);
            const errorMsg = error.response?.data?.message 
                || error.response?.data?.error 
                || 'Failed to create repair job. Please check all fields.';
            message.error(errorMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }}>
                <Title level={2}>
                    <ToolOutlined style={{ marginRight: 12 }} /> Create Repair Job
                </Title>
                <Text type="secondary">Register a new device for repair</Text>
            </motion.div>

            <Row gutter={24} style={{ marginTop: 24 }}>
                <Col xs={24} lg={16}>
                    <Form 
                        form={form} 
                        layout="vertical" 
                        onFinish={handleSubmit} 
                        requiredMark={false}
                        initialValues={{ status: 'PENDING' }}
                    >
                        {/* Customer Selection */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                            <Card title="Customer Details" style={{ marginBottom: 24 }}>
                                <Form.Item
                                    name="customerId"
                                    rules={[{ required: true, message: 'Please select a customer' }]}
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

                        {/* Device Details */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }}>
                            <Card title="Device Information" style={{ marginBottom: 24 }}>
                                <Row gutter={16}>
                                    <Col span={12}>
                                        <Form.Item
                                            name="deviceBrand"
                                            label="Brand"
                                            rules={[{ required: true, message: 'Select brand' }]}
                                        >
                                            <Select placeholder="Select brand" showSearch allowClear>
                                                {mobileBrands.map(brand => (
                                                    <Option key={brand} value={brand}>{brand}</Option>
                                                ))}
                                            </Select>
                                        </Form.Item>
                                    </Col>
                                    <Col span={12}>
                                        <Form.Item
                                            name="deviceModel"
                                            label="Model"
                                            rules={[{ required: true, message: 'Enter model' }]}
                                        >
                                            <Input placeholder="e.g., iPhone 14 Pro" />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Row gutter={16}>
                                    <Col span={12}>
                                        <Form.Item name="imei" label="IMEI Number">
                                            <Input placeholder="15-digit IMEI" maxLength={15} />
                                        </Form.Item>
                                    </Col>
                                    <Col span={12}>
                                        <Form.Item name="deviceColor" label="Color">
                                            <Input placeholder="e.g., Black" />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Form.Item name="devicePassword" label="Password/Pattern (if any)">
                                    <Input.Password placeholder="For technician reference" />
                                </Form.Item>
                            </Card>
                        </motion.div>

                        {/* Problem & Costs */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}>
                            <Card title="Problem Details" style={{ marginBottom: 24 }}>
                                <Form.Item
                                    name="issueDescription"
                                    label="Problem Description"
                                    rules={[{ required: true, message: 'Describe the problem' }]}
                                >
                                    <TextArea
                                        rows={3}
                                        placeholder="Describe the issue..."
                                    />
                                </Form.Item>

                                <Row gutter={16}>
                                    <Col span={8}>
                                        <Form.Item name="estimatedCost" label="Est. Cost (₹)">
                                            <InputNumber
                                                style={{ width: '100%' }}
                                                min={0}
                                                placeholder="Estimated"
                                                prefix="₹"
                                            />
                                        </Form.Item>
                                    </Col>
                                    <Col span={8}>
                                        <Form.Item name="finalCost" label="Final Cost (₹)">
                                            <InputNumber
                                                style={{ width: '100%' }}
                                                min={0}
                                                placeholder="Final"
                                                prefix="₹"
                                            />
                                        </Form.Item>
                                    </Col>
                                    <Col span={8}>
                                        <Form.Item name="advancePaid" label="Advance (₹)">
                                            <InputNumber
                                                style={{ width: '100%' }}
                                                min={0}
                                                placeholder="Advance"
                                                prefix="₹"
                                            />
                                        </Form.Item>
                                    </Col>
                                </Row>

                                <Form.Item name="status" label="Status">
                                    <Select>
                                        <Option value="PENDING">Pending</Option>
                                        <Option value="IN_PROGRESS">In Progress</Option>
                                        <Option value="COMPLETED">Completed</Option>
                                        <Option value="DELIVERED">Delivered</Option>
                                    </Select>
                                </Form.Item>

                                <Form.Item name="notes" label="Additional Notes">
                                    <TextArea rows={2} placeholder="Special instructions" />
                                </Form.Item>
                            </Card>
                        </motion.div>

                        {/* Submit */}
                        <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.4 }}>
                            <Card>
                                <Space>
                                    <Button onClick={() => navigate('/repairs')}>Cancel</Button>
                                    <Button
                                        type="primary"
                                        htmlType="submit"
                                        icon={<SaveOutlined />}
                                        loading={loading}
                                        size="large"
                                    >
                                        Create Repair Job
                                    </Button>
                                </Space>
                            </Card>
                        </motion.div>
                    </Form>
                </Col>

                {/* Right Info Panel */}
                <Col xs={24} lg={8}>
                    <motion.div 
                        initial={{ opacity: 0, x: 20 }} 
                        animate={{ opacity: 1, x: 0 }} 
                        transition={{ delay: 0.2 }}
                        style={{ position: 'sticky', top: 24 }}
                    >
                        <Card title="Repair Status Guide">
                            <Space direction="vertical" style={{ width: '100%' }} size="large">
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <div style={{ width: 12, height: 12, borderRadius: '50%', background: '#faad14', marginRight: 12 }} />
                                    <Text>Pending - Initial status</Text>
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <div style={{ width: 12, height: 12, borderRadius: '50%', background: '#1890ff', marginRight: 12 }} />
                                    <Text>In Progress - Being repaired</Text>
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center' }}>
                                    <div style={{ width: 12, height: 12, borderRadius: '50%', background: '#52c41a', marginRight: 12 }} />
                                    <Text>Completed - Ready for pickup</Text>
                                </div>
                            </Space>
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
            >
                <Form form={customerForm} onFinish={handleCreateCustomer} layout="vertical">
                    <Form.Item name="name" label="Name" rules={[{ required: true }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item name="phone" label="Phone" rules={[{ required: true }, { pattern: /^\d{10}$/, message: 'Invalid phone' }]}>
                        <Input maxLength={10} />
                    </Form.Item>
                    <Form.Item name="email" label="Email">
                        <Input type="email" />
                    </Form.Item>
                    <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                        <Space>
                            <Button onClick={() => setCustomerModalVisible(false)}>Cancel</Button>
                            <Button type="primary" htmlType="submit" loading={loading}>Add</Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default CreateRepair;