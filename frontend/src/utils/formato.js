export function textoTiempo(segundos) {
  const total = Number(segundos) || 0
  const minutos = Math.floor(total / 60)
  const resto = total % 60
  if (minutos === 0) {
    return `${resto} seg`
  }
  if (resto === 0) {
    return minutos === 1 ? '1 min' : `${minutos} min`
  }
  return `${minutos} min ${resto} seg`
}

export function partirSegundos(segundos) {
  const total = Number(segundos) || 0
  return { minutos: Math.floor(total / 60), segundos: total % 60 }
}

export function unirSegundos(minutos, segundos) {
  const min = Math.max(0, Number(minutos) || 0)
  const seg = Math.min(59, Math.max(0, Number(segundos) || 0))
  return min * 60 + seg
}

export function fechaLocal(iso) {
  if (!iso) return ''
  const [anio, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${anio}`
}
