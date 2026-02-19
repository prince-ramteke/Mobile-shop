import { useEffect, useState } from 'react';
import { PlusOutlined, EyeOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import saleService from '../../api/saleService';
import { Card, Table, Button, Typography, Space, message, Modal, InputNumber, Input } from 'antd';


const { Title } = Typography;

const MobileSaleList = () => {
  const [data, setData] = useState([]);
  const [showPendingOnly, setShowPendingOnly] = useState(false);
  const [loading, setLoading] = useState(true);
  const [recoverOpen, setRecoverOpen] = useState(false);
const [selectedSale, setSelectedSale] = useState(null);
const [recoverAmount, setRecoverAmount] = useState(0);

const [ledgerOpen, setLedgerOpen] = useState(false);
const [ledgerData, setLedgerData] = useState([]);

const [searchText, setSearchText] = useState("");



  const navigate = useNavigate();

  useEffect(() => {
  load();
}, [showPendingOnly, searchText]);



 const load = async () => {
  try {
    setLoading(true);
   let list = [];

if (searchText.trim()) {
  list = await saleService.searchMobileSales(searchText);
} else {
  list = await saleService.getMobileSales();
}

    // Pending filter
    if (showPendingOnly) {
      list = list.filter(s => Number(s.pendingAmount) > 0);
    }


    setData(list);
  } finally {
    setLoading(false);
  }
};

const openRecover = (sale) => {
  setSelectedSale(sale);
  setRecoverAmount(0);
  setRecoverOpen(true);
};

const doRecover = async () => {
  if (!recoverAmount || recoverAmount <= 0) {
    message.error("Enter valid amount");
    return;
  }
    if (recoverAmount > selectedSale.pendingAmount) {
    message.error("Amount cannot be greater than pending");
    return;
  }

  try {
    await saleService.recoverPayment(selectedSale.id, recoverAmount);
    message.success("Payment Recovered");
    setRecoverOpen(false);
    load(); // refresh table
  } catch {
    message.error("Recover failed");
  }
};

const openLedger = async (sale) => {
  try {
    const res = await saleService.getRecoveryLedger(sale.id);
    setLedgerData(res || []);
    setSelectedSale(sale);
    setLedgerOpen(true);
  } catch {
    message.error("Failed to load ledger");
  }
};


  const columns = [
   { title: 'ID', dataIndex: 'id' },

{
  title: 'Customer',
  render: (_, r) => (
    <span title={r.customerPhone}>
      {r.customerName}
    </span>
  ),
},


{ title: 'Company', dataIndex: 'company' },
{ title: 'Model', dataIndex: 'model' },
{ title: 'IMEI', dataIndex: 'imei1' },

{
  title: 'Pending',
  dataIndex: 'pendingAmount',
  render: (v) => (
    <span style={{ color: v > 0 ? 'red' : 'green', fontWeight: 600 }}>
      ₹{v}
    </span>
  ),
},

   {
  title: 'Action',
  render: (_, r) => (
    <Space>
      <Button
        icon={<EyeOutlined />}
        onClick={() => navigate(`/mobile-sales/${r.id}`)}
      />

      <Button onClick={() => navigate(`/mobile-sales/${r.id}/edit`)}>
        Edit
      </Button>

      <Button
  type="primary"
  disabled={r.pendingAmount <= 0 || ledgerOpen}
  onClick={() => openRecover(r)}
>
  Recover
</Button>

<Button
  disabled={!r.customerId}
  onClick={() => {
    if (!r.customerId) {
      message.error("Customer ID missing");
      return;
    }
    navigate(`/customer-ledger/${r.customerId}`);
  }}
>
  Customer Ledger
</Button>




      <Button
        onClick={async () => {
          try {
            await saleService.sendReminder(r.id);
            message.success("Reminder Sent on WhatsApp");
          } catch {
            message.error("Failed to send reminder");
          }
        }}
      >
        Reminder
      </Button>

      <Button onClick={() => openLedger(r)}>
  Ledger
</Button>

    </Space>
  ),
}



  ];

  return (
    <Card>
  <Space style={{ width: '100%', justifyContent: 'space-between' }}>
  <Space>
  <Title level={3}>Mobile Sales</Title>

  <Input
    placeholder="Search customer..."
    allowClear
    value={searchText}
    onChange={(e) => setSearchText(e.target.value)}
    style={{ width: 220 }}
  />


    <Button
      type={!showPendingOnly ? 'primary' : 'default'}
      onClick={() => setShowPendingOnly(false)}
    >
      All
    </Button>

    <Button
      danger
      type={showPendingOnly ? 'primary' : 'default'}
      onClick={() => setShowPendingOnly(true)}
    >
      Pending Only
    </Button>
  </Space>

  <Button
    type="primary"
    icon={<PlusOutlined />}
    onClick={() => navigate('/mobile-sales/create')}
  >
    New Sale
  </Button>
</Space>


      <Table
        rowKey="id"
        dataSource={data}
        columns={columns}
        loading={loading}
      />

<Modal
  title={`Recover Payment — Sale #${selectedSale?.id}`}
  open={recoverOpen}
  onOk={doRecover}
onCancel={() => {
  setRecoverOpen(false);
  setRecoverAmount(0);
  setSelectedSale(null);
}}
  okText="Recover"
>
  <p>Pending: ₹{selectedSale?.pendingAmount}</p>

  <InputNumber
    style={{ width: "100%" }}
    placeholder="Enter amount"
    value={recoverAmount}
    onChange={setRecoverAmount}
    min={1}
    max={selectedSale?.pendingAmount}
  />
</Modal>

<Modal
  title={`Recovery Ledger — Sale #${selectedSale?.id}`}
  open={ledgerOpen}
  footer={null}
onCancel={() => {
  setLedgerOpen(false);
  setSelectedSale(null);
}}
>
  <Table
    size="small"
    rowKey="id"
dataSource={[...ledgerData].sort(
  (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
)}
    pagination={false}
    columns={[
      {
        title: "Date",
        dataIndex: "createdAt",
        render: (v) => new Date(v).toLocaleString(),
      },
      {
        title: "Amount",
        dataIndex: "amount",
        render: (v) => `₹${v}`,
      },
      {
        title: "Pending After",
        dataIndex: "pendingAfter",
        render: (v) => `₹${v}`,
      },
    ]}
  />
</Modal>

    </Card>
  );
};

export default MobileSaleList;
