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
package com.viant.dsunit.cli;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Executor represents wrapped for command line executor.
 */
public class Executor {


    public static class Info {
        private Process process;
        private String output;
        private String error;
        private Integer exitValue;
        private int pid;
        private Map<String, String> enviroment;
        private String directory;
        private Future<Boolean> demonFutureResult;

        public String getOutput() {
            return output;
        }


        public String getError() {
            return error;
        }


        public Integer getExitValue() {
            return exitValue;
        }

        public boolean hasExitValue() {
            return exitValue != null;
        }


        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public Process getProcess() {
            return process;
        }

        public Map<String, String> getEnviroment() {
            return enviroment;
        }

        public Future<Boolean> getDemonFutureResult() {
            return demonFutureResult;
        }

        public String getDirectory() {
            return directory;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "\noutput='" + output + '\'' +
                    ",\n error='" + error + '\'' +
                    ",\n exitValue=" + exitValue +
                    ",\n pid=" + pid +
                    ",\n enviroment=" + enviroment +
                    ",\n directory='" + directory + '\'' +
                    '}';
        }
    }

    public static Info execute(final ExecutorService service , final long timeoutInMs, Map<String, String> env, File directory, boolean asDeamon, String... commandWithArguments) {

        final Info result = new Info();
        if (directory == null) {
            directory = new File("");
        }

        result.directory = directory.getAbsolutePath();
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(commandWithArguments);
            processBuilder.directory(directory.getAbsoluteFile());

            processBuilder.environment().putAll(System.getenv());
            if (env != null) {
                processBuilder.environment().putAll(env);
            }
            result.enviroment = processBuilder.environment();
            if (asDeamon) {

                result.demonFutureResult = service.submit(new Callable<Boolean>() {

                    public Boolean call() throws Exception {
                        startProcess(service, timeoutInMs, result, processBuilder);
                        return true;
                    }
                });

                for(int i = 0;i<10;i++) {
                    TimeUnit.SECONDS.sleep(1);
                    result.pid = getProcessPid(service, commandWithArguments);
                    if(result.pid > 0) {
                        break;
                    }
                }
            } else {
                startProcess(service, timeoutInMs, result, processBuilder);
            }
        } catch (Exception e) {
            result.error = e.toString();
        }

        return result;
    }


    private static int getProcessPid(ExecutorService service, String... commandWithArguments) {
        StringBuilder runningCommand = new StringBuilder();
        for (String item : commandWithArguments) {
            if(item.equals("&")) {
                continue;
            }
            if(runningCommand.length() > 0) {
                runningCommand.append(" ");
            }
            runningCommand.append(item);
        }
        Info info = Executor.execute(service, 0, null, null, false,  "sh", "-c", "ps -ef   |  grep '" +  runningCommand.toString() + "' | grep -v 'grep' | awk '{ print  $2 }' ");
        if(Strings.isNullOrEmpty(info.output)) {
            throw new IllegalStateException("Failed to get process info " + info);
        }
        return Integer.parseInt(info.output);
    }

    private static void startProcess(ExecutorService service, long timeoutInMs, Info result, ProcessBuilder processBuilder) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Process process = processBuilder.start();

        result.process = process;
        result.setPid(getPid(process));


        Future<Boolean> futureResult = service.submit(processResultCallable(process, result));

        Future<Boolean> futureErrorResult = service.submit(processErrorCallable(process, result));

        if (timeoutInMs == 0) {
            futureResult.get();
            futureErrorResult.get();

        } else {
            futureResult.get(timeoutInMs, TimeUnit.MILLISECONDS);
            futureErrorResult.get(timeoutInMs, TimeUnit.MILLISECONDS);
        }
        result.exitValue = process.exitValue();
    }


    private static int getPid(Process process) {
        try {
            Field fieled = process.getClass().getDeclaredField("pid");
            fieled.setAccessible(true);
            return fieled.getInt(process);

        } catch (Exception e) {
            return 0;
        }
    }

    public static Callable<Boolean> processResultCallable(final Process process, final Info result) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    while ((line = input.readLine()) != null) {
                        stringBuilder.append(line);

                    }
                    result.output = stringBuilder.toString();
                    return true;
                } catch (IOException e) {
                    if (input != null) {
                        input.close();
                    }
                    throw e;
                }
            }
        };
    }

    public static Callable<Boolean> processErrorCallable(final Process process, final Info result) {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                StringBuilder stringBuilder = new StringBuilder();
                try {
                    while ((line = input.readLine()) != null) {
                        stringBuilder.append(line);

                    }
                    result.error = stringBuilder.toString();
                    return true;
                } catch (IOException e) {
                    if (input != null) {
                        input.close();
                    }
                    throw e;
                }
            }
        };
    }


    public static void killNow(ExecutorService service, int pid) {
        Info info = execute(service, 0, null, null, false, "kill", "-9", "" + pid);
        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            throw new IllegalStateException("Failed to kill process " + pid, e);
        }
        if ((info.hasExitValue() && !(info.getExitValue() == 0) && Strings.isNullOrEmpty(info.getError()))) {
            throw new IllegalStateException("Failed to kill process " + pid + " " + info);
        }

    }

}
