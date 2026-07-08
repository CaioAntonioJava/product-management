import { useEffect, useState, type FormEvent } from 'react';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Divider, MenuItem, TextField, Typography } from '@mui/material';
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

// Formata um número para exibição no padrão pt-BR (vírgula decimal), sem agrupar milhares.
function formatPriceForInput(value?: number): string {
  if (value === undefined || value === null) return '';
  const parts = value.toFixed(2).split('.');
  return `${parts[0]},${parts[1]}`;
}

// Normaliza o texto digitado pelo usuário para que sempre tenha os centavos.
// Entradas válidas:
//   ""            -> ""
//   "19"          -> "19,00"
//   "19,9"        -> "19,90"
//   "19,99"       -> "19,99"
//   "19.99"       -> "19,99"
//   ",50"         -> "0,50"
//   "1.234,56"    -> "1234,56"
// Caso não consiga extrair um número, devolve o texto original para que a
// validação do backend exiba a mensagem de erro apropriada.
function normalizePriceCents(raw: string): string {
  const trimmed = raw.trim();
  if (!trimmed) return '';

  // Remove separador de milhar (ponto) quando há vírgula como decimal: "1.234,56"
  let cleaned = trimmed;
  const hasComma = cleaned.includes(',');
  if (hasComma) {
    cleaned = cleaned.replace(/\./g, '');
  }
  // Troca vírgula por ponto para o parse e remove tudo que não for dígito/pono/sinal.
  cleaned = cleaned.replace(',', '.').replace(/[^\d.]/g, '');

  const num = Number(cleaned);
  if (!Number.isFinite(num)) return raw;

  return num.toFixed(2).replace('.', ',');
}

export function ProductFormModal({ open, onClose, onSaved, categories, product }: Props) {
  const { enqueueSnackbar } = useSnackbar();
  const isEdit = Boolean(product);

  const [form, setForm] = useState<FormState>({
    name: product?.name ?? '',
    price: formatPriceForInput(product?.price),
    stockQuantity: product?.stockQuantity?.toString() ?? '',
    description: product?.description ?? '',
    categoryId: product?.categoryId ?? '',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);

  // Reseta o formulário sempre que o diálogo abre (limpa texto do cadastro anterior).
  useEffect(() => {
    if (open) {
      setForm({
        name: product?.name ?? '',
        price: formatPriceForInput(product?.price),
        stockQuantity: product?.stockQuantity?.toString() ?? '',
        description: product?.description ?? '',
        categoryId: product?.categoryId ?? '',
      });
      setFieldErrors({});
    }
  }, [open, product]);

  const update = (field: keyof FormState, value: string) => {
    setForm((f) => ({ ...f, [field]: value }));
    setFieldErrors((e) => ({ ...e, [field]: '' }));
  };

  // Ao sair do campo de preço, completa com ",00" caso o usuário não tenha digitado os centavos.
  const handlePriceBlur = () => {
    setForm((f) => ({ ...f, price: normalizePriceCents(f.price) }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setFieldErrors({});

    const payload: ProductRequest = {
      name: form.name.trim(),
      price: form.price.trim(),
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
        <DialogTitle sx={{ pb: 1 }}>
          <Typography variant="h6" component="span" sx={{ fontWeight: 700 }}>{isEdit ? 'Editar produto' : 'Novo produto'}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ display: 'block' }}>
            {isEdit ? 'Atualize os dados do produto' : 'Preencha os dados do novo produto'}
          </Typography>
        </DialogTitle>
        <Divider />
        <DialogContent sx={{ py: 3 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
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
                placeholder="R$"
                inputMode="decimal"
                value={form.price}
                onChange={(e) => update('price', e.target.value)}
                onBlur={handlePriceBlur}
                error={Boolean(fieldErrors.price)}
                helperText={fieldErrors.price ?? 'Use vírgula para decimais (ex.: 19,99)'}
                required
                fullWidth
              />
              <TextField
                label="Estoque"
                inputMode="numeric"
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
        <Divider />
        <DialogActions sx={{ p: 2.5, gap: 1 }}>
          <Button onClick={onClose} variant="text" color="inherit">Cancelar</Button>
          <Button type="submit" disabled={saving}>{saving ? 'Salvando…' : 'Salvar'}</Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}