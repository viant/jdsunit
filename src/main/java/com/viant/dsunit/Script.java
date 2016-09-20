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
 * Script represetns a sql script
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Script {


        @JsonProperty("Datastore")
        private String datastore;

        @JsonProperty("Url")
        private String url;

        @JsonProperty("Sqls")
        private List<String> sqls;

        @JsonProperty("Body")
        private String body;

        public String getDatastore() {
                return datastore;
        }

        public void setDatastore(String datastore) {
                this.datastore = datastore;
        }

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {
                this.url = url;
        }

        public List<String> getSqls() {
                return sqls;
        }

        public void setSqls(List<String> sqls) {
                this.sqls = sqls;
        }

        public String getBody() {
                return body;
        }

        public void setBody(String body) {
                this.body = body;
        }
}
