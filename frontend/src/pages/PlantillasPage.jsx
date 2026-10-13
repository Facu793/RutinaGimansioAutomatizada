import { useEffect, useState } from 'react'
import { Link, useLocation, useSearchParams } from 'react-router-dom'
import { api } from '../api/client'
import { Aviso, Confirmacion, Vacio } from '../components/ui'

export default function PlantillasPage() {
  const location = useLocation()
  const [params] = useSearchParams()
  const alumnoId = params.get('alumnoId')
  const [alumno, setAlumno] = useState(null)
  const [nombre, setNombre] = useState('')
  const [plantillas, setPlantillas] = useState([])
  const [error, setError] = useState('')
  const [quitar, setQuitar] = useState(null)

  useEffect(() => {
    if (!alumnoId) return undefined
    api(`/api/alumnos/${alumnoId}`).then(setAlumno).catch((exception) => setError(exception.message))
    return undefined
  }, [alumnoId])

  useEffect(() => {
    const timer = setTimeout(async () => {
      try {
        const consulta = nombre.trim() ? `?nombre=${encodeURIComponent(nombre.trim())}` : ''
        setPlantillas(await api(`/api/plantillas${consulta}`))
        setError('')
      } catch (exception) {
        setError(exception.message)
      }
    }, 250)
    return () => clearTimeout(timer)
  }, [nombre, location.state])

  async function desactivar() {
    try {
      await api(`/api/plantillas/${quitar.id}`, { method: 'DELETE' })
      setQuitar(null)
      setPlantillas((actual) => actual.filter((item) => item.id !== quitar.id))
    } catch (exception) {
      setError(exception.message)
      setQuitar(null)
    }
  }

  function usar(id) {
    const consulta = new URLSearchParams({ plantillaId: id })
    if (alumnoId) consulta.set('alumnoId', alumnoId)
    return `/rutinas/nueva?${consulta.toString()}`
  }

  return (
    <section>
      <div className="encabezado">
        <div>
          <p className="eyebrow">Plantillas</p>
          <h1>Modelos de rutina</h1>
        </div>
        <Link className="btn btn-primary" to="/plantillas/nueva">Nueva plantilla</Link>
      </div>
      <Aviso>{location.state?.mensaje}</Aviso>
      {alumno ? <Aviso>Elegí un modelo para armar la rutina de {alumno.nombre} {alumno.apellido}.</Aviso> : null}
      <p>Un modelo como Fuerza o Hipertrofia se copia al alumno. Después podés cambiar esa rutina sin tocar el modelo.</p>
      <Aviso tipo="error">{error}</Aviso>
      <input className="busqueda" value={nombre} onChange={(event) => setNombre(event.target.value)} placeholder="Buscar plantilla" />
      {plantillas.length === 0 ? <Vacio titulo="No hay plantillas" texto="Creá la primera, por ejemplo Adaptación o Fuerza." /> : null}
      <div className="lista">
        {plantillas.map((plantilla) => (
          <article key={plantilla.id} className="item">
            <div>
              <strong>{plantilla.nombre}</strong>
              <span>{plantilla.objetivo}</span>
            </div>
            <div className="acciones">
              <Link className="btn btn-primary" to={usar(plantilla.id)}>Usar</Link>
              <Link className="btn btn-ghost" to={`/plantillas/${plantilla.id}/editar`}>Editar</Link>
              <button type="button" className="btn btn-danger" onClick={() => setQuitar(plantilla)}>Desactivar</button>
            </div>
          </article>
        ))}
      </div>
      <Confirmacion
        abierto={Boolean(quitar)}
        titulo="Desactivar plantilla"
        texto="Las rutinas que ya se crearon con este modelo quedan igual."
        onCerrar={() => setQuitar(null)}
        onConfirmar={desactivar}
      />
    </section>
  )
}
