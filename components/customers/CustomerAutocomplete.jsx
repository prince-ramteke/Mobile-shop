import { useState } from 'react';
import { Select, Button, Empty } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import customerService from '../../api/customerService';

const CustomerAutocomplete = ({ value, onChange, onCreateNew }) => {
    const [options, setOptions] = useState([]);
    const [loading, setLoading] = useState(false);

    const search = async (query) => {
        if (!query || query.length < 2) {
            setOptions([]);
            return;
        }

        try {
            setLoading(true);
            const list = await customerService.search(query);
            
            // Map to options with full customer object stored
            const mappedOptions = list.map(c => ({
                value: c.id,
                label: `${c.name} (${c.phone})`,
                customer: c // Store full customer object
            }));

            setOptions(mappedOptions);
        } catch (error) {
            console.error('Search failed:', error);
            setOptions([]);
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (selectedValue, option) => {
        // Pass both id and full customer object to parent
        if (option && option.customer) {
            onChange?.(selectedValue, option.customer);
        } else {
            onChange?.(selectedValue, null);
        }
    };

    const handleAddNew = () => {
        // Find the input value from the select search
        const input = document.querySelector('.ant-select-selection-search-input');
        const searchText = input?.value || '';
        onCreateNew?.(searchText);
    };

    return (
        <Select
            showSearch
            value={value}
            onSearch={search}
            onChange={handleChange}
            placeholder="Search customer by name or phone..."
            filterOption={false} // Disable client-side filtering
            loading={loading}
            allowClear
            style={{ width: '100%' }}
            dropdownRender={(menu) => (
                <>
                    {menu}
                    {options.length === 0 && !loading && (
                        <div style={{ padding: '8px', textAlign: 'center', borderTop: '1px solid #f0f0f0' }}>
                            <Button 
                                type="link" 
                                onClick={handleAddNew}
                                icon={<PlusOutlined />}
                            >
                                Add New Customer
                            </Button>
                        </div>
                    )}
                </>
            )}
            notFoundContent={
                <Empty 
                    image={Empty.PRESENTED_IMAGE_SIMPLE} 
                    description="No customers found"
                >
                    <Button type="primary" size="small" icon={<PlusOutlined />} onClick={handleAddNew}>
                        Add New
                    </Button>
                </Empty>
            }
        >
            {options.map(option => (
                <Select.Option key={option.value} value={option.value} customer={option.customer}>
                    {option.label}
                </Select.Option>
            ))}
        </Select>
    );
};

export default CustomerAutocomplete;