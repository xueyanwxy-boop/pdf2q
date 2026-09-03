const AUTH_KEY = 'pdf2q_auth'
const OWNER_KEY = 'pdf2q_owner_token'

/** 生成 UUID；兼容非 localhost。 */
export function createOwnerToken() {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  if (typeof crypto !== 'undefined' && typeof crypto.getRandomValues === 'function') {
    const bytes = new Uint8Array(16)
    crypto.getRandomValues(bytes)
    bytes[6] = (bytes[6] & 0x0f) | 0x40
    bytes[8] = (bytes[8] & 0x3f) | 0x80
    const hex = [...bytes].map((b) => b.toString(16).padStart(2, '0')).join('')
    return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`
  }
  return `t-${Date.now().toString(16)}-${Math.random().toString(16).slice(2, 10)}`
}

export function getOwnerToken() {
  let token = localStorage.getItem(OWNER_KEY)
  if (!token) {
    token = createOwnerToken()
    localStorage.setItem(OWNER_KEY, token)
  }
  return token
}

/** 退出登录后换新的本地身份，避免串号。 */
export function resetOwnerToken() {
  const token = createOwnerToken()
  localStorage.setItem(OWNER_KEY, token)
  return token
}

export function getAuth() {
  try {
    const raw = localStorage.getItem(AUTH_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function setAuth(auth) {
  if (!auth) {
    localStorage.removeItem(AUTH_KEY)
    return
  }
  localStorage.setItem(AUTH_KEY, JSON.stringify(auth))
}

export function clearAuth() {
  localStorage.removeItem(AUTH_KEY)
}

export function defaultNickname() {
  const n = Math.floor(1000 + Math.random() * 9000)
  return `雪岩小弟${n}`
}

export async function api(path, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    'X-Owner-Token': getOwnerToken(),
    ...(options.headers || {}),
  }
  const auth = getAuth()
  if (auth?.token) {
    headers.Authorization = `Bearer ${auth.token}`
  }
  const res = await fetch(path, { ...options, headers })
  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    throw new Error(data.error || `请求失败（${res.status}）`)
  }
  return data
}
