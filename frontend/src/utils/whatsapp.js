export function numeroWhatsapp(telefono) {
  let digitos = String(telefono || '').replace(/\D/g, '')
  if (digitos.startsWith('00')) {
    digitos = digitos.slice(2)
  }
  if (!digitos.startsWith('54')) {
    if (digitos.startsWith('0')) {
      digitos = digitos.slice(1)
    }
    digitos = `549${digitos}`
  }
  if (digitos.length < 11 || digitos.length > 15) {
    return ''
  }
  return digitos
}

export function enlaceWhatsapp(telefono, mensaje) {
  const numero = numeroWhatsapp(telefono)
  if (!numero) {
    return ''
  }
  return `https://wa.me/${numero}?text=${encodeURIComponent(mensaje)}`
}

export function mensajeRutina(rutina) {
  return `Hola ${rutina.alumnoNombre}. Te comparto la rutina "${rutina.nombre}". Vigencia: ${rutina.fechaInicio} al ${rutina.fechaFin}. Adjuntá el PDF que se descargó.`
}
