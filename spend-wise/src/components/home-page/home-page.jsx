import { useState, useEffect } from 'react'
import './home-page.scss'
import Header from '../header/header'
import Footer from '../footer/footer'
import useTheme from '../../hooks/useTheme'
import { getUserIdFromToken } from '../../services/authService'
import { CreateTransactionDto, UpdateTransactionDto, TransactionDto } from '../../dto/transactionDto'
import { BudgetAccountDto, MonthlyStatisticsDto } from '../../dto/budgetAccountDto'

export default function HomePage() {
	const { theme } = useTheme()
	const [currentPage, setCurrentPage] = useState(0)
	const [showAddDialog, setShowAddDialog] = useState(false)
	const [accountId, setAccountId] = useState(null)
	const [accountBalance, setAccountBalance] = useState(0)
	const [loading, setLoading] = useState(true)
	const [error, setError] = useState(null)
	const [monthlyIncome, setMonthlyIncome] = useState(0)
	const [monthlyExpenses, setMonthlyExpenses] = useState(0)
	const [transactions, setTransactions] = useState([])
	const [totalPages, setTotalPages] = useState(0)
	const [totalElements, setTotalElements] = useState(0)
	const [transactionsLoading, setTransactionsLoading] = useState(false)
	const [showFilter, setShowFilter] = useState(false)
	const [filterType, setFilterType] = useState('')
	const [filterCategory, setFilterCategory] = useState('')
	const [sortBy, setSortBy] = useState('date')
	const [sortDirection, setSortDirection] = useState('DESC')
	const [selectedTransaction, setSelectedTransaction] = useState(null)
	const [showTransactionDialog, setShowTransactionDialog] = useState(false)
	const [isEditMode, setIsEditMode] = useState(false)
	const [editFormData, setEditFormData] = useState({
		value: '',
		category: '',
		description: ''
	})
	const [addFormData, setAddFormData] = useState({
		type: 'EXPENSE',
		category: '',
		value: '',
		description: ''
	})
	const [exchangeRates, setExchangeRates] = useState([])
	const [exchangeRatesLoading, setExchangeRatesLoading] = useState(false)
	
	const currentDate = new Date()
	const currentMonth = currentDate.getMonth() + 1
	const currentYear = currentDate.getFullYear()
	
	const difference = monthlyIncome - monthlyExpenses
	
	const total = monthlyIncome + monthlyExpenses
	const incomePercentage = total > 0 ? (monthlyIncome / total) * 100 : 50
	const expensesPercentage = total > 0 ? (monthlyExpenses / total) * 100 : 50
	
	const transactionsPerPage = 5
	
	const fetchAccountData = async () => {
		const userId = getUserIdFromToken()
		const token = localStorage.getItem('token')

		if (!userId || !token) {
			setError('User not authenticated')
			setLoading(false)
			return
		}

		try {
			const accountIdResponse = await fetch(
				`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/budget-accounts/user/${userId}`,
				{
					method: 'GET',
					headers: {
						'Authorization': `Bearer ${token}`,
						'Accept': 'application/json'
					}
				}
			)

			if (!accountIdResponse.ok) {
				throw new Error('Failed to fetch account ID')
			}

			const accountIdText = await accountIdResponse.text()
			const fetchedAccountId = accountIdText.replace(/"/g, '')
			setAccountId(fetchedAccountId)

			const accountResponse = await fetch(
				`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/budget-accounts/${fetchedAccountId}`,
				{
					method: 'GET',
					headers: {
						'Authorization': `Bearer ${token}`,
						'Accept': 'application/json'
					}
				}
			)

			if (!accountResponse.ok) {
				throw new Error('Failed to fetch account details')
			}

			const accountData = await accountResponse.json()
			const budgetAccount = BudgetAccountDto.fromApiResponse(accountData)
			setAccountBalance(budgetAccount.getBalance())
			
			await fetchMonthlyStatistics(fetchedAccountId, token)
		} catch (err) {
			setError(err.message)
		} finally {
			setLoading(false)
		}
	}
	
	const fetchMonthlyStatistics = async (budgetAccountId, token) => {
		try {
			const expensesUrl = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/statistics/expenses/${budgetAccountId}?month=${currentMonth}&year=${currentYear}`
			const expensesResponse = await fetch(expensesUrl, {
				method: 'GET',
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				}
			})
			
			const expensesData = expensesResponse.ok ? await expensesResponse.json() : null
			
			const incomeUrl = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/statistics/income/${budgetAccountId}/${currentYear}`
			const incomeResponse = await fetch(incomeUrl, {
				method: 'GET',
				headers: {
					'Authorization': `Bearer ${token}`,
					'Content-Type': 'application/json'
				}
			})
			
			const incomeData = incomeResponse.ok ? await incomeResponse.json() : null
			
			const statistics = MonthlyStatisticsDto.fromApiResponses(expensesData, incomeData, currentMonth)
			setMonthlyIncome(statistics.monthlyIncome)
			setMonthlyExpenses(statistics.monthlyExpenses)
		} catch (err) {
		}
	}
	
	const fetchTransactions = async (budgetAccountId, token, page = 0) => {
		setTransactionsLoading(true)
		
		try {
			let url = `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/tranzactions/account/${budgetAccountId}/filter?page=${page}&size=${transactionsPerPage}&sortBy=${sortBy}&sortDirection=${sortDirection}`
			
			if (filterType) {
				url += `&type=${filterType}`
			}
			if (filterCategory) {
				url += `&category=${filterCategory}`
			}
			
			const response = await fetch(url, {
				method: 'GET',
				headers: {
					'Authorization': `Bearer ${token}`,
					'Accept': 'application/json'
				}
			})
			
			if (!response.ok) {
				throw new Error('Failed to fetch transactions')
			}
			
			const data = await response.json()
			
			const transformedTransactions = TransactionDto.fromApiResponseList(data.content)
			
			setTransactions(transformedTransactions)
			setTotalPages(data.totalPages)
			setTotalElements(data.totalElements)
		} catch (err) {
		} finally {
			setTransactionsLoading(false)
		}
	}
	
	const fetchExchangeRates = async () => {
		setExchangeRatesLoading(true)
		try {
			const response = await fetch('https://api.exchangerate-api.com/v4/latest/EUR')
			
			if (!response.ok) {
				throw new Error('Failed to fetch exchange rates')
			}
			
			const data = await response.json()
			
			const currencies = [
				{ code: 'USD', flag: '🇺🇸', name: 'US Dollar' },
				{ code: 'GBP', flag: '🇬🇧', name: 'British Pound' },
				{ code: 'RON', flag: '🇷🇴', name: 'Romanian Leu' },
				{ code: 'CHF', flag: '🇨🇭', name: 'Swiss Franc' },
			]
			
			const rates = currencies.map(curr => ({
				currency: curr.code,
				rate: data.rates[curr.code] || 0,
				flag: curr.flag,
				name: curr.name
			}))
			
			setExchangeRates(rates)
		} catch (err) {
			console.error('Error fetching exchange rates:', err)
			setExchangeRates([
				{ currency: 'USD', rate: 1.05, flag: '🇺🇸', name: 'US Dollar' },
				{ currency: 'GBP', rate: 0.83, flag: '🇬🇧', name: 'British Pound' },
				{ currency: 'RON', rate: 4.97, flag: '🇷🇴', name: 'Romanian Leu' },
				{ currency: 'CHF', rate: 0.93, flag: '🇨🇭', name: 'Swiss Franc' },
			])
		} finally {
			setExchangeRatesLoading(false)
		}
	}
	
	useEffect(() => {
		fetchAccountData()
		fetchExchangeRates()
	}, [])
	
	useEffect(() => {
		if (accountId) {
			const token = localStorage.getItem('token')
			if (token) {
				setCurrentPage(0) 
				fetchTransactions(accountId, token, 0)
			}
		}
	}, [accountId, filterType, filterCategory, sortBy, sortDirection])
	
	useEffect(() => {
		if (accountId) {
			const token = localStorage.getItem('token')
			if (token) {
				fetchTransactions(accountId, token, currentPage)
			}
		}
	}, [currentPage])
	
	const handleClearFilters = () => {
		setFilterType('')
		setFilterCategory('')
		setSortBy('date')
		setSortDirection('DESC')
	}
	
	const handleTransactionClick = (transaction) => {
		setSelectedTransaction(transaction)
		setShowTransactionDialog(true)
	}
	
	const handleCloseTransactionDialog = () => {
		setShowTransactionDialog(false)
		setSelectedTransaction(null)
	}
	
	const handleEditTransaction = () => {
		setIsEditMode(true)
		setEditFormData({
			value: Math.abs(selectedTransaction.amount),
			category: selectedTransaction.category,
			description: selectedTransaction.description
		})
	}
	
	const handleCancelEdit = () => {
		setIsEditMode(false)
		setEditFormData({ value: '', category: '', description: '' })
	}
	
	const handleSaveEdit = async () => {
		if (!selectedTransaction) return
		
		const token = localStorage.getItem('token')
		
		if (!editFormData.value || editFormData.value <= 0) {
			alert('Please enter a valid amount')
			return
		}
		if (!editFormData.category) {
			alert('Please select a category')
			return
		}
		
		try {
			const updateDto = UpdateTransactionDto.fromTransaction(selectedTransaction, editFormData)
			const requestBody = updateDto
		
			
			const response = await fetch(
				`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/tranzactions/${selectedTransaction.id}`,
				{
					method: 'PUT',
					headers: {
						'Authorization': `Bearer ${token}`,
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(requestBody)
				}
			)
			
			
			if (response.ok) {
				const responseData = await response.json()
				setIsEditMode(false)
				setShowTransactionDialog(false)
				fetchTransactions(accountId, token, currentPage)
				fetchAccountData()
			} else {
				const errorData = await response.json().catch(() => null)
				alert(errorData?.message || 'Failed to update transaction')
			}
		} catch (err) {
			alert('Error updating transaction')
		}
	}
	
	const handleAddTransaction = async () => {
		const token = localStorage.getItem('token')
		
		if (!addFormData.value || addFormData.value <= 0) {
			alert('Please enter a valid amount')
			return
		}
		if (!addFormData.category) {
			alert('Please select a category')
			return
		}
		if (!addFormData.type) {
			alert('Please select a transaction type')
			return
		}
		
		try {
			const createDto = CreateTransactionDto.fromFormData(accountId, addFormData)
			const requestBody = createDto
			
			const response = await fetch(
				`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/tranzactions`,
				{
					method: 'POST',
					headers: {
						'Authorization': `Bearer ${token}`,
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(requestBody)
				}
			)
			
			if (response.ok) {
				const responseData = await response.json()
				setShowAddDialog(false)
				setAddFormData({ type: 'EXPENSE', category: '', value: '', description: '' })
				fetchTransactions(accountId, token, currentPage)
				fetchAccountData()
			} else {
				const errorData = await response.json().catch(() => null)
				console.error('Create Failed! Error Response:', errorData)
				alert(errorData?.message || 'Failed to create transaction')
			}
		} catch (err) {
			console.error('Error creating transaction:', err)
			alert('Error creating transaction')
		}
	}
	
	const handleDeleteTransaction = async () => {
		if (!selectedTransaction) return
		
		const token = localStorage.getItem('token')
		const confirm = window.confirm('Are you sure you want to delete this transaction?')
		
		if (!confirm) return
		
		try {
			const response = await fetch(
				`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:9090'}/api/tranzactions/${selectedTransaction.id}`,
				{
					method: 'DELETE',
					headers: {
						'Authorization': `Bearer ${token}`,
					}
				}
			)
			
			if (response.ok) {
				setShowTransactionDialog(false)
				fetchTransactions(accountId, token, currentPage)
				fetchAccountData()
			} else {
				alert('Failed to delete transaction')
			}
		} catch (err) {
			alert('Error deleting transaction')
		}
	}
	
	const renderDonutChart = () => {
		const radius = 120
		const strokeWidth = 40
		const centerX = 150
		const centerY = 150
		const circumference = 2 * Math.PI * radius
		
		const incomeDashOffset = 0
		const incomeDashArray = `${(incomePercentage / 100) * circumference} ${circumference}`
		
		const expensesDashOffset = -((incomePercentage / 100) * circumference)
		const expensesDashArray = `${(expensesPercentage / 100) * circumference} ${circumference}`
		
		return (
			<div className="donut-chart">
				<svg width="300" height="300" viewBox="0 0 300 300">
					<circle
						cx={centerX}
						cy={centerY}
						r={radius}
						fill="transparent"
						stroke="#4CAF50"
						strokeWidth={strokeWidth}
						strokeDasharray={incomeDashArray}
						strokeDashoffset={incomeDashOffset}
						transform={`rotate(-90 ${centerX} ${centerY})`}
						strokeLinecap="butt"
					/>
					<circle
						cx={centerX}
						cy={centerY}
						r={radius}
						fill="transparent"
						stroke="#f44336"
						strokeWidth={strokeWidth}
						strokeDasharray={expensesDashArray}
						strokeDashoffset={expensesDashOffset}
						transform={`rotate(-90 ${centerX} ${centerY})`}
						strokeLinecap="butt"
					/>
				</svg>
				<div className="donut-center">
					<div className="difference-label">Balance</div>
					<div className={`difference-amount ${difference >= 0 ? 'positive' : 'negative'}`}>
						€{difference.toFixed(2)}
					</div>
				</div>
				<div className="donut-legend">
					<div className="legend-item">
						<div className="legend-color" style={{ backgroundColor: '#4CAF50' }}></div>
						<span>Income: €{monthlyIncome.toFixed(2)}</span>
					</div>
					<div className="legend-item">
						<div className="legend-color" style={{ backgroundColor: '#f44336' }}></div>
						<span>Expenses: €{monthlyExpenses.toFixed(2)}</span>
					</div>
				</div>
			</div>
		)
	}
	
	return (
		<>
			<Header />
			<div className="home-page" data-theme={theme}>
				<div className="home-container">
					{loading && (
						<div className="loading-message">Loading account data...</div>
					)}
					{error && (
						<div className="error-message">{error}</div>
					)}
					{!loading && !error && (
						<div className="home-content">
							<div className="left-column">
							<div className="account-card">
								<div className="card-label">Account Balance</div>
								<div className="account-balance">€{accountBalance.toFixed(2)}</div>
							</div>
							
							<div className="monthly-card">
								<h3>Current Month</h3>
									{renderDonutChart()}
								</div>
							</div>
						<div className="right-column">
							<div className="transactions-card">
								<div className="card-header">
									<h3>My Transactions</h3>
									<div className="header-buttons">
										<button 
											className={`filter-btn ${showFilter ? 'active' : ''}`}
											onClick={() => setShowFilter(!showFilter)}
										>
											<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
												<polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3" />
											</svg>
											Filter
										</button>
										<button 
											className="add-btn"
											onClick={() => setShowAddDialog(true)}
										>
											<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
												<line x1="12" y1="5" x2="12" y2="19" />
												<line x1="5" y1="12" x2="19" y2="12" />
											</svg>
											Add Transaction
										</button>
									</div>
								</div>
								
								{showFilter && (
									<div className="filter-section">
										<div className="filter-row">
											<div className="filter-group">
												<label>Type</label>
												<select 
													value={filterType} 
													onChange={(e) => setFilterType(e.target.value)}
												>
													<option value="">All</option>
													<option value="INCOME">Income</option>
													<option value="EXPENSE">Expense</option>
												</select>
											</div>
											
											<div className="filter-group">
												<label>Category</label>
												<select 
													value={filterCategory} 
													onChange={(e) => setFilterCategory(e.target.value)}
												>
													<option value="">All</option>
													<option value="FOOD">Food</option>
													<option value="CLOTHING">Clothing</option>
													<option value="TRANSPORT">Transport</option>
													<option value="BILLS">Bills</option>
													<option value="ENTERTAINMENT">Entertainment</option>
													<option value="HEALTH">Health</option>
													<option value="EDUCATION">Education</option>
												<option value="TRAVEL">Travel</option>
												<option value="GIFTS">Gifts</option>
												<option value="OTHER">Other</option>
											</select>
										</div>
									</div>
									
									<div className="filter-row">
										<div className="filter-group">
											<label>Sort By</label>
											<select 
												value={`${sortBy}-${sortDirection}`} 
												onChange={(e) => {
													const [field, direction] = e.target.value.split('-')
													setSortBy(field)
													setSortDirection(direction)
												}}
											>
												<option value="date-DESC">Date (Newest First)</option>
												<option value="date-ASC">Date (Oldest First)</option>
												<option value="value-DESC">Value (Highest First)</option>
												<option value="value-ASC">Value (Lowest First)</option>
											</select>
										</div>
									</div>
									
									<div className="filter-actions">
										<button className="clear-btn" onClick={handleClearFilters}>
											Clear All Filters
										</button>
									</div>
									</div>
								)}
								
								{transactionsLoading ? (
									<div className="loading-message">Loading transactions...</div>
								) : (
									<>
								<div className="transactions-list">
									{transactions.map(transaction => (
										<div 
											key={transaction.id} 
											className={`transaction-item ${transaction.type}`}
											onClick={() => handleTransactionClick(transaction)}
										>
													<div className="transaction-info">
														<div className="transaction-description">{transaction.description}</div>
														<div className="transaction-date">{transaction.date}</div>
													</div>
													<div className={`transaction-amount ${transaction.type}`}>
														€{Math.abs(transaction.amount).toFixed(2)}
													</div>
												</div>
											))}
										</div>
										
										<div className="pagination">
											<button 
												onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
												disabled={currentPage === 0}
											>
												&lt;
											</button>
											<span className="page-info">{currentPage + 1} / {totalPages}</span>
											<button 
												onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
												disabled={currentPage === totalPages - 1}
											>
												&gt;
											</button>
										</div>
									</>
								)}
							</div>
							
							<div className="exchange-card">
								<h3>Exchange Rates (EUR Base)</h3>
								{exchangeRatesLoading ? (
									<div className="loading-message">Loading rates...</div>
								) : (
									<div className="exchange-list">
										{exchangeRates.map(rate => (
											<div key={rate.currency} className="exchange-item">
												<div className="currency-info">
													<span className="currency-code">{rate.currency}</span>
												</div>
												<div className="exchange-rate">€1 = {rate.rate.toFixed(4)} {rate.currency}</div>
											</div>
										))}
									</div>
								)}
							</div>
						</div>
					</div>
					)}
				</div>
				
				{showAddDialog && (
					<div className="dialog-overlay" onClick={() => setShowAddDialog(false)}>
						<div className="dialog" onClick={(e) => e.stopPropagation()}>
						<div className="dialog-header">
							<h3>Add Transaction</h3>
							<button className="close-btn" onClick={() => setShowAddDialog(false)}>×</button>
						</div>
						<div className="dialog-content">
							<div className="edit-form">
								<div className="form-group">
									<label>Type *</label>
									<select 
										value={addFormData.type}
										onChange={(e) => setAddFormData({...addFormData, type: e.target.value})}
									>
										<option value="INCOME">Income</option>
										<option value="EXPENSE">Expense</option>
									</select>
								</div>
								<div className="form-group">
									<label>Amount *</label>
									<input 
										type="number" 
										min="0.01"
										step="0.01"
										value={addFormData.value}
										onChange={(e) => setAddFormData({...addFormData, value: e.target.value})}
										placeholder="Enter amount"
									/>
								</div>
								<div className="form-group">
									<label>Category *</label>
									<select 
										value={addFormData.category}
										onChange={(e) => setAddFormData({...addFormData, category: e.target.value})}
									>
										<option value="">Select category</option>
										<option value="FOOD">Food</option>
										<option value="CLOTHING">Clothing</option>
										<option value="TRANSPORT">Transport</option>
										<option value="BILLS">Bills</option>
										<option value="ENTERTAINMENT">Entertainment</option>
										<option value="HEALTH">Health</option>
										<option value="EDUCATION">Education</option>
										<option value="TRAVEL">Travel</option>
										<option value="GIFTS">Gifts</option>
										<option value="OTHER">Other</option>
									</select>
								</div>
								<div className="form-group">
									<label>Description</label>
									<input 
										type="text" 
										value={addFormData.description}
										onChange={(e) => setAddFormData({...addFormData, description: e.target.value})}
										placeholder="Enter description (optional)"
									/>
								</div>
								<div className="dialog-actions">
									<button className="cancel-btn" onClick={() => {
										setShowAddDialog(false)
										setAddFormData({ type: 'EXPENSE', category: '', value: '', description: '' })
									}}>
										Cancel
									</button>
									<button className="save-btn" onClick={handleAddTransaction}>
										Add Transaction
									</button>
								</div>
							</div>
						</div>
						</div>
					</div>
				)}
				
				{showTransactionDialog && selectedTransaction && (
					<div className="dialog-overlay" onClick={handleCloseTransactionDialog}>
						<div className="dialog transaction-dialog" onClick={(e) => e.stopPropagation()}>
							<div className="dialog-header">
								<h3>Transaction Details</h3>
								<button className="close-btn" onClick={handleCloseTransactionDialog}>×</button>
							</div>
							<div className="dialog-content">
								{isEditMode ? (
									<div className="edit-form">
										<div className="form-group">
											<label>Type (Read-only)</label>
											<input 
												type="text" 
												value={selectedTransaction.type === 'income' ? 'Income' : 'Expense'}
												disabled
												className="readonly-input"
											/>
										</div>
										<div className="form-group">
											<label>Amount *</label>
											<input 
												type="number" 
												min="0.01"
												step="0.01"
												value={editFormData.value}
												onChange={(e) => setEditFormData({...editFormData, value: e.target.value})}
												placeholder="Enter amount"
											/>
										</div>
										<div className="form-group">
											<label>Category *</label>
											<select 
												value={editFormData.category}
												onChange={(e) => setEditFormData({...editFormData, category: e.target.value})}
											>
												<option value="">Select category</option>
												<option value="FOOD">Food</option>
												<option value="CLOTHING">Clothing</option>
												<option value="TRANSPORT">Transport</option>
												<option value="BILLS">Bills</option>
												<option value="ENTERTAINMENT">Entertainment</option>
												<option value="HEALTH">Health</option>
												<option value="EDUCATION">Education</option>
												<option value="TRAVEL">Travel</option>
												<option value="GIFTS">Gifts</option>
												<option value="OTHER">Other</option>
											</select>
										</div>
										<div className="form-group">
											<label>Description</label>
											<input 
												type="text" 
												value={editFormData.description}
												onChange={(e) => setEditFormData({...editFormData, description: e.target.value})}
												placeholder="Enter description (optional)"
											/>
										</div>
										<div className="form-group">
											<label>Date (Read-only)</label>
											<input 
												type="text" 
												value={selectedTransaction.date}
												disabled
												className="readonly-input"
											/>
										</div>
										<div className="dialog-actions">
											<button className="cancel-btn" onClick={handleCancelEdit}>
												Cancel
											</button>
											<button className="save-btn" onClick={handleSaveEdit}>
												Save
											</button>
										</div>
									</div>
								) : (
									<div className="transaction-details">
										<div className="detail-row">
											<span className="detail-label">Type:</span>
											<span className={`detail-value ${selectedTransaction.type}`}>
												{selectedTransaction.type === 'income' ? 'Income' : 'Expense'}
											</span>
										</div>
										<div className="detail-row">
											<span className="detail-label">Amount:</span>
											<span className={`detail-value amount ${selectedTransaction.type}`}>
												€{Math.abs(selectedTransaction.amount).toFixed(2)}
											</span>
										</div>
										<div className="detail-row">
											<span className="detail-label">Description:</span>
											<span className="detail-value">{selectedTransaction.description}</span>
										</div>
										<div className="detail-row">
											<span className="detail-label">Category:</span>
											<span className="detail-value">{selectedTransaction.category || 'N/A'}</span>
										</div>
										<div className="detail-row">
											<span className="detail-label">Date:</span>
											<span className="detail-value">{selectedTransaction.date}</span>
										</div>
									</div>
								)}
								{!isEditMode && (
									<div className="dialog-actions">
										<button className="edit-btn" onClick={handleEditTransaction}>
											Edit
										</button>
										<button className="delete-btn" onClick={handleDeleteTransaction}>
											Delete
										</button>
									</div>
								)}
							</div>
						</div>
					</div>
				)}
			</div>
			<Footer />
		</>
	)
}
