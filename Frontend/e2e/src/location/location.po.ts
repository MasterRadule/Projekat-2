import {browser, by, element} from 'protractor';

export class LocationPage {
  navigateTo(id?: number) {
    const url: string = id ? `/dashboard/locations/${id}` : '/dashboard/locations';
    return browser.get(url) as Promise<any>;
  }

  getLocationNameInput() {
    return element(by.css('input[name="name"]'));
  }

  getLocationStatusCheckbox() {
    return element(by.css('mat-checkbox[name="disabled"] input'));
  }

  getCreateOrEditButton() {
    return element(by.cssContainingText('button', 'Save'));
  }

  getAddSeatGroupButton() {
    return element(by.cssContainingText('button', 'Add'));
  }

  getSeatGroupNameInput() {
    return element(by.css('input[formControlName="seatGroupName"]'));
  }

  getTotalSeatsInput() {
    return element(by.css('input[formControlName="totalSeats"]'));
  }

  selectNumberOfRows(items: number) {
    element(by.css('mat-select[formControlName="rowsNum"]')).click().then(() => {
      element(by.css(`.mat-option[ng-reflect-value="${items}"]`)).click().then(() => {
      });
    });
  }

  selectNumberOfColumns(items: number) {
    element(by.css('mat-select[formControlName="colsNum"]')).click().then(() => {
      element(by.css(`.mat-option[ng-reflect-value="${items}"]`)).click().then(() => {
      });
    });
  }

  getParterreCheckbox() {
    return element(by.css('mat-checkbox[formControlName="parterre"] input'));
  }
}

