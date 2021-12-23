/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projectpackagehunter;

class ElementInfo {

    private String[] details;

    private String header;

    public ElementInfo(String header, String[] details) {
        this.details = details;
        this.header = header;
    }

    public String[] getDetails() {
        return details;
    }

    public void setDetails(String[] details) {
        this.details = details;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        if (details != null) {
            super.toString();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < details.length; i++) {
                stringBuilder.append(details[i]).append("\n");
            }
            return stringBuilder.toString();
        } else {
            super.toString();
            return "NA";
        }
    }
}
