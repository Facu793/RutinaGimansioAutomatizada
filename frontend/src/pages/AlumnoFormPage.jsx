import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import { Aviso, Campo } from '../components/ui'

const vacio = { nombre: '', apellido: '', fechaNacimiento: '', telefono: '', observaciones: '' }

export default function AlumnoFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [formulario, setFormulario] = useState(vacio)
  const [errores, setErrores] = useState({})
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  useEffect(() => {
    if (!id) return undefined
    let activo = true
    api(`/api/alumnos/${id}`).then((alumno) => {
      if (!activo) return
      setFormulario({
        nombre: alumno.nombre,
        apellido: alumno.apellido,
        fechaNacimiento: alumno.fechaNacimiento,
        telefono: alumno.telefono,
        observaciones: alumno.observaciones || '',
      })
    }).catch((exception) => setError(exception.message))
    return () => { activo = false }
  }, [id])

  function cambiar(campo, valor) {
    setFormulario((actual) => ({ ...actual, [campo]: valor }))
  }

  async function enviar(event) {
    event.preventDefault()
    setCargando(true)
    setError('')
    setErrores({})
    try {
      const guardado = await api(id ? `/api/alumnos/${id}` : '/api/alumnos', {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify({ ...formulario, observaciones: formulario.observaciones || null }),
      })
      navigate(`/alumnos/${guardado.id}`, { state: { mensaje: 'Alumno guardado' } })
    } catch (exception) {
      setError(exception.message)
      setErrores(exception.errores || {})
    } finally {
      setCargando(false)
    }
  }

  return (
    <section className="formulario">
      <p className="eyebrow">Alumnos</p>
      <h1>{id ? 'Editar alumno' : 'Nuevo alumno'}</h1>
      <form className="card" onSubmit={enviar}>
        <Aviso tipo="error">{error}</Aviso>
        <Campo label="Nombre" error={errores.nombre}>
          <input value={formulario.nombre} onChange={(event) => cambiar('nombre', event.target.value)} required />
        </Campo>
        <Campo label="Apellido" error={errores.apellido}>
          <input value={formulario.apellido} onChange={(event) => cambiar('apellido', event.target.value)} required />
        </Campo>
        <Campo label="Fecha de nacimiento" error={errores.fechaNacimiento}>
          <input type="date" value={formulario.fechaNacimiento} onChange={(event) => cambiar('fechaNacimiento', event.target.value)} required />
        </Campo>
        <Campo label="Teléfono / WhatsApp" error={errores.telefono}>
          <input value={formulario.telefono} onChange={(event) => cambiar('telefono', event.target.value)} required />
        </Campo>
        <Campo label="Observaciones" error={errores.observaciones}>
          <textarea rows="3" value={formulario.observaciones} onChange={(event) => cambiar('observaciones', event.target.value)} />
        </Campo>
        <div className="acciones">
          <button type="button" className="btn btn-ghost" onClick={() => navigate(-1)}>Cancelar</button>
          <button className="btn btn-primary" type="submit" disabled={cargando}>{cargando ? 'Guardando...' : 'Guardar'}</button>
        </div>
      </form>
    </section>
  )
}
