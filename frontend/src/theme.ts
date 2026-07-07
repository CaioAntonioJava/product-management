import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#1976d2' },
    secondary: { main: '#dc004e' },
  },
  shape: { borderRadius: 8 },
  components: {
    MuiButton: {
      defaultProps: { variant: 'contained' },
    },
  },
});