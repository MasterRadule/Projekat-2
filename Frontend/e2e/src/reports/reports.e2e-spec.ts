import {browser, logging} from 'protractor';
import {ReportsPage} from './reports.po';
import {LoginPage} from '../login/login.po';
import {DashboardPage} from '../dashboard/dashboard.po';

describe('reports page', () => {
  let page: ReportsPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
    const loginPage = new LoginPage();
    loginPage.login('Dickens@example.com', 123);
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });

  beforeEach(() => {
    page = new ReportsPage();
    page.navigateTo();
  });

  it('should not display charts before generating report', () => {
    page.getAppCharts().then((charts) => {
      for (const chart of charts) {
        expect(chart.isDisplayed()).toBeFalsy();
      }
    });
  });

  it('should generate report for start and end date', () => {
    page.setStartDate('February 6, 2020');
    browser.driver.sleep(1000);

    page.setEndDate('February 29, 2020');
    browser.driver.sleep(1000);

    page.getGetReportButton().click().then(() => {
      page.getAppCharts().then((charts) => {
        for (const chart of charts) {
          expect(chart.isDisplayed()).toBeTruthy();
        }
      });
    });
  });

  it('should generate report for start date, end date and location', () => {
    page.setStartDate('February 6, 2020');
    browser.driver.sleep(1000);

    page.setEndDate('February 29, 2020');
    browser.driver.sleep(1000);

    page.selectLocation(3);
    browser.driver.sleep(1000);

    page.getGetReportButton().click().then(() => {
      page.getAppCharts().then((charts) => {
        for (const chart of charts) {
          expect(chart.isDisplayed()).toBeTruthy();
        }
      });
    });
  });

  it('should generate report for start date, end date, location and event', () => {
    page.setStartDate('February 6, 2020');
    browser.driver.sleep(1000);

    page.setEndDate('February 29, 2020');
    browser.driver.sleep(1000);

    page.selectLocation(3);
    browser.driver.sleep(1000);

    page.selectEvent(1);
    browser.driver.sleep(1000);

    page.getGetReportButton().click().then(() => {
      page.getAppCharts().then((charts) => {
        for (const chart of charts) {
          expect(chart.isDisplayed()).toBeTruthy();
        }
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
