import { useNavigate } from 'react-router-dom'
import './footer.scss'
import walletImg from '../../assets/wallet.png'
import useTheme from '../../hooks/useTheme'

export default function Footer() {
  const navigate = useNavigate()
  const { theme } = useTheme()

  return (
    <footer className="app-footer" data-theme={theme}>
      <div className="footer-container">
        <div className="footer-column brand-column">
          <img src={walletImg} alt="SpendWise" className="footer-logo" />
          <h3 className="footer-brand">SpendWise</h3>
        </div>

        <div className="footer-column links-column">
          <h4 className="footer-heading">Navigare</h4>
          <ul className="footer-links">
            <li><button onClick={() => navigate('/home')}>Home</button></li>
            <li><button onClick={() => navigate('/statistics')}>Statistici</button></li>
            <li><button onClick={() => navigate('/contact')}>Contact</button></li>
            <li><button onClick={() => navigate('/profile')}>Profil</button></li>
          </ul>
        </div>

        <div className="footer-column contact-column">
          <h4 className="footer-heading">Contact</h4>
          <ul className="footer-contact">
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
              </svg>
              <span>+40 723 456 789</span>
            </li>
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
              </svg>
              <span>+40 731 987 654</span>
            </li>
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                <polyline points="22,6 12,13 2,6" />
              </svg>
              <span>contact@spendwise.ro</span>
            </li>
            <li>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              <span>București, România</span>
            </li>
          </ul>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; 2025 SpendWise. All rights reserved.</p>
      </div>
    </footer>
  )
}
