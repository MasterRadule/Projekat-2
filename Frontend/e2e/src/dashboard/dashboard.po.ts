import {browser, by, element} from 'protractor';

export class DashboardPage {
  navigateTo() {
    return browser.get('/dashboard') as Promise<any>;
  }

  previewLocations() {
    return browser.get('/dashboard/locations/preview') as Promise<any>;
  }

  previewEvents() {
    return browser.get('/dashboard/events/preview') as Promise<any>;
  }

  getLocationPreviewElements() {
    return element.all(by.tagName('app-location-preview'));
  }

  getEventPreviewElements() {
    return element.all(by.tagName('app-event-preview'));
  }

  getLocationPreviewTitles() {
    return element.all(by.css('mat-card-title'));
  }

  getEventPreviewTitles() {
    return element.all(by.css('mat-card-title'));
  }

  getFirstLocationPreviewToggleStatusButton() {
    return element.all(by.css('mat-slide-toggle')).first();
  }

  getFirstLocationPreviewEditButton() {
    return element.all(by.css('app-location-preview button')).first();
  }

  getFirstEventPreviewEditButton() {
    return element.all(by.css('app-event-preview button')).first();
  }

  getPaginatorLabel() {
    return element(by.className('mat-paginator-range-label'));
  }

  getPreviousPageButton() {
    return element(by.className('mat-paginator-navigation-previous'));
  }

  getNextPageButton() {
    return element(by.className('mat-paginator-navigation-next'));
  }

  selectNumberOfItemsDisplayed(items: number) {
    element(by.css('#mat-select-0')).click().then(() => {
      element(by.css(`.mat-option[ng-reflect-value="${items}"]`)).click().then(() => {
      });
    });
  }

  selectNumberOfEventsDisplayed(items: number) {
    element(by.css('#mat-select-2')).click().then(() => {
      element(by.css(`.mat-option[ng-reflect-value="${items}"]`)).click().then(() => {
      });
    });
  }

  setSearchTextForLocationPreviews(text: string) {
    element(by.css('#search')).sendKeys(text).then(() => {
    });
  }

  setSearchTextForEventPreviews(text: string) {
    element(by.css('#searchInput')).sendKeys(text).then(() => {
    });
  }

  getSearchButtonForLocationPreviews() {
    return element(by.css('.searchButton[type="submit"]'));
  }

  getSearchButtonForEventPreviews() {
    return element(by.css('#buttonSearch'));
  }

  getResetButtonForLocationPreviews() {
    return element(by.css('.searchButton[type="button"]'));
  }

  getResetButtonForEventPreviews() {
    return element(by.css('#buttonReset'));
  }

  setCategory(category: string) {
    element.all(by.css('.mat-select-arrow-wrapper')).first().click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.css(`mat-option[ng-reflect-value="${category}"]`)).click().then();
      });
    });
  }

  setStartDate(date: string) {
    element.all(by.css('mat-datepicker-toggle button')).first().click().then(() => {
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

  logout() {
    this.navigateTo();
    element(by.css('body > app-root > app-dashboard > div > app-toolbar > mat-toolbar > span:nth-child(2) > button:nth-child(2)')).click();
  }
}
