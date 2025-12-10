import { useState, useEffect, useRef } from 'react'
import './statistics-page.scss'
import Header from '../header/header'
import Footer from '../footer/footer'
import useTheme from '../../hooks/useTheme'
import { getUserIdFromToken } from '../../services/authService'
import jsPDF from 'jspdf'
import html2canvas from 'html2canvas'
import * as XLSX from 'xlsx'
import { MonthlyExpensesDto, YearlyIncomeDto } from '../../dto/statisticsDto'

export default function StatisticsPage() {
  const { theme } = useTheme()
  const [monthlyData, setMonthlyData] = useState(null)
  const [yearlyData, setYearlyData] = useState(null)
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1)
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
  const [yearlySelectedYear, setYearlySelectedYear] = useState(new Date().getFullYear())
  const [loading, setLoading] = useState(false)
  const [yearlyLoading, setYearlyLoading] = useState(false)
  const [error, setError] = useState(null)
  const [yearlyError, setYearlyError] = useState(null)
  const [accountId, setAccountId] = useState(null)
  const [hoveredSlice, setHoveredSlice] = useState(null)
  const [tooltipPosition, setTooltipPosition] = useState({ x: 0, y: 0 })
  const chartRef = useRef(null)
  const lineChartRef = useRef(null)

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

  useEffect(() => {
    if (accountId) {
      fetchYearlyStatistics()
    }
  }, [yearlySelectedYear, accountId])

  const fetchAccountId = async () => {
    const userId = getUserIdFromToken()
    const token = localStorage.getItem('token')

    if (!userId || !token) {
      setError('User not authenticated')
      return
    }

    try {
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
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })


      if (!response.ok) {
        const errorText = await response.text()
        console.error('Error response:', errorText)
        throw new Error(`Failed to fetch statistics: ${response.status}`)
      }

      const data = await response.json()
      const monthlyExpensesDto = MonthlyExpensesDto.fromApiResponse(data, selectedMonth, selectedYear)
      setMonthlyData(monthlyExpensesDto)
    } catch (err) {
      console.error('Error fetching statistics:', err)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const fetchYearlyStatistics = async () => {
    setYearlyLoading(true)
    setYearlyError(null)

    const token = localStorage.getItem('token')

    if (!accountId || !token) {
      setYearlyError('Account not found')
      setYearlyLoading(false)
      return
    }

    try {
      const url = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/statistics/income/${accountId}/${yearlySelectedYear}`
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })

      if (!response.ok) {
        const errorText = await response.text()
        console.error('Error response:', errorText)
        throw new Error(`Failed to fetch yearly statistics: ${response.status}`)
      }

      const data = await response.json()
      const yearlyIncomeDto = YearlyIncomeDto.fromApiResponse(data, yearlySelectedYear)
      setYearlyData(yearlyIncomeDto)
    } catch (err) {
      console.error('Error fetching yearly statistics:', err)
      setYearlyError(err.message)
    } finally {
      setYearlyLoading(false)
    }
  }

  const exportToPDF = async () => {
    if (!chartRef.current || !monthlyData) return

    try {
      const canvas = await html2canvas(chartRef.current, {
        backgroundColor: '#ffffff',
        scale: 2
      })
      
      const imgData = canvas.toDataURL('image/png')
      const pdf = new jsPDF('p', 'mm', 'a4')
      
      const pdfWidth = pdf.internal.pageSize.getWidth()
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width
      
      pdf.addImage(imgData, 'PNG', 0, 10, pdfWidth, pdfHeight)
      
      const monthName = months[selectedMonth - 1]
      pdf.save(`Cheltuieli_${monthName}_${selectedYear}.pdf`)
    } catch (error) {
      console.error('Error generating PDF:', error)
      alert('Failed to generate PDF')
    }
  }

  const exportToExcel = () => {
    if (!monthlyData || !monthlyData.hasData()) return

    try {
      const data = monthlyData.toExcelData()

      const worksheet = XLSX.utils.json_to_sheet(data)
      
      const colWidths = [
        { wch: 20 },
        { wch: 15 },
        { wch: 15 },
        { wch: 15 }
      ]
      worksheet['!cols'] = colWidths

      const workbook = XLSX.utils.book_new()
      const monthName = months[selectedMonth - 1]
      XLSX.utils.book_append_sheet(workbook, worksheet, `${monthName} ${selectedYear}`)
      
      XLSX.writeFile(workbook, `Cheltuieli_${monthName}_${selectedYear}.xlsx`)
    } catch (error) {
      console.error('Error generating Excel:', error)
      alert('Failed to generate Excel file')
    }
  }

  const exportYearlyToPDF = async () => {
    if (!lineChartRef.current || !yearlyData) return

    try {
      const canvas = await html2canvas(lineChartRef.current, {
        backgroundColor: '#ffffff',
        scale: 2
      })
      
      const imgData = canvas.toDataURL('image/png')
      const pdf = new jsPDF('p', 'mm', 'a4')
      
      const pdfWidth = pdf.internal.pageSize.getWidth()
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width
      
      pdf.addImage(imgData, 'PNG', 0, 10, pdfWidth, pdfHeight)
      
      pdf.save(`Venituri_${yearlySelectedYear}.pdf`)
    } catch (error) {
      console.error('Error generating PDF:', error)
      alert('Failed to generate PDF')
    }
  }

  const exportYearlyToExcel = () => {
    if (!yearlyData || !yearlyData.hasData()) return

    try {
      const data = yearlyData.toExcelData()

      const worksheet = XLSX.utils.json_to_sheet(data)
      
      const colWidths = [
        { wch: 15 },
        { wch: 15 }
      ]
      worksheet['!cols'] = colWidths

      const workbook = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(workbook, worksheet, `Venituri ${yearlySelectedYear}`)
      
      XLSX.writeFile(workbook, `Venituri_${yearlySelectedYear}.xlsx`)
    } catch (error) {
      console.error('Error generating Excel:', error)
      alert('Failed to generate Excel file')
    }
  }

  const renderLineChart = () => {
    if (!yearlyData || !yearlyData.hasData()) {
      return (
        <div className="no-data">
          <p>No data available for this year</p>
        </div>
      )
    }

    const monthlyIncome = yearlyData.getMonthlyIncomes()
    const maxIncome = yearlyData.getMaxIncome()
    const chartWidth = 600
    const chartHeight = 300
    const padding = { top: 40, right: 40, bottom: 50, left: 60 }
    const plotWidth = chartWidth - padding.left - padding.right
    const plotHeight = chartHeight - padding.top - padding.bottom

    const xScale = (month) => padding.left + ((month - 1) / 11) * plotWidth
    const yScale = (amount) => chartHeight - padding.bottom - (amount / (maxIncome || 1)) * plotHeight

    const points = monthlyIncome.map(d => `${xScale(d.month)},${yScale(d.amount)}`).join(' ')
    
    const pathData = monthlyIncome.map((d, i) => {
      const x = xScale(d.getMonth())
      const y = yScale(d.getAmount())
      return i === 0 ? `M ${x} ${y}` : `L ${x} ${y}`
    }).join(' ')

    const totalYearlyIncome = yearlyData.getTotalYearlyIncome()

    return (
      <div className="line-chart-container">
        <div className="chart-export-content" ref={lineChartRef}>
          <svg width={chartWidth} height={chartHeight} viewBox={`0 0 ${chartWidth} ${chartHeight}`}>
          {[0, 1, 2, 3, 4].map(i => {
            const y = chartHeight - padding.bottom - (i / 4) * plotHeight
            return (
              <g key={i}>
                <line
                  x1={padding.left}
                  y1={y}
                  x2={chartWidth - padding.right}
                  y2={y}
                  stroke="var(--border)"
                  strokeWidth="1"
                  strokeDasharray="5,5"
                  opacity="0.3"
                />
                <text
                  x={padding.left - 10}
                  y={y + 5}
                  textAnchor="end"
                  fill="var(--text-secondary)"
                  fontSize="12"
                >
                  €{((maxIncome / 4) * i).toFixed(0)}
                </text>
              </g>
            )
          })}

          {monthlyIncome.map((d, i) => {
            const x = xScale(d.getMonth())
            return (
              <text
                key={i}
                x={x}
                y={chartHeight - padding.bottom + 20}
                textAnchor="middle"
                fill="var(--text-secondary)"
                fontSize="12"
              >
                {d.getShortMonthName()}
              </text>
            )
          })}

          <path
            d={pathData}
            fill="none"
            stroke="#4CAF50"
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
          />

          {monthlyIncome.map((d, i) => (
            <g key={i}>
              <circle
                cx={xScale(d.getMonth())}
                cy={yScale(d.getAmount())}
                r="6"
                fill="#4CAF50"
                stroke="#fff"
                strokeWidth="2"
              />
              {d.getAmount() > 0 && (
                <text
                  x={xScale(d.getMonth())}
                  y={yScale(d.getAmount()) - 15}
                  textAnchor="middle"
                  fill="var(--text)"
                  fontSize="12"
                  fontWeight="bold"
                >
                  €{d.getAmount().toFixed(0)}
                </text>
              )}
            </g>
          ))}

          <line
            x1={padding.left}
            y1={chartHeight - padding.bottom}
            x2={chartWidth - padding.right}
            y2={chartHeight - padding.bottom}
            stroke="var(--text)"
            strokeWidth="2"
          />
          <line
            x1={padding.left}
            y1={padding.top}
            x2={padding.left}
            y2={chartHeight - padding.bottom}
            stroke="var(--text)"
            strokeWidth="2"
          />
        </svg>

        <div className="total-income">
          <span className="total-label">Total yearly income:</span>
          <span className="total-amount">€{yearlyData.getFormattedTotalYearlyIncome()}</span>
        </div>
        </div>
        
        <div className="export-buttons">
          <button className="export-btn pdf-btn" onClick={exportYearlyToPDF}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg>
            Export PDF
          </button>
          <button className="export-btn excel-btn" onClick={exportYearlyToExcel}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="9" y1="15" x2="15" y2="15" />
              <line x1="9" y1="11" x2="15" y2="11" />
              <line x1="9" y1="19" x2="13" y2="19" />
            </svg>
            Export Excel
          </button>
        </div>
      </div>
    )
  }

  const renderPieChart = () => {
    if (!monthlyData || !monthlyData.hasData()) {
      return (
        <div className="no-data">
          <p>No data available for this period</p>
        </div>
      )
    }

    const total = monthlyData.getTotal()
    const categories = monthlyData.getCategories()
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

    const slices = categories.map((category, index) => {
      const percentage = category.getPercentage(total)
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
        <div className="chart-export-content" ref={chartRef}>
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
                  <strong>{monthlyData.getCategoryByIndex(hoveredSlice).name}</strong>
                  <div className="tooltip-amount">
                    €{monthlyData.getCategoryByIndex(hoveredSlice).getFormattedAmount()}
                  </div>
                  <div className="tooltip-percentage">
                    {monthlyData.getCategoryByIndex(hoveredSlice).getFormattedPercentage(total)}%
                  </div>
                </div>
              </div>
            )}
          </div>
          <div className="total-expenses">
            <span className="total-label">Total cheltuieli:</span>
            <span className="total-amount">€{monthlyData.getFormattedTotal()}</span>
          </div>
        </div>
        <div className="chart-legend">
          {categories.map((category, index) => {
            const percentage = category.getPercentage(total).toFixed(0)
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
        <div className="export-buttons">
          <button className="export-btn pdf-btn" onClick={exportToPDF}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="16" y1="13" x2="8" y2="13" />
              <line x1="16" y1="17" x2="8" y2="17" />
              <polyline points="10 9 9 9 8 9" />
            </svg>
            Export PDF
          </button>
          <button className="export-btn excel-btn" onClick={exportToExcel}>
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
              <polyline points="14 2 14 8 20 8" />
              <line x1="9" y1="15" x2="15" y2="15" />
              <line x1="9" y1="11" x2="15" y2="11" />
              <line x1="9" y1="19" x2="13" y2="19" />
            </svg>
            Export Excel
          </button>
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
              <h2>Annual Income Statistics</h2>
              
              <div className="date-selector">
                <select 
                  value={yearlySelectedYear} 
                  onChange={(e) => setYearlySelectedYear(parseInt(e.target.value))}
                  className="year-select"
                >
                  {[2024, 2025, 2026].map(year => (
                    <option key={year} value={year}>{year}</option>
                  ))}
                </select>
              </div>

              {yearlyLoading && <div className="loading">Loading...</div>}
              {yearlyError && <div className="error-message">{yearlyError}</div>}
              {!yearlyLoading && !yearlyError && renderLineChart()}
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </>
  )
}
