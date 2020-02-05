import {browser, by, element} from 'protractor';

export class ReportsPage {
  navigateTo() {
    return browser.get('/dashboard/reports') as Promise<any>;
  }

  setStartDate(date: string) {
    const datepicker = element.all(by.css('mat-datepicker-toggle button')).first().click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.css(`td[aria-label="${date}"]`)).click().then();
      });
    });
  }

  setEndDate(date: string) {
    element.all(by.css('mat-datepicker-toggle button')).last().click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.css(`td[aria-label="${date}"]`)).click().then();
      });
    });
  }

  selectLocation(position: number) {
    element(by.css('mat-select[name="location"]')).click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.xpath(`//mat-option[${position}]`)).click().then(() => {
        });
      });
    });
  }

  selectEvent(position: number) {
    element(by.css('mat-select[name="category"]')).click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.xpath(`//mat-option[${position}]`)).click().then(() => {
        });
      });
    });
  }

  getGetReportButton() {
    return element(by.css('button[type="submit"]'));
  }

  getAppCharts() {
    return element.all(by.tagName('app-chart'));
  }
}

