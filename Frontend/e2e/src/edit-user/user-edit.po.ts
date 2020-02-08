import {browser, by, element} from 'protractor';

export class MyProfilePage {
  navigateTo() {
    return browser.get('/myProfile');
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

  getChangePasswordButton() {
    return element(by.id('changePassword'));
  }

  getEditButton() {
    return element(by.id('editButton'));
  }

  getSnackBar() {
    return element(by.className('mat-simple-snackbar'));
  }

}

