import {browser, logging} from 'protractor';
import {ReservationPage} from './reservation.po';
import {LoginPage} from '../login/login.po';
import {DashboardPage} from '../dashboard/dashboard.po';

describe('reservation page', () => {
  let page: ReservationPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
    const loginPage = new LoginPage();
    loginPage.login('JennifferHooker@example.com', 123);
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });

  beforeEach(() => {
    page = new ReservationPage();
  });

  it('should show reservation', () => {
    page.navigateTo(1);
    expect(page.getNumberOfTickets().getText()).toBe('2');
    expect(page.getTotalPrice().getText()).toBe('39$');
    expect(page.getIsPaid().getText()).toBe('yes');
  });

  it('should show tickets', () => {
    page.navigateTo(1);
    expect(page.getTicketsDiv().getText()).toBeTruthy();
    expect(page.getTicket(0)).toBeTruthy();
    expect(page.getTicket(1)).toBeTruthy();
  });

  it('should open print window', () => {
    page.navigateTo(1);
    browser.getAllWindowHandles().then((windows) => {
      const startingLength: number = windows.length;
      page.clickFirstPrint().then(() => {
        browser.driver.sleep(1000);
        browser.getAllWindowHandles().then((windows2) => {
          expect(windows2.length).toBe(startingLength + 2);
        });
      });
    });
  });

  afterAll(() => {
    const dashboardPage = new DashboardPage();
    dashboardPage.logout();
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });
});
