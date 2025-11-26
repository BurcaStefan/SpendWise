
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './registration-page.scss'
import walletImg from '../../assets/wallet.png'
import signupIcon from '../../assets/signup-icon.png'

export default function RegistrationPage() {
	const [showPassword, setShowPassword] = useState(false)
	const [theme, setTheme] = useState('light')

	const navigate = useNavigate()

	const handleSubmit = (e) => {
		e.preventDefault()
		console.log('register submit')
	}

	const toggleTheme = () => setTheme((t) => (t === 'light' ? 'dark' : 'light'))

	return (
		<div className="register-root" data-theme={theme}>
			<div className="container">
				<div className="right-section">
					<div className="login-card compact">
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

						<form onSubmit={handleSubmit}>
							<div className="form-group">
								<label htmlFor="nume">First name</label>
								<input type="text" id="nume" name="nume" required />
							</div>

							<div className="form-group">
								<label htmlFor="prenume">Last name</label>
								<input type="text" id="prenume" name="prenume" required />
							</div>

							<div className="form-group">
								<label htmlFor="email">Email address</label>
								<div className="inline-row email-row">
									<input type="email" id="email" name="email" required />
									<button type="button" className="btn btn-send">Send code</button>
								</div>
							</div>

							<div className="form-group">
								<label htmlFor="verificationCode">Verification code</label>
								<div className="inline-row code-row">
									<input type="text" id="verificationCode" name="verificationCode" placeholder="Enter code" />
									<button type="button" className="btn btn-verify">Verify</button>
								</div>
							</div>

							<div className="form-group">
								<label htmlFor="password">Create password</label>
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

							<div className="form-group">
								<label htmlFor="confirm-password">Confirm password</label>
								<div className="password-wrapper">
									<input
										type={showPassword ? 'text' : 'password'}
										id="confirm-password"
										name="confirmPassword"
										required
									/>
									<button
										type="button"
										className="toggle-password"
										aria-label={showPassword ? 'Ascunde parola' : 'Arata parola'}
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
								<span>Already have an account?</span>
								<a href="#" onClick={(e) => { e.preventDefault(); navigate('/login') }}>Sign in here</a>
							</div>

							<div className="buttons">
								<button type="submit" className="btn btn-login">
									<img src={signupIcon} alt="Signup Icon" width="40" height="28" />
                  
									Create account
								</button>
							</div>
						</form>
					</div>
				</div>

				<div className="left-section">
					<div className="register-title">Register</div>
					<div className="logo">
						<img src={walletImg} alt="SpendWise Wallet Logo" />
					</div>
					<p className="welcome">Welcome to<br/>SpendWise!</p>
				</div>
			</div>
		</div>
	)
}
