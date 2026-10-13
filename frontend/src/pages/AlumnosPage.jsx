import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import { Aviso, Vacio } from '../components/ui'

export default function AlumnosPage() {
  const [nombre, setNombre] = useState('')
  const [alumnos, setAlumnos] = useState([])
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    const timer = setTimeout(async () => {
      setCargando(true)
      try {
        const consulta = nombre.trim() ? `?nombre=${encodeURIComponent(nombre.trim())}` : ''
        setAlumnos(await api(`/api/alumnos${consulta}`))
        setError('')
      } catch (exception) {
        setError(exception.message)
      } finally {
        setCargando(false)
      }
    }, 250)
    return () => clearTimeout(timer)
  }, [nombre])

  return (
    <section>
      <div className="encabezado">
        <div>
          <p className="eyebrow">Alumnos</p>
          <h1>Listado</h1>
        </div>
        <Link className="btn btn-primary" to="/alumnos/nuevo">Nuevo alumno</Link>
      </div>
      <input className="busqueda" value={nombre} onChange={(event) => setNombre(event.target.value)} placeholder="Buscar por nombre o apellido" />
      <Aviso tipo="error">{error}</Aviso>
      {cargando ? <p>Cargando...</p> : null}
      {!cargando && alumnos.length === 0 ? <Vacio titulo="No hay alumnos" texto="Creá el primero para empezar a armar rutinas." /> : null}
      <div className="lista">
        {alumnos.map((alumno) => (
          <Link key={alumno.id} className="item" to={`/alumnos/${alumno.id}`}>
            <strong>{alumno.apellido}, {alumno.nombre}</strong>
            <span>{alumno.edad} años · {alumno.telefono}</span>
          </Link>
        ))}
      </div>
    </section>
  )
}
