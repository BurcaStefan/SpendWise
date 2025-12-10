export class CreateTransactionDto {
  constructor(accountId, tranzactionType, categoryType, value, recurrent, description) {
    this.accountId = accountId
    this.tranzactionType = tranzactionType
    this.categoryType = categoryType
    this.value = value
    this.recurrent = recurrent
    this.description = description
  }

  static fromFormData(accountId, formData) {
    return new CreateTransactionDto(
      accountId,
      formData.type,
      formData.category,
      parseFloat(formData.value),
      false,
      formData.description || null
    )
  }
}

export class UpdateTransactionDto {
  constructor(tranzactionType, categoryType, value, recurrent, description, date) {
    this.tranzactionType = tranzactionType
    this.categoryType = categoryType
    this.value = value
    this.recurrent = recurrent
    this.description = description
    this.date = date
  }

  static fromTransaction(transaction, editFormData) {
    return new UpdateTransactionDto(
      transaction.type.toUpperCase(),
      editFormData.category,
      parseFloat(editFormData.value),
      false,
      editFormData.description || null,
      transaction.date
    )
  }
}

export class TransactionDto {
  constructor(id, date, description, amount, type, category) {
    this.id = id
    this.date = date
    this.description = description
    this.amount = amount
    this.type = type
    this.category = category
  }

  static fromApiResponse(apiTransaction) {
    return new TransactionDto(
      apiTransaction.tranzactionId,
      apiTransaction.date,
      apiTransaction.description || apiTransaction.category,
      apiTransaction.type === 'INCOME' ? apiTransaction.value : -apiTransaction.value,
      apiTransaction.type.toLowerCase(),
      apiTransaction.category
    )
  }

  static fromApiResponseList(apiTransactions) {
    return apiTransactions.map(tx => TransactionDto.fromApiResponse(tx))
  }
}
