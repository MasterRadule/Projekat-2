import {browser, by, element} from 'protractor';

export class LoginPage {
  navigateTo() {
    return browser.get('/login');
  }

  getEmailInput() {
    return element(by.id('email'));
  }

  getPasswordInput() {
    return element(by.id('password'));
  }

  getLoginButton() {
    return element(by.id('loginButton'));
  }

  getErrorMessage() {
    return element(by.id('errorEmail'));
  }

  getSnackBar() {
    return element(by.className('mat-simple-snackbar'));
  }

  login(email, password) {
    this.navigateTo();
    this.getEmailInput().sendKeys(email);
    this.getPasswordInput().sendKeys(password);
    this.getLoginButton().click();
  }

}

