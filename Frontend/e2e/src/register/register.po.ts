import {browser, by, element} from 'protractor';

export class RegisterPage {
  navigateTo() {
    return browser.get('/register');
  }

  getFirstNameInput() {
    return element(by.id('firstName'));
  }

  getLastNameInput() {
    return element(by.id('lastName'));
  }

  getEmailInput() {
    return element(by.id('email'));
  }

  getPasswordInput() {
    return element(by.id('password'));
  }

  getRegisterButton() {
    return element(by.id('registerButton'));
  }

  getEmailErrorMessage() {
    return element(by.id('emailError'));
  }

  getPasswordErrorMessage() {
    return element(by.id('passwordError'));
  }

  getPasswordLengthErrorMessage() {
    return element(by.id('passwordLengthError'));
  }

  getSnackBar() {
    return element(by.className('mat-simple-snackbar'));
  }
}

