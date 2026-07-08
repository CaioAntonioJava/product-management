import { useEffect, useState } from 'react';
import { Box, Fade, Tooltip } from '@mui/material';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUpRounded';

export function ScrollToTop() {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      const scrolled = window.scrollY || document.documentElement.scrollTop;
      const totalHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
      const atBottom = totalHeight > 0 && scrolled >= totalHeight - 8;
      setVisible(atBottom);
    };

    handleScroll();
    window.addEventListener('scroll', handleScroll, { passive: true });
    window.addEventListener('resize', handleScroll);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      window.removeEventListener('resize', handleScroll);
    };
  }, []);

  const handleClick = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <Fade in={visible} timeout={{ enter: 250, exit: 200 }} unmountOnExit>
      <Tooltip title="Voltar ao topo" placement="left">
        <Box
          component="button"
          type="button"
          aria-label="Voltar ao topo"
          onClick={handleClick}
          sx={{
            position: 'fixed',
            right: { xs: 16, sm: 24, md: 32 },
            bottom: { xs: 16, sm: 24, md: 32 },
            width: 48,
            height: 48,
            border: 'none',
            cursor: 'pointer',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'primary.contrastText',
            background: 'linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%)',
            boxShadow: '0 8px 24px rgba(99,102,241,0.45)',
            borderRadius: '50%',
            transition: 'transform 0.2s ease, box-shadow 0.2s ease',
            zIndex: (t) => t.zIndex.tooltip,
            '&:hover': {
              transform: 'translateY(-3px)',
              boxShadow: '0 12px 28px rgba(99,102,241,0.55)',
            },
            '&:active': { transform: 'translateY(0)' },
            '&:focus-visible': {
              outline: '2px solid',
              outlineColor: 'primary.main',
              outlineOffset: 3,
            },
          }}
        >
          <KeyboardArrowUpIcon sx={{ fontSize: 28 }} />
        </Box>
      </Tooltip>
    </Fade>
  );
}