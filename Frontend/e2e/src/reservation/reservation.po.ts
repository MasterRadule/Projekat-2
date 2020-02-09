import {browser, by, element} from 'protractor';

export class ReservationPage {
  navigateTo(id?: number) {
    const url: string = id ? `/dashboard/reservations/${id}` : '/dashboard/reservations';
    return browser.get(url) as Promise<any>;
  }

  getNumberOfTickets() {
    return element(by.css('#reservation_content_table > tr:nth-child(1) > td:nth-child(2)'));
  }

  getTotalPrice() {
    return element(by.css('#reservation_content_table > tr:nth-child(2) > td:nth-child(2)'));
  }

  getIsPaid() {
    return element(by.css('#reservation_content_table > tr:nth-child(3) > td:nth-child(2)'));
  }

  getTicketsDiv() {
    return element(by.css('#tickets_div'));
  }

  clickFirstPrint() {
    return element(by.css('#tickets_div > div:nth-child(1) > div:nth-child(2) > button')).click();
  }

  getTicket(index: number) {
    const parent = element(by.repeater('f in feed'));
    return parent.all(by.css('#tickets_div')).get(index);
  }

}

