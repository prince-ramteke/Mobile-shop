import { useEffect, useState } from 'react';
import { Card, Descriptions, Tag, Button, message } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import repairService from '../../api/repairService';
import Loading from '../../components/common/Loading';

const RepairDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [repair, setRepair] = useState(null);
  const [loading, setLoading] = useState(true);
  
useEffect(() => {
  if (id) loadRepair();
}, [id]);


  const loadRepair = async () => {
    try {
      const data = await repairService.getById(id);
      setRepair(data);
    } catch (err) {
      message.error("Failed to load repair");
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loading text="Loading repair..." />;

  if (!repair) return <div>Repair not found</div>;

  return (
    <Card
      title={`Repair Details — ${repair.jobNumber}`}
      extra={
        <Button onClick={() => navigate(`/repairs/edit/${id}`)}>
          Edit
        </Button>
      }
    >
      <Descriptions column={2} bordered>
        <Descriptions.Item label="Customer">
          {repair.customerName}
        </Descriptions.Item>

        <Descriptions.Item label="Phone">
          {repair.customerPhone}
        </Descriptions.Item>

        <Descriptions.Item label="Device">
          {repair.deviceBrand} {repair.deviceModel}
        </Descriptions.Item>

        <Descriptions.Item label="IMEI">
          {repair.imei}
        </Descriptions.Item>

        <Descriptions.Item label="Status">
          <Tag color={
            repair.status === 'PENDING' ? 'orange' :
            repair.status === 'IN_PROGRESS' ? 'blue' :
            repair.status === 'COMPLETED' ? 'green' :
            'purple'
          }>
            {repair.status}
          </Tag>
        </Descriptions.Item>

        <Descriptions.Item label="Total Cost">
          ₹{repair.finalCost ?? 0}
        </Descriptions.Item>

        <Descriptions.Item label="Advance Paid">
          ₹{repair.advancePaid}
        </Descriptions.Item>

        <Descriptions.Item label="Pending Amount">
          ₹{repair.pendingAmount}
        </Descriptions.Item>

        <Descriptions.Item label="Issue">
          {repair.issueDescription}
        </Descriptions.Item>

        <Descriptions.Item label="Notes">
          {repair.notes}
        </Descriptions.Item>
      </Descriptions>
    </Card>
  );
};

export default RepairDetails;
