import {browser} from 'protractor';
import {MyProfilePage} from './user-edit.po';
import {LoginPage} from '../login/login.po';
import {DashboardPage} from '../dashboard/dashboard.po';

describe('edit profile page', () => {
  let page: MyProfilePage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
    const loginPage = new LoginPage();
    loginPage.login('Dickens@example.com', 123);
    browser.driver.sleep(1000);
    browser.waitForAngular();

  });

  beforeEach(() => {
    page = new MyProfilePage();
    page.navigateTo();
  });

  it('should edit user', () => {
    page.getFirstNameInput().clear();
    page.getFirstNameInput().sendKeys('Alfredo');
    page.getLastNameInput().clear();
    page.getLastNameInput().sendKeys('Alfredic');
    expect(page.getEmailInput().isEnabled()).toBe(false);
    expect(page.getEditButton().isEnabled()).toBe(true);
    page.getEditButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/myProfile');
      expect(page.getSnackBar().getText()).toContain('User edited successfully!');
    });
  });

  it('should redirect to change password page', () => {
    expect(page.getEmailInput().isEnabled()).toBe(false);
    page.getChangePasswordButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/changePassword');
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
