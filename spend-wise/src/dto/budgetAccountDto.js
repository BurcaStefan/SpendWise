export class BudgetAccountDto {
  constructor(budgetAccountId, userId, value) {
    this.budgetAccountId = budgetAccountId
    this.userId = userId
    this.value = value
  }

  static fromApiResponse(apiAccount) {
    return new BudgetAccountDto(
      apiAccount.budgetAccountId,
      apiAccount.userId,
      apiAccount.value
    )
  }

  getAccountId() {
    return this.budgetAccountId
  }

  getBalance() {
    return this.value
  }
}

export class MonthlyStatisticsDto {
  constructor(monthlyIncome, monthlyExpenses) {
    this.monthlyIncome = monthlyIncome
    this.monthlyExpenses = monthlyExpenses
  }

  static fromApiResponses(expensesData, incomeData, currentMonth) {
    const totalExpenses = expensesData?.total || 0
    const currentMonthIncome = incomeData?.incomeByMonth?.[currentMonth.toString()] || 0
    
    return new MonthlyStatisticsDto(
      currentMonthIncome,
      totalExpenses
    )
  }

  getDifference() {
    return this.monthlyIncome - this.monthlyExpenses
  }

  getIncomePercentage() {
    const total = this.monthlyIncome + this.monthlyExpenses
    return total > 0 ? (this.monthlyIncome / total) * 100 : 50
  }

  getExpensesPercentage() {
    const total = this.monthlyIncome + this.monthlyExpenses
    return total > 0 ? (this.monthlyExpenses / total) * 100 : 50
  }
}
