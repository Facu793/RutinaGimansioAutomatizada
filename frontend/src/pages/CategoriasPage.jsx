import { useEffect, useState } from 'react'
import { api } from '../api/client'
import { Aviso, Campo, Confirmacion, Vacio } from '../components/ui'

export default function CategoriasPage() {
  const [categorias, setCategorias] = useState([])
  const [nombre, setNombre] = useState('')
  const [editando, setEditando] = useState(null)
  const [error, setError] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [quitar, setQuitar] = useState(null)

  async function cargar() {
    setCategorias(await api('/api/categorias'))
  }

  useEffect(() => {
    cargar().catch((exception) => setError(exception.message))
  }, [])

  async function guardar(event) {
    event.preventDefault()
    setError('')
    setMensaje('')
    try {
      if (editando) {
        await api(`/api/categorias/${editando}`, { method: 'PUT', body: JSON.stringify({ nombre }) })
      } else {
        await api('/api/categorias', { method: 'POST', body: JSON.stringify({ nombre }) })
      }
      setNombre('')
      setEditando(null)
      setMensaje('Categoría guardada')
      await cargar()
    } catch (exception) {
      setError(exception.message)
    }
  }

  async function desactivar() {
    try {
      await api(`/api/categorias/${quitar.id}`, { method: 'DELETE' })
      setQuitar(null)
      await cargar()
    } catch (exception) {
      setError(exception.message)
      setQuitar(null)
    }
  }

  return (
    <section>
      <p className="eyebrow">Catálogo</p>
      <h1>Categorías</h1>
      <Aviso>{mensaje}</Aviso>
      <Aviso tipo="error">{error}</Aviso>
      <form className="card fila-form" onSubmit={guardar}>
        <Campo label={editando ? 'Nuevo nombre' : 'Nueva categoría'}>
          <input value={nombre} onChange={(event) => setNombre(event.target.value)} required />
        </Campo>
        <button className="btn btn-primary" type="submit">{editando ? 'Actualizar' : 'Agregar'}</button>
      </form>
      {categorias.length === 0 ? <Vacio titulo="Sin categorías" texto="Agregá la primera categoría." /> : null}
      <div className="lista">
        {categorias.map((categoria) => (
          <article key={categoria.id} className="item">
            <strong>{categoria.nombre}</strong>
            <div className="acciones">
              <button type="button" className="btn btn-ghost" onClick={() => { setEditando(categoria.id); setNombre(categoria.nombre) }}>Editar</button>
              <button type="button" className="btn btn-danger" onClick={() => setQuitar(categoria)}>Desactivar</button>
            </div>
          </article>
        ))}
      </div>
      <Confirmacion
        abierto={Boolean(quitar)}
        titulo="Desactivar categoría"
        texto="Solo se puede desactivar si no tiene ejercicios activos."
        onCerrar={() => setQuitar(null)}
        onConfirmar={desactivar}
      />
    </section>
  )
}
