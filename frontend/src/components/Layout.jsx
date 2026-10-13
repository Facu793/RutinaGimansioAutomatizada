import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const enlaces = [
  ['/', 'Inicio'],
  ['/alumnos', 'Alumnos'],
  ['/ejercicios', 'Ejercicios'],
  ['/categorias', 'Categorías'],
  ['/plantillas', 'Plantillas'],
]

export default function Layout() {
  const { username, salir } = useAuth()
  const navigate = useNavigate()
  const [menuAbierto, setMenuAbierto] = useState(false)

  function cerrar() {
    salir()
    navigate('/login')
  }

  return (
    <div className="app">
      <header className="barra">
        <div className="barra-marca">
          <span className="logo">GR</span>
          <strong>Gestión de rutinas</strong>
        </div>
        <button type="button" className="menu-boton" onClick={() => setMenuAbierto((valor) => !valor)} aria-expanded={menuAbierto}>
          Menú
        </button>
        <nav className={menuAbierto ? 'nav abierto' : 'nav'}>
          {enlaces.map(([ruta, texto]) => (
            <NavLink key={ruta} to={ruta} end={ruta === '/'} onClick={() => setMenuAbierto(false)}>
              {texto}
            </NavLink>
          ))}
          <span className="usuario">{username}</span>
          <button type="button" className="btn btn-ghost" onClick={cerrar}>Salir</button>
        </nav>
      </header>
      <main className="contenido">
        <Outlet />
      </main>
    </div>
  )
}
