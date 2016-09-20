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

import org.junit.Test;
import   org.junit.Assert;

import java.util.concurrent.Executors;

/**
 *
 */
public class ExecutorTest {

    @Test
    public void testExecutor() {
        try {
            Executor.Info response = Executor.execute(Executors.newFixedThreadPool(1),   100000, null, null,false,  "pwd");
            Assert.assertTrue("should return current path", response.getOutput().length() > 0);
            } catch (Exception e) {
            throw new IllegalStateException("Failed to executr time");
        }

    }


}
