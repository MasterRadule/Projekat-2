import {browser} from 'protractor';
import {RegisterPage} from './register.po';

describe('register page', () => {
  let page: RegisterPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
  });

  beforeEach(() => {
    page = new RegisterPage();
    page.navigateTo();
  });

  it('should show error message on failed email validation', () => {
    page.getEmailInput().click();
    page.getEmailInput().sendKeys('JennifferHookerexample.com');
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    expect(page.getEmailErrorMessage().isDisplayed()).toBe(true);
  });

  it('should show error messages when password validations fails', () => {
    page.getPasswordInput().click();
    page.getPasswordInput().sendKeys('123');
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    expect(page.getPasswordErrorMessage().isPresent()).toBe(true);
    expect(page.getPasswordLengthErrorMessage().isPresent()).toBe(true);
  });

  it('should show error message if email is taken', () => {
    page.getFirstNameInput().sendKeys("Petar");
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    page.getLastNameInput().sendKeys("Petrovic");
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    page.getEmailInput().sendKeys('Dickens@example.com');
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    page.getPasswordInput().sendKeys('KtsNvt1*');
    expect(page.getRegisterButton().isEnabled()).toBe(true);
    page.getRegisterButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Email already taken!');
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/register');
    });
  });

  it('should register', () => {
    page.getFirstNameInput().sendKeys("Petar");
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    page.getLastNameInput().sendKeys("Petrovic");
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    page.getEmailInput().sendKeys('petar.petrovic@example.com');
    expect(page.getRegisterButton().isEnabled()).toBe(false);
    page.getPasswordInput().sendKeys('KtsNvt1*');
    expect(page.getRegisterButton().isEnabled()).toBe(true);
    page.getRegisterButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Registration successful! Check mail to verify account!');
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/login');
    });
  });

});
