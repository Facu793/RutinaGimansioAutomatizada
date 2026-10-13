export function Campo({ label, error, children }) {
  return (
    <label className="campo">
      <span>{label}</span>
      {children}
      {error ? <small className="error">{error}</small> : null}
    </label>
  )
}

export function Aviso({ tipo = 'ok', children }) {
  if (!children) return null
  return <p className={tipo === 'error' ? 'aviso aviso-error' : 'aviso aviso-ok'} role="status">{children}</p>
}

export function Vacio({ titulo, texto }) {
  return (
    <div className="vacio">
      <strong>{titulo}</strong>
      <p>{texto}</p>
    </div>
  )
}

export function Confirmacion({ abierto, titulo, texto, onConfirmar, onCerrar, cargando }) {
  if (!abierto) return null
  return (
    <div className="modal-fondo" role="presentation" onClick={onCerrar}>
      <div className="modal" role="dialog" aria-modal="true" aria-labelledby="confirm-titulo" onClick={(event) => event.stopPropagation()}>
        <h2 id="confirm-titulo">{titulo}</h2>
        <p>{texto}</p>
        <div className="acciones">
          <button type="button" className="btn btn-ghost" onClick={onCerrar}>Cancelar</button>
          <button type="button" className="btn btn-danger" onClick={onConfirmar} disabled={cargando}>
            {cargando ? 'Procesando...' : 'Confirmar'}
          </button>
        </div>
      </div>
    </div>
  )
}

export function CamposSegundos({ segundos, onChange }) {
  const total = Number(segundos) || 0
  const minutos = Math.floor(total / 60)
  const resto = total % 60

  function cambiar(minutosNuevos, segundosNuevos) {
    const min = Math.max(0, Number(minutosNuevos) || 0)
    const seg = Math.min(59, Math.max(0, Number(segundosNuevos) || 0))
    onChange(min * 60 + seg)
  }

  return (
    <div className="tiempo">
      <label>
        min
        <input type="number" min="0" value={minutos} onChange={(event) => cambiar(event.target.value, resto)} />
      </label>
      <label>
        seg
        <input type="number" min="0" max="59" value={resto} onChange={(event) => cambiar(minutos, event.target.value)} />
      </label>
    </div>
  )
}
