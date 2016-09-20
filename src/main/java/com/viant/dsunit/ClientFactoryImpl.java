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


import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.conn.HttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

/**
 * The ClientFactoryImpl is to be injected as a singleton to create
 * Jersey clients that use HTTP persistent connections.
 */
@Singleton
public class ClientFactoryImpl implements ClientFactory {

    private final HttpClientConnectionManager connectionManager;

    @Inject
    public ClientFactoryImpl(HttpClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Client getClient() {
        ClientConfig clientConfig = new ClientConfig();

        clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, this.connectionManager);
        clientConfig.register(JacksonFeature.class);


        return ClientBuilder.newClient(clientConfig);
    }



}
