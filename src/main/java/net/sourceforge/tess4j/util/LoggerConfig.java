/**
 * Copyright @ 2015 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j.util;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Logging configuration.
 *
 * @author O.J. Sousa Rodrigues
 */
public enum LoggerConfig {

    INSTANCE;

    private boolean isLoaded = false;

    /**
     * This method loads the Logger configuration.
     *
     * @return true if the Logger configuration was loaded successfully.
     */
    public boolean loadConfig() {

        try {
            if (!isLoaded) {
                SLF4JBridgeHandler.removeHandlersForRootLogger();
                SLF4JBridgeHandler.install();
                this.isLoaded = true;
            }
        } catch (final Exception e) {
            System.err.println("Logger configuration could not be loaded.");
        }

        return this.isLoaded;
    }
}
