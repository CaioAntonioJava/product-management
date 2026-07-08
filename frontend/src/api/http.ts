import axios from 'axios';
import { ApiException, type ApiError } from '../types';

export const http = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'}`,
  headers: { 'Content-Type': 'application/json' },
});

function toApiError(data: unknown): ApiError {
  if (data && typeof data === 'object') {
    return data as ApiError;
  }
  return { message: String(data) };
}

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const apiError = toApiError(error.response.data);
      const message = apiError.message ?? `Erro ${error.response.status}`;
      throw new ApiException(message, apiError.details, error.response.status);
    }
    if (error.request) {
      throw new ApiException('Sem resposta do servidor. Verifique se o backend está em execução.');
    }
    throw new ApiException(error.message ?? 'Erro desconhecido');
  },
);