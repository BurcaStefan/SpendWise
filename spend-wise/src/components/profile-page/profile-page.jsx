import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import './profile-page.scss'
import Header from '../header/header'
import Footer from '../footer/footer'
import useTheme from '../../hooks/useTheme'
import useAuth from '../../hooks/useAuth'
import { getUserIdFromToken } from '../../services/authService'
import { UpdateNamesDto, UpdatePasswordDto } from '../../dto/userDto'

export default function ProfilePage() {
  const { theme, setTheme } = useTheme()
  const { logout } = useAuth()
  const navigate = useNavigate()
  
  const [userData, setUserData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [showAccountMenu, setShowAccountMenu] = useState(true)
  const [showSettingsMenu, setShowSettingsMenu] = useState(true)
  const [editedData, setEditedData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    currentPassword: '',
    password: ''
  })

  useEffect(() => {
    fetchUserData()
  }, [])

  const fetchUserData = async () => {
    setLoading(true)
    setError(null)
    
    const userId = getUserIdFromToken()
    if (!userId) {
      setError('User not authenticated')
      setLoading(false)
      navigate('/login')
      return
    }

    try {
      const token = localStorage.getItem('token')
      const response = await fetch(
        `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/users/${userId}`,
        {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      )

      if (!response.ok) {
        if (response.status === 401) {
          logout()
          navigate('/login')
          return
        }
        const errorData = await response.json()
        throw new Error(errorData.message || 'Failed to fetch user data')
      }

      const data = await response.json()
      setUserData(data)
      setEditedData({
        firstName: data.firstname || '',
        lastName: data.lastname || '',
        email: data.email || '',
        currentPassword: '',
        password: ''
      })
    } catch (err) {
      setError(err.message || 'Something went wrong')
    } finally {
      setLoading(false)
    }
  }

  const handleEditChange = (e) => {
    const { name, value } = e.target
    setEditedData(prev => ({ ...prev, [name]: value }))
  }

  const handleSaveChanges = async () => {
    const userId = getUserIdFromToken()
    const token = localStorage.getItem('token')

    try {
      if (editedData.firstName || editedData.lastName) {
        const namesDto = UpdateNamesDto.fromFormData(editedData)
        
        console.log('Sending names update request:', namesDto)
        
        const namesResponse = await fetch(
          `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/users/${userId}/names`,
          {
            method: 'PATCH',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(namesDto)
          }
        )

        console.log('Names update response status:', namesResponse.status)
        
        if (!namesResponse.ok) {
          const errorData = await namesResponse.json()
          console.error('Names update error:', errorData)
          throw new Error(errorData.message || 'Failed to update names')
        }
        
        const namesData = await namesResponse.json()
        console.log('Names updated successfully:', namesData)
      }

      // Update password if provided
      if (editedData.password && editedData.password.trim() !== '') {
        const passwordDto = UpdatePasswordDto.fromFormData(editedData)
        
        console.log('Sending password update request')
        
        const passwordResponse = await fetch(
          `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/users/${userId}/password`,
          {
            method: 'PATCH',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(passwordDto)
          }
        )

        console.log('Password update response status:', passwordResponse.status)

        if (!passwordResponse.ok) {
          const errorData = await passwordResponse.json()
          console.error('Password update error:', errorData)
          throw new Error(errorData.message || 'Failed to update password')
        }
        
        const passwordData = await passwordResponse.json()
        console.log('Password updated successfully:', passwordData)
      }

      await fetchUserData()
      setShowAccountMenu(false)
      setEditedData(prev => ({ ...prev, password: '', currentPassword: '' }))
      setError(null)
      console.log('All updates completed successfully')
    } catch (err) {
      console.error('Update error:', err)
      setError(err.message)
    }
  }

  const handleDeleteAccount = async () => {
    if (!window.confirm('Are you sure you want to delete your account? This action is irreversible.')) {
      return
    }

    const userId = getUserIdFromToken()
    const token = localStorage.getItem('token')

    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/users/${userId}`,
        {
          method: 'DELETE',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      )

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Failed to delete account')
      }

      logout()
      navigate('/login')
    } catch (err) {
      setError(err.message)
    }
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  if (loading) {
    return (
      <>
        <Header />
        <div className="profile-page" data-theme={theme}>
          <div className="loading">Loading...</div>
        </div>
        <Footer />
      </>
    )
  }

  if (error) {
    return (
      <>
        <Header />
        <div className="profile-page" data-theme={theme}>
          <div className="error-message">{error}</div>
        </div>
        <Footer />
      </>
    )
  }

  return (
    <>
      <Header />
      <div className="profile-page" data-theme={theme}>
        <div className="profile-container">
          <div className="left-column">
            <div className="profile-sidebar">
              <div className="profile-avatar">
                <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <h2 className="profile-name">
                {userData?.firstname && userData?.lastname 
                  ? `${userData.firstname} ${userData.lastname}` 
                  : 'User'}
              </h2>
              <p className="profile-email">{userData?.email}</p>

              <div className="profile-menu">
                <button 
                  className={`menu-item ${showAccountMenu ? 'active' : ''}`} 
                  onClick={() => setShowAccountMenu(!showAccountMenu)}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                    <circle cx="12" cy="7" r="4" />
                  </svg>
                  My Account
                  <svg className="arrow-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polyline points="9 18 15 12 9 6" />
                  </svg>
                </button>

                <button 
                  className={`menu-item ${showSettingsMenu ? 'active' : ''}`} 
                  onClick={() => setShowSettingsMenu(!showSettingsMenu)}
                >
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                  Settings
                  <svg className="arrow-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polyline points="9 18 15 12 9 6" />
                  </svg>
                </button>

                <button className="menu-item logout-btn" onClick={handleLogout}>
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                    <polyline points="16 17 21 12 16 7" />
                    <line x1="21" y1="12" x2="9" y2="12" />
                  </svg>
                  Logout
                </button>
              </div>
            </div>

            {showSettingsMenu && (
              <div className="settings-dropdown">
                <div className="settings-header">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                  <h4>Settings</h4>
                  <button className="close-btn" onClick={() => setShowSettingsMenu(false)}>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <line x1="18" y1="6" x2="6" y2="18" />
                      <line x1="6" y1="6" x2="18" y2="18" />
                    </svg>
                  </button>
                </div>
                
                <div className="settings-item">
                  <label>Theme</label>
                  <select value={theme} onChange={(e) => setTheme(e.target.value)}>
                    <option value="light">Light</option>
                    <option value="dark">Dark</option>
                  </select>
                </div>

                <div className="settings-item">
                  <label>Language</label>
                  <select defaultValue="english">
                    <option value="english">English</option>
                    <option value="romanian">Romanian</option>
                  </select>
                </div>
              </div>
            )}
          </div>

          {showAccountMenu && (
            <div className="profile-content">
              <div className="profile-edit-card">
                <div className="card-header">
                  <h3>Personal Information</h3>
                  <button className="close-btn" onClick={() => setShowAccountMenu(false)}>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <line x1="18" y1="6" x2="6" y2="18" />
                      <line x1="6" y1="6" x2="18" y2="18" />
                    </svg>
                  </button>
                </div>

                <div className="edit-form">
                  <div className="form-group">
                    <label>First Name</label>
                    <input
                      type="text"
                      name="firstName"
                      value={editedData.firstName}
                      onChange={handleEditChange}
                      placeholder="John"
                    />
                  </div>

                  <div className="form-group">
                    <label>Last Name</label>
                    <input
                      type="text"
                      name="lastName"
                      value={editedData.lastName}
                      onChange={handleEditChange}
                      placeholder="Doe"
                    />
                  </div>

                  <div className="form-group">
                    <label>Current Password</label>
                    <input
                      type="password"
                      name="currentPassword"
                      value={editedData.currentPassword}
                      onChange={handleEditChange}
                      placeholder="Required to change password"
                    />
                  </div>

                  <div className="form-group">
                    <label>New Password</label>
                    <input
                      type="password"
                      name="password"
                      value={editedData.password}
                      onChange={handleEditChange}
                      placeholder="Leave empty to keep current password"
                    />
                  </div>

                  <div className="form-actions">
                    <button className="save-btn" onClick={handleSaveChanges}>
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <polyline points="20 6 9 17 4 12" />
                      </svg>
                      Save Changes
                    </button>
                    {error && <p className="error-text">{error}</p>}
                  </div>

                  <div className="danger-zone">
                    <button className="danger-btn" onClick={handleDeleteAccount}>
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <polyline points="3 6 5 6 21 6" />
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" />
                      </svg>
                      Delete Account
                    </button>
                    <button className="logout-action-btn" onClick={handleLogout}>
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                        <polyline points="16 17 21 12 16 7" />
                        <line x1="21" y1="12" x2="9" y2="12" />
                      </svg>
                      Logout
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

      </div>
      <Footer />
    </>
  )
}
