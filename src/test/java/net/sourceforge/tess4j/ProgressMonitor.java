/**
 * Copyright @ 2014 Quan Nguyen
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
package net.sourceforge.tess4j;

import com.sun.jna.Pointer;
import net.sourceforge.tess4j.util.LoggHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.sourceforge.tess4j.ITessAPI.TRUE;

class ProgressMonitor extends Thread {

    ITessAPI.ETEXT_DESC monitor;
    StringBuilder outputMessage = new StringBuilder();

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());

    public ProgressMonitor(ITessAPI.ETEXT_DESC monitor) {
        this.monitor = monitor;
    }

    public String getMessage() {
        return outputMessage.toString();
    }

    @Override
    public void run() {
        try {
            while (true) {
                logger.info("ocr alive: " + (monitor.ocr_alive == TRUE));
                logger.info("progress: " + monitor.progress);
                outputMessage.append(monitor.more_to_come);
                if (monitor.progress >= 100) {
                    break;
                }
                Thread.sleep(100);
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Cancels OCR operation.
     */
    public void cancel() {
        monitor.cancel = new ITessAPI.CANCEL_FUNC() {
            @Override
            public boolean invoke(Pointer cancel_this, int words) {
                return true;
            }
        };
    }
    
    /**
     * Resets cancel flag.
     */
    public void reset() {
        monitor.cancel = null;
    }
}
