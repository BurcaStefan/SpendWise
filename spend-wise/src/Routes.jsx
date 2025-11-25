
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/login-page/login-page'

export default function AppRoutes() {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Navigate to="/login" replace />} />
				<Route path="/login" element={<Login />} />
			</Routes>
		</BrowserRouter>
	)
}

