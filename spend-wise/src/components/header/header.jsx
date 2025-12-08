import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './header.scss'
import walletImg from '../../assets/wallet.png'
import useTheme from '../../hooks/useTheme'

export default function Header() {
  const navigate = useNavigate()
  const { theme } = useTheme()
  const [menuOpen, setMenuOpen] = useState(false)

  const handleNavClick = (path) => {
    navigate(path)
    setMenuOpen(false)
  }

  return (
    <header className="app-header" data-theme={theme}>
      <div className="header-container">
        <div className="header-left" onClick={() => navigate('/home')}>
          <img src={walletImg} alt="SpendWise" className="header-logo" />
        </div>

        <button 
          className="hamburger-btn" 
          onClick={() => setMenuOpen(!menuOpen)}
          aria-label="Toggle menu"
        >
          <span></span>
          <span></span>
          <span></span>
        </button>

        <nav className={`header-nav ${menuOpen ? 'open' : ''}`}>
          <button className="nav-item" onClick={() => navigate('/home')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
              <polyline points="9 22 9 12 15 12 15 22" />
            </svg>
            Home
          </button>

          <button className="nav-item" onClick={() => navigate('/statistics')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" />
            </svg>
            Statistics
          </button>

          <button className="nav-item" onClick={() => navigate('/contact')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
            </svg>
            Contact
          </button>

          <button className="nav-item profile-btn" onClick={() => navigate('/profile')}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
            Profile
          </button>
        </nav>
      </div>
    </header>
  )
}
