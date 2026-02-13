import { useState } from 'react';
import { Form, Input, Typography, message, Checkbox } from 'antd';
import { motion } from 'framer-motion';
import { useNavigate, useLocation } from 'react-router-dom';
import { UserOutlined, LockOutlined, MobileOutlined } from '@ant-design/icons';
import { useAuth } from '../../context/AuthContext';
import AnimatedButton from '../../components/common/AnimatedButton';

const { Title, Text, Paragraph } = Typography;

const Login = () => {
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const from = location.state?.from?.pathname || '/';

    const handleSubmit = async (values) => {
        setLoading(true);
        try {
            const result = await login(values.username, values.password);
            if (result.success) {
                navigate(from, { replace: true });
            }
        } catch (error) {
            message.error('Login failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    // Background animation variants
    const floatingVariants = {
        animate: {
            y: [0, -20, 0],
            transition: {
                duration: 4,
                repeat: Infinity,
                ease: 'easeInOut',
            },
        },
    };

    return (
        <div
            style={{
                minHeight: '100vh',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'linear-gradient(135deg, #0a1628 0%, #1e3a5f 50%, #0a1628 100%)',
                padding: 24,
                position: 'relative',
                overflow: 'hidden',
            }}
        >
            {/* Animated Background Elements */}
            <motion.div
                variants={floatingVariants}
                animate="animate"
                style={{
                    position: 'absolute',
                    top: '10%',
                    left: '10%',
                    width: 200,
                    height: 200,
                    borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(0, 212, 255, 0.15) 0%, transparent 70%)',
                }}
            />
            <motion.div
                variants={floatingVariants}
                animate="animate"
                style={{
                    position: 'absolute',
                    bottom: '15%',
                    right: '10%',
                    width: 300,
                    height: 300,
                    borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(0, 212, 255, 0.1) 0%, transparent 70%)',
                    animationDelay: '1s',
                }}
            />
            <motion.div
                animate={{
                    y: [0, 15, 0],
                    transition: { duration: 5, repeat: Infinity, ease: 'easeInOut' },
                }}
                style={{
                    position: 'absolute',
                    top: '40%',
                    right: '20%',
                    width: 150,
                    height: 150,
                    borderRadius: '50%',
                    background: 'radial-gradient(circle, rgba(30, 58, 95, 0.3) 0%, transparent 70%)',
                }}
            />

            {/* Login Card */}
            <motion.div
                initial={{ opacity: 0, y: 30, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                transition={{ duration: 0.5 }}
                style={{
                    width: '100%',
                    maxWidth: 440,
                    background: 'rgba(26, 41, 66, 0.9)',
                    backdropFilter: 'blur(20px)',
                    borderRadius: 24,
                    padding: '48px 40px',
                    boxShadow: '0 25px 50px -12px rgba(0, 0, 0, 0.5), 0 0 0 1px rgba(0, 212, 255, 0.1)',
                    position: 'relative',
                    zIndex: 10,
                }}
            >
                {/* Logo */}
                <motion.div
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ delay: 0.2, type: 'spring', stiffness: 200 }}
                    style={{
                        width: 80,
                        height: 80,
                        margin: '0 auto 24px',
                        borderRadius: 20,
                        background: 'linear-gradient(135deg, #00d4ff 0%, #1e3a5f 100%)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        boxShadow: '0 10px 30px rgba(0, 212, 255, 0.3)',
                    }}
                >
                    <MobileOutlined style={{ fontSize: 40, color: '#fff' }} />
                </motion.div>

                {/* Title */}
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.3 }}
                    style={{ textAlign: 'center', marginBottom: 32 }}
                >
                    <Title
                        level={2}
                        style={{
                            margin: 0,
                            color: '#fff',
                            fontWeight: 700,
                        }}
                    >
                        Saurabh Mobile
                    </Title>
                    <Text style={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                        Shop Management System
                    </Text>
                </motion.div>

                {/* Login Form */}
                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.4 }}
                >
                    <Form
                        name="login"
                        onFinish={handleSubmit}
                        layout="vertical"
                        requiredMark={false}
                        size="large"
                    >
                        <Form.Item
                            name="username"
                            rules={[{ required: true, message: 'Please enter your username' }]}
                        >
                            <Input
                                prefix={<UserOutlined style={{ color: 'rgba(255, 255, 255, 0.4)' }} />}
                                placeholder="Username"
                                style={{
                                    background: 'rgba(255, 255, 255, 0.05)',
                                    border: '1px solid rgba(255, 255, 255, 0.1)',
                                    borderRadius: 12,
                                    height: 50,
                                    color: '#fff',
                                }}
                            />
                        </Form.Item>

                        <Form.Item
                            name="password"
                            rules={[{ required: true, message: 'Please enter your password' }]}
                        >
                            <Input.Password
                                prefix={<LockOutlined style={{ color: 'rgba(255, 255, 255, 0.4)' }} />}
                                placeholder="Password"
                                style={{
                                    background: 'rgba(255, 255, 255, 0.05)',
                                    border: '1px solid rgba(255, 255, 255, 0.1)',
                                    borderRadius: 12,
                                    height: 50,
                                    color: '#fff',
                                }}
                            />
                        </Form.Item>

                        <Form.Item name="remember" valuePropName="checked">
                            <Checkbox style={{ color: 'rgba(255, 255, 255, 0.6)' }}>
                                Remember me
                            </Checkbox>
                        </Form.Item>

                        <Form.Item style={{ marginBottom: 0 }}>
                            <AnimatedButton
                                variant="primary"
                                size="large"
                                block
                                htmlType="submit"
                                loading={loading}
                                style={{
                                    height: 50,
                                    borderRadius: 12,
                                    fontSize: 16,
                                    fontWeight: 600,
                                }}
                            >
                                Sign In
                            </AnimatedButton>
                        </Form.Item>
                    </Form>
                </motion.div>

                {/* Footer */}
                <motion.div
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.6 }}
                    style={{ textAlign: 'center', marginTop: 24 }}
                >
                    <Text style={{ color: 'rgba(255, 255, 255, 0.4)', fontSize: 13 }}>
                        © 2024 Saurabh Mobile Shop. All rights reserved.
                    </Text>
                </motion.div>
            </motion.div>
        </div>
    );
};

export default Login;
