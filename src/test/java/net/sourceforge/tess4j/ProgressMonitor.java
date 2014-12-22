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

import static net.sourceforge.tess4j.ITessAPI.TRUE;

class ProgressMonitor extends Thread {

    ITessAPI.ETEXT_DESC monitor;
    StringBuilder outputMessage = new StringBuilder();

    ProgressMonitor(ITessAPI.ETEXT_DESC monitor) {
        this.monitor = monitor;
    }

    String getMessage() {
        return outputMessage.toString();
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.err.println("ocr alive: " + (monitor.ocr_alive == TRUE));
                System.err.println("progress: " + monitor.progress);
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
}
