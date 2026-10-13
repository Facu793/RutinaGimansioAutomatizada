import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import ModuloEditor, { moduloDesdeApi, moduloParaApi, moduloVacio } from '../components/rutina/ModuloEditor'
import { Aviso, Campo } from '../components/ui'

export default function PlantillaFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [nombre, setNombre] = useState('')
  const [objetivo, setObjetivo] = useState('')
  const [observaciones, setObservaciones] = useState('')
  const [modulos, setModulos] = useState([moduloVacio(1)])
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  useEffect(() => {
    if (!id) return undefined
    api(`/api/plantillas/${id}`).then((plantilla) => {
      setNombre(plantilla.nombre)
      setObjetivo(plantilla.objetivo)
      setObservaciones(plantilla.observaciones || '')
      setModulos(plantilla.modulos.map(moduloDesdeApi))
    }).catch((exception) => setError(exception.message))
    return undefined
  }, [id])

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
    if (modulos.some((modulo) => modulo.ejercicios.length === 0 || modulo.ejercicios.some((linea) => !linea.ejercicioId))) {
      setError('Cada módulo necesita al menos un ejercicio elegido del catálogo')
      return
    }
    setCargando(true)
    setError('')
    const cuerpo = {
      nombre,
      objetivo,
      observaciones: observaciones || null,
      modulos: modulos.map(moduloParaApi),
    }
    try {
      await api(id ? `/api/plantillas/${id}` : '/api/plantillas', {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(cuerpo),
      })
      navigate('/plantillas', { state: { mensaje: 'Plantilla guardada' } })
    } catch (exception) {
      setError(exception.message)
    } finally {
      setCargando(false)
    }
  }

  return (
    <section>
      <p className="eyebrow">Plantilla</p>
      <h1>{id ? 'Editar plantilla' : 'Nueva plantilla'}</h1>
      <Aviso>El peso se calcula recién cuando usás la plantilla en un alumno. Cambiarla después no modifica las rutinas ya creadas.</Aviso>
      <form onSubmit={enviar} className="rutina-form">
        <Aviso tipo="error">{error}</Aviso>
        <article className="card">
          <Campo label="Nombre">
            <input value={nombre} onChange={(event) => setNombre(event.target.value)} required placeholder="Por ejemplo, Fuerza" />
          </Campo>
          <Campo label="Objetivo">
            <input value={objetivo} onChange={(event) => setObjetivo(event.target.value)} required />
          </Campo>
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
            alumnoId={null}
            onChange={(valor) => setModulos((actual) => actual.map((item) => (item.key === modulo.key ? valor : item)))}
            onQuitar={() => setModulos((actual) => actual.filter((item) => item.key !== modulo.key))}
            onMover={(delta) => moverModulo(indice, delta)}
          />
        ))}
        <button type="button" className="btn btn-ghost" onClick={() => setModulos((actual) => [...actual, moduloVacio(actual.length + 1)])}>
          Agregar módulo
        </button>
        <div className="acciones">
          <button type="button" className="btn btn-ghost" onClick={() => navigate('/plantillas')}>Cancelar</button>
          <button className="btn btn-primary" type="submit" disabled={cargando}>{cargando ? 'Guardando...' : 'Guardar plantilla'}</button>
        </div>
      </form>
    </section>
  )
}
