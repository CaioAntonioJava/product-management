import { Box, Button, Typography } from '@mui/material';
import { motion } from 'framer-motion';
import { SentimentDissatisfiedRounded } from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { PageTransition } from '../components/PageTransition';

export function NotFoundPage() {
  return (
    <PageTransition>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', py: 12 }}>
        <motion.div
          initial={{ scale: 0.7, opacity: 0, rotate: -10 }}
          animate={{ scale: 1, opacity: 1, rotate: 0 }}
          transition={{ duration: 0.45, ease: 'easeOut' }}
          style={{
            width: 120,
            height: 120,
            borderRadius: '50%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            marginBottom: 24,
            background: 'linear-gradient(135deg, rgba(99,102,241,0.16), rgba(139,92,246,0.12))',
            color: 'primary.main',
          }}
        >
          <SentimentDissatisfiedRounded sx={{ fontSize: 64 }} />
        </motion.div>
        <Typography variant="h3" sx={{ fontWeight: 800 }} gutterBottom>404</Typography>
        <Typography color="text.secondary" gutterBottom>Página não encontrada.</Typography>
        <Button component={Link} to="/products" sx={{ mt: 3 }}>Ir para Produtos</Button>
      </Box>
    </PageTransition>
  );
}