import { useEffect, useState } from "react";
import { Card, Table, Typography, Button, Space, message } from "antd";
import { useParams, useNavigate } from "react-router-dom";
import saleService from "../../api/saleService";

const { Title } = Typography;

const CustomerLedger = () => {
  const { customerId } = useParams();
  const navigate = useNavigate();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadLedger = async () => {
    try {
      const res = await saleService.getCustomerLedger(customerId);
      setData(res || []);
    } catch {
      message.error("Failed to load customer ledger");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLedger();
  }, [customerId]);

  const columns = [
    {
      title: "Date",
      dataIndex: "date",
      render: (v) => new Date(v).toLocaleString(),
    },
    {
      title: "Type",
      dataIndex: "type",
      render: (v) => (
        <span style={{ color: v === "SALE" ? "#1890ff" : "green" }}>
          {v}
        </span>
      ),
    },
    {
      title: "Sale ID",
      dataIndex: "saleId",
    },
    {
      title: "Debit (Sale)",
      dataIndex: "debit",
      render: (v) => (v ? `₹${v}` : "-"),
    },
    {
      title: "Credit (Payment)",
      dataIndex: "credit",
      render: (v) => (v ? `₹${v}` : "-"),
    },
    {
      title: "Running Balance",
      dataIndex: "balance",
      render: (v) => (
        <span style={{ fontWeight: 600, color: v > 0 ? "red" : "green" }}>
          ₹{v}
        </span>
      ),
    },
  ];

  return (
    <Card>
      <Space style={{ width: "100%", justifyContent: "space-between" }}>
        <Title level={3}>Customer Full Ledger</Title>

        <Button onClick={() => navigate(-1)}>Back</Button>
      </Space>

      <Table
        rowKey={(r, i) => i}
        columns={columns}
        dataSource={data}
        loading={loading}
        pagination={false}
      />
    </Card>
  );
};

export default CustomerLedger;
