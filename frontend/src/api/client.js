const TOKEN_KEY = 'gr_token'
const USER_KEY = 'gr_usuario'

export function leerSesion() {
  return {
    token: sessionStorage.getItem(TOKEN_KEY),
    username: sessionStorage.getItem(USER_KEY),
  }
}

export function guardarSesion(token, username) {
  sessionStorage.setItem(TOKEN_KEY, token)
  sessionStorage.setItem(USER_KEY, username)
}

export function cerrarSesion() {
  sessionStorage.removeItem(TOKEN_KEY)
  sessionStorage.removeItem(USER_KEY)
}

export class ApiError extends Error {
  constructor(mensaje, errores = {}) {
    super(mensaje)
    this.errores = errores
  }
}

export async function api(path, options = {}) {
  const headers = { ...(options.headers || {}) }
  if (options.body) {
    headers['Content-Type'] = 'application/json'
  }
  const token = sessionStorage.getItem(TOKEN_KEY)
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }

  const response = await fetch(path, { ...options, headers })
  if (response.status === 204) {
    return null
  }

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    if (response.status === 401 && !path.startsWith('/api/auth/login')) {
      cerrarSesion()
      window.location.assign('/login')
    }
    throw new ApiError(data.mensaje || 'No se pudo completar la acción', data.errores || {})
  }
  return data
}

export async function descargar(path) {
  const token = sessionStorage.getItem(TOKEN_KEY)
  const response = await fetch(path, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  })
  if (!response.ok) {
    const data = await response.json().catch(() => ({}))
    if (response.status === 401) {
      cerrarSesion()
      window.location.assign('/login')
    }
    throw new ApiError(data.mensaje || 'No se pudo descargar el PDF', data.errores || {})
  }
  const blob = await response.blob()
  const disposition = response.headers.get('Content-Disposition') || ''
  const archivo = disposition.match(/filename="([^"]+)"/)
  const enlace = document.createElement('a')
  enlace.href = URL.createObjectURL(blob)
  enlace.download = archivo ? archivo[1] : 'rutina.pdf'
  enlace.click()
  URL.revokeObjectURL(enlace.href)
}
