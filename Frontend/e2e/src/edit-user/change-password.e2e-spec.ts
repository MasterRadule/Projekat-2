import {browser} from 'protractor';
import {ChangePasswordPage} from './change-password.po';
import {LoginPage} from '../login/login.po';
import {DashboardPage} from '../dashboard/dashboard.po';

describe('change password page', () => {
  let page: ChangePasswordPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
    const loginPage = new LoginPage();
    loginPage.login('JimmySales4@example.com', 123);
    browser.driver.sleep(1000);
    browser.waitForAngular();

  });

  beforeEach(() => {
    page = new ChangePasswordPage();
    page.navigateTo();
  });

  it('should show error on wrong old password', () => {
    expect(page.getEditButton().isEnabled()).toBe(false);
    page.getOldPasswordInput().sendKeys('1234');
    page.getPasswordInput().sendKeys('KtsNvtTim1+');
    page.getRepeatedPasswordInput().sendKeys('KtsNvtTim1+');
    expect(page.getEditButton().isEnabled()).toBe(true);
    page.getEditButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/changePassword');
      expect(page.getSnackBar().getText()).toContain('Old password is incorrect');
      browser.sleep(1000);
    });
  });

  it('should show error on different passwords', () => {
    expect(page.getEditButton().isEnabled()).toBe(false);
    page.getOldPasswordInput().sendKeys('123');
    page.getPasswordInput().sendKeys('KtsNvtTim1+');
    page.getRepeatedPasswordInput().sendKeys('KtsNvtTim1++');
    expect(page.getEditButton().isEnabled()).toBe(true);
    page.getEditButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/changePassword');
      expect(page.getSnackBar().getText()).toContain('Passwords don\'t match');
      browser.sleep(1000);
    });
  });

  it('should show error message on failed password validation', () => {
    expect(page.getEditButton().isEnabled()).toBe(false);
    page.getOldPasswordInput().sendKeys('123');
    page.getPasswordInput().sendKeys('KtsNvtTim1');
    page.getRepeatedPasswordInput().sendKeys('KtsNvtTim1');
    expect(page.getPasswordError().isPresent()).toBe(true);
    expect(page.getRepeatedPasswordError().isPresent()).toBe(true);
    expect(page.getEditButton().isEnabled()).toBe(false);
    browser.sleep(1000);

  });

  it('should change password', () => {
    expect(page.getEditButton().isEnabled()).toBe(false);
    page.getOldPasswordInput().sendKeys('123');
    page.getPasswordInput().sendKeys('KtsNvtTim1+');
    page.getRepeatedPasswordInput().sendKeys('KtsNvtTim1+');
    expect(page.getPasswordError().isPresent()).toBe(false);
    expect(page.getRepeatedPasswordError().isPresent()).toBe(false);
    expect(page.getEditButton().isEnabled()).toBe(true);
    page.getEditButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/myProfile');
      expect(page.getSnackBar().getText()).toContain('Password changed successfully!');
      browser.sleep(1000);
    });

  });

  it('should return on edit user page', () => {
    page.getReturnButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/myProfile');
      browser.driver.sleep(2000);
    });
  });

  afterAll(() => {
    const dashboardPage = new DashboardPage();
    dashboardPage.logout();
    browser.driver.sleep(1000);
    browser.waitForAngular();

  });

});
