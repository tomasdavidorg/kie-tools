/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.definition.property.type;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
public class ScriptType implements PropertyType {

    public static final String name = "stunner.bpmn.ScriptType";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScriptType)) {
            return false;
        }
        ScriptType that = (ScriptType) o;
        if (!name.equals(that.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(name.hashCode());
    }

    @Override
    public String toString() {
        return "ScriptType{" +
                "name='" + name + '\'' +
                '}';
    }
}
