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

import java.util.Map;

/**
 * DatastoreConfig represent datastore config.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DatastoreConfig {

    @JsonProperty("DriverName")
    private String driverName;

    @JsonProperty("PoolSize")
    private int poolSize;

    @JsonProperty("MaxPoolSize")
    private int maxPoolSize;

    @JsonProperty("Descriptor")
    private String descriptor;

    @JsonProperty("Parameters")
    private Map<String, String> parameters;

    @JsonProperty("SecretParametersURL")
    private String secretParametersURL;//url to JSON object, this delegates credential or secret out of dev

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getSecretParametersURL() {
        return secretParametersURL;
    }

    public void setSecretParametersURL(String secretParametersURL) {
        this.secretParametersURL = secretParametersURL;
    }
}
