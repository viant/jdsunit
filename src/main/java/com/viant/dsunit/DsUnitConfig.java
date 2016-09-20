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

import java.util.List;

/**
 * DsUnitConfig
 */
public class DsUnitConfig {


    private Boolean autoInstall;
    private Boolean runLocally;
    private String goPath;
    private String goBinPath;
    private List<String> drivers;

    private String serverName;//remote dsunit server hostname
    private int serverPort;
    private String testDirectory;//it will be expanded from 'test://' protocol


    public Boolean getAutoInstall() {
        return autoInstall;
    }


    public Boolean getRunLocally() {
        return runLocally;
    }

    public void setRunLocally(Boolean runLocally) {
        this.runLocally = runLocally;
    }

    public void setAutoInstall(Boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    public String getGoPath() {
        return goPath;
    }

    public void setGoPath(String goPath) {
        this.goPath = goPath;
    }

    public String getGoBinPath() {
        return goBinPath;
    }

    public void setGoBinPath(String goBinPath) {
        this.goBinPath = goBinPath;
    }

    public String getTestDirectory() {
        return testDirectory;
    }

    public void setTestDirectory(String testDirectory) {
        this.testDirectory = testDirectory;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public List<String> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<String> drivers) {
        this.drivers = drivers;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}
