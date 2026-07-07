import { useEffect, useMemo, useState } from 'react';
import {
  Box, Button, IconButton, InputAdornment, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Toolbar, Typography, CircularProgress, Chip,
} from '@mui/material';
import { Add, Edit, Delete, Search, Clear } from '@mui/icons-material';
import { useSnackbar } from 'notistack';
import { useProducts } from '../hooks/useProducts';
import { useCategories } from '../hooks/useCategories';
import { productsApi } from '../api/products';
import { ProductFormModal } from '../components/ProductFormModal';
import { ConfirmDialog } from '../components/ConfirmDialog';
import type { Product } from '../types';

function formatCurrency(value: number) {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
}

export function ProductsPage() {
  const { enqueueSnackbar } = useSnackbar();
  const { products, loading, error, reload } = useProducts();
  const { categories } = useCategories();

  const [searchName, setSearchName] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('');
  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Product | null>(null);
  const [toDelete, setToDelete] = useState<Product | null>(null);
  const [deleting, setDeleting] = useState(false);

  // Filtros aplicados localmente sobre a lista carregada; para nome usamos o endpoint quando o usuário aciona a busca.
  const [foundByName, setFoundByName] = useState<Product | null>(null);
  const [searching, setSearching] = useState(false);

  useEffect(() => {
    if (error) enqueueSnackbar(error, { variant: 'error' });
  }, [error, enqueueSnackbar]);

  const visibleProducts = useMemo(() => {
    let list = products;
    if (foundByName) list = [foundByName];
    if (categoryFilter) list = list.filter((p) => p.categoryId === categoryFilter);
    return list;
  }, [products, foundByName, categoryFilter]);

  const openCreate = () => { setEditing(null); setFormOpen(true); };
  const openEdit = (p: Product) => { setEditing(p); setFormOpen(true); };

  const handleSearchByName = async () => {
    if (!searchName.trim()) { setFoundByName(null); return; }
    setSearching(true);
    try {
      const result = await productsApi.getByName(searchName.trim());
      setFoundByName(result);
      setCategoryFilter('');
      enqueueSnackbar('Produto encontrado', { variant: 'success' });
    } catch (err) {
      setFoundByName(null);
      enqueueSnackbar(err instanceof Error ? err.message : 'Produto não encontrado', { variant: 'error' });
    } finally {
      setSearching(false);
    }
  };

  const clearFilters = () => {
    setSearchName('');
    setCategoryFilter('');
    setFoundByName(null);
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

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Produtos</Typography>

      <Toolbar disableGutters sx={{ gap: 1, flexWrap: 'wrap' }}>
        <TextField
          size="small"
          placeholder="Buscar por nome…"
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
          onKeyDown={(e) => { if (e.key === 'Enter') handleSearchByName(); }}
          InputProps={{
            startAdornment: (<InputAdornment position="start"><Search /></InputAdornment>),
          }}
        />
        <Button onClick={handleSearchByName} disabled={searching} variant="outlined">
          {searching ? <CircularProgress size={20} /> : 'Buscar'}
        </Button>
        <Select
          size="small"
          displayEmpty
          value={categoryFilter}
          onChange={(e) => { setCategoryFilter(e.target.value); setFoundByName(null); }}
          sx={{ minWidth: 180 }}
        >
          <MenuItem value="">Todas as categorias</MenuItem>
          {categories.map((c) => (
            <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
          ))}
        </Select>
        <Button onClick={clearFilters} startIcon={<Clear />} variant="text" color="inherit">Limpar</Button>
        <Box sx={{ flexGrow: 1 }} />
        <Button onClick={openCreate} startIcon={<Add />}>Novo produto</Button>
      </Toolbar>

      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead>
            <TableRow>
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
              <TableRow>
                <TableCell colSpan={6} align="center"><CircularProgress sx={{ my: 3 }} /></TableCell>
              </TableRow>
            )}
            {!loading && visibleProducts.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <Typography color="text.secondary" sx={{ py: 3 }}>Nenhum produto encontrado.</Typography>
                </TableCell>
              </TableRow>
            )}
            {visibleProducts.map((p) => (
              <TableRow key={p.id} hover>
                <TableCell>{p.name}</TableCell>
                <TableCell align="right">{formatCurrency(p.price)}</TableCell>
                <TableCell align="right">
                  <Chip size="small" label={p.stockQuantity} color={p.stockQuantity > 0 ? 'success' : 'default'} />
                </TableCell>
                <TableCell>{p.categoryName}</TableCell>
                <TableCell>{formatDate(p.createdAt)}</TableCell>
                <TableCell align="right">
                  <IconButton onClick={() => openEdit(p)} color="primary"><Edit /></IconButton>
                  <IconButton onClick={() => setToDelete(p)} color="error"><Delete /></IconButton>
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
  );
}