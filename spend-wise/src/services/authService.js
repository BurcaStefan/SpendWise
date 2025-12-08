import { request } from './apiClient';

async function login({ email, password }) {
  return request('/api/users/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
}

function getUserIdFromToken() {
  const token = localStorage.getItem('token');
  if (!token) return null;
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub || payload.userId || payload.id || payload.user_id || null;
  } catch (err) {
    console.error('Failed to decode token:', err);
    return null;
  }
}

export { login, getUserIdFromToken };
