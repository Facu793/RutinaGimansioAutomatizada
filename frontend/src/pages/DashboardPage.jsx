import { Link } from 'react-router-dom'

const accesos = [
  ['/alumnos', 'Alumnos', 'Registrar, buscar y ver el historial.'],
  ['/rutinas/nueva', 'Crear rutina', 'Armar módulos y circuitos.'],
  ['/ejercicios', 'Ejercicios', 'Mantener el catálogo del gimnasio.'],
  ['/plantillas', 'Plantillas', 'Modelos reutilizables de rutinas.'],
]

export default function DashboardPage() {
  return (
    <section>
      <p className="eyebrow">Inicio</p>
      <h1>¿Qué querés hacer?</h1>
      <div className="grilla">
        {accesos.map(([ruta, titulo, texto]) => (
          <Link key={ruta} className="acceso" to={ruta}>
            <h2>{titulo}</h2>
            <p>{texto}</p>
          </Link>
        ))}
      </div>
    </section>
  )
}
