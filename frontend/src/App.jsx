import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import Layout from './components/Layout'
import AlumnoDetallePage from './pages/AlumnoDetallePage'
import AlumnoFormPage from './pages/AlumnoFormPage'
import AlumnosPage from './pages/AlumnosPage'
import CategoriasPage from './pages/CategoriasPage'
import DashboardPage from './pages/DashboardPage'
import EjerciciosPage from './pages/EjerciciosPage'
import LoginPage from './pages/LoginPage'
import PlantillaFormPage from './pages/PlantillaFormPage'
import PlantillasPage from './pages/PlantillasPage'
import RmPage from './pages/RmPage'
import RutinaDetallePage from './pages/RutinaDetallePage'
import RutinaFormPage from './pages/RutinaFormPage'

function Protegido({ children }) {
  const { autenticado } = useAuth()
  if (!autenticado) {
    return <Navigate to="/login" replace />
  }
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<Protegido><Layout /></Protegido>}>
        <Route index element={<DashboardPage />} />
        <Route path="alumnos" element={<AlumnosPage />} />
        <Route path="alumnos/nuevo" element={<AlumnoFormPage />} />
        <Route path="alumnos/:id" element={<AlumnoDetallePage />} />
        <Route path="alumnos/:id/editar" element={<AlumnoFormPage />} />
        <Route path="alumnos/:id/rm" element={<RmPage />} />
        <Route path="ejercicios" element={<EjerciciosPage />} />
        <Route path="categorias" element={<CategoriasPage />} />
        <Route path="plantillas" element={<PlantillasPage />} />
        <Route path="plantillas/nueva" element={<PlantillaFormPage />} />
        <Route path="plantillas/:id/editar" element={<PlantillaFormPage />} />
        <Route path="rutinas/nueva" element={<RutinaFormPage />} />
        <Route path="rutinas/:id" element={<RutinaDetallePage />} />
        <Route path="rutinas/:id/editar" element={<RutinaFormPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
