import { useState } from 'react';
import { Button, Modal, message, Typography, Space } from 'antd';
import { motion } from 'framer-motion';
import { WhatsAppOutlined, SendOutlined, CheckCircleOutlined } from '@ant-design/icons';
import reminderService from '../../api/reminderService';

const { Text, Paragraph } = Typography;

const ReminderTrigger = ({
    type = 'due', // 'due', 'invoice', 'repair'
    targetId,
    customerName,
    amount,
    buttonText = 'Send Reminder',
    buttonSize = 'middle',
    buttonType = 'default',
    showPreview = true,
    onSuccess,
    onError,
}) => {
    const [loading, setLoading] = useState(false);
    const [previewVisible, setPreviewVisible] = useState(false);
    const [previewMessage, setPreviewMessage] = useState('');
    const [sent, setSent] = useState(false);

    // Get preview message from API
    const loadPreview = async () => {
        try {
            setLoading(true);
            const preview = await reminderService.previewMessage(type, targetId);
            setPreviewMessage(preview.message || preview);
            setPreviewVisible(true);
        } catch (error) {
            console.error('Failed to load preview:', error);
            message.error('Failed to load message preview');
        } finally {
            setLoading(false);
        }
    };

    // Send reminder
    const sendReminder = async () => {
        try {
            setLoading(true);

            let response;
            switch (type) {
                case 'due':
                    response = await reminderService.sendReminderToCustomer(targetId);
                    break;
                case 'invoice':
                    response = await reminderService.sendInvoiceWhatsApp(targetId);
                    break;
                case 'repair':
                    response = await reminderService.sendRepairStatusWhatsApp(targetId);
                    break;
                default:
                    throw new Error('Invalid reminder type');
            }

            setSent(true);
            setPreviewVisible(false);
            message.success('Reminder sent successfully via WhatsApp!');
            onSuccess?.(response);

            // Reset sent status after 3 seconds
            setTimeout(() => setSent(false), 3000);
        } catch (error) {
            console.error('Failed to send reminder:', error);
            message.error(error.response?.data?.message || 'Failed to send reminder');
            onError?.(error);
        } finally {
            setLoading(false);
        }
    };

    // Handle button click
    const handleClick = () => {
        if (showPreview) {
            loadPreview();
        } else {
            sendReminder();
        }
    };

    // Get button icon based on state
    const getButtonIcon = () => {
        if (sent) return <CheckCircleOutlined />;
        return <WhatsAppOutlined />;
    };

    // Get button style
    const getButtonStyle = () => {
        if (sent) {
            return {
                background: '#10b981',
                borderColor: '#10b981',
                color: '#fff',
            };
        }
        return {
            background: '#25D366',
            borderColor: '#25D366',
            color: '#fff',
        };
    };

    return (
        <>
            <motion.div
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
            >
                <Button
                    type={buttonType}
                    size={buttonSize}
                    icon={getButtonIcon()}
                    loading={loading}
                    onClick={handleClick}
                    style={getButtonStyle()}
                >
                    {sent ? 'Sent!' : buttonText}
                </Button>
            </motion.div>

            {/* Preview Modal */}
            <Modal
                title={
                    <Space>
                        <WhatsAppOutlined style={{ color: '#25D366', fontSize: 20 }} />
                        <span>WhatsApp Message Preview</span>
                    </Space>
                }
                open={previewVisible}
                onCancel={() => setPreviewVisible(false)}
                footer={[
                    <Button key="cancel" onClick={() => setPreviewVisible(false)}>
                        Cancel
                    </Button>,
                    <Button
                        key="send"
                        type="primary"
                        icon={<SendOutlined />}
                        loading={loading}
                        onClick={sendReminder}
                        style={{ background: '#25D366', borderColor: '#25D366' }}
                    >
                        Send Message
                    </Button>,
                ]}
            >
                <div style={{ marginBottom: 16 }}>
                    <Text type="secondary">To: </Text>
                    <Text strong>{customerName}</Text>
                    {amount && (
                        <>
                            <br />
                            <Text type="secondary">Amount: </Text>
                            <Text strong style={{ color: 'var(--color-error)' }}>
                                ₹{amount?.toLocaleString()}
                            </Text>
                        </>
                    )}
                </div>

                <div
                    style={{
                        background: '#e7ffdb',
                        padding: 16,
                        borderRadius: 8,
                        border: '1px solid #c1e6b5',
                    }}
                >
                    <Paragraph
                        style={{
                            margin: 0,
                            whiteSpace: 'pre-wrap',
                            color: '#1a1a1a',
                        }}
                    >
                        {previewMessage || 'Loading message...'}
                    </Paragraph>
                </div>

                <div style={{ marginTop: 12 }}>
                    <Text type="secondary" style={{ fontSize: 12 }}>
                        This message will be sent via WhatsApp to the customer's registered phone number.
                    </Text>
                </div>
            </Modal>
        </>
    );
};

export default ReminderTrigger;
