import {browser, by, element} from 'protractor';

export class LocationPage {
  navigateTo(id: number) {
    return browser.get(`/dashboard/locations/${id}`) as Promise<any>;
  }
}

