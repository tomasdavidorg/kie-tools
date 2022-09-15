/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

describe("Try sample test", () => {
  beforeEach(() => {
    cy.visit("/");
  });

  it("should check all samples are present", () => {
    cy.ouia({ ouiaType: "sample-title" }).should(($titles) => {
      expect($titles).length(6);
      expect($titles.eq(0)).text("Greetings");
      expect($titles.eq(1)).text("Greetings with Kafka events");
      expect($titles.eq(2)).text("Compensation");
      expect($titles.eq(3)).text("Dashbuilder Kitchensink");
      expect($titles.eq(4)).text("Products Dashboard");
      expect($titles.eq(5)).text("Serverless Workflow Report");
    });
  });

  it("should check Greetings sample", () => {
    cy.ouia({ ouiaId: "greetings-try-swf-sample-button" }).click();

    cy.loadEditor();
  });
});
