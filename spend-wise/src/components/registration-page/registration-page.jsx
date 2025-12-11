
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './registration-page.scss'
import walletImg from '../../assets/wallet.png'
import signupIcon from '../../assets/signup-icon.png'
import useTheme from '../../hooks/useTheme'
import { RegisterUserDto } from '../../dto/userDto'
import { hashText, verifyHash } from '../../utils/hashUtils'

export default function RegistrationPage() {
	const [showPassword, setShowPassword] = useState(false)
	const { theme, toggleTheme } = useTheme()
	const navigate = useNavigate()

	const [formData, setFormData] = useState({
		firstName: '',
		lastName: '',
		email: '',
		password: '',
		confirmPassword: '',
		verificationCode: ''
	})

	const [isCodeSent, setIsCodeSent] = useState(false)
	const [isCodeVerified, setIsCodeVerified] = useState(false)
	const [isSendingCode, setIsSendingCode] = useState(false)
	const [error, setError] = useState('')
	const [loading, setLoading] = useState(false)

	const handleInputChange = (e) => {
		const { name, value } = e.target
		setFormData(prev => ({ ...prev, [name]: value }))
		setError('')
	}

	const handleSendCode = async () => {
		if (!formData.email) {
			setError('Please enter an email address')
			return
		}

		setIsSendingCode(true)
		setError('')

		try {
			const response = await fetch(
				`${import.meta.env.REACT_APP_API_URL}/api/email/send-email-verification-code?email=${encodeURIComponent(formData.email)}`,
				{
					method: 'POST'
				}
			)

			if (!response.ok) {
				throw new Error('Failed to send verification code')
			}

			const data = await response.json()

			const codeValue = data.code !== undefined ? data.code : data
			const codeString = String(codeValue)
			const hashedCode = await hashText(codeString)
			localStorage.setItem('verificationCodeHash', hashedCode)

			setIsCodeSent(true)
			setError('')
		} catch (err) {
			setError('Failed to send verification code. Please try again.')
		} finally {
			setIsSendingCode(false)
		}
	}

	const handleVerifyCode = async () => {
		if (!formData.verificationCode) {
			setError('Please enter the verification code')
			return
		}

		const storedHash = localStorage.getItem('verificationCodeHash')
		if (!storedHash) {
			setError('No verification code found. Please request a new code.')
			return
		}

		try {
			const isValid = await verifyHash(formData.verificationCode, storedHash)

			if (isValid) {
				setIsCodeVerified(true)
				setError('')
			} else {
				setError('Invalid verification code. Please try again.')
			}
		} catch (err) {
			setError('Error verifying code. Please try again.')
		}
	}

	const handleSubmit = async (e) => {
		e.preventDefault()
		setError('')

		if (!formData.firstName || !formData.lastName || !formData.email || !formData.password || !formData.confirmPassword) {
			setError('All fields are required')
			return
		}

		if (formData.password !== formData.confirmPassword) {
			setError('Passwords do not match')
			return
		}

		if (!isCodeVerified) {
			setError('Please verify your email address first')
			return
		}

		setLoading(true)

		try {
			const registerDto = RegisterUserDto.fromFormData(formData)

			const registerResponse = await fetch(
				`${import.meta.env.REACT_APP_API_URL}/api/users`,
				{
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(registerDto)
				}
			)

			if (!registerResponse.ok) {
				const errorData = await registerResponse.json()
				throw new Error(errorData.message || 'Registration failed')
			}

			const userData = await registerResponse.json()

			const userId = userData.id || userData.userId
			
			const budgetAccountRequestBody = {
				userId: userId
			}

			const budgetAccountResponse = await fetch(
				`${import.meta.env.REACT_APP_API_URL}/api/budget-accounts`,
				{
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(budgetAccountRequestBody)
				}
			)

			if (!budgetAccountResponse.ok) {
				const errorData = await budgetAccountResponse.json()
			} else {
				const budgetAccountData = await budgetAccountResponse.json()
			}

			localStorage.removeItem('verificationCodeHash')

			navigate('/login')
		} catch (err) {
			setError(err.message || 'Registration failed. Please try again.')
		} finally {
			setLoading(false)
		}
	}

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
								<input 
									type="text" 
									id="nume" 
									name="firstName" 
									value={formData.firstName}
									onChange={handleInputChange}
									required 
								/>
							</div>

							<div className="form-group">
								<label htmlFor="prenume">Last name</label>
								<input 
									type="text" 
									id="prenume" 
									name="lastName" 
									value={formData.lastName}
									onChange={handleInputChange}
									required 
								/>
							</div>

							<div className="form-group">
								<label htmlFor="email">Email address</label>
								<div className="inline-row email-row">
									<input 
										type="email" 
										id="email" 
										name="email" 
										value={formData.email}
										onChange={handleInputChange}
										disabled={isCodeSent}
										required 
									/>
									<button 
										type="button" 
										className="btn btn-send"
										onClick={handleSendCode}
										disabled={isSendingCode || isCodeSent}
									>
										{isSendingCode ? 'Sending...' : isCodeSent ? 'Code sent' : 'Send code'}
									</button>
								</div>
							</div>

							<div className="form-group">
								<label htmlFor="verificationCode">Verification code</label>
								<div className="inline-row code-row">
									<input 
										type="text" 
										id="verificationCode" 
										name="verificationCode" 
										value={formData.verificationCode}
										onChange={handleInputChange}
										placeholder="Enter code"
										disabled={!isCodeSent || isCodeVerified}
									/>
									<button 
										type="button" 
										className={`btn btn-verify ${isCodeVerified ? 'verified' : ''}`}
										onClick={handleVerifyCode}
										disabled={!isCodeSent || isCodeVerified}
									>
										{isCodeVerified ? '✓ Verified' : 'Verify'}
									</button>
								</div>
							</div>

							<div className="form-group">
								<label htmlFor="password">Create password</label>
								<div className="password-wrapper">
									<input
										type={showPassword ? 'text' : 'password'}
										id="password"
										name="password"
										value={formData.password}
										onChange={handleInputChange}
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
										value={formData.confirmPassword}
										onChange={handleInputChange}
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
								<span>Already have an account?</span>
								<a href="#" onClick={(e) => { e.preventDefault(); navigate('/login') }}>Sign in here</a>
							</div>

							{error && <div className="error-message">{error}</div>}

							<div className="buttons">
								<button type="submit" className="btn btn-login" disabled={loading || !isCodeVerified}>
									<img src={signupIcon} alt="Signup Icon" width="40" height="28" />
									{loading ? 'Creating account...' : 'Create account'}
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
