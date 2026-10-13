import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Aviso, Campo, Confirmacion, Vacio } from '../components/ui'

const vacio = { nombre: '', categoriaId: '' }

export default function EjerciciosPage() {
  const [texto, setTexto] = useState('')
  const [ejercicios, setEjercicios] = useState([])
  const [categorias, setCategorias] = useState([])
  const [formulario, setFormulario] = useState(vacio)
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [quitar, setQuitar] = useState(null)

  useEffect(() => {
    api('/api/categorias').then(setCategorias).catch((exception) => setError(exception.message))
  }, [])

  useEffect(() => {
    const timer = setTimeout(async () => {
      try {
        const consulta = texto.trim() ? `?nombre=${encodeURIComponent(texto.trim())}` : ''
        setEjercicios(await api(`/api/ejercicios${consulta}`))
      } catch (exception) {
        setError(exception.message)
      }
    }, 250)
    return () => clearTimeout(timer)
  }, [texto, mensaje])

  async function guardar(event) {
    event.preventDefault()
    setError('')
    setMensaje('')
    const cuerpo = { nombre: formulario.nombre, categoriaId: Number(formulario.categoriaId) }
    try {
      if (editando) {
        await api(`/api/ejercicios/${editando}`, { method: 'PUT', body: JSON.stringify(cuerpo) })
      } else {
        await api('/api/ejercicios', { method: 'POST', body: JSON.stringify(cuerpo) })
      }
      setFormulario(vacio)
      setEditando(null)
      setMensaje('Ejercicio guardado')
    } catch (exception) {
      setError(exception.message)
    }
  }

  async function desactivar() {
    try {
      await api(`/api/ejercicios/${quitar.id}`, { method: 'DELETE' })
      setQuitar(null)
      setMensaje('Ejercicio desactivado')
    } catch (exception) {
      setError(exception.message)
      setQuitar(null)
    }
  }

  return (
    <section>
      <p className="eyebrow">Catálogo</p>
      <h1>Ejercicios</h1>
      <Aviso>{mensaje}</Aviso>
      <Aviso tipo="error">{error}</Aviso>
      <form className="card" onSubmit={guardar}>
        <Campo label="Nombre">
          <input value={formulario.nombre} onChange={(event) => setFormulario((actual) => ({ ...actual, nombre: event.target.value }))} required />
        </Campo>
        <Campo label="Categoría">
          <select value={formulario.categoriaId} onChange={(event) => setFormulario((actual) => ({ ...actual, categoriaId: event.target.value }))} required>
            <option value="">Elegí una categoría</option>
            {categorias.map((categoria) => <option key={categoria.id} value={categoria.id}>{categoria.nombre}</option>)}
          </select>
        </Campo>
        <button className="btn btn-primary" type="submit">{editando ? 'Actualizar' : 'Agregar ejercicio'}</button>
      </form>
      <input className="busqueda" value={texto} onChange={(event) => setTexto(event.target.value)} placeholder="Buscar ejercicio" />
      {ejercicios.length === 0 ? <Vacio titulo="No hay ejercicios" texto="Agregá uno o cambiá la búsqueda." /> : null}
      <div className="lista">
        {ejercicios.map((ejercicio) => (
          <article key={ejercicio.id} className="item">
            <div>
              <strong>{ejercicio.nombre}</strong>
              <span>{ejercicio.categoria.nombre}</span>
            </div>
            <div className="acciones">
              <button type="button" className="btn btn-ghost" onClick={() => { setEditando(ejercicio.id); setFormulario({ nombre: ejercicio.nombre, categoriaId: String(ejercicio.categoria.id) }) }}>Editar</button>
              <button type="button" className="btn btn-danger" onClick={() => setQuitar(ejercicio)}>Desactivar</button>
            </div>
          </article>
        ))}
      </div>
      <Confirmacion
        abierto={Boolean(quitar)}
        titulo="Desactivar ejercicio"
        texto="Las rutinas ya creadas conservan este ejercicio."
        onCerrar={() => setQuitar(null)}
        onConfirmar={desactivar}
      />
    </section>
  )
}
