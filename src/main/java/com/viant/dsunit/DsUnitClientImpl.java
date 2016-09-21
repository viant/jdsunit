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

import com.google.common.base.Strings;
import com.viant.dsunit.cli.Executor;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * DsUnitClientImpl provides an implementation to dsunit client
 */
public class DsUnitClientImpl implements DsUnitService {

    public static final String VERSION = "/v1/";
    public static final String URI_TEMPLATE = VERSION + "%s";

    public static final String INIT_URI = String.format(URI_TEMPLATE, "init");
    public static final String EXECUTE_URI = String.format(URI_TEMPLATE, "execute");
    public static final String PREPARE_URI = String.format(URI_TEMPLATE, "prepare");
    public static final String EXPECT_URI = String.format(URI_TEMPLATE, "expect");


    private final DsUnitConfig config;
    private final ClientFactory clientFactory;
    private final Executor.Info info;
    private final ExecutorService service;
    private final Map<String, TableDescriptor> tables;

    public DsUnitClientImpl(DsUnitConfig config, ClientFactory clientFactory, ExecutorService service, Executor.Info info) {
        this.config = config;
        this.clientFactory = clientFactory;
        this.info = info;
        this.service = service;
        this.tables = new HashMap<String, TableDescriptor>();
    }

    public Response init(InitDatastoreRequest request) {
        Client client = this.clientFactory.getClient();
        String[] path = new String[]{INIT_URI};

        normalizeRequest(request);


        AsyncInvoker invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
        javax.ws.rs.core.Response restResponse;

        try {
            restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();


            if (restResponse.getStatus() == 401) {
                invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
                restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();

            }

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to submit init datastore request", ex);
        }
        handleInternalError(restResponse);
        Response response = restResponse.readEntity(new GenericType<Response>() {
        });
        return response;
    }

