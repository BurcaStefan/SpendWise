import { useState, useEffect } from 'react'
import './statistics-page.scss'
import Header from '../header/header'
import Footer from '../footer/footer'
import useTheme from '../../hooks/useTheme'
import { getUserIdFromToken } from '../../services/authService'

export default function StatisticsPage() {
  const { theme } = useTheme()
  const [monthlyData, setMonthlyData] = useState(null)
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1)
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [accountId, setAccountId] = useState(null)
  const [hoveredSlice, setHoveredSlice] = useState(null)
  const [tooltipPosition, setTooltipPosition] = useState({ x: 0, y: 0 })

  const months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ]

  useEffect(() => {
    fetchAccountId()
  }, [])

  useEffect(() => {
    if (accountId) {
      fetchMonthlyStatistics()
    }
  }, [selectedMonth, selectedYear, accountId])

  const fetchAccountId = async () => {
    const userId = getUserIdFromToken()
    const token = localStorage.getItem('token')

    if (!userId || !token) {
      setError('User not authenticated')
      return
    }

    try {
      console.log('Fetching account ID for user:', userId)
      const response = await fetch(
        `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/budget-accounts/user/${userId}`,
        {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      )

      if (!response.ok) {
        throw new Error('Failed to fetch account ID')
      }

      const accountIdResponse = await response.text()
      console.log('Account ID received:', accountIdResponse)
      setAccountId(accountIdResponse.replace(/"/g, ''))
    } catch (err) {
      console.error('Error fetching account ID:', err)
      setError(err.message)
    }
  }

  const fetchMonthlyStatistics = async () => {
    setLoading(true)
    setError(null)

    const token = localStorage.getItem('token')

    if (!accountId || !token) {
      setError('Account not found')
      setLoading(false)
      return
    }

    try {
      const url = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/statistics/expenses/${accountId}?month=${selectedMonth}&year=${selectedYear}`
      console.log('Fetching statistics from:', url)
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      console.log('Response status:', response.status)

      if (!response.ok) {
        const errorText = await response.text()
        console.error('Error response:', errorText)
        throw new Error(`Failed to fetch statistics: ${response.status}`)
      }

      const data = await response.json()
      console.log('Statistics data received:', data)
      setMonthlyData(data)
    } catch (err) {
      console.error('Error fetching statistics:', err)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const renderPieChart = () => {
    if (!monthlyData || !monthlyData.categories || monthlyData.categories.length === 0) {
      return (
        <div className="no-data">
          <p>No data available for this period</p>
        </div>
      )
    }

    const total = monthlyData.total
    const radius = 120
    const centerX = 150
    const centerY = 150
    let currentAngle = -90

    const handleSliceInteraction = (e, index, category) => {
      const rect = e.currentTarget.getBoundingClientRect()
      const svgRect = e.currentTarget.ownerSVGElement.getBoundingClientRect()
      setTooltipPosition({
        x: e.clientX - svgRect.left,
        y: e.clientY - svgRect.top
      })
      setHoveredSlice(index)
    }

    const handleSliceLeave = () => {
      setHoveredSlice(null)
    }

    const slices = monthlyData.categories.map((category, index) => {
      const percentage = (category.amount / total) * 100
      const sliceAngle = (percentage / 100) * 360
      const startAngle = currentAngle
      const endAngle = currentAngle + sliceAngle

      const startRad = (startAngle * Math.PI) / 180
      const endRad = (endAngle * Math.PI) / 180

      const x1 = centerX + radius * Math.cos(startRad)
      const y1 = centerY + radius * Math.sin(startRad)
      const x2 = centerX + radius * Math.cos(endRad)
      const y2 = centerY + radius * Math.sin(endRad)

      const largeArc = sliceAngle > 180 ? 1 : 0

      const pathData = [
        `M ${centerX} ${centerY}`,
        `L ${x1} ${y1}`,
        `A ${radius} ${radius} 0 ${largeArc} 1 ${x2} ${y2}`,
        'Z'
      ].join(' ')

      currentAngle += sliceAngle

      const isHovered = hoveredSlice === index

      return (
        <path
          key={index}
          d={pathData}
          fill={category.color}
          stroke="transparent"
          strokeWidth="2"
          style={{
            cursor: 'pointer',
            opacity: hoveredSlice !== null && !isHovered ? 0.6 : 1,
            transition: 'opacity 0.2s ease',
            filter: isHovered ? 'brightness(1.1)' : 'none'
          }}
          onMouseEnter={(e) => handleSliceInteraction(e, index, category)}
          onMouseMove={(e) => handleSliceInteraction(e, index, category)}
          onMouseLeave={handleSliceLeave}
          onClick={(e) => handleSliceInteraction(e, index, category)}
        />
      )
    })

    return (
      <div className="pie-chart-container">
        <div style={{ position: 'relative' }}>
          <svg width="300" height="300" viewBox="0 0 300 300">
            {slices}
          </svg>
          {hoveredSlice !== null && (
            <div 
              className="chart-tooltip"
              style={{
                position: 'absolute',
                left: `${tooltipPosition.x}px`,
                top: `${tooltipPosition.y}px`,
                transform: 'translate(-50%, -120%)',
                pointerEvents: 'none'
              }}
            >
              <div className="tooltip-content">
                <strong>{monthlyData.categories[hoveredSlice].name}</strong>
                <div className="tooltip-amount">
                  €{monthlyData.categories[hoveredSlice].amount.toFixed(2)}
                </div>
                <div className="tooltip-percentage">
                  {((monthlyData.categories[hoveredSlice].amount / total) * 100).toFixed(1)}%
                </div>
              </div>
            </div>
          )}
        </div>
        <div className="total-expenses">
          <span className="total-label">Total cheltuieli:</span>
          <span className="total-amount">€{total.toFixed(2)}</span>
        </div>
        <div className="chart-legend">
          {monthlyData.categories.map((category, index) => {
            const percentage = ((category.amount / total) * 100).toFixed(0)
            return (
              <div 
                key={index} 
                className="legend-item"
                onMouseEnter={() => setHoveredSlice(index)}
                onMouseLeave={() => setHoveredSlice(null)}
                onClick={() => setHoveredSlice(hoveredSlice === index ? null : index)}
                style={{ cursor: 'pointer' }}
              >
                <div className="legend-color" style={{ backgroundColor: category.color }}></div>
                <span className="legend-label">{category.name}</span>
                <span className="legend-percentage">{percentage}%</span>
              </div>
            )
          })}
        </div>
      </div>
    )
  }

  return (
    <>
      <Header />
      <div className="statistics-page" data-theme={theme}>
        <div className="statistics-container">
          <div className="statistics-content">
            <div className="statistics-card monthly-card">
              <h2>Monthly expenses statistics</h2>
              
              <div className="date-selector">
                <select 
                  value={selectedMonth} 
                  onChange={(e) => setSelectedMonth(parseInt(e.target.value))}
                  className="month-select"
                >
                  {months.map((month, index) => (
                    <option key={index} value={index + 1}>{month}</option>
                  ))}
                </select>
                <select 
                  value={selectedYear} 
                  onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                  className="year-select"
                >
                  {[2024, 2025, 2026].map(year => (
                    <option key={year} value={year}>{year}</option>
                  ))}
                </select>
              </div>

              {loading && <div className="loading">Loading...</div>}
              {error && <div className="error-message">{error}</div>}
              {!loading && !error && renderPieChart()}
            </div>

            <div className="statistics-card yearly-card">
              <h2>Annual Statistics</h2>
              
              <div className="date-selector">
                <select 
                  value={selectedYear} 
                  onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                  className="year-select"
                >
                  {[2024, 2025, 2026].map(year => (
                    <option key={year} value={year}>{year}</option>
                  ))}
                </select>
              </div>

              <div className="line-chart-placeholder">
                <p className="placeholder-text">Annual statistics will be available soon</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  )
}
