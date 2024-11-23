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
module net.sourceforge.tess4j {
    requires com.sun.jna;
    requires jai.imageio.core;
    requires java.desktop;
    requires java.xml;
    requires jboss.vfs;
    requires lept4j;
    requires org.apache.commons.io;
    requires org.apache.pdfbox;
    requires org.apache.pdfbox.io;
    requires org.apache.pdfbox.tools;
    requires org.slf4j;

    exports net.sourceforge.tess4j;
    exports net.sourceforge.tess4j.util;
    exports com.recognition.software.jdeskew;
}