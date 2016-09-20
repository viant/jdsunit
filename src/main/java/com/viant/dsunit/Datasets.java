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
 * Datasets represents a collection of Dataset's for Datastore
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Datasets {

    @JsonProperty("Datastore")
    private String datastore;

    @JsonProperty("Datasets")
    private List<Dataset> datasets;

    public String getDatastore() {
        return datastore;
    }

    public void setDatastore(String datastore) {
        this.datastore = datastore;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<Dataset> datasets) {
        this.datasets = datasets;
    }
}
