/*
 *
 *
 * Copyright 2012-2016 Viant.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 */
package com.viant.dsunit;

import org.codehaus.jackson.annotate .JsonIgnoreProperties;
import org.codehaus.jackson.annotate .JsonProperty;

import java.util.List;

/**
 * ExpectDatasetRequest represent datastore verification request.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ExpectDatasetRequest {


    @JsonProperty("Expect")
    private List<Datasets> expect;

    @JsonProperty("CheckPolicy")
    private int checkPolicy;

    public List<Datasets> getExpect() {
        return expect;
    }

    public void setExpect(List<Datasets> expect) {
        this.expect = expect;
    }

    public int getCheckPolicy() {
        return checkPolicy;
    }

    public void setCheckPolicy(int checkPolicy) {
        this.checkPolicy = checkPolicy;
    }
}
