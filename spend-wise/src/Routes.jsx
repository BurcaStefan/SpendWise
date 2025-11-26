
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/login-page/login-page'
import Registration from './components/registration-page/registration-page'

export default function AppRoutes() {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Navigate to="/login" replace />} />
				<Route path="/login" element={<Login />} />
				<Route path="/register" element={<Registration />} />
			</Routes>
		</BrowserRouter>
	)
}

