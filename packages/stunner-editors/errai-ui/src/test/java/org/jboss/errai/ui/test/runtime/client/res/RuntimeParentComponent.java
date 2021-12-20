/*
 * Copyright (C) 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jboss.errai.ui.test.runtime.client.res;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.ServerTemplateProvider;
import org.jboss.errai.ui.shared.TemplateInitializedEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;

@Templated(value = "/RuntimeParentComponent.html", provider = ServerTemplateProvider.class)
public class RuntimeParentComponent extends Composite {

  @Inject
  @DataField
  private RuntimeChildComponent c1;

  private Button button;

  @PostConstruct
  public void init() {
    TemplateInitializedEvent.Handler handler = new TemplateInitializedEvent.Handler() {
      @Override
      public void onInitialized() {
        c1.getElement().setAttribute("id", "c1");
        button.getElement().setAttribute("id", "c2");
      }
    };
    this.addHandler(handler, TemplateInitializedEvent.TYPE);
  }

  public Button getButton() {
    return button;
  }

  @Inject
  public void setButton(@DataField("c2") Button button) {
    this.button = button;
  }

}
