import { Application, SpectronClient } from "spectron";
import { initApp } from "./Tools";

let app: Application;
let client: SpectronClient;

beforeEach(async () => {
  app = await initApp();
  client = app.client;
});

test("navigation", async () => {
  // check header brand
  const headerBrand = await client.$(".pf-c-brand");
  expect(await headerBrand.getAttribute("alt")).toEqual("Business Modeler Logo");
  expect(await headerBrand.getAttribute("src")).toContain("images/BusinessModeler_Logo.svg");

  // open sidebar (if it is closed)
  const sidebar = await client.$("#page-sidebar");
  const sidebarClass = await sidebar.getAttribute("class");
  if (!sidebarClass.includes("expanded")) {
    const sidebarButton = await client.$("#nav-toggle");
    await sidebarButton.click();
  }

  // open Learn more page
  const learnMoreNav = await client.$("[data-ouia-component-id='learn-more-nav']");
  await client.waitUntil(async () => await learnMoreNav.isDisplayed(), { timeout: 2000 });
  await learnMoreNav.click();

  // check Learn more cards
  const learnMoreCards = await client.$$("[data-ouia-component-type='PF4/Card'] > div > h2");
  const learMoreCardsTitles = await Promise.all(await learnMoreCards.map(async card => await card.getText()));
  expect(learMoreCardsTitles).toEqual(["Why BPMN?", "Why DMN?", "About Business Modeler Preview"]);

  // open Files page
  const filesNav = await client.$("[data-ouia-component-id='files-nav']");
  await filesNav.click();

  // check New files cards
  const newFileCards = await client.$$("[data-ouia-component-type='PF4/Card'] > div > h3");
  const newFileCardTitles = await Promise.all(newFileCards.map(async card => await card.getText()));
  expect(newFileCardTitles).toEqual([
    "Blank Workflow (.BPMN)",
    "Blank Decision Model (.DMN)",
    "Sample Workflow (.BPMN)",
    "Sample Decision Model (.DMN)",
    "Open from source"
  ]);

  // check Recent Files section
  const recentFilesSectionTitle = await client.$("[data-ouia-component-id='recent-files-section-title']");
  expect(await recentFilesSectionTitle.getText()).toEqual("Recent Files");
});

afterEach(async () => {
  await app.stop();
});
