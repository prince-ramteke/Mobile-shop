import { useEffect, useState } from 'react';
import { Card, Input, Button, message, Row, Col } from 'antd';
import { useParams, useNavigate } from 'react-router-dom';
import saleService from '../../api/saleService';

const EditMobileSale = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    company: '',
    model: '',
    imei1: '',
    imei2: '',
    quantity: 1,
    price: 0,
    advancePaid: 0,
    warrantyYears: 1,
    customerName: '',
    customerPhone: '',
    customerAddress: ''
  });

  useEffect(() => {
    load();
  }, []);

  const load = async () => {
    const sale = await saleService.getMobileSaleById(id);

    setForm({
      company: sale.company,
      model: sale.model,
      imei1: sale.imei1,
      imei2: sale.imei2,
      quantity: sale.quantity,
      price: sale.price,
      advancePaid: sale.advancePaid,
      warrantyYears: sale.warrantyYears,
      customerName: sale.customerName,
      customerPhone: sale.customerPhone,
      customerAddress: sale.customerAddress
    });
  };

  const handleChange = (key, value) => {
    setForm(prev => ({ ...prev, [key]: value }));
  };

  const update = async () => {
    await saleService.updateMobileSale(id, form);
    message.success('Mobile Sale Updated');
    navigate(`/mobile-sales/${id}`);
  };

  return (
    <Card title={`Edit Mobile Sale #${id}`}>

      <Row gutter={16}>
        <Col span={12}>
          <Input
            placeholder="Company"
            value={form.company}
            onChange={e => handleChange('company', e.target.value)}
          />
        </Col>

        <Col span={12}>
          <Input
            placeholder="Model"
            value={form.model}
            onChange={e => handleChange('model', e.target.value)}
          />
        </Col>
      </Row>

      <br />

      <Row gutter={16}>
        <Col span={12}>
          <Input
            placeholder="IMEI 1"
            value={form.imei1}
            onChange={e => handleChange('imei1', e.target.value)}
          />
        </Col>

        <Col span={12}>
          <Input
            placeholder="IMEI 2"
            value={form.imei2}
            onChange={e => handleChange('imei2', e.target.value)}
          />
        </Col>
      </Row>

      <br />

      <Row gutter={16}>
        <Col span={8}>
          <Input
            type="number"
            placeholder="Price"
            value={form.price}
            onChange={e => handleChange('price', e.target.value)}
          />
        </Col>

        <Col span={8}>
          <Input
            type="number"
            placeholder="Advance Paid"
            value={form.advancePaid}
            onChange={e => handleChange('advancePaid', e.target.value)}
          />
        </Col>

        <Col span={8}>
          <Input
            type="number"
            placeholder="Warranty Years"
            value={form.warrantyYears}
            onChange={e => handleChange('warrantyYears', e.target.value)}
          />
        </Col>
      </Row>

      <br />

      <Input
        placeholder="Customer Name"
        value={form.customerName}
        onChange={e => handleChange('customerName', e.target.value)}
      />
      <br /><br />

      <Input
        placeholder="Customer Phone"
        value={form.customerPhone}
        onChange={e => handleChange('customerPhone', e.target.value)}
      />
      <br /><br />

      <Input
        placeholder="Customer Address"
        value={form.customerAddress}
        onChange={e => handleChange('customerAddress', e.target.value)}
      />

      <br /><br />

      <Button type="primary" onClick={update}>
        Update Sale
      </Button>
    </Card>
  );
};

export default EditMobileSale;
