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

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * UrlUtility
 */
public class UrlUtil {



    public static String readFromUrl(String urlText) {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();
        try {
            if (urlText.startsWith("file://")) {
                urlText = urlText.replace("file://", "");
                try {
                    inputStream = new FileInputStream(urlText);
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("Failed to open file " + urlText, e);
                }
            } else {
                URL url = null;
                try {
                    url = new URL(urlText);
                    inputStream = url.openStream();
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to open url " + urlText, e);
                }
            }

            InputStreamReader reader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(reader);

            String inputLine = "";
            try {
                while ((inputLine = bufferedReader.readLine()) != null) {
                    result.append(inputLine).append("\n");
                }
            } catch (IOException e) {
                throw new IllegalStateException("Failed to read stream " + urlText, e);
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {

                }
            }
            if(bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignore) {

                }
            }
        }
        return result.toString();
    }

}
