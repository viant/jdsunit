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

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

/**
 * BigQuery integration test
 */
public class BigQueryDsUnitTest {


    private DsUnitService getDsUnitService() {
        DsUnitServiceFactory factory = new DsUnitServiceFactory();
        DsUnitConfig config = new DsUnitConfig();

        config.setTestDirectory(new File("src/test").getAbsolutePath());
        config.setGoBinPath("/usr/local/go/bin/go");

        //Go path stores dsunit with all dependecnies - if you want to get the build faster move out of target with may be deleted all time
        //or install dsunit in global gopath and disable autoinstall
        config.setGoPath(new File("target/").getAbsolutePath());


        //driver for big query connectivity
        config.setDrivers(Arrays.asList("github.com/viant/bgc", "github.com/mattn/go-sqlite3"));
        config.setServerName("http://localhost");
        config.setServerPort(8078);

        config.setAutoInstall(true);//automatically install dsunit (as long go is installed)
        config.setRunLocally(true);//runs dsunit in local mode per each service instance

        return factory.create(config);
    }


    @Test
    public void testBigQuery() {


        DsUnitService service = getDsUnitService();
        try {
            Response response = null;
            response = service.initFromURL("test://resources/bg/datastore_init.json");
            Assert.assertEquals("should have ok status " + response, "ok", response.getStatus());




            //populate datastore(BigQuery) with data defined in json files in src/test/resources starting with test1_prepare_<table>
            response = service.prepareDatastore("MyDataset", new File("src/test/resources/bg").getAbsolutePath(), "test1");
            Assert.assertEquals("should have ok status " + response, "ok", response.getStatus());

            //Business logic would come here
            //

            //active:true, comments:"xyz", id:4, last_access_time:"2016-03-01 08:43:00", salary:12800, username:"Vudi"

            {
                //verify that datastore(BigQuery) has snapshot of data defined in json files in src/test/resources starting with test1_expect_<table>
                ExpectResponse expectResponse = service.expectDatasets("MyDataset", new File("src/test/resources/bg").getAbsolutePath(), "test1", DsUnitService.SNAPSHOT_DATASET_CHECK_POLICY);
                Assert.assertEquals("should have ok status " + expectResponse, "ok", expectResponse.getStatus());
            }

            {

                //verify that datastore(BigQuery) has snapshot of data defined in json files in src/test/resources starting with test2_expect_<table>
                //test2 data has different data than datastore should there should be an error
                ExpectResponse expectResponse = service.expectDatasets("MyDataset", new File("src/test/resources/bg").getAbsolutePath(), "test2", DsUnitService.SNAPSHOT_DATASET_CHECK_POLICY);
                Assert.assertEquals("should have error status " + expectResponse, "error", expectResponse.getStatus());
            }
        } finally {
            service.close();
        }

    }

}
