import { useEffect, useState } from 'react'
import { api } from '../api/client'

export default function AutocompletarEjercicio({ onElegir }) {
  const [texto, setTexto] = useState('')
  const [opciones, setOpciones] = useState([])
  const [buscando, setBuscando] = useState(false)

  useEffect(() => {
    if (texto.trim().length < 2) {
      setOpciones([])
      return undefined
    }
    const timer = setTimeout(async () => {
      setBuscando(true)
      try {
        const data = await api(`/api/ejercicios?nombre=${encodeURIComponent(texto.trim())}`)
        setOpciones(data)
      } catch {
        setOpciones([])
      } finally {
        setBuscando(false)
      }
    }, 250)
    return () => clearTimeout(timer)
  }, [texto])

  function elegir(ejercicio) {
    onElegir(ejercicio)
    setTexto('')
    setOpciones([])
  }

  return (
    <div className="autocomplete">
      <input
        value={texto}
        onChange={(event) => setTexto(event.target.value)}
        placeholder="Escribí, por ejemplo, pres"
        aria-label="Buscar ejercicio"
      />
      {buscando ? <small>Buscando...</small> : null}
      {texto.trim().length >= 2 && !buscando && opciones.length === 0 ? <small>No hay ejercicios con ese nombre.</small> : null}
      {opciones.length > 0 ? (
        <ul className="autocomplete-lista">
          {opciones.map((ejercicio) => (
            <li key={ejercicio.id}>
              <button type="button" onClick={() => elegir(ejercicio)}>
                <strong>{ejercicio.nombre}</strong>
                <span>{ejercicio.categoria.nombre}</span>
              </button>
            </li>
          ))}
        </ul>
      ) : null}
    </div>
  )
}
