import { Button, Typography, Box } from '@mui/material';
import { Link } from 'react-router-dom';

export function NotFoundPage() {
  return (
    <Box sx={{ textAlign: 'center', py: 10 }}>
      <Typography variant="h3" gutterBottom>404</Typography>
      <Typography color="text.secondary" gutterBottom>Página não encontrada.</Typography>
      <Button component={Link} to="/products" sx={{ mt: 2 }}>Ir para Produtos</Button>
    </Box>
  );
}