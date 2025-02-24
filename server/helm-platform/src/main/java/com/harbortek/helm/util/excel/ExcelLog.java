/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.util.excel;

public class ExcelLog {

    @ExcelCell(index = 0)
    private Integer rowNum;
    private Object object;

    @ExcelCell(index = 1)
    private String log;

    /**
     * @return the rowNum
     */
    public Integer getRowNum() {
        return rowNum;
    }

    /**
     * @param rowNum
     *            the rowNum to set
     */
    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object
     *            the object to set
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * @return the log
     */
    public String getLog() {
        return log;
    }

    /**
     * @param object
     * @param log
     */
    public ExcelLog(Object object, String log) {
        super();
        this.object = object;
        this.log = log;
    }

    /**
     * @param rowNum
     * @param object
     * @param log
     */
    public ExcelLog(Object object, String log, Integer rowNum) {
        super();
        this.rowNum = rowNum;
        this.object = object;
        this.log = log;
    }

    /**
     * @param log
     *            the log to set
     */
    public void setLog(String log) {
        this.log = log;
    }

}
