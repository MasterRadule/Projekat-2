import {browser} from 'protractor';
import {LoginPage} from './login.po';

describe('login page', () => {
  let page: LoginPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
  });

  beforeEach(() => {
    page = new LoginPage();
    page.navigateTo();
  });

  it('should not login with wrong credentials', () => {
    page.getEmailInput().sendKeys('JennifferHooker@example.com');
    expect(page.getLoginButton().isEnabled()).toBe(false);
    page.getPasswordInput().sendKeys('111');
    expect(page.getLoginButton().isEnabled()).toBe(true);
    page.getLoginButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Invalid email or password');
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/login')
    });
  });

  it('should show error message on failed email validation', () => {
    page.getEmailInput().click();
    page.getEmailInput().sendKeys('JennifferHookerexample.com');
    expect(page.getLoginButton().isEnabled()).toBe(false);
    expect(page.getErrorMessage().isDisplayed()).toBe(true);
  });

  it('should login', () => {
    page.getEmailInput().sendKeys('JennifferHooker@example.com');
    expect(page.getLoginButton().isEnabled()).toBe(false);
    page.getPasswordInput().sendKeys('123');
    expect(page.getLoginButton().isEnabled()).toBe(true);
    page.getLoginButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/dashboard/events/preview');
      let valLocalStorage = browser.executeScript("return window.localStorage.getItem('token');");
      expect(valLocalStorage).not.toBeNull();
    });
  });

});
