import {browser, logging} from 'protractor';
import {DashboardPage} from './dashboard.po';
import {LoginPage} from '../login/login.po';

describe('dashboard page', () => {
  let page: DashboardPage;

  beforeAll(() => {
    browser.driver.manage().window().maximize();
    const loginPage = new LoginPage();
    loginPage.login('Dickens@example.com', 123);
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });

  beforeEach(() => {
    page = new DashboardPage();
  });

  it('should display location previews', () => {
    page.previewLocations();
    expect(page.getLocationPreviewElements().count()).toBe(6);
  });

  it('should change number of displayed location previews', () => {
    page.previewLocations();
    expect(page.getLocationPreviewElements().count()).toBe(6);

    page.selectNumberOfItemsDisplayed(3);
    expect(page.getLocationPreviewElements().count()).toBe(3);

    page.selectNumberOfItemsDisplayed(9);
    expect(page.getLocationPreviewElements().count()).toBe(9);

    page.selectNumberOfItemsDisplayed(6);
    expect(page.getLocationPreviewElements().count()).toBe(6);
  });

  it('should go to next page of location previews and return to previous', () => {
    page.previewLocations();

    const nextPageButton = page.getNextPageButton();
    expect(nextPageButton).toBeTruthy();

    nextPageButton.click();
    expect(page.getLocationPreviewElements().count()).toBe(6);
    expect(page.getPaginatorLabel().getText()).toBe('7 – 12 of 30');

    const previousPageButton = page.getPreviousPageButton();
    expect(previousPageButton).toBeTruthy();

    page.getPreviousPageButton().click();
    expect(page.getPaginatorLabel().getText()).toBe('1 – 6 of 30');
  });

  it('should search location previews', () => {
    page.previewLocations();

    const searchText = 'wich';
    page.setSearchTextForLocationPreviews(searchText);

    const searchButton = page.getSearchButtonForLocationPreviews();

    searchButton.click();
    expect(page.getLocationPreviewElements().count()).toBe(2);
    page.getLocationPreviewTitles().then((titles) => {
      for (const title of titles) {
        title.getText().then((value) => {
          expect(value.toLowerCase()).toContain(searchText.toLowerCase());
        });
      }
    });

    const resetButton = page.getResetButtonForLocationPreviews();

    resetButton.click();
    expect(page.getLocationPreviewElements().count()).toBe(6);
  });

  it('should toggle location status', () => {
    page.previewLocations();

    const toggleButton = page.getFirstLocationPreviewToggleStatusButton();
    const input = toggleButton.$('input');

    browser.executeScript('arguments[0].scrollIntoView()', toggleButton).then(() => {
      expect(input.getAttribute('aria-checked')).toEqual('true');

      browser.executeScript('arguments[0].click()', input).then(() => {
        browser.sleep(1000);
        expect(input.getAttribute('aria-checked')).toEqual('false');

        browser.executeScript('arguments[0].click()', input).then(() => {
          browser.sleep(1000);
          expect(input.getAttribute('aria-checked')).toEqual('true');
        });
      });
    });
  });

  it('should go to edit location page', () => {
    page.previewLocations();

    const editButton = page.getFirstLocationPreviewEditButton();

    browser.executeScript('arguments[0].scrollIntoView', editButton).then(() => {
      browser.executeScript('arguments[0].click()', editButton).then(() => {
        expect(browser.getCurrentUrl()).toEqual('http://localhost:4200/dashboard/locations/1');
      });
    });
  });

  it('should display event previews', () => {
    page.previewEvents();
    expect(page.getEventPreviewElements().count()).toBe(6);
  });

  it('should change number of displayed event previews', () => {
    page.previewEvents();
    expect(page.getEventPreviewElements().count()).toBe(6);

    page.selectNumberOfEventsDisplayed(3);
    expect(page.getEventPreviewElements().count()).toBe(3);

    page.selectNumberOfEventsDisplayed(9);
    expect(page.getEventPreviewElements().count()).toBe(9);

    page.selectNumberOfEventsDisplayed(6);
    expect(page.getEventPreviewElements().count()).toBe(6);
  });

  it('should go to next page of event previews and return to previous', () => {
    page.previewEvents();

    const nextPageButton = page.getNextPageButton();
    expect(nextPageButton).toBeTruthy();

    nextPageButton.click();
    expect(page.getEventPreviewElements().count()).toBe(6);
    expect(page.getPaginatorLabel().getText()).toBe('7 – 12 of 25');

    const previousPageButton = page.getPreviousPageButton();
    expect(previousPageButton).toBeTruthy();

    page.getPreviousPageButton().click();
    expect(page.getPaginatorLabel().getText()).toBe('1 – 6 of 25');
  });

  it('should search event previews by search text', () => {
    page.previewEvents();

    const searchText = 'co';
    page.setSearchTextForEventPreviews(searchText);

    const searchButton = page.getSearchButtonForEventPreviews();

    searchButton.click();
    expect(page.getEventPreviewElements().count()).toBe(4);
    page.getEventPreviewTitles().then((titles) => {
      for (const title of titles) {
        title.getText().then((value) => {
          expect(value.toLowerCase()).toContain(searchText.toLowerCase());
        });
      }
    });

    const resetButton = page.getResetButtonForEventPreviews();

    resetButton.click();
    expect(page.getEventPreviewElements().count()).toBe(6);
  });

  it('should search event previews by category', () => {
    page.previewEvents();

    const category = 'Fair';
    page.setCategory(category);

    const searchButton = page.getSearchButtonForEventPreviews();

    searchButton.click();
    expect(page.getEventPreviewElements().count()).toBe(5);

    const resetButton = page.getResetButtonForEventPreviews();

    resetButton.click();
    expect(page.getEventPreviewElements().count()).toBe(6);
  });

  it('should search event previews by start date and end date', () => {
    page.setStartDate('February 10, 2020');
    browser.driver.sleep(1000);

    page.setEndDate('February 28, 2020');
    browser.driver.sleep(1000);

    const searchButton = page.getSearchButtonForEventPreviews();

    searchButton.click();
    expect(page.getEventPreviewElements().count()).toBe(6);
    expect(page.getPaginatorLabel().getText()).toBe('1 – 6 of 9');

    const resetButton = page.getResetButtonForEventPreviews();

    resetButton.click();
    expect(page.getEventPreviewElements().count()).toBe(6);
    expect(page.getPaginatorLabel().getText()).toBe('1 – 6 of 25');
  });

  it('should go to edit event page', () => {
    page.previewEvents();

    const editButton = page.getFirstEventPreviewEditButton();

    browser.executeScript('arguments[0].scrollIntoView', editButton).then(() => {
      browser.executeScript('arguments[0].click()', editButton).then(() => {
        expect(browser.getCurrentUrl()).toEqual('http://localhost:4200/dashboard/events/1');
      });
    });
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });

  afterAll(() => {
    page.logout();
    browser.driver.sleep(1000);
    browser.waitForAngular();
  });
});
