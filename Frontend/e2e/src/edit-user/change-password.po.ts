import {browser, by, element} from 'protractor';

export class ChangePasswordPage {
  navigateTo() {
    return browser.get('/changePassword');
  }

  getOldPasswordInput() {
    return element(by.id('oldPassword'));
  }

  getPasswordInput() {
    return element(by.id('password'));
  }

  getRepeatedPasswordInput() {
    return element(by.id('repeatedPassword'));
  }

  getReturnButton() {
    return element(by.id('returnButton'));
  }

  getEditButton() {
    return element(by.id('editButton'));
  }

  getRepeatedPasswordError() {
    return element(by.id('repeatedPasswordError'));
  }

  getPasswordError() {
    return element(by.id('newPasswordError'));
  }

  getSnackBar() {
    return element(by.className('mat-simple-snackbar'));
  }

}

