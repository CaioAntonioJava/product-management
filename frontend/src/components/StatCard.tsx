import { Box, Card, CardContent, Typography, useTheme } from '@mui/material';
import { motion } from 'framer-motion';
import type { ReactNode } from 'react';

interface StatCardProps {
  label: string;
  value: string | number;
  icon: ReactNode;
  gradient?: string;
  delay?: number;
}

export function StatCard({ label, value, icon, gradient, delay = 0 }: StatCardProps) {
  const theme = useTheme();
  const isDark = theme.palette.mode === 'dark';

  return (
    <Card
      component={motion.div}
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3, delay }}
      sx={{
        position: 'relative',
        overflow: 'hidden',
        height: '100%',
      }}
    >
      <Box
        sx={{
          position: 'absolute',
          inset: 0,
          background: gradient ?? 'linear-gradient(135deg, rgba(99,102,241,0.10), rgba(139,92,246,0.06))',
          opacity: isDark ? 0.6 : 1,
        }}
      />
      <CardContent sx={{ position: 'relative', p: 3, '&:last-child': { pb: 3 } }}>
        <Box sx={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
          <Box>
            <Typography variant="overline" color="text.secondary">{label}</Typography>
            <Typography variant="h4" sx={{ mt: 0.5 }}>{value}</Typography>
          </Box>
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              width: 48,
              height: 48,
              borderRadius: 3,
              background: gradient ?? 'linear-gradient(135deg, #6366f1, #8b5cf6)',
              color: '#fff',
              boxShadow: '0 6px 16px rgba(99,102,241,0.35)',
            }}
          >
            {icon}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );
}