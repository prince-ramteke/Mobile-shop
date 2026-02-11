import { motion } from 'framer-motion';
import { Button, Typography } from 'antd';
import Lottie from 'lottie-react';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';

const { Title, Text } = Typography;

// Inline empty state animation data
const emptyAnimationData = {
    v: "5.7.4",
    fr: 30,
    ip: 0,
    op: 90,
    w: 300,
    h: 300,
    nm: "Empty",
    ddd: 0,
    assets: [],
    layers: [
        {
            ddd: 0,
            ind: 1,
            ty: 4,
            nm: "Box",
            sr: 1,
            ks: {
                o: { a: 0, k: 100 },
                r: { a: 0, k: 0 },
                p: { a: 0, k: [150, 150, 0] },
                a: { a: 0, k: [0, 0, 0] },
                s: {
                    a: 1,
                    k: [
                        { t: 0, s: [100, 100, 100], e: [105, 95, 100] },
                        { t: 45, s: [105, 95, 100], e: [100, 100, 100] },
                        { t: 90, s: [100, 100, 100] }
                    ]
                }
            },
            shapes: [
                {
                    ty: "rc",
                    d: 1,
                    s: { a: 0, k: [120, 80] },
                    p: { a: 0, k: [0, 20] },
                    r: { a: 0, k: 8 }
                },
                {
                    ty: "st",
                    c: { a: 0, k: [0.6, 0.65, 0.7, 1] },
                    o: { a: 0, k: 100 },
                    w: { a: 0, k: 4 },
                    lc: 2,
                    lj: 2
                },
                {
                    ty: "fl",
                    c: { a: 0, k: [0.9, 0.92, 0.94, 1] },
                    o: { a: 0, k: 100 }
                }
            ]
        },
        {
            ddd: 0,
            ind: 2,
            ty: 4,
            nm: "Lines",
            sr: 1,
            ks: {
                o: {
                    a: 1,
                    k: [
                        { t: 0, s: [50], e: [80] },
                        { t: 45, s: [80], e: [50] },
                        { t: 90, s: [50] }
                    ]
                },
                p: { a: 0, k: [150, 150, 0] }
            },
            shapes: [
                {
                    ty: "rc",
                    s: { a: 0, k: [60, 8] },
                    p: { a: 0, k: [0, -20] },
                    r: { a: 0, k: 4 }
                },
                {
                    ty: "rc",
                    s: { a: 0, k: [40, 8] },
                    p: { a: 0, k: [-10, -5] },
                    r: { a: 0, k: 4 }
                },
                {
                    ty: "fl",
                    c: { a: 0, k: [0.7, 0.75, 0.8, 1] },
                    o: { a: 0, k: 100 }
                }
            ]
        }
    ]
};

const EmptyState = ({
    title = 'No Data Found',
    description = 'There are no items to display at the moment.',
    actionText,
    onAction,
    showRefresh = false,
    onRefresh,
    icon,
    size = 'default',
}) => {
    const sizeConfig = {
        small: { lottieSize: 120, titleSize: 16, descSize: 13, padding: 24 },
        default: { lottieSize: 180, titleSize: 20, descSize: 14, padding: 40 },
        large: { lottieSize: 240, titleSize: 24, descSize: 16, padding: 60 },
    };

    const config = sizeConfig[size] || sizeConfig.default;

    return (
        <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4 }}
            style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                padding: config.padding,
                textAlign: 'center',
            }}
        >
            {icon || (
                <Lottie
                    animationData={emptyAnimationData}
                    loop
                    style={{
                        width: config.lottieSize,
                        height: config.lottieSize,
                        marginBottom: 16,
                    }}
                />
            )}

            <motion.div
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ delay: 0.2 }}
            >
                <Title
                    level={4}
                    style={{
                        fontSize: config.titleSize,
                        margin: 0,
                        marginBottom: 8,
                        color: 'var(--color-text-primary)',
                    }}
                >
                    {title}
                </Title>
                <Text
                    style={{
                        fontSize: config.descSize,
                        color: 'var(--color-text-secondary)',
                        display: 'block',
                        maxWidth: 320,
                        marginBottom: 24,
                    }}
                >
                    {description}
                </Text>
            </motion.div>

            <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.3 }}
                style={{ display: 'flex', gap: 12 }}
            >
                {actionText && onAction && (
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={onAction}
                        size="large"
                    >
                        {actionText}
                    </Button>
                )}
                {showRefresh && onRefresh && (
                    <Button
                        icon={<ReloadOutlined />}
                        onClick={onRefresh}
                        size="large"
                    >
                        Refresh
                    </Button>
                )}
            </motion.div>
        </motion.div>
    );
};

export default EmptyState;
