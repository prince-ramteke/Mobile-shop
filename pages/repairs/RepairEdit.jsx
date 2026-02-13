import { useEffect, useState } from 'react';
import { Card, Form, Input, Select, Button, message } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import repairService from '../../api/repairService';
import Loading from '../../components/common/Loading';

const { Option } = Select;

const RepairEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadRepair();
  }, []);

  const [repairData, setRepairData] = useState(null);

const loadRepair = async () => {
  try {
    const data = await repairService.getById(id);
    setRepairData(data);   // ⭐ store full data

    form.setFieldsValue({
      deviceBrand: data.deviceBrand,
      deviceModel: data.deviceModel,
      imei: data.imei,
      status: data.status,
      finalCost: data.finalCost,
      advancePaid: data.advancePaid,
      issueDescription: data.issueDescription
    });

  } catch (err) {
    console.error(err);
    message.error("Failed to load repair");
  } finally {
    setLoading(false);
  }
};


const onFinish = async (values) => {
  try {
    const payload = {
      customerId: repairData.customerId,   // REQUIRED
      deviceBrand: values.deviceBrand,
      deviceModel: values.deviceModel,
      imei: values.imei,
      issueDescription: values.issueDescription,
      estimatedCost: repairData.estimatedCost ?? values.finalCost ?? 0, // ⭐ FIX
      finalCost: values.finalCost ?? 0,
      advancePaid: values.advancePaid ?? 0,
      status: values.status
    };

    await repairService.update(id, payload);

    message.success("Repair updated");
    navigate(`/repairs/${id}`);

  } catch (err) {
    console.error(err?.response?.data || err);
    message.error("Update failed");
  }
};




  if (loading) return <Loading text="Loading repair..." />;

  return (
    <Card title="Edit Repair Job">
      <Form form={form} layout="vertical" onFinish={onFinish}>

        <Form.Item label="Device Brand" name="deviceBrand">
          <Input />
        </Form.Item>

        <Form.Item label="Device Model" name="deviceModel">
          <Input />
        </Form.Item>

        <Form.Item label="IMEI" name="imei">
          <Input />
        </Form.Item>

        <Form.Item label="Status" name="status">
          <Select>
            <Option value="PENDING">Pending</Option>
            <Option value="IN_PROGRESS">In Progress</Option>
            <Option value="COMPLETED">Completed</Option>
            <Option value="DELIVERED">Delivered</Option>
          </Select>
        </Form.Item>

        <Form.Item label="Final Cost" name="finalCost">
          <Input type="number" />
        </Form.Item>

        <Form.Item label="Advance Paid" name="advancePaid">
          <Input type="number" />
        </Form.Item>

        <Form.Item label="Issue Description" name="issueDescription">
          <Input.TextArea rows={3} />
        </Form.Item>

        <Button type="primary" htmlType="submit">
          Save Changes
        </Button>
      </Form>
    </Card>
  );
};

export default RepairEdit;
