import { http } from './http';
import type { Product, ProductRequest, ProductPatch } from '../types';

export const productsApi = {
  list(): Promise<Product[]> {
    return http.get<Product[]>('/products').then((r) => r.data);
  },
  getByCategory(categoryId: string): Promise<Product[]> {
    return http.get<Product[]>(`/products/by-category/${categoryId}`).then((r) => r.data);
  },
  getByName(name: string): Promise<Product> {
    return http.get<Product>(`/products/by-name/${encodeURIComponent(name)}`).then((r) => r.data);
  },
  get(id: string): Promise<Product> {
    return http.get<Product>(`/products/${id}`).then((r) => r.data);
  },
  create(payload: ProductRequest): Promise<Product> {
    return http.post<Product>('/products', payload).then((r) => r.data);
  },
  update(id: string, payload: ProductRequest): Promise<Product> {
    return http.put<Product>(`/products/${id}`, payload).then((r) => r.data);
  },
  patch(id: string, payload: ProductPatch): Promise<Product> {
    return http.patch<Product>(`/products/${id}`, payload).then((r) => r.data);
  },
  remove(id: string): Promise<void> {
    return http.delete(`/products/${id}`).then(() => undefined);
  },
};