import { request } from './apiClient';

async function login({ email, password }) {
  return request('/api/users/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
}

export { login };
