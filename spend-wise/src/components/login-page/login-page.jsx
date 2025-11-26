import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './login-page.scss'
import walletImg from '../../assets/wallet.png'
import loginIcon from '../../assets/login-icon.png'
import signupIcon from '../../assets/signup-icon.png'
import useAuth from '../../hooks/useAuth'

export default function LoginPage() {
    const [showPassword, setShowPassword] = useState(false)
    const [theme, setTheme] = useState('light')

    const navigate = useNavigate()
    const { login, loading } = useAuth()

    const handleSubmit = async (e) => {
        e.preventDefault()
        const form = new FormData(e.target)
        const email = form.get('email')?.toString() || ''
        const password = form.get('password')?.toString() || ''

        try {
            await login({ email, password })
            console.log('Login succeeded (component)')
            navigate('/home', { replace: true })
        } catch (err) {
            console.log('Login failed (component)', err)
        }
    }

    const toggleTheme = () => setTheme((t) => (t === 'light' ? 'dark' : 'light'))

    return (
        <div className="login-root" data-theme={theme}>
            <div className="container">
                <div className="left-section">
                    <div className="logo">
                        <img src={walletImg} alt="SpendWise Wallet Logo" />
                    </div>
                    <h1 className="brand">SpendWise</h1>
                    <p className="tagline">Your money, your rules.</p>
                </div>

                <div className="right-section">
                    <div className="login-card">
                        <button
                            type="button"
                            className="theme-toggle"
                            onClick={toggleTheme}
                            aria-pressed={theme === 'dark'}
                            aria-label="Toggle theme"
                        >
                            {theme === 'light' ? (
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
                                    <path d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z" />
                                </svg>
                            ) : (
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
                                    <circle cx="12" cy="12" r="4" />
                                    <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
                                </svg>
                            )}
                        </button>
                        <div className="icon">
                            <svg width="70" height="70" viewBox="0 0 50 50">
                                <polygon points="25,5 45,15 45,35 25,45 5,35 5,15" fill="none" stroke="#000" strokeWidth="2.5" />
                                <circle cx="25" cy="20" r="7" fill="none" stroke="#000" strokeWidth="2.5" />
                                <path d="M15 38 Q15 28 25 28 Q35 28 35 38" fill="none" stroke="#000" strokeWidth="2.5" />
                            </svg>
                        </div>
                        <h2>Login</h2>

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label htmlFor="email">Email address</label>
                                <input type="email" id="email" name="email" required />
                            </div>

                            <div className="form-group">
                                <label htmlFor="password">Password</label>
                                <div className="password-wrapper">
                                    <input
                                        type={showPassword ? 'text' : 'password'}
                                        id="password"
                                        name="password"
                                        required
                                    />
                                    <button
                                        type="button"
                                        className="toggle-password"
                                        aria-label={showPassword ? 'Hide password' : 'Show password'}
                                        onClick={() => setShowPassword((s) => !s)}
                                    >
                                        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                                            <circle cx="12" cy="12" r="3" />
                                        </svg>
                                    </button>
                                </div>
                            </div>

                            <div className="forgot-password">
                                <span>Forgot your password?</span>
                                <a href="#">Reset password</a>
                            </div>

                            <div className="buttons">
                                <button type="submit" className="btn btn-login" disabled={loading}>
                                    <img src={loginIcon} alt="Login Icon" width="40" height="28" />
                                    Sign in
                                </button>
                                <button type="button" className="btn btn-signup" onClick={() => navigate('/register')}>
                                    <img src={signupIcon} alt="Signup Icon" width="40" height="28" />
                                    Create account
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}
