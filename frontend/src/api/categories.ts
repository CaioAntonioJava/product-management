import { http } from './http';
import type { Category, CategoryRequest } from '../types';

export const categoriesApi = {
  list(): Promise<Category[]> {
    return http.get<Category[]>('/categories').then((r) => r.data);
  },
  create(payload: CategoryRequest): Promise<Category> {
    return http.post<Category>('/categories', payload).then((r) => r.data);
  },
  update(id: string, payload: CategoryRequest): Promise<Category> {
    return http.put<Category>(`/categories/${id}`, payload).then((r) => r.data);
  },
  remove(id: string): Promise<void> {
    return http.delete(`/categories/${id}`).then(() => undefined);
  },
};