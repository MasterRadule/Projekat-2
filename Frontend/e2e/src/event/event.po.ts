import {browser, by, element} from 'protractor';

export class EventPage {
  navigateTo(id?: number) {
    const url: string = id ? `/dashboard/events/${id}` : '/dashboard/events';
    return browser.get(url) as Promise<any>;
  }

  getEventNameInput() {
    return element(by.css('input[name="name"]'));
  }

  getEventDescriptionTextarea() {
    return element(by.css('textarea[name="description"]'));
  }

  setCategory(category: string) {
    element.all(by.css('.mat-select-arrow-wrapper')).first().click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.cssContainingText('span.mat-option-text', category)).click();
      });
    });
  }

  getCategory() {
    return element(by.css('mat-select[name="category"]'));
  }

  getLocation() {
    return element(by.css('mat-select[name="locationID"] .mat-select-value-text>span'));
  }

  getEventReservationDeadlineDaysInput() {
    return element(by.css('input[name="reservationDeadlineDays"]'));
  }

  getEventMaximumTicketsPerReservationInput() {
    return element(by.css('input[name="maxTicketsPerReservation"]'));
  }

  getEventActiveForReservationsCheckbox() {
    return element(by.css('mat-checkbox[name="activeForReservations"] input'));
  }

  getEventCancelledCheckbox() {
    return element(by.css('mat-checkbox[name="cancelled"] input'));
  }

  getEventDayButton() {
    return element(by.css('#addEvDayButton'));
  }

  setEventDayDate(date: string) {
    element.all(by.css('mat-datepicker-toggle button')).first().click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.css(`td[aria-label="${date}"]`)).click().then();
      });
    });
  }

  setEventDayTime(time: string) {
    element(by.css('input[type="time"]')).sendKeys(time);
  }

  getAddEventDayButton() {
    return element(by.css('#addEventDay'));
  }

  setLocation(location: string) {
    element.all(by.css('.mat-select-arrow-wrapper')).last().click().then(() => {
      browser.driver.sleep(1000).then(() => {
        element(by.cssContainingText('span.mat-option-text', location)).click();
      });
    });
  }

  clickOnSeatGroup(toRight: number, toBottom: number) {
    const canvas = element(by.css('canvas'));

    browser.actions().mouseMove(canvas, {x: toRight, y: toBottom}).click().perform();
  }

  getEnabledSeatGroupCheckbox() {
    return element(by.css('mat-checkbox[name="enabled"] input'));
  }

  getSaveEventButton() {
    return element(by.css('#saveEventButton'));
  }

  getSnackBar() {
    return element(by.className('mat-simple-snackbar'));
  }
}
