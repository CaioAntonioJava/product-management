import { useState, type FormEvent } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, MenuItem, TextField, Box } from '@mui/material';
import { useSnackbar } from 'notistack';
import { productsApi } from '../api/products';
import type { Category, Product, ProductRequest } from '../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onSaved: () => void;
  categories: Category[];
  product?: Product | null;
}

interface FormState {
  name: string;
  price: string;
  stockQuantity: string;
  description: string;
  categoryId: string;
}

export function ProductFormModal({ open, onClose, onSaved, categories, product }: Props) {
  const { enqueueSnackbar } = useSnackbar();
  const isEdit = Boolean(product);

  const [form, setForm] = useState<FormState>({
    name: product?.name ?? '',
    price: product?.price?.toString() ?? '',
    stockQuantity: product?.stockQuantity?.toString() ?? '',
    description: product?.description ?? '',
    categoryId: product?.categoryId ?? '',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  const update = (field: keyof FormState, value: string) => {
    setForm((f) => ({ ...f, [field]: value }));
    setFieldErrors((e) => ({ ...e, [field]: '' }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setFieldErrors({});

    const payload: ProductRequest = {
      name: form.name.trim(),
      price: Number(form.price),
      stockQuantity: Number(form.stockQuantity),
      description: form.description.trim() || undefined,
      categoryId: form.categoryId,
    };

    try {
      if (isEdit && product) {
        await productsApi.update(product.id, payload);
        enqueueSnackbar('Produto atualizado com sucesso', { variant: 'success' });
      } else {
        await productsApi.create(payload);
        enqueueSnackbar('Produto criado com sucesso', { variant: 'success' });
      }
      onSaved();
      onClose();
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erro ao salvar produto';
      const details = (err as { details?: Record<string, string> }).details;
      if (details) setFieldErrors(details);
      enqueueSnackbar(message, { variant: 'error' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <form onSubmit={handleSubmit}>
        <DialogTitle>{isEdit ? 'Editar produto' : 'Novo produto'}</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <TextField
              label="Nome"
              value={form.name}
              onChange={(e) => update('name', e.target.value)}
              error={Boolean(fieldErrors.name)}
              helperText={fieldErrors.name}
              required
              fullWidth
            />
            <Box sx={{ display: 'flex', gap: 2 }}>
              <TextField
                label="Preço"
                type="number"
                inputProps={{ step: '0.01', min: 0 }}
                value={form.price}
                onChange={(e) => update('price', e.target.value)}
                error={Boolean(fieldErrors.price)}
                helperText={fieldErrors.price}
                required
                fullWidth
              />
              <TextField
                label="Estoque"
                type="number"
                inputProps={{ step: '1', min: 0 }}
                value={form.stockQuantity}
                onChange={(e) => update('stockQuantity', e.target.value)}
                error={Boolean(fieldErrors.stockQuantity)}
                helperText={fieldErrors.stockQuantity}
                required
                fullWidth
              />
            </Box>
            <TextField
              label="Descrição"
              value={form.description}
              onChange={(e) => update('description', e.target.value)}
              error={Boolean(fieldErrors.description)}
              helperText={fieldErrors.description}
              multiline
              rows={3}
              fullWidth
            />
            <TextField
              select
              label="Categoria"
              value={form.categoryId}
              onChange={(e) => update('categoryId', e.target.value)}
              error={Boolean(fieldErrors.categoryId)}
              helperText={fieldErrors.categoryId ?? 'Selecione a categoria'}
              required
              fullWidth
            >
              {categories.length === 0 && <MenuItem value="" disabled>Nenhuma categoria cadastrada</MenuItem>}
              {categories.map((c) => (
                <MenuItem key={c.id} value={c.id}>{c.name}</MenuItem>
              ))}
            </TextField>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="inherit">Cancelar</Button>
          <Button type="submit" disabled={saving}>{saving ? 'Salvando…' : 'Salvar'}</Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}