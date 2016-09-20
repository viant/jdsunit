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
import java.util.Map;

/**
 * DsUnitDatastoreConfig represets DsUnitDatastoreConfig dsunit config
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DsUnitDatastoreConfig {

    @JsonProperty("Datastore")
    private String datastore; //name of datastore registered in manager registry

    @JsonProperty("Config")
    private DatastoreConfig config; // datastore manager config

    @JsonProperty("ConfigUrl")
    private String configUrl; //url with DatastoreConfig JSON.

    @JsonProperty("AdminDbName")
    private String adminDbName;//optional admin datastore name, needed for sql datastore to drop/create database

    @JsonProperty("ClearDatastore")
    private Boolean clearDatastore;//flag to reset datastore (depending on dialablable it could be either drop/create datastore for CanDrop/CanCreate dialects, or drop/create tables

    @JsonProperty("Descriptors")
    private List<TableDescriptor> descriptors;

    @JsonProperty("DatasetMapping")
    private Map<String, DatasetMapping> datasetMapping; //key represent name of dataset to be mapped

    public String getDatastore() {
        return datastore;
    }

    public void setDatastore(String datastore) {
        this.datastore = datastore;
    }

    public DatastoreConfig getConfig() {
        return config;
    }

    public void setConfig(DatastoreConfig config) {
        this.config = config;
    }

    public String getConfigUrl() {
        return configUrl;
    }

    public void setConfigUrl(String configUrl) {
        this.configUrl = configUrl;
    }

    public String getAdminDbName() {
        return adminDbName;
    }

    public void setAdminDbName(String adminDbName) {
        this.adminDbName = adminDbName;
    }

    public Boolean getClearDatastore() {
        return clearDatastore;
    }

    public void setClearDatastore(Boolean clearDatastore) {
        this.clearDatastore = clearDatastore;
    }

    public List<TableDescriptor> getDescriptors() {
        return descriptors;
    }

    public void setDescriptors(List<TableDescriptor> descriptors) {
        this.descriptors = descriptors;
    }

    public Map<String, DatasetMapping> getDatasetMapping() {
        return datasetMapping;
    }

    public void setDatasetMapping(Map<String, DatasetMapping> datasetMapping) {
        this.datasetMapping = datasetMapping;
    }

    @Override
    public String toString() {
        return "DsUnitDatastoreConfig{" +
                "datastore='" + datastore + '\'' +
                ", config=" + config +
                ", configUrl='" + configUrl + '\'' +
                ", adminDbName='" + adminDbName + '\'' +
                ", clearDatastore=" + clearDatastore +
                ", descriptors=" + descriptors +
                ", datasetMapping=" + datasetMapping +
                '}';
    }
}
