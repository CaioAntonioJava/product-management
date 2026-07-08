import { useEffect, useMemo, useState } from 'react';
import {
  Avatar, Box, Button, Grid, IconButton, Paper, Skeleton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Toolbar, Typography,
} from '@mui/material';
import { Add, Delete, Edit, CategoryRounded, FileDownloadRounded } from '@mui/icons-material';
import { motion } from 'framer-motion';
import { useSnackbar } from 'notistack';
import { useCategories } from '../hooks/useCategories';
import { categoriesApi } from '../api/categories';
import { CategoryFormDialog } from '../components/CategoryFormDialog';
import { ConfirmDialog } from '../components/ConfirmDialog';
import { EmptyState } from '../components/EmptyState';
import { StatCard } from '../components/StatCard';
import { PageTransition } from '../components/PageTransition';
import { BRAND_GRADIENT } from '../theme';
import { exportCategoriesToExcel } from '../utils/exports';
import type { Category } from '../types';

function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
}

function initials(name: string) {
  return name.slice(0, 1).toUpperCase();
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

  const total = useMemo(() => categories.length, [categories]);

  const openCreate = () => { setEditing(null); setFormOpen(true); };
  const openEdit = (c: Category) => { setEditing(c); setFormOpen(true); };

  const handleExport = () => {
    try {
      exportCategoriesToExcel(categories);
      enqueueSnackbar(`${categories.length} categoria(s) exportada(s)`, { variant: 'success' });
    } catch (err) {
      enqueueSnackbar(err instanceof Error ? err.message : 'Erro ao exportar', { variant: 'error' });
    }
  };

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
    <PageTransition>
      <Box>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>Categorias</Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={4}>
            <StatCard
              label="Total de categorias"
              value={total}
              icon={<CategoryRounded />}
              gradient="linear-gradient(135deg, #8b5cf6, #d946ef)"
              delay={0}
            />
          </Grid>
        </Grid>

        <Toolbar disableGutters sx={{ mb: 2, gap: 1.5 }}>
          <Box sx={{ flexGrow: 1 }} />
          <Button
            onClick={handleExport}
            startIcon={<FileDownloadRounded />}
            variant="outlined"
            color="inherit"
            disabled={loading || categories.length === 0}
          >
            Exportar Excel
          </Button>
          <Button
            onClick={openCreate}
            startIcon={<Add />}
            sx={{ backgroundColor: '#8b5cf6', color: '#fff', '&:hover': { backgroundColor: '#7c3aed' } }}
          >
            Nova categoria
          </Button>
        </Toolbar>

        <TableContainer component={Paper} sx={{ borderRadius: 3, overflow: 'hidden' }}>
          <Table>
            <TableHead>
              <TableRow sx={{ bgcolor: 'action.hover' }}>
                <TableCell>Nome</TableCell>
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
                  </TableRow>
                ))
              )}
              {!loading && categories.length === 0 && (
                <TableRow>
                  <TableCell colSpan={3} sx={{ borderBottom: 'none' }}>
                    <EmptyState
                      icon={<CategoryRounded />}
                      title="Nenhuma categoria encontrada"
                      description="Crie sua primeira categoria para organizar seus produtos."
                      action={<Button onClick={openCreate} startIcon={<Add />}>Nova categoria</Button>}
                    />
                  </TableCell>
                </TableRow>
              )}
              {!loading && categories.map((c, index) => (
                <TableRow
                  key={c.id}
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
                        {initials(c.name)}
                      </Avatar>
                      <Typography fontWeight={600}>{c.name}</Typography>
                    </Box>
                  </TableCell>
                  <TableCell>{formatDate(c.createdAt)}</TableCell>
                  <TableCell align="right">
                    <IconButton onClick={() => openEdit(c)} color="primary" size="small"><Edit fontSize="small" /></IconButton>
                    <IconButton onClick={() => setToDelete(c)} color="error" size="small"><Delete fontSize="small" /></IconButton>
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
    </PageTransition>
  );
}