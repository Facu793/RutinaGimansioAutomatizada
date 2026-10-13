import { createContext, useContext, useMemo, useState } from 'react'
import { api, cerrarSesion, guardarSesion, leerSesion } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [sesion, setSesion] = useState(leerSesion)

  const value = useMemo(() => ({
    username: sesion.username,
    autenticado: Boolean(sesion.token),
    async ingresar(username, password) {
      const data = await api('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      })
      guardarSesion(data.token, data.username)
      setSesion({ token: data.token, username: data.username })
    },
    salir() {
      cerrarSesion()
      setSesion({ token: null, username: null })
    },
  }), [sesion])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
