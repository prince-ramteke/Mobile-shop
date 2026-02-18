import { useState } from 'react';
import { Card, Form, Input, InputNumber, Button, Typography, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import saleService from '../../api/saleService';

const { Title } = Typography;

const CreateMobileSale = () => {
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (values) => {
        try {
            setLoading(true);
            const res = await saleService.createMobileSale(values);
            message.success('Mobile Sale Created');
navigate(`/mobile-sales/${res.id || res}`);
        } catch (err) {
            console.error(err);
            message.error('Failed to create mobile sale');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Card>
            <Title level={3}>New Phone Sale</Title>

            <Form form={form} layout="vertical" onFinish={handleSubmit}>

                <Form.Item name="company" label="Company" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="model" label="Model" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="imei1" label="IMEI 1" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="imei2" label="IMEI 2">
                    <Input />
                </Form.Item>

                <Form.Item name="price" label="Price" rules={[{ required: true }]}>
                    <InputNumber style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item name="quantity" label="Quantity" initialValue={1}>
                    <InputNumber style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item name="advancePaid" label="Advance Paid" initialValue={0}>
                    <InputNumber style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item name="warrantyYears" label="Warranty Years" initialValue={1}>
                    <InputNumber style={{ width: '100%' }} />
                </Form.Item>

                <Form.Item name="customerName" label="Customer Name" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="customerPhone" label="Phone" rules={[{ required: true }]}>
                    <Input />
                </Form.Item>

                <Form.Item name="customerAddress" label="Address">
                    <Input.TextArea rows={2} />
                </Form.Item>

                <Button type="primary" htmlType="submit" loading={loading} block>
                    Create Sale
                </Button>
            </Form>
        </Card>
    );
};

export default CreateMobileSale;
