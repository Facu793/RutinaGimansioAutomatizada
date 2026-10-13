import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api/client'
import AutocompletarEjercicio from '../components/AutocompletarEjercicio'
import { Aviso, Campo, Confirmacion, Vacio } from '../components/ui'

export default function RmPage() {
  const { id } = useParams()
  const [alumno, setAlumno] = useState(null)
  const [registros, setRegistros] = useState([])
  const [pesos, setPesos] = useState({})
  const [nuevo, setNuevo] = useState(null)
  const [pesoNuevo, setPesoNuevo] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [quitar, setQuitar] = useState(null)

  async function cargar() {
    const [datos, rms] = await Promise.all([
      api(`/api/alumnos/${id}`),
      api(`/api/alumnos/${id}/rm`),
    ])
    setAlumno(datos)
    setRegistros(rms)
    setPesos(Object.fromEntries(rms.map((registro) => [registro.ejercicioId, String(registro.peso)])))
  }

  useEffect(() => {
    cargar().catch((exception) => setError(exception.message))
  }, [id])

  async function guardar(ejercicioId, peso) {
    setError('')
    setMensaje('')
    try {
      await api(`/api/alumnos/${id}/rm`, {
        method: 'PUT',
        body: JSON.stringify({ ejercicioId, peso: Number(peso) }),
      })
      setNuevo(null)
      setPesoNuevo('')
      setMensaje('1RM guardado')
      await cargar()
    } catch (exception) {
      setError(exception.message)
    }
  }

  async function eliminar() {
    try {
      await api(`/api/alumnos/${id}/rm/${quitar.ejercicioId}`, { method: 'DELETE' })
      setQuitar(null)
      await cargar()
    } catch (exception) {
      setError(exception.message)
      setQuitar(null)
    }
  }

  return (
    <section>
      <p className="eyebrow">1RM</p>
      <h1>{alumno ? `${alumno.nombre} ${alumno.apellido}` : 'Cargando...'}</h1>
      <Aviso>{mensaje}</Aviso>
      <Aviso tipo="error">{error}</Aviso>
      <article className="card">
        <h2>Agregar ejercicio</h2>
        <AutocompletarEjercicio onElegir={setNuevo} />
        {nuevo ? (
          <div className="fila-rm">
            <strong>{nuevo.nombre}</strong>
            <Campo label="Peso en kg">
              <input type="number" min="0.01" step="0.5" value={pesoNuevo} onChange={(event) => setPesoNuevo(event.target.value)} />
            </Campo>
            <button type="button" className="btn btn-primary" onClick={() => guardar(nuevo.id, pesoNuevo)}>Guardar</button>
          </div>
        ) : null}
      </article>
      <h2>Registros</h2>
      {registros.length === 0 ? <Vacio titulo="Sin 1RM" texto="Buscá un ejercicio y cargá el peso máximo." /> : null}
      <div className="lista">
        {registros.map((registro) => (
          <article key={registro.id} className="item item-form">
            <div>
              <strong>{registro.ejercicioNombre}</strong>
              <span>{registro.categoriaNombre}</span>
            </div>
            <input type="number" min="0.01" step="0.5" value={pesos[registro.ejercicioId] || ''} onChange={(event) => setPesos((actual) => ({ ...actual, [registro.ejercicioId]: event.target.value }))} />
            <button type="button" className="btn btn-primary" onClick={() => guardar(registro.ejercicioId, pesos[registro.ejercicioId])}>Actualizar</button>
            <button type="button" className="btn btn-danger" onClick={() => setQuitar(registro)}>Quitar</button>
          </article>
        ))}
      </div>
      <Link className="btn btn-ghost" to={`/alumnos/${id}`}>Volver al alumno</Link>
      <Confirmacion
        abierto={Boolean(quitar)}
        titulo="Quitar 1RM"
        texto={quitar ? `Se elimina el 1RM de ${quitar.ejercicioNombre}.` : ''}
        onCerrar={() => setQuitar(null)}
        onConfirmar={eliminar}
      />
    </section>
  )
}
