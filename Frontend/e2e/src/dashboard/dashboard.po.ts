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

  setSearchTextForLocationPreviews(text: string) {
    element(by.css('#search')).sendKeys(text).then(() => {
    });
  }

  getSearchButtonForLocationPreviews() {
    return element(by.css('.searchButton[type="submit"]'));
  }

  getResetButtonForLocationPreviews() {
    return element(by.css('.searchButton[type="button"]'));
  }
}

