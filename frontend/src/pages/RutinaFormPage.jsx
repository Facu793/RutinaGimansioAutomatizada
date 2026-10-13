import { useEffect, useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { api } from '../api/client'
import ModuloEditor, { moduloDesdeApi, moduloParaApi, moduloVacio } from '../components/rutina/ModuloEditor'
import { Aviso, Campo } from '../components/ui'

export default function RutinaFormPage() {
  const { id } = useParams()
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const [alumno, setAlumno] = useState(null)
  const [busqueda, setBusqueda] = useState('')
  const [coincidencias, setCoincidencias] = useState([])
  const [nombre, setNombre] = useState('')
  const [objetivo, setObjetivo] = useState('')
  const [fechaInicio, setFechaInicio] = useState('')
  const [fechaFin, setFechaFin] = useState('')
  const [observaciones, setObservaciones] = useState('')
  const [modulos, setModulos] = useState([moduloVacio(1)])
  const [aviso, setAviso] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  useEffect(() => {
    const alumnoId = params.get('alumnoId')
    if (!alumnoId || id) return undefined
    api(`/api/alumnos/${alumnoId}`).then(setAlumno).catch((exception) => setError(exception.message))
    return undefined
  }, [params, id])

  useEffect(() => {
    if (!id) return undefined
    api(`/api/rutinas/${id}`).then((rutina) => {
      setAlumno({ id: rutina.alumnoId, nombre: rutina.alumnoNombre, apellido: rutina.alumnoApellido })
      setNombre(rutina.nombre)
      setObjetivo(rutina.objetivo)
      setFechaInicio(rutina.fechaInicio)
      setFechaFin(rutina.fechaFin)
      setObservaciones(rutina.observaciones || '')
      setModulos(rutina.modulos.map(moduloDesdeApi))
    }).catch((exception) => setError(exception.message))
    return undefined
  }, [id])

  useEffect(() => {
    if (id) return undefined
    const desde = params.get('desde')
    const plantillaId = params.get('plantillaId')
    if (!desde && !plantillaId) return undefined
    const pedido = desde
      ? api(`/api/rutinas/${desde}`).then((rutina) => {
        setAlumno({ id: rutina.alumnoId, nombre: rutina.alumnoNombre, apellido: rutina.alumnoApellido })
        setNombre(`Copia de ${rutina.nombre}`.slice(0, 120))
        setObjetivo(rutina.objetivo)
        setFechaInicio(rutina.fechaInicio)
        setFechaFin(rutina.fechaFin)
        setObservaciones(rutina.observaciones || '')
        setModulos(rutina.modulos.map(moduloDesdeApi))
        setAviso('Esta es una copia. Al guardarla se crea una rutina nueva y la anterior queda igual.')
      })
      : api(`/api/plantillas/${plantillaId}`).then((plantilla) => {
        setNombre(plantilla.nombre)
        setObjetivo(plantilla.objetivo)
        setObservaciones(plantilla.observaciones || '')
        setModulos(plantilla.modulos.map(moduloDesdeApi))
        setAviso('Sale de una plantilla. Guardar crea la rutina del alumno y no cambia la plantilla.')
      })
    pedido.catch((exception) => setError(exception.message))
    return undefined
  }, [id, params])

  useEffect(() => {
    if (alumno || busqueda.trim().length < 2) {
      setCoincidencias([])
      return undefined
    }
    const timer = setTimeout(async () => {
      setCoincidencias(await api(`/api/alumnos?nombre=${encodeURIComponent(busqueda.trim())}`))
    }, 250)
    return () => clearTimeout(timer)
  }, [busqueda, alumno])

  function moverModulo(desde, delta) {
    const hacia = desde + delta
    if (hacia < 0 || hacia >= modulos.length) return
    const copia = [...modulos]
    const [modulo] = copia.splice(desde, 1)
    copia.splice(hacia, 0, modulo)
    setModulos(copia)
  }

  async function enviar(event) {
    event.preventDefault()
    if (!alumno) {
      setError('Elegí un alumno')
      return
    }
    if (modulos.some((modulo) => modulo.ejercicios.length === 0 || modulo.ejercicios.some((linea) => !linea.ejercicioId))) {
      setError('Cada módulo necesita al menos un ejercicio elegido del catálogo')
      return
    }
    setCargando(true)
    setError('')
    const cuerpo = {
      alumnoId: alumno.id,
      nombre,
      objetivo,
      fechaInicio,
      fechaFin,
      observaciones: observaciones || null,
      modulos: modulos.map(moduloParaApi),
    }
    try {
      const guardada = await api(id ? `/api/rutinas/${id}` : '/api/rutinas', {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(cuerpo),
      })
      navigate(`/rutinas/${guardada.id}`, { state: { mensaje: 'Rutina guardada' } })
    } catch (exception) {
      setError(exception.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <section>
      <p className="eyebrow">Rutina</p>
      <h1>{id ? 'Editar rutina' : params.get('desde') ? 'Duplicar rutina' : params.get('plantillaId') ? 'Rutina desde plantilla' : 'Nueva rutina'}</h1>
      <Aviso>{aviso}</Aviso>
      <form onSubmit={enviar} className="rutina-form">
        <Aviso tipo="error">{error}</Aviso>
        <article className="card">
          <Campo label="Alumno">
            {alumno ? (
              <strong className="alumno-elegido">{alumno.apellido ? `${alumno.apellido}, ${alumno.nombre}` : `${alumno.nombre} ${alumno.apellido || ''}`}</strong>
            ) : (
              <>
                <input value={busqueda} onChange={(event) => setBusqueda(event.target.value)} placeholder="Buscar alumno" />
                <div className="lista compacta">
                  {coincidencias.map((item) => (
                    <button type="button" key={item.id} className="item" onClick={() => { setAlumno(item); setBusqueda('') }}>
                      {item.apellido}, {item.nombre}
                    </button>
                  ))}
                </div>
              </>
            )}
          </Campo>
          <Campo label="Nombre">
            <input value={nombre} onChange={(event) => setNombre(event.target.value)} required />
          </Campo>
          <Campo label="Objetivo">
            <input value={objetivo} onChange={(event) => setObjetivo(event.target.value)} required />
          </Campo>
          <div className="dos">
            <Campo label="Inicio">
              <input type="date" value={fechaInicio} onChange={(event) => setFechaInicio(event.target.value)} required />
            </Campo>
            <Campo label="Finalización">
              <input type="date" value={fechaFin} onChange={(event) => setFechaFin(event.target.value)} required />
            </Campo>
          </div>
          <Campo label="Observaciones">
            <textarea rows="3" value={observaciones} onChange={(event) => setObservaciones(event.target.value)} />
          </Campo>
        </article>
        {modulos.map((modulo, indice) => (
          <ModuloEditor
            key={modulo.key}
            modulo={modulo}
            indice={indice}
            total={modulos.length}
            alumnoId={alumno?.id}
            onChange={(valor) => setModulos((actual) => actual.map((item) => (item.key === modulo.key ? valor : item)))}
            onQuitar={() => setModulos((actual) => actual.filter((item) => item.key !== modulo.key))}
            onMover={(delta) => moverModulo(indice, delta)}
          />
        ))}
        <button type="button" className="btn btn-ghost" onClick={() => setModulos((actual) => [...actual, moduloVacio(actual.length + 1)])}>
          Agregar módulo
        </button>
        <div className="acciones">
          <button type="button" className="btn btn-ghost" onClick={() => navigate(-1)}>Cancelar</button>
          <button className="btn btn-primary" type="submit" disabled={cargando}>{cargando ? 'Guardando...' : 'Guardar rutina'}</button>
        </div>
      </form>
    </section>
  )
}
