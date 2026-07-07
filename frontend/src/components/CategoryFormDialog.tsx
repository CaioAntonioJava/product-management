import { useEffect, useState, type FormEvent } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, TextField, Typography } from '@mui/material';
import { useSnackbar } from 'notistack';
import { categoriesApi } from '../api/categories';
import type { Category, CategoryRequest } from '../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onSaved: () => void;
  category?: Category | null;
}

export function CategoryFormDialog({ open, onClose, onSaved, category }: Props) {
  const { enqueueSnackbar } = useSnackbar();
  const isEdit = Boolean(category);

  const [name, setName] = useState(category?.name ?? '');
  const [fieldError, setFieldError] = useState('');
  const [saving, setSaving] = useState(false);

  // Reseta o formulário sempre que o diálogo abre (limpa texto do cadastro anterior).
  useEffect(() => {
    if (open) {
      setName(category?.name ?? '');
      setFieldError('');
    }
  }, [open, category]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setFieldError('');

    const payload: CategoryRequest = { name: name.trim() };

    try {
      if (isEdit && category) {
        await categoriesApi.update(category.id, payload);
        enqueueSnackbar('Categoria atualizada com sucesso', { variant: 'success' });
      } else {
        await categoriesApi.create(payload);
        enqueueSnackbar('Categoria criada com sucesso', { variant: 'success' });
      }
      onSaved();
      onClose();
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao salvar categoria';
      const details = (err as { details?: Record<string, string> }).details;
      if (details?.name) setFieldError(details.name);
      enqueueSnackbar(message, { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <form onSubmit={handleSubmit}>
        <DialogTitle sx={{ pb: 1 }}>
          <Typography variant="h6" component="span" sx={{ fontWeight: 700 }}>{isEdit ? 'Editar categoria' : 'Nova categoria'}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ display: 'block' }}>
            {isEdit ? 'Atualize o nome da categoria' : 'Dê um nome à nova categoria'}
          </Typography>
        </DialogTitle>
        <Divider />
        <DialogContent sx={{ py: 3 }}>
          <TextField
            autoFocus
            label="Nome"
            value={name}
            onChange={(e) => { setName(e.target.value); setFieldError(''); }}
            error={Boolean(fieldError)}
            helperText={fieldError}
            required
            fullWidth
          />
        </DialogContent>
        <Divider />
        <DialogActions sx={{ p: 2.5, gap: 1 }}>
          <Button onClick={onClose} variant="text" color="inherit">Cancelar</Button>
          <Button type="submit" disabled={saving}>{saving ? 'Salvando…' : 'Salvar'}</Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}