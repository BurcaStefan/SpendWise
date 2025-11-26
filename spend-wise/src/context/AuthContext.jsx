import { createContext, useContext, useState } from 'react'
import { login as apiLogin } from '../services/authService'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const login = async (credentials) => {
    setLoading(true)
    setError(null)
    try {
      const body = await apiLogin(credentials)
      if (body?.token) {
        localStorage.setItem('token', body.token)
        setToken(body.token)
        console.log('Login successful')
        return body
      } else {
        throw new Error('No token in response')
      }
    } catch (err) {
      setError(err)
      console.error('Login failed', err)
      throw err
    } finally {
      setLoading(false)
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    setToken(null)
  }

  return (
    <AuthContext.Provider value={{ token, setToken, login, logout, loading, error }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuthContext() {
  return useContext(AuthContext)
}

export default AuthContext
