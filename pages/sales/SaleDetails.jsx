import { useState, useEffect } from 'react';
import {
    Card,
    Typography,
    Row,
    Col,
    Table,
    Tag,
    Button,
    Divider,
    Space,
    message,
    Descriptions,
    Modal,
    InputNumber,
    Select,
    Form,
} from 'antd';
import { motion } from 'framer-motion';
import { useParams, useNavigate } from 'react-router-dom';
import {
    PrinterOutlined,
    WhatsAppOutlined,
    ArrowLeftOutlined,
    DownloadOutlined,
    DollarOutlined,
    CheckCircleOutlined,
} from '@ant-design/icons';
import saleService from '../../api/saleService';
import reminderService from '../../api/reminderService';
import Loading from '../../components/common/Loading';

const { Title, Text } = Typography;

const SaleDetails = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [sale, setSale] = useState(null);
    const [loading, setLoading] = useState(true);
    const [paymentModalVisible, setPaymentModalVisible] = useState(false);
    const [sendingWhatsApp, setSendingWhatsApp] = useState(false);
    const [downloadingPdf, setDownloadingPdf] = useState(false);
    const [form] = Form.useForm();

    useEffect(() => {
        loadSale();
    }, [id]);

    const loadSale = async () => {
        try {
            setLoading(true);
            const data = await saleService.getById(id);
            setSale(data);
        } catch (error) {
            console.error('Failed to load sale:', error);
            message.error('Failed to load sale details');
        } finally {
            setLoading(false);
        }
    };

    const handleDownloadInvoice = async () => {
        try {
            setDownloadingPdf(true);
            const blob = await saleService.generateInvoice(id);
            
            // Create download link
            const url = window.URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
            const link = document.createElement('a');
            link.href = url;
            link.download = `Invoice-${sale?.invoiceNumber || id}.pdf`;
            document.body.appendChild(link);
            link.click();
            
            // Cleanup
            setTimeout(() => {
                document.body.removeChild(link);
                window.URL.revokeObjectURL(url);
            }, 100);
            
            message.success('Invoice downloaded successfully');
        } catch (error) {
            console.error('Failed to download invoice:', error);
            message.error(error.response?.data?.message || 'Failed to download invoice');
        } finally {
            setDownloadingPdf(false);
        }
    };

    const handleSendWhatsApp = async () => {
        try {
            setSendingWhatsApp(true);
            await reminderService.sendInvoiceWhatsApp(id);
            message.success('Invoice sent via WhatsApp');
        } catch (error) {
            console.error('Failed to send WhatsApp:', error);
            message.error('Failed to send WhatsApp message');
        } finally {
            setSendingWhatsApp(false);
        }
    };

    const handleAddPayment = async (values) => {
        try {
            await saleService.addPayment(id, values);
            message.success('Payment added successfully');
            setPaymentModalVisible(false);
            loadSale();
        } catch (error) {
            console.error('Failed to add payment:', error);
            message.error('Failed to add payment');
        }
    };

    const handlePrint = () => {
        window.print();
    };

    if (loading) {
        return <Loading fullScreen={false} text="Loading invoice..." />;
    }

    if (!sale) {
        return <div>Sale not found</div>;
    }

    const itemColumns = [
        {
            title: 'S.No',
            key: 'sno',
            width: 60,
            render: (_, __, index) => index + 1,
        },
        {
            title: 'Type',
            dataIndex: 'type',
            key: 'type',
            render: (type) => <Tag>{type?.toUpperCase()}</Tag>,
        },
        {
            title: 'Item/Service',
            dataIndex: 'itemName',  // Changed from name to itemName to match backend
            key: 'itemName',
        },
        {
            title: 'Qty',
            dataIndex: 'quantity',
            key: 'quantity',
            width: 80,
        },
        {
            title: 'Price',
            dataIndex: 'price',  // Changed from unitPrice to price
            key: 'price',
            render: (price) => `₹${Number(price)?.toLocaleString() || 0}`,
        },
        {
            title: 'Total',
            key: 'total',
            render: (_, record) => `₹${((record.quantity || 0) * (record.price || 0))?.toLocaleString()}`,
        },
    ];

    return (
        <div>
            {/* Page Header */}
            <motion.div
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                className="page-header no-print"
            >
                <Row justify="space-between" align="middle">
                    <Col>
                        <Space>
                            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/sales')}>
                                Back
                            </Button>
                            <div>
                                <Title level={2} style={{ margin: 0 }}>
                                    Invoice #{sale.invoiceNumber || sale.id}
                                </Title>
                                <Text type="secondary">
                                    {new Date(sale.createdAt || sale.saleDate).toLocaleDateString()}
                                </Text>
                            </div>
                        </Space>
                    </Col>
                    <Col>
                        <Space>
                            <Button icon={<PrinterOutlined />} onClick={handlePrint}>
                                Print
                            </Button>
                            <Button 
                                icon={<DownloadOutlined />} 
                                onClick={handleDownloadInvoice}
                                loading={downloadingPdf}
                            >
                                Download PDF
                            </Button>
                            <Button
                                icon={<WhatsAppOutlined />}
                                loading={sendingWhatsApp}
                                onClick={handleSendWhatsApp}
                                style={{ background: '#25D366', borderColor: '#25D366', color: '#fff' }}
                            >
                                Send via WhatsApp
                            </Button>
                            {sale.pendingAmount > 0 && (
                                <Button
                                    type="primary"
                                    icon={<DollarOutlined />}
                                    onClick={() => setPaymentModalVisible(true)}
                                >
                                    Add Payment
                                </Button>
                            )}
                        </Space>
                    </Col>
                </Row>
            </motion.div>

            {/* Invoice Content */}
            <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.1 }}
            >
                <Card>
                    {/* Header */}
                    <Row justify="space-between" style={{ marginBottom: 24 }}>
                        <Col>
                            <Title level={3} style={{ margin: 0, color: '#00d4ff' }}>
                                Saurabh Mobile Shop
                            </Title>
                            <Text type="secondary">Mobile Sales & Repair Services</Text>
                        </Col>
                        <Col style={{ textAlign: 'right' }}>
                            <Tag color={sale.pendingAmount > 0 ? 'orange' : 'green'} style={{ fontSize: 14, padding: '4px 12px' }}>
                                {sale.pendingAmount > 0 ? 'PENDING' : 'PAID'}
                            </Tag>
                        </Col>
                    </Row>

                    <Divider />

                    {/* Customer & Invoice Info */}
                    <Row gutter={24} style={{ marginBottom: 24 }}>
                        <Col span={12}>
                            <Text strong style={{ display: 'block', marginBottom: 8 }}>Bill To:</Text>
                            <Text strong style={{ fontSize: 16 }}>{sale.customerName || sale.customer?.name || 'N/A'}</Text>
                            <br />
                            <Text type="secondary">{sale.customerPhone || sale.customer?.phone}</Text>
                            <br />
                            <Text type="secondary">{sale.customer?.address}</Text>
                        </Col>
                        <Col span={12} style={{ textAlign: 'right' }}>
                            <Descriptions column={1} size="small">
                                <Descriptions.Item label="Invoice No">
                                    {sale.invoiceNumber || `INV-${sale.id}`}
                                </Descriptions.Item>
                                <Descriptions.Item label="Date">
                                    {new Date(sale.saleDate || sale.createdAt).toLocaleDateString()}
                                </Descriptions.Item>
                                <Descriptions.Item label="Payment Method">
                                    {sale.paymentMode || 'CASH'}
                                </Descriptions.Item>
                            </Descriptions>
                        </Col>
                    </Row>

                    {/* Items Table */}
                    <Table
                        columns={itemColumns}
                        dataSource={sale.items || []}
                        rowKey={(record, index) => index}
                        pagination={false}
                        style={{ marginBottom: 24 }}
                    />

                    {/* Totals */}
                    <Row justify="end">
                        <Col xs={24} sm={12} md={8}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                <Text>Subtotal:</Text>
                                <Text>₹{Number(sale.subTotal || 0)?.toLocaleString()}</Text>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                <Text type="secondary">CGST ({(sale.gstRate || 18)/2}%):</Text>
                                <Text type="secondary">₹{Number(sale.cgstAmount || 0)?.toLocaleString()}</Text>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                <Text type="secondary">SGST ({(sale.gstRate || 18)/2}%):</Text>
                                <Text type="secondary">₹{Number(sale.sgstAmount || 0)?.toLocaleString()}</Text>
                            </div>
                            <Divider style={{ margin: '8px 0' }} />
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                <Title level={4} style={{ margin: 0 }}>Grand Total:</Title>
                                <Title level={4} style={{ margin: 0, color: '#00d4ff' }}>
                                    ₹{Number(sale.grandTotal || 0)?.toLocaleString()}
                                </Title>
                            </div>
                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                <Text>Received:</Text>
                                <Text style={{ color: '#10b981' }}>₹{Number(sale.amountReceived || 0)?.toLocaleString()}</Text>
                            </div>
                            {sale.pendingAmount > 0 && (
                                <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                    <Text>Pending:</Text>
                                    <Text style={{ color: '#ef4444' }}>₹{Number(sale.pendingAmount)?.toLocaleString()}</Text>
                                </div>
                            )}
                        </Col>
                    </Row>

                    <Divider />

                    {/* Footer */}
                    <div style={{ textAlign: 'center' }}>
                        <Text type="secondary">Thank you for your business!</Text>
                    </div>
                </Card>
            </motion.div>

            {/* Add Payment Modal */}
            <Modal
                title="Add Payment"
                open={paymentModalVisible}
                onCancel={() => setPaymentModalVisible(false)}
                footer={null}
            >
                <Form form={form} layout="vertical" onFinish={handleAddPayment}>
                    <Form.Item
                        name="amount"
                        label="Amount"
                        rules={[{ required: true, message: 'Please enter amount' }]}
                    >
                        <InputNumber
                            style={{ width: '100%' }}
                            min={1}
                            max={sale.pendingAmount}
                            formatter={(value) => `₹ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
                            parser={(value) => value.replace(/₹\s?|(,*)/g, '')}
                            placeholder={`Max: ₹${sale.pendingAmount?.toLocaleString()}`}
                        />
                    </Form.Item>
                    <Form.Item name="paymentMode" label="Payment Method" initialValue="CASH">
                        <Select>
                            <Select.Option value="CASH">Cash</Select.Option>
                            <Select.Option value="UPI">UPI</Select.Option>
                            <Select.Option value="CARD">Card</Select.Option>
                            <Select.Option value="BANK_TRANSFER">Bank Transfer</Select.Option>
                        </Select>
                    </Form.Item>
                    <Form.Item style={{ marginBottom: 0, textAlign: 'right' }}>
                        <Space>
                            <Button onClick={() => setPaymentModalVisible(false)}>Cancel</Button>
                            <Button type="primary" htmlType="submit">Add Payment</Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default SaleDetails;