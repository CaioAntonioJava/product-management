import { createTheme, type ThemeOptions, type Theme, type Shadows } from '@mui/material/styles';

export type ThemeMode = 'light' | 'dark';

export const BRAND_GRADIENT = 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)';
export const BRAND_GRADIENT_SOFT = 'linear-gradient(135deg, rgba(99,102,241,0.12) 0%, rgba(139,92,246,0.12) 100%)';

const lightPalette = {
  primary: { main: '#6366f1', light: '#818cf8', dark: '#4f46e5', contrastText: '#ffffff' },
  secondary: { main: '#8b5cf6', light: '#a78bfa', dark: '#7c3aed', contrastText: '#ffffff' },
  background: { default: '#f5f7fb', paper: '#ffffff' },
  text: { primary: '#0f172a', secondary: '#475569' },
  divider: 'rgba(15, 23, 42, 0.08)',
  success: { main: '#10b981' },
  warning: { main: '#f59e0b' },
  error: { main: '#ef4444' },
};

const darkPalette = {
  primary: { main: '#818cf8', light: '#a5b4fc', dark: '#6366f1', contrastText: '#0f1117' },
  secondary: { main: '#a78bfa', light: '#c4b5fd', dark: '#8b5cf6', contrastText: '#0f1117' },
  background: { default: '#0f1117', paper: '#171a23' },
  text: { primary: '#e5e7eb', secondary: '#9ca3af' },
  divider: 'rgba(255, 255, 255, 0.08)',
  success: { main: '#34d399' },
  warning: { main: '#fbbf24' },
  error: { main: '#f87171' },
};

function buildShadows(base: string[]): Shadows {
  const shadows: string[] = [...base];
  while (shadows.length < 25) shadows.push(base[base.length - 1]);
  return shadows as unknown as Shadows;
}

const softShadows = buildShadows([
  'none',
  '0 1px 2px rgba(15,23,42,0.06), 0 1px 3px rgba(15,23,42,0.04)',
  '0 2px 8px rgba(15,23,42,0.06), 0 1px 4px rgba(15,23,42,0.04)',
  '0 6px 16px rgba(15,23,42,0.08), 0 2px 6px rgba(15,23,42,0.04)',
  '0 10px 24px rgba(15,23,42,0.10), 0 4px 8px rgba(15,23,42,0.04)',
  '0 14px 32px rgba(15,23,42,0.12), 0 6px 12px rgba(15,23,42,0.04)',
  '0 16px 40px rgba(15,23,42,0.14), 0 8px 16px rgba(15,23,42,0.06)',
]);

const darkShadows = buildShadows([
  'none',
  '0 1px 2px rgba(0,0,0,0.4)',
  '0 2px 8px rgba(0,0,0,0.45)',
  '0 6px 16px rgba(0,0,0,0.5)',
  '0 10px 24px rgba(0,0,0,0.55)',
  '0 14px 32px rgba(0,0,0,0.6)',
  '0 16px 40px rgba(0,0,0,0.65)',
]);

function getDesignTokens(mode: ThemeMode): ThemeOptions {
  const palette = mode === 'light' ? lightPalette : darkPalette;
  const shadows = mode === 'light' ? softShadows : darkShadows;

  return {
    palette: {
      mode,
      ...palette,
    },
    shape: { borderRadius: 12 },
    shadows,
    typography: {
      fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
      h4: { fontWeight: 700, letterSpacing: '-0.02em' },
      h5: { fontWeight: 700, letterSpacing: '-0.01em' },
      h6: { fontWeight: 600 },
      subtitle1: { fontWeight: 600 },
      button: { textTransform: 'none', fontWeight: 600 },
    },
    components: {
      MuiCssBaseline: {
        styleOverrides: {
          body: {
            scrollbarColor: mode === 'dark' ? '#2a2e3a #0f1117' : '#cbd5e1 #f5f7fb',
            '&::-webkit-scrollbar, & *::-webkit-scrollbar': { width: 10, height: 10 },
            '&::-webkit-scrollbar-track, & *::-webkit-scrollbar-track': { background: 'transparent' },
            '&::-webkit-scrollbar-thumb, & *::-webkit-scrollbar-thumb': {
              backgroundColor: mode === 'dark' ? '#2a2e3a' : '#cbd5e1',
              borderRadius: 8,
              border: '2px solid transparent',
              backgroundClip: 'content-box',
            },
            '&::-webkit-scrollbar-thumb:hover': {
              backgroundColor: mode === 'dark' ? '#3a3f4f' : '#94a3b8',
            },
          },
        },
      },
      MuiButton: {
        defaultProps: { disableElevation: true },
        styleOverrides: {
          root: { borderRadius: 10, paddingInline: 18, transition: 'all 0.2s ease' },
          containedPrimary: {
            background: BRAND_GRADIENT,
            boxShadow: '0 4px 12px rgba(99,102,241,0.35)',
            '&:hover': { boxShadow: '0 6px 18px rgba(99,102,241,0.45)', transform: 'translateY(-1px)' },
          },
          containedSecondary: {
            background: BRAND_GRADIENT,
            boxShadow: '0 4px 12px rgba(139,92,246,0.35)',
            '&:hover': { boxShadow: '0 6px 18px rgba(139,92,246,0.45)' },
          },
        },
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: 'none',
          },
        },
      },
      MuiCard: {
        defaultProps: { elevation: 0 },
        styleOverrides: {
          root: {
            borderRadius: 16,
            border: `1px solid ${mode === 'dark' ? 'rgba(255,255,255,0.06)' : 'rgba(15,23,42,0.06)'}`,
          },
        },
      },
      MuiAppBar: {
        styleOverrides: {
          root: {
            boxShadow: 'none',
            borderBottom: `1px solid ${mode === 'dark' ? 'rgba(255,255,255,0.06)' : 'rgba(15,23,42,0.06)'}`,
          },
        },
      },
      MuiDrawer: {
        styleOverrides: {
          paper: {
            borderRight: `1px solid ${mode === 'dark' ? 'rgba(255,255,255,0.06)' : 'rgba(15,23,42,0.06)'}`,
          },
        },
      },
      MuiTextField: {
        defaultProps: { size: 'small' },
      },
      MuiOutlinedInput: {
        styleOverrides: {
          root: { borderRadius: 10 },
        },
      },
      MuiChip: {
        styleOverrides: {
          root: { fontWeight: 600 },
        },
      },
      MuiDialog: {
        styleOverrides: {
          paper: { borderRadius: 16 },
        },
      },
      MuiTableCell: {
        styleOverrides: {
          root: { borderColor: mode === 'dark' ? 'rgba(255,255,255,0.06)' : 'rgba(15,23,42,0.06)' },
          head: { fontWeight: 700 },
        },
      },
      MuiListItemButton: {
        styleOverrides: {
          root: { borderRadius: 10, margin: '4px 8px', paddingRight: 12 },
        },
      },
    },
  };
}

export function createAppTheme(mode: ThemeMode): Theme {
  return createTheme(getDesignTokens(mode));
}