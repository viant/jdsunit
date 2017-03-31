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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DsUnitServiceFactory represents dsunit service factory
 */
public class DsUnitServiceFactory {



    public static final String DSUNITURL = "github.com/viant/dsunit";
    private static final Logger logger = Logger.getLogger(DsUnitServiceFactory.class.getName());



    public Executor.Info runDsUnit(ExecutorService service, DsUnitConfig config) {
        String goCode = getDsUnitServerLoaderCode(config);

        FileOutputStream fileOutputStream = null;
        File installDir = new File(config.getGoPath());
        File dsUnitParentDirectory = new File(installDir, "src/dsunit");
        if(! dsUnitParentDirectory.exists()) {
            dsUnitParentDirectory.mkdirs();
        }
        File dsUnitServerLaunchFile = new File(dsUnitParentDirectory, "dsunit-server.go");

        try {
            fileOutputStream = new FileOutputStream(dsUnitServerLaunchFile);
            fileOutputStream.write(goCode.getBytes());


            logger.log(Level.INFO, "Updating dsunit dependenices ...");
            Executor.Info info = runCommand(service, dsUnitServerLaunchFile.getParentFile(), config, false, "sh", "-c", "cd " + dsUnitServerLaunchFile.getParent() + " && " + config.getGoBinPath() + " get");
            TimeUnit.SECONDS.sleep(8);
            if ((info.hasExitValue() && !(info.getExitValue() == 0) && Strings.isNullOrEmpty(info.getError()))) {
                throw new IllegalStateException("Failed to update dependencies for:" + dsUnitServerLaunchFile.getAbsolutePath() + " on " + info);
            }

            info = runCommand(service, dsUnitServerLaunchFile.getParentFile(), config, true, config.getGoBinPath(), "run", dsUnitServerLaunchFile.getName());
            TimeUnit.SECONDS.sleep(8);
            if ((info.hasExitValue() && !(info.getExitValue() == 0) && Strings.isNullOrEmpty(info.getError()))) {
                throw new IllegalStateException("Failed to run dsunit  server:" + dsUnitServerLaunchFile.getAbsolutePath() + " " + info);
            }
            logger.log(Level.INFO, "Started dsunit server pid " + info.getPid());
            return info;

        } catch (InterruptedException e) {
            throw new IllegalStateException("Failed to create/write file: " + dsUnitServerLaunchFile.getAbsolutePath(), e);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create/write file: " + dsUnitServerLaunchFile.getAbsolutePath(), e);
        } finally {
            if(fileOutputStream  != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException ignore) {

                }
            }
        }


    }

    private String getDsUnitServerLoaderCode(DsUnitConfig config) {
        File serverFile = new File(new File(config.getGoPath()), "src/" + DSUNITURL + "/server/dsunit-server.go");
        if (!serverFile.exists()) {
            throw new IllegalStateException("Failed to checkout dsunit - serve file missing: " + serverFile.getAbsolutePath());
        }
        String content = UrlUtil.readFromUrl("file://"+ serverFile);
        StringBuilder drivers = new StringBuilder("\"" +DSUNITURL+"\"" ).append("\n");
        for(String driver : config.getDrivers()) {
            drivers.append("\t_ ").append("\"" +driver+"\"").append("\n");
        }
        content = content.replace("\"" +DSUNITURL + "\"", drivers.toString());
        content = content.replace("package server", "package main");
        content = content.replace("8071", "" + config.getServerPort());
        return content;
    }


    public void resetDsUnit(ExecutorService service) {
        logger.log(Level.INFO, "Finding and killing existing dsunit process");
        Executor.Info info = Executor.execute(service, 0, null, null, false, "sh", "-c", "ps -c -ef | grep dsunit | awk {'print $2'}");
        if (!Strings.isNullOrEmpty(info.getOutput())) {
            try {
                int pid = Integer.parseInt(info.getOutput());
                logger.log(Level.INFO, "killing existing dsunit process pid = " + pid);
                Executor.killNow(service, pid);
            } catch (NumberFormatException ex) {
                logger.log(Level.INFO, "no existing processes are running");
            }
        }
    }

    public void checkOutDsUnit(ExecutorService service, DsUnitConfig config) {
        logger.log(Level.INFO, "Checking out dsunit");
        Executor.Info info = runCommand(service, new File(config.getGoPath()), config, false,  config.getGoBinPath(), "get", DSUNITURL);
        if ((info.hasExitValue() && !(info.getExitValue() == 0) && Strings.isNullOrEmpty(info.getError()))) {
            throw new IllegalStateException("Failed to checkout dsunit:" + info);
      }

    }


    public Executor.Info runCommand(ExecutorService service, File directory, DsUnitConfig config, boolean asDeamon, String ... arguments) {
        Map<String, String> env = new HashMap<String, String>();
        env.put("GOPATH", config.getGoPath());
        return Executor.execute(service, 0, env, directory, asDeamon, arguments);
    }


    


    public void validateConfig(DsUnitConfig config) {
        if (Strings.isNullOrEmpty(config.getGoBinPath())) {
            throw new IllegalStateException("goBinPath was empty");
        }
        if (!new File(config.getGoBinPath()).exists()) {
            throw new IllegalStateException("goBinPath path not found");
        }

        if (config.getGoPath().contains("${GOPATH}")) {
            config.setGoPath(System.getenv("GOPATH"));
        }
        if (Strings.isNullOrEmpty(config.getGoPath())) {
            throw new IllegalStateException("GoPath was empty");
        }

        if (!new File(config.getGoPath()).exists()) {
            new File(config.getGoPath()).mkdirs();
        }
        if(config.getAutoInstall()) {
            if (config.getDrivers() == null) {
                throw new IllegalStateException("Drivers was null");
            }
        }
        if (config.getServerPort() == 0) {
            throw new IllegalStateException("ServerPort was not set");
        }
        if(Strings.isNullOrEmpty(config.getServerName())) {
            throw new IllegalStateException("ServerName was not set");
        }

    }

    public DsUnitService create(DsUnitConfig config) {
        ExecutorService service = Executors.newCachedThreadPool();
        validateConfig(config);
        Executor.Info info = new Executor.Info();
        if(Boolean.TRUE.equals(config.getAutoInstall())) {
            checkOutDsUnit(service, config);
            info = Executor.execute(service, 0, null, null, false, "sh", "-c", "ps -c -ef | grep dsunit | awk {'print $2'}");
        }
        if(Boolean.TRUE.equals(config.getRunLocally())) {
            resetDsUnit(service);
            info = runDsUnit(service, config);
        }


        return new DsUnitClientImpl(config, new ClientFactoryImpl(), service, info);
    }

}
