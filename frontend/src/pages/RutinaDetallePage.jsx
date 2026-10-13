import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { api, descargar } from '../api/client'
import { Aviso, Confirmacion } from '../components/ui'
import { fechaLocal, textoTiempo } from '../utils/formato'

function textoEjercicio(ejercicio) {
  if (ejercicio.modalidad === 'REPETICIONES') {
    const carga = ejercicio.porcentajeRm ? ` · ${ejercicio.porcentajeRm}%` : ''
    return `${ejercicio.repeticiones} repeticiones${carga}`
  }
  if (ejercicio.modalidad === 'TIEMPO') {
    return textoTiempo(ejercicio.duracionSegundos)
  }
  const unidad = ejercicio.unidadDistancia === 'KILOMETROS' ? 'km' : 'm'
  return `${ejercicio.distancia} ${unidad}`
}

export default function RutinaDetallePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const [rutina, setRutina] = useState(null)
  const [error, setError] = useState('')
  const [confirmar, setConfirmar] = useState(false)
  const [bajando, setBajando] = useState(false)

  useEffect(() => {
    api(`/api/rutinas/${id}`).then(setRutina).catch((exception) => setError(exception.message))
  }, [id])

  async function bajarPdf() {
    setBajando(true)
    setError('')
    try {
      await descargar(`/api/rutinas/${id}/pdf`)
    } catch (exception) {
      setError(exception.message)
    } finally {
      setBajando(false)
    }
  }

  async function desactivar() {
    try {
      await api(`/api/rutinas/${id}`, { method: 'DELETE' })
      navigate(`/alumnos/${rutina.alumnoId}`)
    } catch (exception) {
      setError(exception.message)
      setConfirmar(false)
    }
  }

  if (!rutina) {
    return <p>{error || 'Cargando...'}</p>
  }

  return (
    <section>
      <p className="eyebrow">Rutina</p>
      <h1>{rutina.nombre}</h1>
      <Aviso>{location.state?.mensaje}</Aviso>
      <Aviso tipo="error">{error}</Aviso>
      {rutina.advertencias?.length ? rutina.advertencias.map((texto) => <Aviso key={texto} tipo="error">{texto}</Aviso>) : null}
      <article className="card ficha">
        <p><span>Alumno</span><strong>{rutina.alumnoNombre} {rutina.alumnoApellido}</strong></p>
        <p><span>Edad al inicio</span><strong>{rutina.edad} años</strong></p>
        <p><span>Objetivo</span><strong>{rutina.objetivo}</strong></p>
        <p><span>Vigencia</span><strong>{fechaLocal(rutina.fechaInicio)} al {fechaLocal(rutina.fechaFin)}</strong></p>
        <p><span>Observaciones</span><strong>{rutina.observaciones || 'Sin observaciones'}</strong></p>
      </article>
      {rutina.modulos.map((modulo) => (
        <article key={modulo.id || modulo.orden} className="card modulo-vista">
          <header>
            <h2>{modulo.nombre}</h2>
            <strong>{modulo.rondas} rondas</strong>
          </header>
          <p className="ayuda">Descanso entre ejercicios: {textoTiempo(modulo.descansoEjercicioSegundos)}. Entre rondas: {textoTiempo(modulo.descansoRondaSegundos)}.</p>
          <ol>
            {modulo.ejercicios.map((ejercicio) => (
              <li key={ejercicio.id || ejercicio.orden}>
                <div>
                  <strong>{ejercicio.orden}. {ejercicio.ejercicioNombre}</strong>
                  <span>{ejercicio.categoriaNombre} · {textoEjercicio(ejercicio)}</span>
                </div>
                {ejercicio.pesoCalculado != null ? <span className="banderin">{ejercicio.pesoCalculado} kg</span> : null}
              </li>
            ))}
          </ol>
        </article>
      ))}
      <div className="acciones">
        <button type="button" className="btn btn-primary" onClick={bajarPdf} disabled={bajando}>
          {bajando ? 'Preparando PDF...' : 'Descargar PDF'}
        </button>
        <Link className="btn btn-ghost" to={`/rutinas/${rutina.id}/editar`}>Editar</Link>
        <Link className="btn btn-ghost" to={`/rutinas/nueva?desde=${rutina.id}`}>Duplicar</Link>
        <Link className="btn btn-ghost" to={`/alumnos/${rutina.alumnoId}`}>Volver al alumno</Link>
        {rutina.activa ? <button type="button" className="btn btn-danger" onClick={() => setConfirmar(true)}>Desactivar</button> : null}
      </div>
      <Confirmacion
        abierto={confirmar}
        titulo="Desactivar rutina"
        texto="La rutina sale del historial visible, pero se puede volver a consultar."
        onCerrar={() => setConfirmar(false)}
        onConfirmar={desactivar}
      />
    </section>
  )
}
