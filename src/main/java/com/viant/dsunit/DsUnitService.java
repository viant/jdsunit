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

import java.util.Map;

/**
 * DsUnit client
 */

public interface DsUnitService {


    int FULL_TABLE_DATASET_CHECK_POLICY = 0;
    //SnapshotDatasetCheckPolicy policy will drive comparison of subset of  actual datastore data that is is listed in expected dataset
    int SNAPSHOT_DATASET_CHECK_POLICY = 1;


    //Init creates datastore manager and register it in manaer registry, if ClearDatastore flag is set it will drop and create datastore.
    Response init(InitDatastoreRequest request);

    //InitFromUrl reads from url  InitDatastoresRequest JSON and initializes
    Response initFromURL(String url);


    //ExecuteScripts executes script defined in the request
    Response executeScripts(ExecuteScriptRequest request);

    //ExecuteScripts loads ExecuteScriptsExecuteScripts JSON from url and executes it.
    Response executeScriptsFromURL(String url);


    //PrepareDatastore prepares datastore for passed datastore, it matches with prefix followed by prepare_<tablename> all file in base dir , see DatasetTestManager#PrepareDatastore
    Response prepareDatastore(String datastore, String baseDir, String prefix);

    //PrepareDatastore prepares datastore for request, see DatasetTestManager#PrepareDatastore
    Response prepareDatastore(PrepareDatastoreRequest request);


    //PrepareDatastore laods PrepareDatastoreRequest JSON from url to prepare datastore, see DatasetTestManager#PrepareDatastore
    Response prepareDatastoreFromURL(String url);


    //ExpectDatasets verifies dataset data in datastore for matches files with prefix followed by expect_<tablename> in base dir , see DatasetTestManager#PrepareDatastore
    ExpectResponse expectDatasets(String datastore, String baseDir, String prefix, int checkPolicy);


    //ExpectDatasets verifies dataset data in datastore for matches files with prefix followed by expect_<tablename> in base dir , see DatasetTestManager#PrepareDatastore
    ExpectResponse expectDatasets(String datastore, String tableName, int checkPolicy, Map<String, Object>... rowValues);


    //ExpectDatasets verifies dataset data in datastore for passed in request, see DatasetTestManager#ExpectDataset
    ExpectResponse expectDatasets(ExpectDatasetRequest request);

    //ExpectDatasets loads ExpectDatasetRequest json from url to verify dataset, see DatasetTestManager#ExpectDataset
    ExpectResponse expectDatasetsFromURL(String url);

    //ExpectDatasets verifieds dataset data in datastore for matches with json content
    ExpectResponse expectDatasetsFromJsonContent(String datastore, String tableName, String content, int checkPolity);


    void close();

}
