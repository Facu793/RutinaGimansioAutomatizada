import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { Aviso, Campo } from '../components/ui'

export default function LoginPage() {
  const { autenticado, ingresar } = useAuth()
  const navigate = useNavigate()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  if (autenticado) {
    return <Navigate to="/" replace />
  }

  async function enviar(event) {
    event.preventDefault()
    setError('')
    setCargando(true)
    try {
      await ingresar(username.trim(), password)
      navigate('/')
    } catch (exception) {
      setError(exception.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <main className="login">
      <form className="card login-card" onSubmit={enviar}>
        <p className="eyebrow">Gimnasio</p>
        <h1>Gestión de rutinas</h1>
        <p className="ayuda">Ingresá con el usuario del dueño del gimnasio.</p>
        <Aviso tipo="error">{error}</Aviso>
        <Campo label="Usuario">
          <input value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" required />
        </Campo>
        <Campo label="Contraseña">
          <input type="password" value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" required />
        </Campo>
        <button className="btn btn-primary" type="submit" disabled={cargando}>
          {cargando ? 'Ingresando...' : 'Ingresar'}
        </button>
      </form>
    </main>
  )
}
