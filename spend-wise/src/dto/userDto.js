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

export class LoginDto {
  constructor(email, password) {
    this.email = email
    this.password = password
  }

  static fromCredentials(credentials) {
    return new LoginDto(
      credentials.email || '',
      credentials.password || ''
    )
  }
}

export class ContactFormDto {
  constructor(name, email, subject, message) {
    this.name = name
    this.email = email
    this.subject = subject
    this.message = message
  }

  static fromFormData(data) {
    return new ContactFormDto(
      data.name || '',
      data.email || '',
      data.subject || '',
      data.message || ''
    )
  }
}
