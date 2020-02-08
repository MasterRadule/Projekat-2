import {browser, logging} from 'protractor';
import {EventPage} from './event.po';
import {LoginPage} from '../login/login.po';
import {DashboardPage} from '../dashboard/dashboard.po';

describe('event page', () => {
  let page: EventPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
    const loginPage = new LoginPage();
    loginPage.login('Dickens@example.com', 123);
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });

  beforeEach(() => {
    page = new EventPage();
  });

  it('should create event', () => {
    page.navigateTo();

    page.getEventNameInput().sendKeys('Test event');
    page.getEventDescriptionTextarea().sendKeys('Description of event');
    page.setCategory('Music');
    page.getEventReservationDeadlineDaysInput().clear();
    page.getEventReservationDeadlineDaysInput().sendKeys(5);
    page.getEventMaximumTicketsPerReservationInput().clear();
    page.getEventMaximumTicketsPerReservationInput().sendKeys(5);
    browser.executeScript('arguments[0].click()', page.getEventActiveForReservationsCheckbox());
    page.getEventDayButton().click();
    browser.driver.sleep(1000);
    page.setEventDayDate('February 29, 2020');
    page.setEventDayTime('05:00');
    page.getAddEventDayButton().click();
    browser.driver.sleep(2000);
    page.setLocation('Manchester');
    browser.driver.sleep(2000);
    page.clickOnSeatGroup(86, 199);
    browser.executeScript('arguments[0].click()', page.getEnabledSeatGroupCheckbox());

    page.getSaveEventButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/dashboard/events/[0-9]+');
    });
  });

  it('should show error message when number of reservation deadline days is invalid', () => {
    page.navigateTo();

    page.getEventNameInput().sendKeys('Test event 2');
    page.getEventDescriptionTextarea().sendKeys('Description of event');
    page.setCategory('Music');
    page.getEventReservationDeadlineDaysInput().clear();
    page.getEventReservationDeadlineDaysInput().sendKeys(25);
    page.getEventMaximumTicketsPerReservationInput().clear();
    page.getEventMaximumTicketsPerReservationInput().sendKeys(5);
    browser.executeScript('arguments[0].click()', page.getEventActiveForReservationsCheckbox());
    page.getEventDayButton().click();
    browser.driver.sleep(1000);
    page.setEventDayDate('February 29, 2020');
    page.setEventDayTime('05:00');
    page.getAddEventDayButton().click();
    browser.driver.sleep(2000);
    page.setLocation('Manchester');
    browser.driver.sleep(2000);
    page.clickOnSeatGroup(86, 199);
    browser.executeScript('arguments[0].click()', page.getEnabledSeatGroupCheckbox());

    page.getSaveEventButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Number of reservation deadline days must ' +
        'be less than number of days left until the event');
    });
  });

  it('should show error message when event day date is before today\'s date', () => {
    page.navigateTo();

    page.getEventNameInput().sendKeys('Test event 3');
    page.getEventDescriptionTextarea().sendKeys('Description of event');
    page.setCategory('Music');
    page.getEventReservationDeadlineDaysInput().clear();
    page.getEventReservationDeadlineDaysInput().sendKeys(25);
    page.getEventMaximumTicketsPerReservationInput().clear();
    page.getEventMaximumTicketsPerReservationInput().sendKeys(5);
    browser.executeScript('arguments[0].click()', page.getEventActiveForReservationsCheckbox());
    page.getEventDayButton().click();
    browser.driver.sleep(1000);
    page.setEventDayDate('February 7, 2020');
    page.setEventDayTime('05:00');
    page.getAddEventDayButton().click();
    browser.driver.sleep(2000);
    page.setLocation('Manchester');
    browser.driver.sleep(2000);
    page.clickOnSeatGroup(86, 199);
    browser.executeScript('arguments[0].click()', page.getEnabledSeatGroupCheckbox());

    page.getSaveEventButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Event day date must be after today\'s date');
    });
  });

  it('should edit event', () => {
    page.navigateTo(1);

    const eventDescription = 'Voluptatibus in iste. Voluptas quibusdam odio; omnis cum architecto. Est natus aut. ' +
      'Voluptatibus corrupti at. Reiciendis non error. Qui sapiente minima.';

    expect(page.getEventNameInput().getAttribute('value')).toEqual('Conputor');
    expect(page.getEventDescriptionTextarea().getAttribute('value')).toEqual(eventDescription);
    expect(page.getCategory().getAttribute('ng-reflect-model')).toEqual('Competition');
    expect(page.getEventReservationDeadlineDaysInput().getAttribute('value')).toEqual('5');
    expect(page.getEventMaximumTicketsPerReservationInput().getAttribute('value')).toEqual('5');
    expect(page.getEventActiveForReservationsCheckbox().getAttribute('aria-checked')).toEqual('true');
    expect(page.getEventCancelledCheckbox().getAttribute('aria-checked')).toEqual('false');
    expect(page.getLocation().getText()).toEqual('Huddersfield');

    browser.driver.sleep(2000);
    page.getEventNameInput().clear();
    page.getEventNameInput().sendKeys('Computor');
    page.setCategory('Sport');
    page.getEventMaximumTicketsPerReservationInput().clear();
    page.getEventMaximumTicketsPerReservationInput().sendKeys(3);
    page.getSaveEventButton().click().then(() => {
      expect(page.getEventNameInput().getAttribute('value')).toEqual('Computor');
      expect(page.getCategory().getAttribute('ng-reflect-model')).toEqual('Sport');
      expect(page.getEventMaximumTicketsPerReservationInput().getAttribute('value')).toEqual('3');
    });
  });

  it('should show error message when location cannot be changed', () => {
    page.navigateTo(1);
    page.setLocation('Sheffield');
    browser.driver.sleep(3000);
    page.clickOnSeatGroup(98, 15);
    browser.executeScript('arguments[0].click()', page.getEnabledSeatGroupCheckbox());

    page.getSaveEventButton().click().then(() => {
      expect(page.getSnackBar().getText()).toContain('Location cannot be changed if reservation for event exist');
    });
  });

  afterAll(() => {
    const dashboardPage = new DashboardPage();
    dashboardPage.logout();
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });

});
