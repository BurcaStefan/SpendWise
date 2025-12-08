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

export class RegisterUserDto {
  constructor(firstname, lastname, email, password) {
    this.firstname = firstname
    this.lastname = lastname
    this.email = email
    this.password = password
  }

  static fromFormData(data) {
    return new RegisterUserDto(
      data.firstName || '',
      data.lastName || '',
      data.email || '',
      data.password || ''
    )
  }
}