    @SuppressWarnings("unchecked")
    private void normalizeRequest(InitDatastoreRequest request) {
        tables.clear();
        for (DsUnitDatastoreConfig config : request.getDatastoreConfigs()) {
            for(TableDescriptor descriptor: config.getDescriptors()) {
                descriptor.setSchemaUrl(expandTestProtocolAsUrl(descriptor.getSchemaUrl()));
                if (!Strings.isNullOrEmpty(descriptor.getSchemaUrl())) {

                    String schemBody = UrlUtil.readFromUrl(descriptor.getSchemaUrl());
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Map schema = mapper.readValue(schemBody, HashMap.class);
                        if(schema.containsKey("Fields")) {
                            descriptor.setSchema(List.class.cast(schema.get("Fields")));
                        }
                    } catch (IOException e) {
                        throw new IllegalStateException("Failed read request from url", e);
                    }
                }

                tables.put(descriptor.getTable(), descriptor);
            }
            config.setConfigUrl(expandTestProtocolAsUrl(config.getConfigUrl()));

            if (!Strings.isNullOrEmpty(config.getConfigUrl())) {
                String configBody = UrlUtil.readFromUrl(config.getConfigUrl());

                ObjectMapper mapper = new ObjectMapper();
                try {
                    DatastoreConfig datastoreConfig = mapper.readValue(configBody, DatastoreConfig.class);
                    config.setConfig(datastoreConfig);
                } catch (IOException e) {
                    throw new IllegalStateException("Failed read request from url", e);
                }
            }





            if (config.getConfig() == null) {
                throw new IllegalStateException("Config was null on DsUnitDatastoreConfig ! " + config);
            }



            config.getConfig().setDescriptor(expandTestProtocolAsPath(config.getConfig().getDescriptor()));
            if (config.getConfig().getParameters() != null) {
                for (Map.Entry<String, String> entry : config.getConfig().getParameters().entrySet()) {
                    config.getConfig().getParameters().put(entry.getKey(), expandTestProtocolAsPath(entry.getValue()));
                }
            }
        }
    }

    public Response initFromURL(String url) {
        url = expandTestProtocolAsUrl(url);
        String content = UrlUtil.readFromUrl(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            InitDatastoreRequest request = mapper.readValue(content, InitDatastoreRequest.class);
            return init(request);
        } catch (IOException e) {
            throw new IllegalStateException("Failed read request from url", e);
        }
    }

    public Response executeScripts(ExecuteScriptRequest request) {
        for (Script script : request.getScripts()) {
            script.setUrl(expandTestProtocolAsUrl(script.getUrl()));
        }
        Client client = this.clientFactory.getClient();
        String[] path = new String[]{EXECUTE_URI};
        AsyncInvoker invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
        javax.ws.rs.core.Response restResponse;
        try {
            restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();
            if (restResponse.getStatus() == 401) {
                invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
                restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();

            }

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to submit init datastore request", ex);
        }
        handleInternalError(restResponse);


        Response response = restResponse.readEntity(new GenericType<Response>() {});
        return response;
    }

    public Response executeScriptsFromURL(String url) {
        url = expandTestProtocolAsUrl(url);
        String content = UrlUtil.readFromUrl(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExecuteScriptRequest request = mapper.readValue(content, ExecuteScriptRequest.class);
            return executeScripts(request);
        } catch (IOException e) {
            throw new IllegalStateException("Failed read request from url", e);
        }
    }



    @SuppressWarnings("unchecked")
    protected Datasets buildDatasets(String datastore, String baseDir, String prefix) {
        List<File> candidates = getFileCandidates(baseDir, prefix);
        Datasets result = new Datasets();
        result.setDatastore(datastore);
        result.setDatasets(new ArrayList<Dataset>());
        for(File candidate: candidates) {
            if(candidate.getName().endsWith(".json")) {

                List<Object> fileRows = getFileData(candidate);
                TableDescriptor descriptor = getTableDescriptor(prefix, candidate);
                Dataset dataset = new Dataset();
                dataset.setTable(descriptor.getTable());
                dataset.setFromQuery(descriptor.getFromQuery());
                dataset.setAutoincrement(descriptor.getAutoincrement());
                dataset.setPkColumns(descriptor.getPkColumns());
                dataset.setSchema(descriptor.getSchema());
                dataset.setSchemaUrl(descriptor.getSchemaUrl());
                result.getDatasets().add(dataset);
                List<Row> datasetRows = new ArrayList<Row>();
                Set<String> allColumns = new HashSet<String>();
                dataset.setRows(datasetRows);
                for(int i = 0;i<fileRows.size();i++) {
                    //TODO optimize building dataset, relly only each unique column sets needs separate table descriptor
                    Map<String, Object> rowValues = Map.class.cast(fileRows.get(i));
                    Row datasetRow = new Row();
                    datasetRow.setSource(candidate.getName()+"[" + (i+ 1)+"]");
                    datasetRow.setValues(rowValues);
                    datasetRows.add(datasetRow);
                    allColumns.addAll(rowValues.keySet());
                }
                dataset.setColumns(new ArrayList<String>(allColumns));
            }
        }
        return result;
    }

    private TableDescriptor getTableDescriptor(String prefix, File candidate) {
        int extensionIndex = candidate.getName().lastIndexOf('.');
        String table = candidate.getName().substring(prefix.length(), extensionIndex);
        TableDescriptor tableDescriptor  = this.tables.get(table);
        if(tableDescriptor == null) {
            throw new IllegalStateException("Failed to lookup table descriptor for " + table);
        }
        return tableDescriptor;
    }

    @SuppressWarnings("unchecked")
    private List<Object> getFileData(File candidate) {
        String fileContent = UrlUtil.readFromUrl("file://" + candidate.getAbsolutePath());
        ObjectMapper mapper = new ObjectMapper();
        try {
           return mapper.readValue(fileContent, ArrayList.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed read request from url", e);
        }
    }

    public Response prepareDatastore(String datastore, String baseDir, String prefix) {
        Datasets datasets = buildDatasets(datastore, baseDir, prefix+"_prepare_");
        PrepareDatastoreRequest prepareDatastoreRequest = new PrepareDatastoreRequest();
        prepareDatastoreRequest.setPrepare(Arrays.asList(datasets));
        return prepareDatastore(prepareDatastoreRequest);
    }


    protected List<File> getFileCandidates(String baseDir, String prefix) {
        File baseDirectory = new File(baseDir);
        if(!baseDirectory.exists()) {
            throw new IllegalStateException("base directory no found " + baseDirectory.getAbsolutePath());
        }
        List<File> candidates = new ArrayList<File>();
        for(File file: baseDirectory.listFiles()) {
            if(file.getName().startsWith(prefix)) {
                candidates.add(file);
            }
        }
        return candidates;
    }


    public Response prepareDatastore(PrepareDatastoreRequest request) {
        Client client = this.clientFactory.getClient();
        String[] path = new String[]{PREPARE_URI};

        AsyncInvoker invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
        javax.ws.rs.core.Response restResponse;

        try {
            restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();
            if (restResponse.getStatus() == 401) {
                invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
                restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();

            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to submit init datastore request", ex);
        }
        handleInternalError(restResponse);
        Response response = restResponse.readEntity(new GenericType<Response>() {});

        return response;
    }

    public Response prepareDatastoreFromURL(String url) {
        String content = UrlUtil.readFromUrl(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            PrepareDatastoreRequest request = mapper.readValue(content, PrepareDatastoreRequest.class);
            return prepareDatastore(request);
        } catch (IOException e) {
            throw new IllegalStateException("Failed read request from url", e);
        }
    }

    public ExpectResponse expectDatasets(String datastore, String baseDir, String prefix, int checkPolicy) {
        Datasets datasets = buildDatasets(datastore, baseDir, prefix+"_expect_");
        ExpectDatasetRequest expectDatasetRequest = new ExpectDatasetRequest();
        expectDatasetRequest.setExpect(Arrays.asList(datasets));
        expectDatasetRequest.setCheckPolicy(checkPolicy);
        return expectDatasets(expectDatasetRequest);
    }



    public ExpectResponse expectDatasets(ExpectDatasetRequest request) {
        Client client = this.clientFactory.getClient();
        String[] path = new String[]{EXPECT_URI};
        AsyncInvoker invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
        javax.ws.rs.core.Response restResponse;
        try {
            restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();
            if (restResponse.getStatus() == 401) {
                invoker = getAsyncInvoker(client, Collections.<String, String>emptyMap(), path);
                restResponse = invoker.post(Entity.entity(request, MediaType.APPLICATION_JSON)).get();

            }
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to submit init datastore request", ex);
        }
        handleInternalError (restResponse);
        ExpectResponse response = restResponse.readEntity(new GenericType<ExpectResponse>() {
        });
        return response;
    }


    public ExpectResponse expectDatasetsFromURL(String url) {
        String content = UrlUtil.readFromUrl(url);
        ObjectMapper mapper = new ObjectMapper();
        try {
            ExpectDatasetRequest request = mapper.readValue(content, ExpectDatasetRequest.class);
            return expectDatasets(request);
        } catch (IOException e) {
            throw new IllegalStateException("Failed read request from url", e);
        }
    }

    public void close() {
        service.shutdown();
        if (info.getPid() > 0) {
            try {
                Executor.killNow(service, info.getPid());
            } catch (RuntimeException ignore) {

            }
        }

    }


    private AsyncInvoker getAsyncInvoker(Client client, Map<String, String> queryString, String... paths) {

        String target = config.getServerName();
        if (config.getServerPort() != 80) {
            target = target + ":" + config.getServerPort();
        }
        WebTarget webTarget = client.target(target);

        for (String path : paths) {
            webTarget = webTarget.path(path);
        }
        for (String key : queryString.keySet()) {
            webTarget = webTarget.queryParam(key, queryString.get(key));
        }
        return webTarget.request(MediaType.APPLICATION_JSON)
                .async();
    }


    private void handleInternalError(javax.ws.rs.core.Response restResponse) {
        if(restResponse.getStatus() == 500) {
            throw new IllegalStateException("Failed to send request:    " + restResponse.getStatusInfo() + " " + restResponse.getHeaderString("Error"));
        }

    }


    public String expandTestProtocolAsPath(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("test://", config.getTestDirectory()+"/");
    }

    public String expandTestProtocolAsUrl(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("test://", "file://" + config.getTestDirectory()+"/");
    }
}
