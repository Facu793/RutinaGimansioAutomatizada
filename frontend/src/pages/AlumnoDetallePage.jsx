import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { api, descargar } from '../api/client'
import { Aviso, Confirmacion, Vacio } from '../components/ui'
import { fechaLocal } from '../utils/formato'
import { enlaceWhatsapp, mensajeRutina } from '../utils/whatsapp'

function IconoDescarga() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path fill="currentColor" d="M11 2h2v8.2l2.3-2.3 1.4 1.4L12 14.1 7.3 9.3l1.4-1.4L11 10.2V2zM4 16h2v3h12v-3h2v5H4v-5z" />
    </svg>
  )
}

function IconoWhatsapp() {
  return (
    <svg viewBox="0 0 24 24" aria-hidden="true">
      <path fill="currentColor" d="M12 3.2A8.2 8.2 0 0 0 5.1 15.7L4 20l4.4-1.1A8.2 8.2 0 1 0 12 3.2zm0 1.6a6.6 6.6 0 0 1 5.6 10 6.6 6.6 0 0 1-8.6 1.3l-.4-.2-2.6.7.7-2.5-.3-.4A6.6 6.6 0 0 1 12 4.8z" />
    </svg>
  )
}

export default function AlumnoDetallePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const location = useLocation()
  const [alumno, setAlumno] = useState(null)
  const [rutinas, setRutinas] = useState([])
  const [error, setError] = useState('')
  const [confirmar, setConfirmar] = useState(false)
  const [cargando, setCargando] = useState(false)
  const [bajandoId, setBajandoId] = useState(null)
  const [aviso, setAviso] = useState('')

  useEffect(() => {
    let activo = true
    Promise.all([api(`/api/alumnos/${id}`), api(`/api/alumnos/${id}/rutinas`)])
      .then(([datos, historial]) => {
        if (!activo) return
        setAlumno(datos)
        setRutinas(historial)
      })
      .catch((exception) => setError(exception.message))
    return () => { activo = false }
  }, [id])

  async function bajarPdf(rutinaId) {
    setBajandoId(rutinaId)
    setError('')
    setAviso('')
    try {
      await descargar(`/api/rutinas/${rutinaId}/pdf`)
      return true
    } catch (exception) {
      setError(exception.message)
      return false
    } finally {
      setBajandoId(null)
    }
  }

  async function enviarWhatsapp(rutina) {
    const url = enlaceWhatsapp(alumno.telefono, mensajeRutina({
      alumnoNombre: alumno.nombre,
      nombre: rutina.nombre,
      fechaInicio: fechaLocal(rutina.fechaInicio),
      fechaFin: fechaLocal(rutina.fechaFin),
    }))
    if (!url) {
      setError('Cargá un teléfono de WhatsApp en la ficha del alumno.')
      return
    }
    setError('')
    window.open(url, '_blank', 'noopener,noreferrer')
    const listo = await bajarPdf(rutina.id)
    if (listo) {
      setAviso('Se abrió WhatsApp y se descargó el PDF. Adjuntalo en el chat: desde el navegador no se puede enviar el archivo solo.')
    }
  }

  async function desactivar() {
    setCargando(true)
    try {
      await api(`/api/alumnos/${id}`, { method: 'DELETE' })
      navigate('/alumnos')
    } catch (exception) {
      setError(exception.message)
      setConfirmar(false)
    } finally {
      setCargando(false)
    }
  }

  if (!alumno) {
    return <p>{error || 'Cargando...'}</p>
  }

  return (
    <section>
      <p className="eyebrow">Alumno</p>
      <h1>{alumno.nombre} {alumno.apellido}</h1>
      <Aviso>{aviso || location.state?.mensaje}</Aviso>
      <Aviso tipo="error">{error}</Aviso>
      <article className="card ficha">
        <p><span>Edad</span><strong>{alumno.edad} años</strong></p>
        <p><span>Nacimiento</span><strong>{fechaLocal(alumno.fechaNacimiento)}</strong></p>
        <p><span>WhatsApp</span><strong>{alumno.telefono}</strong></p>
        <p><span>Observaciones</span><strong>{alumno.observaciones || 'Sin observaciones'}</strong></p>
      </article>
      <div className="acciones">
        <Link className="btn btn-primary" to={`/rutinas/nueva?alumnoId=${alumno.id}`}>Nueva rutina</Link>
        <Link className="btn btn-ghost" to={`/plantillas?alumnoId=${alumno.id}`}>Desde plantilla</Link>
        <Link className="btn btn-ghost" to={`/alumnos/${alumno.id}/rm`}>1RM</Link>
        <Link className="btn btn-ghost" to={`/alumnos/${alumno.id}/editar`}>Editar</Link>
        <button type="button" className="btn btn-danger" onClick={() => setConfirmar(true)}>Desactivar</button>
      </div>
      <h2>Historial de rutinas</h2>
      {rutinas.length === 0 ? <Vacio titulo="Todavía no hay rutinas" texto="Creá la primera con el botón de arriba." /> : null}
      <div className="lista">
        {rutinas.map((rutina) => (
          <article key={rutina.id} className="item">
            <Link className="item-link" to={`/rutinas/${rutina.id}`}>
              <strong>{rutina.nombre}</strong>
              <span>{rutina.objetivo} · {fechaLocal(rutina.fechaInicio)} al {fechaLocal(rutina.fechaFin)}</span>
            </Link>
            <div className="acciones">
              <button
                type="button"
                className="btn-icono"
                aria-label={`Descargar PDF de ${rutina.nombre}`}
                disabled={bajandoId === rutina.id}
                onClick={() => bajarPdf(rutina.id)}
              >
                <IconoDescarga />
              </button>
              <button
                type="button"
                className="btn-icono"
                aria-label={`Enviar por WhatsApp ${rutina.nombre}`}
                disabled={bajandoId === rutina.id}
                onClick={() => enviarWhatsapp(rutina)}
              >
                <IconoWhatsapp />
              </button>
            </div>
          </article>
        ))}
      </div>
      <Confirmacion
        abierto={confirmar}
        titulo="Desactivar alumno"
        texto="El alumno deja de aparecer en el listado. Sus rutinas se conservan."
        onCerrar={() => setConfirmar(false)}
        onConfirmar={desactivar}
        cargando={cargando}
      />
    </section>
  )
}
