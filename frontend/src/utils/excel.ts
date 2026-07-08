import * as XLSX from 'xlsx';

export interface ExcelColumn<T> {
  header: string;
  // Largura sugerida em caracteres (wch do SheetJS).
  width?: number;
  // Formata o valor da célula. Recebe o valor original e retorna string.
  format?: (value: T) => string | number;
  // Extrai o valor da linha. Se ausente, usa a chave `key` no objeto.
  key?: keyof T;
  // Quando true, interpreta o valor como data e formata como dd/mm/yyyy hh:mm.
  asDate?: boolean;
}

export interface ExportOptions<T> {
  filename: string;
  sheetName?: string;
  columns: ExcelColumn<T>[];
  rows: T[];
}

const HEADER_FILL = {
  patternType: 'solid',
  fgColor: { rgb: 'FF8B5CF6' }, // roxo padrão do sistema
} as const;

const HEADER_FONT = {
  bold: true,
  color: { rgb: 'FFFFFFFF' },
} as const;

function formatDate(value: string): string {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value));
}

export function exportToExcel<T>({ filename, sheetName = 'Dados', columns, rows }: ExportOptions<T>): void {
  const aoa: unknown[][] = [];

  // Cabeçalho
  aoa.push(columns.map((c) => c.header));

  // Linhas
  for (const row of rows) {
    const line = columns.map((col) => {
      const raw = col.key ? (row as Record<string, unknown>)[col.key as string] : undefined;
      if (col.asDate && typeof raw === 'string') return formatDate(raw);
      if (col.format) return col.format(row);
      return raw as unknown;
    });
    aoa.push(line);
  }

  const worksheet = XLSX.utils.aoa_to_sheet(aoa);

  // Larguras de coluna
  worksheet['!cols'] = columns.map((c) => ({ wch: c.width ?? 18 }));

  // Estilo do cabeçalho
  for (let colIndex = 0; colIndex < columns.length; colIndex++) {
    const cellRef = XLSX.utils.encode_cell({ r: 0, c: colIndex });
    const cell = worksheet[cellRef];
    if (cell) {
      (cell as XLSX.CellObject).s = {
        fill: HEADER_FILL,
        font: HEADER_FONT,
        alignment: { vertical: 'center' },
      };
    }
  }

  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, sheetName);

  const arrayBuffer = XLSX.write(workbook, { type: 'array', bookType: 'xlsx' });
  const blob = new Blob([arrayBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename.endsWith('.xlsx') ? filename : `${filename}.xlsx`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

export function todayFilename(prefix: string): string {
  const now = new Date();
  const yyyy = now.getFullYear();
  const mm = String(now.getMonth() + 1).padStart(2, '0');
  const dd = String(now.getDate()).padStart(2, '0');
  return `${prefix}_${yyyy}-${mm}-${dd}.xlsx`;
}