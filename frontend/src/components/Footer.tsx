import { Box, Stack, Typography, Link as MuiLink } from '@mui/material';
import PersonIcon from '@mui/icons-material/PersonRounded';
import WhatsAppIcon from '@mui/icons-material/WhatsApp';
import EmailIcon from '@mui/icons-material/EmailRounded';

const WHATSAPP_NUMBER = '5519982465781';
const EMAIL = 'caioantonio.dev@gmail.com';

export function Footer() {
  return (
    <Box
      component="footer"
      sx={{
        mt: 6,
        mx: { xs: 0, sm: 0 },
        mb: { xs: 2, sm: 3 },
        borderRadius: 3,
        border: 1,
        borderColor: 'divider',
        bgcolor: 'background.paper',
        boxShadow: 1,
        overflow: 'hidden',
      }}
    >
      <Box
        sx={{
          px: { xs: 2.5, sm: 4 },
          py: { xs: 2.5, sm: 3 },
          display: 'flex',
          flexDirection: { xs: 'column', md: 'row' },
          alignItems: { xs: 'flex-start', md: 'center' },
          justifyContent: 'space-between',
          gap: { xs: 2.5, md: 4 },
        }}
      >
        <Stack
          direction="row"
          spacing={1.5}
          alignItems="center"
          sx={{ flex: { md: '0 0 auto' } }}
        >
          <Box
            sx={{
              width: 40,
              height: 40,
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              bgcolor: 'primary.main',
              color: 'primary.contrastText',
              flexShrink: 0,
            }}
          >
            <PersonIcon fontSize="small" />
          </Box>
          <Box>
            <Typography variant="body2" sx={{ fontWeight: 600, lineHeight: 1.2 }}>
              Desenvolvido por: Caio Henrique Antonio
            </Typography>
            <Typography variant="caption" color="text.secondary" sx={{ lineHeight: 1.2 }}>
              Product Management • Full Stack Developer
            </Typography>
          </Box>
        </Stack>

        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={{ xs: 1.5, sm: 3 }}
          divider={
            <Box
              sx={{
                display: { xs: 'none', sm: 'block' },
                width: '1px',
                alignSelf: 'stretch',
                bgcolor: 'divider',
              }}
            />
          }
          alignItems={{ xs: 'flex-start', sm: 'center' }}
          sx={{ flex: { md: 1 }, justifyContent: { md: 'flex-end' } }}
        >
          <MuiLink
            href={`https://wa.me/${WHATSAPP_NUMBER}`}
            target="_blank"
            rel="noopener noreferrer"
            underline="none"
            sx={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 1,
              color: 'text.primary',
              transition: 'color 0.2s ease',
              '&:hover': { color: 'success.main' },
            }}
          >
            <Box
              sx={{
                width: 32,
                height: 32,
                borderRadius: '50%',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'success.main',
                color: '#fff',
                flexShrink: 0,
              }}
            >
              <WhatsAppIcon sx={{ fontSize: 18 }} />
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', lineHeight: 1.1 }}>
                Whatsapp
              </Typography>
              <Typography variant="body2" sx={{ fontWeight: 500, lineHeight: 1.2 }}>
                (19) 98246-5781
              </Typography>
            </Box>
          </MuiLink>

          <MuiLink
            href={`mailto:${EMAIL}`}
            underline="none"
            sx={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: 1,
              color: 'text.primary',
              transition: 'color 0.2s ease',
              '&:hover': { color: 'primary.main' },
            }}
          >
            <Box
              sx={{
                width: 32,
                height: 32,
                borderRadius: '50%',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'primary.main',
                color: 'primary.contrastText',
                flexShrink: 0,
              }}
            >
              <EmailIcon sx={{ fontSize: 18 }} />
            </Box>
            <Box>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', lineHeight: 1.1 }}>
                Email
              </Typography>
              <Typography variant="body2" sx={{ fontWeight: 500, lineHeight: 1.2 }}>
                caioantonio.dev@gmail.com
              </Typography>
            </Box>
          </MuiLink>
        </Stack>
      </Box>
    </Box>
  );
}