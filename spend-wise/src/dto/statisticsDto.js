export class ExpenseCategoryDto {
  constructor(name, amount, color) {
    this.name = name
    this.amount = amount
    this.color = color
  }

  static fromApiResponse(apiCategory) {
    return new ExpenseCategoryDto(
      apiCategory.name,
      parseFloat(apiCategory.amount),
      apiCategory.color
    )
  }

  getPercentage(total) {
    return total > 0 ? (this.amount / total) * 100 : 0
  }

  getFormattedAmount() {
    return this.amount.toFixed(2)
  }

  getFormattedPercentage(total) {
    return this.getPercentage(total).toFixed(1)
  }
}

export class MonthlyExpensesDto {
  constructor(total, categories, month, year) {
    this.total = total
    this.categories = categories
    this.month = month
    this.year = year
  }

  static fromApiResponse(apiData, month, year) {
    const categories = (apiData.categories || []).map(cat => 
      ExpenseCategoryDto.fromApiResponse(cat)
    )
    
    return new MonthlyExpensesDto(
      parseFloat(apiData.total || 0),
      categories,
      month,
      year
    )
  }

  getTotal() {
    return this.total
  }

  getCategories() {
    return this.categories
  }

  hasData() {
    return this.categories.length > 0 && this.total > 0
  }

  getFormattedTotal() {
    return this.total.toFixed(2)
  }

  getCategoryByIndex(index) {
    return this.categories[index] || null
  }

  toExcelData() {
    const data = this.categories.map(category => ({
      'Categorie': category.name,
      'Suma (€)': category.getFormattedAmount(),
      'Procent (%)': category.getFormattedPercentage(this.total),
      'Culoare': category.color
    }))

    data.push({
      'Categorie': 'TOTAL',
      'Suma (€)': this.getFormattedTotal(),
      'Procent (%)': '100.00',
      'Culoare': ''
    })

    return data
  }
}

export class MonthlyIncomeDto {
  constructor(month, amount) {
    this.month = month
    this.amount = amount
  }

  getMonth() {
    return this.month
  }

  getAmount() {
    return this.amount
  }

  getFormattedAmount() {
    return this.amount.toFixed(2)
  }

  getMonthName() {
    const monthNames = ['January', 'February', 'March', 'April', 'May', 'June', 
                        'July', 'August', 'September', 'October', 'November', 'December']
    return monthNames[this.month - 1] || 'Unknown'
  }

  getShortMonthName() {
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 
                        'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
    return monthNames[this.month - 1] || 'N/A'
  }
}

export class YearlyIncomeDto {
  constructor(incomeByMonth, year) {
    this.incomeByMonth = incomeByMonth
    this.year = year
  }

  static fromApiResponse(apiData, year) {
    const monthlyIncomes = Object.entries(apiData.incomeByMonth || {})
      .map(([month, amount]) => new MonthlyIncomeDto(
        parseInt(month),
        parseFloat(amount)
      ))
      .sort((a, b) => a.month - b.month)

    return new YearlyIncomeDto(monthlyIncomes, year)
  }

  getMonthlyIncomes() {
    return this.incomeByMonth
  }

  getTotalYearlyIncome() {
    return this.incomeByMonth.reduce((sum, monthData) => sum + monthData.amount, 0)
  }

  getFormattedTotalYearlyIncome() {
    return this.getTotalYearlyIncome().toFixed(2)
  }

  getMaxIncome() {
    if (this.incomeByMonth.length === 0) return 0
    return Math.max(...this.incomeByMonth.map(d => d.amount))
  }

  hasData() {
    return this.incomeByMonth.length > 0
  }

  toExcelData() {
    const data = this.incomeByMonth.map(monthData => ({
      'Luna': monthData.getMonthName(),
      'Venituri (€)': monthData.getFormattedAmount()
    }))

    data.push({
      'Luna': 'TOTAL',
      'Venituri (€)': this.getFormattedTotalYearlyIncome()
    })

    return data
  }
}
