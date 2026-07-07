import { IconButton, Tooltip } from '@mui/material';
import { motion } from 'framer-motion';
import DarkModeIcon from '@mui/icons-material/DarkModeRounded';
import LightModeIcon from '@mui/icons-material/LightModeRounded';
import { useThemeMode } from '../context/ThemeContext';

export function ThemeToggle() {
  const { mode, toggleTheme } = useThemeMode();
  const isDark = mode === 'dark';

  return (
    <Tooltip title={isDark ? 'Tema claro' : 'Tema escuro'}>
      <IconButton onClick={toggleTheme} color="inherit" size="small">
        <motion.span
          key={mode}
          initial={{ rotate: -90, opacity: 0, scale: 0.6 }}
          animate={{ rotate: 0, opacity: 1, scale: 1 }}
          transition={{ duration: 0.25 }}
          style={{ display: 'inline-flex' }}
        >
          {isDark ? <LightModeIcon /> : <DarkModeIcon />}
        </motion.span>
      </IconButton>
    </Tooltip>
  );
}