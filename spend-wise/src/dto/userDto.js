export class UpdateNamesDto {
  constructor(firstname, lastname) {
    this.firstname = firstname
    this.lastname = lastname
  }

  static fromFormData(data) {
    return new UpdateNamesDto(
      data.firstName || '',
      data.lastName || ''
    )
  }
}

export class UpdatePasswordDto {
  constructor(currentPassword, newPassword) {
    this.currentPassword = currentPassword
    this.newPassword = newPassword
  }

  static fromFormData(data) {
    return new UpdatePasswordDto(
      data.currentPassword || '',
      data.password || ''
    )
  }
}
