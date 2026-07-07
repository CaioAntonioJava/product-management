import { useState, type FormEvent } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
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
        <DialogTitle>{isEdit ? 'Editar categoria' : 'Nova categoria'}</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            label="Nome"
            value={name}
            onChange={(e) => { setName(e.target.value); setFieldError(''); }}
            error={Boolean(fieldError)}
            helperText={fieldError}
            required
            fullWidth
            sx={{ mt: 1 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="inherit">Cancelar</Button>
          <Button type="submit" disabled={saving}>{saving ? 'Salvando…' : 'Salvar'}</Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}