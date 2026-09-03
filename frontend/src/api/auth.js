import { api, clearAuth, getAuth, resetOwnerToken, setAuth } from './client'

export function register({ phone, password, nickname }) {
  return api('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ phone, password, nickname }),
  }).then((data) => {
    setAuth(data)
    return data
  })
}

export function login({ phone, password }) {
  return api('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ phone, password }),
  }).then((data) => {
    setAuth(data)
    return data
  })
}

export async function logout() {
  try {
    if (getAuth()?.token) {
      await api('/api/auth/logout', { method: 'POST' })
    }
  } catch {
    // ignore
  }
  clearAuth()
  resetOwnerToken()
}

export function syncCloud() {
  return api('/api/cloud/sync', { method: 'POST' })
}

export function updateNickname(nickname) {
  return api('/api/auth/nickname', {
    method: 'PUT',
    body: JSON.stringify({ nickname }),
  }).then((data) => {
    const auth = getAuth()
    if (auth) {
      setAuth({ ...auth, nickname: data.nickname })
    }
    return data
  })
}
