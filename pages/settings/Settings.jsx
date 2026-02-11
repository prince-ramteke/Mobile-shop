import { useState, useEffect } from 'react';
import {
    Card,
    Form,
    Input,
    InputNumber,
    Button,
    Typography,
    Row,
    Col,
    Switch,
    Divider,
    message,
    Space,
    Tabs,
} from 'antd';
import { motion } from 'framer-motion';
import {
    SettingOutlined,
    SaveOutlined,
    ShopOutlined,
    PercentageOutlined,
    WhatsAppOutlined,
    FileTextOutlined,
} from '@ant-design/icons';
import settingsService from '../../api/settingsService';
import Loading from '../../components/common/Loading';
import { useAuth } from '../../context/AuthContext';

const { Title, Text } = Typography;
const { TextArea } = Input;

const Settings = () => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const { isAdmin } = useAuth();

    useEffect(() => {
        loadSettings();
    }, []);

    const loadSettings = async () => {
        try {
            setLoading(true);
            const data = await settingsService.getSettings();
            form.setFieldsValue(data);
        } catch (error) {
            console.error('Failed to load settings:', error);
            // Default values
            form.setFieldsValue({
                shopName: 'Saurabh Mobile Shop',
                phone: '9876543210',
                address: 'Main Market, City',
                gstNumber: '',
                gstPercentage: 18,
                invoiceFooter: 'Thank you for your business!',
                whatsappEnabled: true,
                autoReminders: true,
                reminderDays: 7,
            });
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (values) => {
        try {
            setSaving(true);
            await settingsService.updateSettings(values);
            message.success('Settings saved successfully');
        } catch (error) {
            console.error('Failed to save settings:', error);
            message.error('Failed to save settings');
        } finally {
            setSaving(false);
        }
    };

    const handleTestWhatsApp = async () => {
        try {
            await settingsService.testWhatsAppConnection();
            message.success('WhatsApp connection successful!');
        } catch (error) {
            console.error('WhatsApp test failed:', error);
            message.error('WhatsApp connection failed');
        }
    };

    if (!isAdmin()) {
        return (
            <div style={{ textAlign: 'center', padding: 60 }}>
                <Title level={3}>Access Denied</Title>
                <Text type="secondary">Only administrators can access settings.</Text>
            </div>
        );
    }

    if (loading) {
        return <Loading fullScreen={false} text="Loading settings..." />;
    }

    const tabItems = [
        {
            key: 'shop',
            label: (
                <span>
                    <ShopOutlined />
                    Shop Details
                </span>
            ),
            children: (
                <Row gutter={24}>
                    <Col span={12}>
                        <Form.Item
                            name="shopName"
                            label="Shop Name"
                            rules={[{ required: true }]}
                        >
                            <Input prefix={<ShopOutlined />} placeholder="Enter shop name" />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item
                            name="phone"
                            label="Phone Number"
                            rules={[{ required: true }]}
                        >
                            <Input placeholder="Enter phone number" />
                        </Form.Item>
                    </Col>
                    <Col span={24}>
                        <Form.Item name="address" label="Address">
                            <TextArea rows={2} placeholder="Enter shop address" />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item name="gstNumber" label="GST Number">
                            <Input placeholder="Enter GST number" />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item name="email" label="Email">
                            <Input type="email" placeholder="Enter email address" />
                        </Form.Item>
                    </Col>
                </Row>
            ),
        },
        {
            key: 'tax',
            label: (
                <span>
                    <PercentageOutlined />
                    Tax & GST
                </span>
            ),
            children: (
                <Row gutter={24}>
                    <Col span={8}>
                        <Form.Item
                            name="gstPercentage"
                            label="GST Percentage"
                            rules={[{ required: true }]}
                        >
                            <InputNumber
                                min={0}
                                max={100}
                                formatter={(value) => `${value}%`}
                                parser={(value) => value.replace('%', '')}
                                style={{ width: '100%' }}
                            />
                        </Form.Item>
                    </Col>
                    <Col span={8}>
                        <Form.Item name="cgstPercentage" label="CGST (Auto-calculated)">
                            <InputNumber
                                disabled
                                value={form.getFieldValue('gstPercentage') / 2}
                                formatter={(value) => `${value}%`}
                                style={{ width: '100%' }}
                            />
                        </Form.Item>
                    </Col>
                    <Col span={8}>
                        <Form.Item name="sgstPercentage" label="SGST (Auto-calculated)">
                            <InputNumber
                                disabled
                                value={form.getFieldValue('gstPercentage') / 2}
                                formatter={(value) => `${value}%`}
                                style={{ width: '100%' }}
                            />
                        </Form.Item>
                    </Col>
                    <Col span={24}>
                        <Text type="secondary">
                            Note: CGST and SGST are automatically calculated as half of the total GST percentage.
                        </Text>
                    </Col>
                </Row>
            ),
        },
        {
            key: 'invoice',
            label: (
                <span>
                    <FileTextOutlined />
                    Invoice
                </span>
            ),
            children: (
                <Row gutter={24}>
                    <Col span={24}>
                        <Form.Item
                            name="invoiceHeader"
                            label="Invoice Header Text"
                        >
                            <Input placeholder="Custom header text for invoices" />
                        </Form.Item>
                    </Col>
                    <Col span={24}>
                        <Form.Item
                            name="invoiceFooter"
                            label="Invoice Footer Text"
                        >
                            <TextArea
                                rows={3}
                                placeholder="Thank you message, terms & conditions, etc."
                            />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item
                            name="invoicePrefix"
                            label="Invoice Number Prefix"
                        >
                            <Input placeholder="e.g., INV, SMS" />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item
                            name="nextInvoiceNumber"
                            label="Next Invoice Number"
                        >
                            <InputNumber min={1} style={{ width: '100%' }} />
                        </Form.Item>
                    </Col>
                </Row>
            ),
        },
        {
            key: 'whatsapp',
            label: (
                <span>
                    <WhatsAppOutlined />
                    WhatsApp
                </span>
            ),
            children: (
                <Row gutter={24}>
                    <Col span={24}>
                        <Form.Item
                            name="whatsappEnabled"
                            label="Enable WhatsApp Notifications"
                            valuePropName="checked"
                        >
                            <Switch />
                        </Form.Item>
                    </Col>
                    <Col span={24}>
                        <Form.Item
                            name="autoReminders"
                            label="Enable Auto Reminders"
                            valuePropName="checked"
                        >
                            <Switch />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item
                            name="reminderDays"
                            label="Send Reminder After (days)"
                        >
                            <InputNumber min={1} max={30} style={{ width: '100%' }} />
                        </Form.Item>
                    </Col>
                    <Col span={12}>
                        <Form.Item label="Test Connection">
                            <Button
                                icon={<WhatsAppOutlined />}
                                onClick={handleTestWhatsApp}
                                style={{ background: '#25D366', borderColor: '#25D366', color: '#fff' }}
                            >
                                Test WhatsApp
                            </Button>
                        </Form.Item>
                    </Col>
                    <Col span={24}>
                        <Form.Item
                            name="whatsappTemplate"
                            label="Default Message Template"
                        >
                            <TextArea
                                rows={4}
                                placeholder="Use {customer_name}, {amount}, {invoice_no} as placeholders"
                            />
                        </Form.Item>
                    </Col>
                </Row>
            ),
        },
    ];

    return (
        <div>
            {/* Page Header */}
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="page-header"
            >
                <Title level={2} className="page-title">
                    <SettingOutlined style={{ marginRight: 12 }} />
                    Settings
                </Title>
                <Text type="secondary">Configure shop settings and preferences</Text>
            </motion.div>

            {/* Settings Form */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
            >
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                    requiredMark={false}
                >
                    <Card>
                        <Tabs items={tabItems} />

                        <Divider />

                        <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                            <Space>
                                <Button onClick={loadSettings}>Reset</Button>
                                <Button
                                    type="primary"
                                    htmlType="submit"
                                    icon={<SaveOutlined />}
                                    loading={saving}
                                    size="large"
                                >
                                    Save Settings
                                </Button>
                            </Space>
                        </Form.Item>
                    </Card>
                </Form>
            </motion.div>
        </div>
    );
};

export default Settings;
