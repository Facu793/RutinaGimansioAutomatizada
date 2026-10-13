import { useEffect, useState } from 'react'
import { api } from '../../api/client'
import AutocompletarEjercicio from '../AutocompletarEjercicio'
import { CamposSegundos, Campo } from '../ui'
import { textoTiempo } from '../../utils/formato'

function lineaVacia() {
  return {
    key: crypto.randomUUID(),
    ejercicioId: null,
    ejercicioNombre: '',
    categoriaNombre: '',
    modalidad: 'REPETICIONES',
    repeticiones: 8,
    duracionSegundos: 45,
    distancia: 1,
    unidadDistancia: 'KILOMETROS',
    porcentajeRm: '',
    pesoCalculado: null,
    advertencia: '',
  }
}

function LineaEjercicio({ linea, alumnoId, onChange, onQuitar, onSubir, onBajar }) {
  const [calculo, setCalculo] = useState({ peso: linea.pesoCalculado, advertencia: linea.advertencia || '' })

  useEffect(() => {
    if (!alumnoId || !linea.ejercicioId || linea.modalidad !== 'REPETICIONES' || linea.porcentajeRm === '') {
      setCalculo({ peso: null, advertencia: '' })
      return undefined
    }
    let cancelado = false
    const timer = setTimeout(async () => {
      try {
        const resultado = await api(`/api/alumnos/${alumnoId}/rm/calculo`, {
          method: 'POST',
          body: JSON.stringify({ ejercicioId: linea.ejercicioId, porcentaje: Number(linea.porcentajeRm) }),
        })
        if (!cancelado) {
          setCalculo({ peso: resultado.pesoCalculado, advertencia: resultado.advertencia || '' })
        }
      } catch (exception) {
        if (!cancelado) {
          setCalculo({ peso: null, advertencia: exception.message })
        }
      }
    }, 300)
    return () => {
      cancelado = true
      clearTimeout(timer)
    }
  }, [alumnoId, linea.ejercicioId, linea.modalidad, linea.porcentajeRm])

  return (
    <article className="linea">
      <header>
        <div>
          <strong>{linea.ejercicioNombre || 'Ejercicio sin elegir'}</strong>
          <span>{linea.categoriaNombre}</span>
        </div>
        {calculo.peso != null ? <span className="banderin">{calculo.peso} kg</span> : null}
      </header>
      <Campo label="Modalidad">
        <select value={linea.modalidad} onChange={(event) => onChange({ ...linea, modalidad: event.target.value })}>
          <option value="REPETICIONES">Repeticiones</option>
          <option value="TIEMPO">Tiempo</option>
          <option value="DISTANCIA">Distancia</option>
        </select>
      </Campo>
      {linea.modalidad === 'REPETICIONES' ? (
        <>
          <Campo label="Repeticiones">
            <input type="number" min="1" value={linea.repeticiones} onChange={(event) => onChange({ ...linea, repeticiones: Number(event.target.value) })} />
          </Campo>
          <Campo label="% del 1RM">
            <input type="number" min="1" max="100" step="0.5" value={linea.porcentajeRm} placeholder="Opcional" onChange={(event) => onChange({ ...linea, porcentajeRm: event.target.value })} />
          </Campo>
          {calculo.advertencia ? <p className="aviso aviso-error">{calculo.advertencia}</p> : null}
        </>
      ) : null}
      {linea.modalidad === 'TIEMPO' ? (
        <Campo label={`Duración · ${textoTiempo(linea.duracionSegundos)}`}>
          <CamposSegundos segundos={linea.duracionSegundos} onChange={(duracionSegundos) => onChange({ ...linea, duracionSegundos })} />
        </Campo>
      ) : null}
      {linea.modalidad === 'DISTANCIA' ? (
        <div className="dos">
          <Campo label="Distancia">
            <input type="number" min="0.01" step="0.01" value={linea.distancia} onChange={(event) => onChange({ ...linea, distancia: Number(event.target.value) })} />
          </Campo>
          <Campo label="Unidad">
            <select value={linea.unidadDistancia} onChange={(event) => onChange({ ...linea, unidadDistancia: event.target.value })}>
              <option value="KILOMETROS">Kilómetros</option>
              <option value="METROS">Metros</option>
            </select>
          </Campo>
        </div>
      ) : null}
      <div className="acciones">
        <button type="button" className="btn btn-ghost" onClick={onSubir}>Subir</button>
        <button type="button" className="btn btn-ghost" onClick={onBajar}>Bajar</button>
        <button type="button" className="btn btn-danger" onClick={onQuitar}>Quitar</button>
      </div>
    </article>
  )
}

export function moduloVacio(numero) {
  return {
    key: crypto.randomUUID(),
    nombre: `Módulo ${numero}`,
    rondas: 3,
    descansoEjercicioSegundos: 0,
    descansoRondaSegundos: 60,
    ejercicios: [],
  }
}

