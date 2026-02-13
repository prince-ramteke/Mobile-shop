import { motion } from 'framer-motion';
import { Button } from 'antd';

const AnimatedButton = ({
    children,
    variant = 'primary',
    size = 'middle',
    icon,
    loading = false,
    disabled = false,
    onClick,
    block = false,
    htmlType = 'button',
    style = {},
    className = '',
    ...props
}) => {
    // Variant styles
    const getVariantStyles = () => {
        switch (variant) {
            case 'primary':
                return {
                    background: 'linear-gradient(135deg, #1e3a5f 0%, #00d4ff 100%)',
                    color: '#ffffff',
                    border: 'none',
                };
            case 'secondary':
                return {
                    background: 'var(--color-bg-tertiary)',
                    color: 'var(--color-text-primary)',
                    border: '1px solid var(--color-border)',
                };
            case 'success':
                return {
                    background: 'linear-gradient(135deg, #059669 0%, #10b981 100%)',
                    color: '#ffffff',
                    border: 'none',
                };
            case 'danger':
                return {
                    background: 'linear-gradient(135deg, #dc2626 0%, #ef4444 100%)',
                    color: '#ffffff',
                    border: 'none',
                };
            case 'warning':
                return {
                    background: 'linear-gradient(135deg, #d97706 0%, #f59e0b 100%)',
                    color: '#ffffff',
                    border: 'none',
                };
            case 'ghost':
                return {
                    background: 'transparent',
                    color: 'var(--color-accent)',
                    border: '1px solid var(--color-accent)',
                };
            case 'link':
                return {
                    background: 'transparent',
                    color: 'var(--color-accent)',
                    border: 'none',
                };
            default:
                return {};
        }
    };

    // Size styles
    const getSizeStyles = () => {
        switch (size) {
            case 'small':
                return { padding: '4px 12px', fontSize: 13 };
            case 'large':
                return { padding: '12px 24px', fontSize: 16 };
            default:
                return { padding: '8px 16px', fontSize: 14 };
        }
    };

    const buttonStyles = {
        ...getVariantStyles(),
        ...getSizeStyles(),
        borderRadius: 8,
        fontWeight: 500,
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 8,
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.5 : 1,
        width: block ? '100%' : 'auto',
        transition: 'all 0.2s ease',
        boxShadow: variant === 'primary' || variant === 'success' || variant === 'danger'
            ? '0 4px 15px rgba(0, 0, 0, 0.15)'
            : 'none',
        ...style,
    };

    return (
        <motion.button
            whileHover={!disabled ? {
                scale: 1.02,
                y: -2,
                boxShadow: '0 8px 25px rgba(0, 0, 0, 0.2)',
            } : {}}
            whileTap={!disabled ? { scale: 0.98 } : {}}
            transition={{ type: 'spring', stiffness: 400, damping: 17 }}
            onClick={disabled ? undefined : onClick}
            disabled={disabled || loading}
            type={htmlType}
            style={buttonStyles}
            className={className}
            {...props}
        >
            {loading ? (
                <motion.span
                    animate={{ rotate: 360 }}
                    transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
                    style={{
                        display: 'inline-block',
                        width: 16,
                        height: 16,
                        border: '2px solid currentColor',
                        borderTopColor: 'transparent',
                        borderRadius: '50%',
                    }}
                />
            ) : (
                icon
            )}
            {children}
        </motion.button>
    );
};

// Preset button components
export const PrimaryButton = (props) => <AnimatedButton variant="primary" {...props} />;
export const SecondaryButton = (props) => <AnimatedButton variant="secondary" {...props} />;
export const SuccessButton = (props) => <AnimatedButton variant="success" {...props} />;
export const DangerButton = (props) => <AnimatedButton variant="danger" {...props} />;
export const WarningButton = (props) => <AnimatedButton variant="warning" {...props} />;
export const GhostButton = (props) => <AnimatedButton variant="ghost" {...props} />;

export default AnimatedButton;
