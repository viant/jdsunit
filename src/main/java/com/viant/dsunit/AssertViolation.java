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

/**
 * AssertViolation represents test violation
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AssertViolation {


    @JsonProperty("Datastore")
    private String datastore;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Table")
    private String table;

    @JsonProperty("Key")
    private String key;

    @JsonProperty("Expected")
    private String expected;

    @JsonProperty("Actual")
    private String actual;

    @JsonProperty("Source")
    private String source;


    public String getDatastore() {
        return datastore;
    }

    public void setDatastore(String datastore) {
        this.datastore = datastore;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    @Override
    public String toString() {
        return "AssertViolation{" +
                "datastore='" + datastore + '\'' +
                ", type='" + type + '\'' +
                ", table='" + table + '\'' +
                ", key='" + key + '\'' +
                ", expected='" + expected + '\'' +
                ", actual='" + actual + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}

