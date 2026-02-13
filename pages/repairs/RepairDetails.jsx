import { useEffect, useState } from 'react';
import { Card, Descriptions, Tag, Button, message, Modal, InputNumber } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import repairService from '../../api/repairService';
import Loading from '../../components/common/Loading';

const th = {
  borderBottom: "1px solid #eee",
  padding: 8,
  textAlign: "left",
  background: "#fafafa"
};

const td = {
  borderBottom: "1px solid #eee",
  padding: 8
};


const RepairDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [repair, setRepair] = useState(null);
  const [paymentModal, setPaymentModal] = useState(false);
const [paymentAmount, setPaymentAmount] = useState(0);
const [payments, setPayments] = useState([]);
// ================= DOWNLOAD INVOICE =================
const handleDownloadInvoice = async () => {
  try {
message.loading({ content: "Preparing invoice...", key: "invoice" });

const blob = await repairService.downloadInvoice(id);

message.success({ content: "Invoice downloaded", key: "invoice", duration: 2 });


    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `Repair-Invoice-${repair?.jobNumber || id}.pdf`;

    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);

  } catch (err) {
    console.error(err);
    message.error("Failed to download invoice");
  }
};

// ================= SEND WHATSAPP =================
const handleSendWhatsApp = () => {
  if (!repair?.customerPhone) {
    message.error("Customer phone not found");
    return;
  }

  const msg = `Hello ${repair.customerName},
Your repair invoice is ready.

Invoice: ${repair.jobNumber}
Device: ${repair.deviceBrand} ${repair.deviceModel}
Total: ₹${repair.finalCost}

Thank you 🙏
Saurabh Mobile Shop`;

const phone = repair.customerPhone.replace(/\D/g, "").slice(-10);
const url = `https://wa.me/91${phone}?text=` + encodeURIComponent(msg);


  window.open(url, "_blank");
};



  const [loading, setLoading] = useState(true);
  
useEffect(() => {
  if (id) loadRepair();
}, [id]);


  const loadRepair = async () => {
  try {
    const data = await repairService.getById(id);
    setRepair(data);

    // ⭐ Load payment history
    const paymentList = await repairService.getPayments(id);
    setPayments(paymentList);

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
  <>
    {/* Receive Payment */}
    <Button
      type="primary"
      style={{ marginRight: 8 }}
      disabled={repair.pendingAmount <= 0}
      onClick={() => setPaymentModal(true)}
    >
      Receive Payment
    </Button>

    {/* Download Invoice */}
    <Button
      style={{ marginRight: 8 }}
      onClick={handleDownloadInvoice}
    >
      Download Invoice
    </Button>

    {/* WhatsApp */}
    <Button
      style={{ marginRight: 8, background: "#25D366", color: "white" }}
      onClick={handleSendWhatsApp}
    >
      WhatsApp
    </Button>

    {/* Edit */}
    <Button onClick={() => navigate(`/repairs/edit/${id}`)}>
      Edit
    </Button>
  </>
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

      <br />

<Card title="Payment History / Ledger">
  {payments.length === 0 ? (
    <div>No payments yet</div>
  ) : (
    <table style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr>
          <th style={th}>Date</th>
          <th style={th}>Amount</th>
          <th style={th}>Note</th>
        </tr>
      </thead>
      <tbody>
        {payments.map((p) => (
          <tr key={p.id}>
            <td style={td}>
              {new Date(p.paidAt).toLocaleString()}
            </td>
            <td style={td}>₹{p.amount}</td>
            <td style={td}>{p.note || "-"}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )}
</Card>


      <Modal
  title="Receive Payment"
  open={paymentModal}
  onCancel={() => setPaymentModal(false)}


onOk={async () => {
  if (!paymentAmount || paymentAmount <= 0) {
    message.error("Enter valid amount");
    return;
  }

  if (paymentAmount > repair.pendingAmount) {
    message.error("Amount cannot exceed pending");
    return;
  }

 try {
  const res = await repairService.receivePayment(id, paymentAmount);

  if (res?.warning) {
    message.warning("Payment may have succeeded. Please check updated amount.");
  } else {
    message.success("Payment received");
  }

  setPaymentModal(false);
  setPaymentAmount(0);

  await loadRepair();


} catch (err) {
  const msg = err?.error || err?.message || "Payment failed";
  message.error(msg);
}

}}

>
  <InputNumber
  style={{ width: "100%" }}
  min={1}
  max={repair.pendingAmount}
  value={paymentAmount}
  onChange={setPaymentAmount}
/>

</Modal>

    </Card>
  );
};

export default RepairDetails;
