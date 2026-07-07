export interface Product {
  id: string;
  name: string;
  price: number;
  stockQuantity: number;
  description?: string | null;
  categoryId: string;
  categoryName: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductRequest {
  name: string;
  price: number;
  stockQuantity: number;
  description?: string;
  categoryId: string;
}

export interface ProductPatch {
  name?: string;
  price?: number;
  stockQuantity?: number;
  description?: string;
  categoryId?: string;
}

export interface Category {
  id: string;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryRequest {
  name: string;
}

export interface ApiError {
  timestamp?: string;
  status?: number;
  error?: string;
  message?: string;
  details?: Record<string, string>;
}

export class ApiException extends Error {
  details?: Record<string, string>;
  status?: number;

  constructor(message: string, details?: Record<string, string>, status?: number) {
    super(message);
    this.name = 'ApiException';
    this.details = details;
    this.status = status;
  }
}