import { useState, type ReactNode } from 'react';
import { AppBar, Avatar, Box, Drawer, IconButton, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Toolbar, Typography, useTheme } from '@mui/material';
import MenuIcon from '@mui/icons-material/MenuRounded';
import Inventory2Icon from '@mui/icons-material/Inventory2Rounded';
import CategoryIcon from '@mui/icons-material/CategoryRounded';
import { Link, useLocation } from 'react-router-dom';
import { ThemeToggle } from './ThemeToggle';
import { Footer } from './Footer';
import { ScrollToTop } from './ScrollToTop';
import { BRAND_GRADIENT } from '../theme';

const drawerWidth = 260;

const navItems = [
  { to: '/products', label: 'Produtos', icon: <Inventory2Icon /> },
  { to: '/categories', label: 'Categorias', icon: <CategoryIcon /> },
];

export function Layout({ children }: { children: ReactNode }) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const location = useLocation();
  const theme = useTheme();
  const isDark = theme.palette.mode === 'dark';

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Toolbar sx={{ gap: 1.5, px: 2.5 }}>
        <Avatar
          sx={{
            width: 36,
            height: 36,
            background: BRAND_GRADIENT,
            boxShadow: '0 4px 12px rgba(99,102,241,0.35)',
          }}
        >
          <Inventory2Icon fontSize="small" />
        </Avatar>
        <Box>
          <Typography variant="subtitle1" sx={{ fontWeight: 700, lineHeight: 1.1 }}>Product</Typography>
          <Typography variant="caption" color="text.secondary" sx={{ lineHeight: 1.1 }}>Management</Typography>
        </Box>
      </Toolbar>
      <List sx={{ px: 0, py: 1, flex: 1 }}>
        {navItems.map((item) => {
          const selected = location.pathname.startsWith(item.to);
          return (
            <ListItem key={item.to} disablePadding>
              <ListItemButton
                component={Link}
                to={item.to}
                selected={selected}
                onClick={() => setMobileOpen(false)}
                sx={{
                  '&.Mui-selected': {
                    background: isDark ? 'rgba(129,140,248,0.16)' : 'rgba(99,102,241,0.10)',
                    color: 'primary.main',
                    '& .MuiListItemIcon-root': { color: 'primary.main' },
                  },
                  '&.Mui-selected:hover': { background: isDark ? 'rgba(129,140,248,0.22)' : 'rgba(99,102,241,0.14)' },
                }}
              >
                <ListItemIcon sx={{ minWidth: 40, color: selected ? 'primary.main' : 'text.secondary' }}>{item.icon}</ListItemIcon>
                <ListItemText primary={item.label} primaryTypographyProps={{ fontWeight: selected ? 600 : 500 }} />
              </ListItemButton>
            </ListItem>
          );
        })}
      </List>
      <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
        <Typography variant="caption" color="text.secondary">v1.0 • Spring Boot + React</Typography>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          bgcolor: isDark ? 'rgba(15,17,23,0.72)' : 'rgba(255,255,255,0.72)',
          color: 'text.primary',
          backdropFilter: 'blur(12px)',
          WebkitBackdropFilter: 'blur(12px)',
          zIndex: (t) => t.zIndex.drawer + 1,
        }}
      >
        <Toolbar sx={{ gap: 1 }}>
          <IconButton color="inherit" edge="start" onClick={() => setMobileOpen(!mobileOpen)} sx={{ mr: 1, display: { sm: 'none' } }}>
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap sx={{ flexGrow: 1, fontWeight: 700 }}>
            {navItems.find((n) => location.pathname.startsWith(n.to))?.label ?? 'Product Management'}
          </Typography>
          <ThemeToggle />
        </Toolbar>
      </AppBar>
      <Box component="nav" sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}>
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={() => setMobileOpen(false)}
          ModalProps={{ keepMounted: true }}
          sx={{ display: { xs: 'block', sm: 'none' }, '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' } }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{ display: { xs: 'none', sm: 'block' }, '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box' } }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      <Box component="main" sx={{ flexGrow: 1, p: { xs: 2, sm: 3, md: 4 }, width: { sm: `calc(100% - ${drawerWidth}px)` }, maxWidth: 1400, mx: 'auto', display: 'flex', flexDirection: 'column' }}>
        <Toolbar />
        {children}
        <Footer />
      </Box>
      <ScrollToTop />
    </Box>
  );
}