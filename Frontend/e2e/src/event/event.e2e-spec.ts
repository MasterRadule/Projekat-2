import {browser, logging} from 'protractor';
import {EventPage} from './event.po';

describe('location page', () => {
  let page: EventPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
  });

  beforeEach(() => {
    page = new EventPage();
  });

  it('should create event', () => {
    page.navigateTo();

    page.getEventNameInput().sendKeys('New event');
    page.getEventDescriptionTextarea().sendKeys('Description of event');
    page.setCategory('Music');
    page.getEventReservationDeadlineDaysInput().sendKeys(5);
    page.getEventMaximumTicketsPerReservationInput().sendKeys(5);
    browser.executeScript('arguments[0].click()', page.getEventActiveForReservationsCheckbox());
    page.getEventDayButton().click();
    browser.driver.sleep(1000);
    page.setEventDayDate('February 29, 2020');
    page.setEventDayTime('05:00');
    page.getAddEventDayButton().click();
    browser.driver.sleep(1000);
    page.setLocation('Manchester');
    browser.driver.sleep(1000);
    page.clickOnSeatGroup(86, 193);
    browser.executeScript('arguments[0].click()', page.getEnabledSeatGroupCheckbox());

    page.getSaveEventButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/dashboard/events/[0-9]+');
    });
  });

  it('should edit event', () => {
    page.navigateTo(1);

    const eventDescription = 'Voluptatibus in iste. Voluptas quibusdam odio; omnis cum architecto. Est natus aut. ' +
      'Voluptatibus corrupti at. Reiciendis non error. Qui sapiente minima.';

    expect(page.getEventNameInput().getAttribute('value')).toEqual('Conputor');
    expect(page.getEventDescriptionTextarea().getAttribute('value')).toEqual(eventDescription);
    expect(page.getCategory().getAttribute('ng-reflect-model')).toEqual('Competition');
    expect(page.getEventReservationDeadlineDaysInput().getAttribute('value')).toEqual(5);
    expect(page.getEventMaximumTicketsPerReservationInput().getAttribute('value')).toEqual(5);
    expect(page.getEventActiveForReservationsCheckbox().getAttribute('aria-checked')).toEqual('true');
    expect(page.getEventCancelledCheckbox().getAttribute('aria-checked')).toEqual('false');
    expect(page.getLocation().getAttribute('ng-reflect-model')).toEqual('Huddersfield');

    page.getEventNameInput().clear();
    page.getEventNameInput().sendKeys('Computor');
    page.setCategory('Sport');
    page.getEventMaximumTicketsPerReservationInput().clear();
    page.getEventMaximumTicketsPerReservationInput().sendKeys(3);
    page.getSaveEventButton().click().then(() => {
      expect(page.getEventNameInput().getAttribute('value')).toEqual('Computor');
      expect(page.getCategory().getAttribute('ng-reflect-model')).toEqual('Sport');
      expect(page.getEventMaximumTicketsPerReservationInput().getAttribute('value')).toEqual(3);
    });
  });

  it('should show error message when location cannot be changed', () => {
    page.navigateTo(1);

    page.setLocation('Sheffield');
    browser.driver.sleep(1000);
    page.clickOnSeatGroup(98, 9);
    browser.executeScript('arguments[0].click()', page.getEnabledSeatGroupCheckbox());

    page.getSaveEventButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Location cannot be changed if reservation for event exist');
    });
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });

});
