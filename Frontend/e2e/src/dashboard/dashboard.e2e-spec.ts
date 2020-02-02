import {browser, by, logging} from 'protractor';
import {DashboardPage} from './dashboard.po';

describe('dashboard page', () => {
  let page: DashboardPage;

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

    const resetButton = page.getResetButtonForLocationPreviews();

    resetButton.click();
    expect(page.getLocationPreviewElements().count()).toBe(6);
  });

  afterEach(async () => {
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
