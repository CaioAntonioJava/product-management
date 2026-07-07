import { useEffect, useState } from 'react';
import {
  Box, Button, IconButton, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography, CircularProgress, Toolbar,
} from '@mui/material';
import { Add, Edit, Delete } from '@mui/icons-material';
import { useSnackbar } from 'notistack';
import { useCategories } from '../hooks/useCategories';
import { categoriesApi } from '../api/categories';
import { CategoryFormDialog } from '../components/CategoryFormDialog';
import { ConfirmDialog } from '../components/ConfirmDialog';
import type { Category } from '../types';

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
}

export function CategoriesPage() {
  const { enqueueSnackbar } = useSnackbar();
  const { categories, loading, error, reload } = useCategories();

  const [formOpen, setFormOpen] = useState(false);
  const [editing, setEditing] = useState<Category | null>(null);
  const [toDelete, setToDelete] = useState<Category | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (error) enqueueSnackbar(error, { variant: 'error' });
  }, [error, enqueueSnackbar]);

  const openCreate = () => { setEditing(null); setFormOpen(true); };
  const openEdit = (c: Category) => { setEditing(c); setFormOpen(true); };

  const handleDelete = async () => {
    if (!toDelete) return;
    setDeleting(true);
    try {
      await categoriesApi.remove(toDelete.id);
      enqueueSnackbar('Categoria excluída', { variant: 'success' });
      setToDelete(null);
      reload();
    } catch (err) {
      enqueueSnackbar(err instanceof Error ? err.message : 'Erro ao excluir categoria', { variant: 'error' });
    } finally {
      setDeleting(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>Categorias</Typography>

      <Toolbar disableGutters>
        <Box sx={{ flexGrow: 1 }} />
        <Button onClick={openCreate} startIcon={<Add />}>Nova categoria</Button>
      </Toolbar>

      <TableContainer component={Paper} sx={{ mt: 2 }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Nome</TableCell>
              <TableCell>Criado em</TableCell>
              <TableCell align="right">Ações</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading && (
              <TableRow>
                <TableCell colSpan={3} align="center"><CircularProgress sx={{ my: 3 }} /></TableCell>
              </TableRow>
            )}
            {!loading && categories.length === 0 && (
              <TableRow>
                <TableCell colSpan={3} align="center">
                  <Typography color="text.secondary" sx={{ py: 3 }}>Nenhuma categoria encontrada.</Typography>
                </TableCell>
              </TableRow>
            )}
            {categories.map((c) => (
              <TableRow key={c.id} hover>
                <TableCell>{c.name}</TableCell>
                <TableCell>{formatDate(c.createdAt)}</TableCell>
                <TableCell align="right">
                  <IconButton onClick={() => openEdit(c)} color="primary"><Edit /></IconButton>
                  <IconButton onClick={() => setToDelete(c)} color="error"><Delete /></IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <CategoryFormDialog
        open={formOpen}
        onClose={() => setFormOpen(false)}
        onSaved={reload}
        category={editing}
      />

      <ConfirmDialog
        open={Boolean(toDelete)}
        title="Excluir categoria"
        description={`Deseja realmente excluir "${toDelete?.name}"? Produtos vinculados podem impedir a exclusão.`}
        confirmText="Excluir"
        onConfirm={handleDelete}
        onClose={() => setToDelete(null)}
        loading={deleting}
      />
    </Box>
  );
}