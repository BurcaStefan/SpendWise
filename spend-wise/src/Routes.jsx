
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/login-page/login-page'
import Registration from './components/registration-page/registration-page'
import Home from './components/home-page/home-page'
import Contact from './components/contact-page/contact-page'
import ProfilePage from './components/profile-page/profile-page'
import StatisticsPage from './components/statistics-page/statistics-page'

export default function AppRoutes() {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Navigate to="/login" replace />} />
				<Route path="/login" element={<Login />} />
				<Route path="/register" element={<Registration />} />
				<Route path="/home" element={<Home />} />
				<Route path="/contact" element={<Contact />} />
				<Route path="/profile" element={<ProfilePage />} />
				<Route path="/statistics" element={<StatisticsPage />} />
			</Routes>
		</BrowserRouter>
	)
}
