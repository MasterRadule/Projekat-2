import {browser, logging} from 'protractor';
import {LocationPage} from './location.po';

describe('location page', () => {
  let page: LocationPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
  });

  beforeEach(() => {
    page = new LocationPage();
  });

  it('should create location', () => {
    page.navigateTo();

    page.getLocationNameInput().sendKeys('New location');

    page.getCreateOrEditButton().click().then(() => {
      expect(browser.getCurrentUrl()).toMatch('http://localhost:4200/dashboard/locations/[0-9]+');
    });
  });

  it('should edit location', () => {
    page.navigateTo(1);

    expect(page.getLocationNameInput().getAttribute('value')).toEqual('Huddersfield');
    expect(page.getLocationStatusCheckbox().getAttribute('aria-checked')).toEqual('false');

    page.getLocationNameInput().clear();
    page.getLocationNameInput().sendKeys('new name');
    browser.executeScript('arguments[0].click()', page.getLocationStatusCheckbox()).then(() => {
      page.getCreateOrEditButton().click().then(() => {
        expect(page.getLocationNameInput().getAttribute('value')).toEqual('new name');
        expect(page.getLocationStatusCheckbox().getAttribute('aria-checked')).toEqual('true');
      });
    });
  });

  it('should disable adding seats before location is created', () => {
    page.navigateTo();

    expect(page.getAddSeatGroupButton().isEnabled()).toEqual(false);
  });

  it('should add regular seat group', () => {
    page.navigateTo(1);

    page.getSeatGroupNameInput().sendKeys('new seat group');
    page.selectNumberOfRows(3);
    browser.driver.sleep(1000);
    page.selectNumberOfColumns(3);

    expect(page.getTotalSeatsInput().getAttribute('value')).toEqual('9');

    expect(page.getAddSeatGroupButton().isEnabled()).toEqual(true);
    page.getAddSeatGroupButton().click();
  });

  it('should add parterre', () => {
    page.navigateTo(1);

    browser.executeScript('arguments[0].click()', page.getParterreCheckbox()).then(() => {
      page.getSeatGroupNameInput().sendKeys('new parterre');
      page.getTotalSeatsInput().sendKeys(30);

      expect(page.getAddSeatGroupButton().isEnabled()).toEqual(true);
      page.getAddSeatGroupButton().click();
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
