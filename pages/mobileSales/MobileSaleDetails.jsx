import { useEffect, useState } from 'react';
import { Card, Typography, Button, message } from 'antd';
import { useParams } from 'react-router-dom';
import saleService from '../../api/saleService';

const { Title, Text } = Typography;

const MobileSaleDetails = () => {
  const { id } = useParams();
  const [sale, setSale] = useState(null);

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    const res = await saleService.getMobileSaleById(id);
    setSale(res);
  };

  const download = async () => {
    const blob = await saleService.downloadMobileInvoice(id);
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.href = url;
    link.download = `invoice-${id}.pdf`;
    link.click();
    message.success('Invoice Downloaded');
  };

  if (!sale) return null;

  return (
    <Card>
      <Title level={3}>Mobile Sale #{sale.id}</Title>

      <Text><b>Company:</b> {sale.company}</Text><br/>
<Text><b>Model:</b> {sale.model}</Text><br/>
<Text><b>IMEI1:</b> {sale.imei1}</Text><br/>
<Text><b>IMEI2:</b> {sale.imei2}</Text><br/>

<br/>

<Text><b>Price:</b> ₹{sale.price}</Text><br/>
<Text><b>Quantity:</b> {sale.quantity}</Text><br/>
<Text><b>Total Amount:</b> ₹{sale.totalAmount}</Text><br/>

<br/>

<Text><b>Advance Paid:</b> ₹{sale.advancePaid}</Text><br/>
<Text><b>Pending Amount:</b> ₹{sale.pendingAmount}</Text><br/>

<br/>

<Text><b>Warranty:</b> {sale.warrantyYears} year(s)</Text><br/>
<Text><b>Expiry Date:</b> {sale.warrantyExpiry}</Text><br/>

<br/>

<Title level={5}>Customer Details</Title>

<Text><b>Name:</b> {sale.customerName}</Text><br/>
<Text><b>Phone:</b> {sale.customerPhone}</Text><br/>
<Text><b>Address:</b> {sale.customerAddress}</Text><br/>


      <Button type="primary" onClick={download} style={{ marginTop: 20 }}>
        Download Invoice
      </Button>
    </Card>
  );
};

export default MobileSaleDetails;