export function moduloDesdeApi(modulo) {
  return {
    key: crypto.randomUUID(),
    nombre: modulo.nombre,
    rondas: modulo.rondas,
    descansoEjercicioSegundos: modulo.descansoEjercicioSegundos,
    descansoRondaSegundos: modulo.descansoRondaSegundos,
    ejercicios: modulo.ejercicios.map((ejercicio) => ({
      key: crypto.randomUUID(),
      ejercicioId: ejercicio.ejercicioId,
      ejercicioNombre: ejercicio.ejercicioNombre,
      categoriaNombre: ejercicio.categoriaNombre,
      modalidad: ejercicio.modalidad,
      repeticiones: ejercicio.repeticiones ?? 8,
      duracionSegundos: ejercicio.duracionSegundos ?? 45,
      distancia: ejercicio.distancia ?? 1,
      unidadDistancia: ejercicio.unidadDistancia || 'KILOMETROS',
      porcentajeRm: ejercicio.porcentajeRm ?? '',
      pesoCalculado: ejercicio.pesoCalculado,
      advertencia: '',
    })),
  }
}

export function moduloParaApi(modulo) {
  return {
    nombre: modulo.nombre,
    rondas: Number(modulo.rondas),
    descansoEjercicioSegundos: Number(modulo.descansoEjercicioSegundos),
    descansoRondaSegundos: Number(modulo.descansoRondaSegundos),
    ejercicios: modulo.ejercicios.map((linea) => {
      const base = { ejercicioId: linea.ejercicioId, modalidad: linea.modalidad }
      if (linea.modalidad === 'REPETICIONES') {
        return {
          ...base,
          repeticiones: Number(linea.repeticiones),
          porcentajeRm: linea.porcentajeRm === '' ? null : Number(linea.porcentajeRm),
        }
      }
      if (linea.modalidad === 'TIEMPO') {
        return { ...base, duracionSegundos: Number(linea.duracionSegundos) }
      }
      return { ...base, distancia: Number(linea.distancia), unidadDistancia: linea.unidadDistancia }
    }),
  }
}

export default function ModuloEditor({ modulo, indice, total, alumnoId, onChange, onQuitar, onMover }) {
  function cambiarLinea(indiceLinea, linea) {
    const ejercicios = modulo.ejercicios.map((actual, posicion) => (posicion === indiceLinea ? linea : actual))
    onChange({ ...modulo, ejercicios })
  }

  function moverLinea(desde, hacia) {
    if (hacia < 0 || hacia >= modulo.ejercicios.length) return
    const ejercicios = [...modulo.ejercicios]
    const [linea] = ejercicios.splice(desde, 1)
    ejercicios.splice(hacia, 0, linea)
    onChange({ ...modulo, ejercicios })
  }

  function agregar(ejercicio) {
    onChange({
      ...modulo,
      ejercicios: [...modulo.ejercicios, { ...lineaVacia(), ejercicioId: ejercicio.id, ejercicioNombre: ejercicio.nombre, categoriaNombre: ejercicio.categoria.nombre }],
    })
  }

  return (
    <section className="card modulo">
      <div className="encabezado">
        <h2>Módulo {indice + 1}</h2>
        <div className="acciones">
          <button type="button" className="btn btn-ghost" onClick={() => onMover(-1)} disabled={indice === 0}>Subir</button>
          <button type="button" className="btn btn-ghost" onClick={() => onMover(1)} disabled={indice === total - 1}>Bajar</button>
          <button type="button" className="btn btn-danger" onClick={onQuitar} disabled={total === 1}>Quitar módulo</button>
        </div>
      </div>
      <Campo label="Nombre">
        <input value={modulo.nombre} onChange={(event) => onChange({ ...modulo, nombre: event.target.value })} />
      </Campo>
      <div className="tres">
        <Campo label="Rondas">
          <input type="number" min="1" max="99" value={modulo.rondas} onChange={(event) => onChange({ ...modulo, rondas: Number(event.target.value) })} />
        </Campo>
        <Campo label={`Descanso entre ejercicios · ${textoTiempo(modulo.descansoEjercicioSegundos)}`}>
          <CamposSegundos segundos={modulo.descansoEjercicioSegundos} onChange={(descansoEjercicioSegundos) => onChange({ ...modulo, descansoEjercicioSegundos })} />
        </Campo>
        <Campo label={`Descanso entre rondas · ${textoTiempo(modulo.descansoRondaSegundos)}`}>
          <CamposSegundos segundos={modulo.descansoRondaSegundos} onChange={(descansoRondaSegundos) => onChange({ ...modulo, descansoRondaSegundos })} />
        </Campo>
      </div>
      <p className="ayuda">Al terminar el último ejercicio se completa una ronda. El circuito se repite la cantidad de rondas.</p>
      {modulo.ejercicios.map((linea, posicion) => (
        <LineaEjercicio
          key={linea.key}
          linea={linea}
          alumnoId={alumnoId}
          onChange={(valor) => cambiarLinea(posicion, valor)}
          onQuitar={() => onChange({ ...modulo, ejercicios: modulo.ejercicios.filter((actual) => actual.key !== linea.key) })}
          onSubir={() => moverLinea(posicion, posicion - 1)}
          onBajar={() => moverLinea(posicion, posicion + 1)}
        />
      ))}
      <AutocompletarEjercicio onElegir={agregar} />
    </section>
  )
}
