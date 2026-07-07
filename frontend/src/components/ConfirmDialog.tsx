import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Divider, IconButton, Typography } from '@mui/material';
import WarningRoundedIcon from '@mui/icons-material/WarningRounded';
import { Box } from '@mui/material';

interface Props {
  open: boolean;
  title?: string;
  description?: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onClose: () => void;
  loading?: boolean;
}

export function ConfirmDialog({
  open,
  title = 'Confirmar ação',
  description = 'Tem certeza que deseja continuar?',
  confirmText = 'Confirmar',
  cancelText = 'Cancelar',
  onConfirm,
  onClose,
  loading,
}: Props) {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="xs">
      <DialogTitle sx={{ display: 'flex', alignItems: 'center', gap: 1.5, pb: 1 }}>
        <IconButton
          disableRipple
          size="small"
          sx={{
            bgcolor: 'error.main',
            color: '#fff',
            '&:hover': { bgcolor: 'error.dark' },
            cursor: 'default',
          }}
        >
          <WarningRoundedIcon fontSize="small" />
        </IconButton>
        <Typography variant="h6" component="span" sx={{ fontWeight: 700 }}>{title}</Typography>
      </DialogTitle>
      <Divider />
      <DialogContent sx={{ py: 3 }}>
        <DialogContentText>{description}</DialogContentText>
      </DialogContent>
      <Box sx={{ px: 3, pb: 1 }}>
        <Divider />
      </Box>
      <DialogActions sx={{ p: 2.5, gap: 1 }}>
        <Button onClick={onClose} variant="text" color="inherit" disabled={loading}>{cancelText}</Button>
        <Button onClick={onConfirm} color="error" disabled={loading}>{loading ? 'Excluindo…' : confirmText}</Button>
      </DialogActions>
    </Dialog>
  );
}