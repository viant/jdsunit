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

import com.fasterxml.jackson.annotation .JsonIgnoreProperties;
import com.fasterxml.jackson.annotation .JsonProperty;

import java.util.List;

/**
 * DatasetMapping represents a dataset mapping, mapping allow to route data defined in only one dataset to many datasets.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DatasetMapping {


        @JsonProperty("Table")
        private String table;

        @JsonProperty("Columns")
        private List<DatasetColumn> columns;

        @JsonProperty("Associations")
        private List<DatasetMapping> associations;


        public String getTable() {
                return table;
        }

        public void setTable(String table) {
                this.table = table;
        }

        public List<DatasetColumn> getColumns() {
                return columns;
        }

        public void setColumns(List<DatasetColumn> columns) {
                this.columns = columns;
        }

        public List<DatasetMapping> getAssociations() {
                return associations;
        }

        public void setAssociations(List<DatasetMapping> associations) {
                this.associations = associations;
        }
}
