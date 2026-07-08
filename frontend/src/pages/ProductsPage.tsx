import { useEffect, useMemo, useState } from 'react';
import {
  Avatar, Box, Button, Chip, CircularProgress, Grid, IconButton, InputAdornment, MenuItem, Paper, Select, Skeleton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Toolbar, Typography,
} from '@mui/material';
import { Add, Delete, Edit, Search, Clear, Inventory2Rounded, CategoryRounded, WarningAmberRounded, AttachMoneyRounded, FileDownloadRounded } from '@mui/icons-material';
import { motion } from 'framer-motion';
import { useSnackbar } from 'notistack';
import { useProducts } from '../hooks/useProducts';
import { useCategories } from '../hooks/useCategories';
import { productsApi } from '../api/products';
import { ProductFormModal } from '../components/ProductFormModal';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { StatCard } from '../components/StatCard';
import { PageTransition } from '../components/PageTransition';
import { BRAND_GRADIENT } from '../theme';
import { exportProductsToExcel } from '../utils/exports';
import type { Product } from '../types';

function formatCurrency(value: number) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
}

function initials(name: string) {
  return name.slice(0, 1).toUpperCase();
}

export function ProductsPage() {
  const { enqueueSnackbar } = useSnackbar();
  const { products, loading, error, reload, setProducts } = useProducts();
  const { categories } = useCategories();

  const [searchName, setSearchName] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Product | null>(null);
  const [toDelete, setToDelete] = useState<Product | null>(null);
  const [deleting, setDeleting] = useState(false);

  const [searching, setSearching] = useState(false);

  useEffect(() => {
    if (error) enqueueSnackbar(error, { variant: 'error' });
  }, [error, enqueueSnackbar]);

  // Live filter: refetch products as the user types (debounced 300ms).
  useEffect(() => {
    let cancelled = false;
    const handle = setTimeout(async () => {
      setSearching(true);
      try {
        const data = await productsApi.list(searchName);
        if (!cancelled) setProducts(data);
      } catch (err) {
        if (!cancelled) {
          enqueueSnackbar(err instanceof Error ? err.message : 'Erro ao buscar produtos', { variant: 'error' });
        }
      } finally {
        if (!cancelled) setSearching(false);
      }
    }, 300);
    return () => {
      cancelled = true;
      clearTimeout(handle);
    };
  }, [searchName, setProducts, enqueueSnackbar]);

  const visibleProducts = useMemo(() => {
    if (!categoryFilter) return products;
    return products.filter((p) => p.categoryId === categoryFilter);
  }, [products, categoryFilter]);

  const stats = useMemo(() => {
    const lowStock = products.filter((p) => p.stockQuantity <= 0).length;
    const totalValue = products.reduce((sum, p) => sum + p.price * p.stockQuantity, 0);
    return {
      total: products.length,
      categories: categories.length,
      lowStock,
      totalValue,
    };
  }, [products, categories]);

  const openCreate = () => { setEditing(null); setFormOpen(true); };
  const openEdit = (p: Product) => { setEditing(p); setFormOpen(true); };

  const handleExport = () => {
    try {
      exportProductsToExcel(visibleProducts);
      enqueueSnackbar(`${visibleProducts.length} produto(s) exportado(s)`, { variant: 'success' });
    } catch (err) {
      enqueueSnackbar(err instanceof Error ? err.message : 'Erro ao exportar', { variant: 'error' });
    }
  };

  const clearFilters = () => {
    setSearchName('');
    setCategoryFilter('');
  };

  const handleDelete = async () => {
    if (!toDelete) return;
    setDeleting(true);
    try {
      await productsApi.remove(toDelete.id);
      enqueueSnackbar('Produto excluído', { variant: 'success' });
      setToDelete(null);
      reload();
    } catch (err) {
      enqueueSnackbar(err instanceof Error ? err.message : 'Erro ao excluir produto', { variant: 'error' });
    } finally {
      setDeleting(false);
    }
  };

  const hasFilters = Boolean(searchName || categoryFilter);

  return (
    <PageTransition>
      <Box>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>Produtos</Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              label="Total de produtos"
              value={stats.total}
              icon={<Inventory2Rounded />}
              delay={0}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              label="Categorias"
              value={stats.categories}
              icon={<CategoryRounded />}
              gradient="linear-gradient(135deg, #8b5cf6, #d946ef)"
              delay={0.05}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              label="Produtos sem estoque"
              value={stats.lowStock}
              icon={<WarningAmberRounded />}
              gradient="linear-gradient(135deg, #f59e0b, #ef4444)"
              delay={0.1}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <StatCard
              label="Valor em estoque"
              value={formatCurrency(stats.totalValue)}
              icon={<AttachMoneyRounded />}
              gradient="linear-gradient(135deg, #10b981, #06b6d4)"
              delay={0.15}
            />
          </Grid>
        </Grid>

        <Toolbar disableGutters sx={{ gap: 1.5, flexWrap: 'wrap', mb: 2 }}>
          <TextField
            placeholder="Buscar produto por nome..."
            value={searchName}
            onChange={(e) => { setSearchName(e.target.value); setCategoryFilter(''); }}
            size="small"
            InputProps={{
              startAdornment: (<InputAdornment position="start" sx={{ color: '#8b5cf6' }}><Search fontSize="small" /></InputAdornment>),
              endAdornment: searching ? (
                <InputAdornment position="end">
                  <CircularProgress size={16} sx={{ color: '#8b5cf6' }} />
                </InputAdornment>
              ) : undefined,
            }}
            sx={{ minWidth: 400, flexGrow: 1, maxWidth: 800 }}
          />
          {hasFilters && (
            <Button onClick={clearFilters} startIcon={<Clear />} variant="text" color="inherit" size="small">Limpar</Button>
          )}
          <Button
            onClick={handleExport}
            startIcon={<FileDownloadRounded />}
            variant="outlined"
            color="inherit"
            disabled={loading || visibleProducts.length === 0}
          >
            Exportar Excel
          </Button>
          <Box sx={{ flexGrow: 1 }} />
          <Select
            displayEmpty
            value={categoryFilter}
            onChange={(e) => { setCategoryFilter(e.target.value); setSearchName(''); }}
            size="small"
            sx={{
              minWidth: 200,
              backgroundColor: '#8b5cf6',
              color: '#fff',
              '& .MuiSelect-select': { color: '#fff' },
              '& .MuiOutlinedInput-notchedOutline': { borderColor: 'transparent' },
              '&:hover .MuiOutlinedInput-notchedOutline': { borderColor: 'transparent' },
              '&.Mui-focused .MuiOutlinedInput-notchedOutline': { borderColor: 'transparent' },
              '& .MuiSelect-icon': { color: '#fff' },
              '&:hover': { backgroundColor: '#7c3aed' },
            }}
            renderValue={(value) => (value ? categories.find((c) => c.id === value)?.name : 'Todas as categorias')}
          >
            <MenuItem value="">Todas as categorias</MenuItem>
            {categories.map((c) => (
              <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
            ))}
          </Select>
          <Button
            onClick={openCreate}
            startIcon={<Add />}
            sx={{ backgroundColor: '#8b5cf6', color: '#fff', '&:hover': { backgroundColor: '#7c3aed' } }}
          >
            Novo produto
          </Button>
        </Toolbar>

        <TableContainer component={Paper} sx={{ borderRadius: 3, overflow: 'hidden' }}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'action.hover' }}>
                <TableCell>Nome</TableCell>
                <TableCell align="right">Preço</TableCell>
                <TableCell align="right">Estoque</TableCell>
                <TableCell>Categoria</TableCell>
                <TableCell>Criado em</TableCell>
                <TableCell align="right">Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading && (
                Array.from({ length: 4 }).map((_, i) => (
                  <TableRow key={i}>
                    <TableCell><Skeleton /></TableCell>
                    <TableCell><Skeleton /></TableCell>
                    <TableCell><Skeleton /></TableCell>
                    <TableCell><Skeleton /></TableCell>
                    <TableCell><Skeleton /></TableCell>
                    <TableCell><Skeleton /></TableCell>
                  </TableRow>
                ))
              )}
              {!loading && visibleProducts.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} sx={{ borderBottom: 'none' }}>
                    <EmptyState
                      icon={<Inventory2Rounded />}
                      title="Nenhum produto encontrado"
                      description={hasFilters ? 'Tente ajustar os filtros de busca.' : 'Crie seu primeiro produto para começar.'}
                      action={!hasFilters && <Button onClick={openCreate} startIcon={<Add />}>Novo produto</Button>}
                    />
                  </TableCell>
                </TableRow>
              )}
              {!loading && visibleProducts.map((p, index) => (
                <TableRow
                  key={p.id}
                  component={motion.tr}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.2, delay: Math.min(index * 0.03, 0.3) }}
                  hover
                  sx={{ '&:last-child td': { borderBottom: 0 } }}
                >
                  <TableCell>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                      <Avatar sx={{ width: 40, height: 40, background: BRAND_GRADIENT, fontSize: 16, fontWeight: 700 }}>
                        {initials(p.name)}
                      </Avatar>
                      <Box>
                        <Typography fontWeight={600}>{p.name}</Typography>
                        {p.description && (
                          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', maxWidth: 280, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                            {p.description}
                          </Typography>
                        )}
                      </Box>
                    </Box>
                  </TableCell>
                  <TableCell align="right" sx={{ fontWeight: 600 }}>{formatCurrency(p.price)}</TableCell>
                  <TableCell align="right">
                    <Chip
                      size="small"
                      label={p.stockQuantity}
                      color={p.stockQuantity > 0 ? 'success' : 'error'}
                      variant={p.stockQuantity > 0 ? 'filled' : 'outlined'}
                    />
                  </TableCell>
                  <TableCell>{p.categoryName}</TableCell>
                  <TableCell>{formatDate(p.createdAt)}</TableCell>
                  <TableCell align="right">
                    <IconButton onClick={() => openEdit(p)} color="primary" size="small"><Edit fontSize="small" /></IconButton>
                    <IconButton onClick={() => setToDelete(p)} color="error" size="small"><Delete fontSize="small" /></IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>

        <ProductFormModal
          open={formOpen}
          onClose={() => setFormOpen(false)}
          onSaved={reload}
          categories={categories}
          product={editing}
        />

        <ConfirmDialog
          open={Boolean(toDelete)}
          title="Excluir produto"
          description={`Deseja realmente excluir "${toDelete?.name}"? Esta ação não pode ser desfeita.`}
          confirmText="Excluir"
          onConfirm={handleDelete}
          onClose={() => setToDelete(null)}
          loading={deleting}
        />
      </Box>
    </PageTransition>
  );
}