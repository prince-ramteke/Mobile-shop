import { useMemo } from 'react';
import { motion } from 'framer-motion';
import Lottie from 'lottie-react';

// Inline loading animation data (simple spinner)
const loadingAnimationData = {
    v: "5.7.4",
    fr: 60,
    ip: 0,
    op: 60,
    w: 200,
    h: 200,
    nm: "Loading",
    ddd: 0,
    assets: [],
    layers: [
        {
            ddd: 0,
            ind: 1,
            ty: 4,
            nm: "Circle",
            sr: 1,
            ks: {
                o: { a: 0, k: 100 },
                r: {
                    a: 1,
                    k: [
                        { t: 0, s: [0], e: [360] },
                        { t: 60, s: [360] }
                    ]
                },
                p: { a: 0, k: [100, 100, 0] },
                a: { a: 0, k: [0, 0, 0] },
                s: { a: 0, k: [100, 100, 100] }
            },
            shapes: [
                {
                    ty: "el",
                    s: { a: 0, k: [80, 80] },
                    p: { a: 0, k: [0, 0] }
                },
                {
                    ty: "st",
                    c: { a: 0, k: [0, 0.83, 1, 1] },
                    o: { a: 0, k: 100 },
                    w: { a: 0, k: 8 },
                    lc: 2,
                    lj: 1,
                    d: [
                        { n: "d", v: { a: 0, k: 0 } },
                        { n: "g", v: { a: 0, k: 150 } }
                    ]
                },
                {
                    ty: "tm",
                    s: { a: 0, k: 0 },
                    e: { a: 0, k: 75 },
                    o: { a: 0, k: 0 },
                    m: 1
                }
            ]
        }
    ]
};

const Loading = ({
    fullScreen = true,
    size = 'default',
    text = 'Loading...',
    showText = true
}) => {
    const sizeConfig = useMemo(() => {
        switch (size) {
            case 'small':
                return { width: 60, height: 60, fontSize: 12 };
            case 'large':
                return { width: 150, height: 150, fontSize: 18 };
            default:
                return { width: 100, height: 100, fontSize: 14 };
        }
    }, [size]);

    const content = (
        <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.9 }}
            style={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                gap: 16,
            }}
        >
            <Lottie
                animationData={loadingAnimationData}
                loop
                style={{
                    width: sizeConfig.width,
                    height: sizeConfig.height,
                }}
            />
            {showText && (
                <motion.p
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: 0.2 }}
                    style={{
                        color: 'var(--color-text-secondary)',
                        fontSize: sizeConfig.fontSize,
                        margin: 0,
                        fontWeight: 500,
                    }}
                >
                    {text}
                </motion.p>
            )}
        </motion.div>
    );

    if (fullScreen) {
        return (
            <div
                style={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    background: 'var(--color-bg-primary)',
                    zIndex: 9999,
                }}
            >
                {content}
            </div>
        );
    }

    return (
        <div
            style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                padding: 40,
                minHeight: 200,
            }}
        >
            {content}
        </div>
    );
};

// Simple Spinner Component
export const Spinner = ({ size = 24, color = 'var(--color-accent)' }) => (
    <motion.div
        animate={{ rotate: 360 }}
        transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
        style={{
            width: size,
            height: size,
            border: `3px solid ${color}`,
            borderTopColor: 'transparent',
            borderRadius: '50%',
        }}
    />
);

export default Loading;
