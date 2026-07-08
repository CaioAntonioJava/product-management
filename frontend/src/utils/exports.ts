import { exportToExcel, todayFilename, type ExcelColumn } from './excel';
import type { Product, Category } from '../types';

const currencyFormatter = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

const productColumns: ExcelColumn<Product>[] = [
  { header: 'ID', key: 'id', width: 38 },
  { header: 'Nome', key: 'name', width: 30 },
  { header: 'Preço', key: 'price', width: 16, format: (p) => currencyFormatter.format(p.price) },
  { header: 'Estoque', key: 'stockQuantity', width: 12 },
  { header: 'Categoria', key: 'categoryName', width: 20 },
  { header: 'Descrição', key: 'description', width: 40 },
  { header: 'Criado em', key: 'createdAt', width: 18, asDate: true },
  { header: 'Atualizado em', key: 'updatedAt', width: 18, asDate: true },
];

const categoryColumns: ExcelColumn<Category>[] = [
  { header: 'ID', key: 'id', width: 38 },
  { header: 'Nome', key: 'name', width: 30 },
  { header: 'Criado em', key: 'createdAt', width: 18, asDate: true },
  { header: 'Atualizado em', key: 'updatedAt', width: 18, asDate: true },
];

export function exportProductsToExcel(products: Product[]): void {
  exportToExcel({
    filename: todayFilename('produtos'),
    sheetName: 'Produtos',
    columns: productColumns,
    rows: products,
  });
}

export function exportCategoriesToExcel(categories: Category[]): void {
  exportToExcel({
    filename: todayFilename('categorias'),
    sheetName: 'Categorias',
    columns: categoryColumns,
    rows: categories,
  });
}