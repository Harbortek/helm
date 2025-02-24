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

import java.util.List;

/**
 * The <code>ExcelLogs</code>
 *
 * @author sargeras.wang
 * @version 1.0, Created at 2013年9月17日
 */
public class ExcelLogs {
    private int totalRowCount;
    private int errorRowCount;
    private String errorLogFilePath;

    private Boolean hasError;
    private List<ExcelLog> logList;
    /**
     *
     */
    public ExcelLogs() {
        super();
        hasError = false;
    }


    public int getTotalRowCount() {
        return totalRowCount;
    }

    public void setTotalRowCount(int totalRowCount) {
        this.totalRowCount = totalRowCount;
    }

    public int getErrorRowCount() {
        return errorRowCount;
    }

    public void setErrorRowCount(int errorRowCount) {
        this.errorRowCount = errorRowCount;
    }

    /**
     * @return the hasError
     */
    public Boolean getHasError() {
        return hasError;
    }

    /**
     * @param hasError
     *            the hasError to set
     */
    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    /**
     * @return the logList
     */
    public List<ExcelLog> getLogList() {
        return logList;
    }

//    public List<ExcelLog> getErrorLogList() {
//        List<ExcelLog> errList = new ArrayList<>();
//        for (ExcelLog log : this.logList) {
//            if (log != null && StringUtils.isNotBlank(log.getLog())) {
//                errorLogList.add(log);
//            }
//        }
//        return errorLogList;
//    }

    /**
     * @param logList
     *            the logList to set
     */
    public void setLogList(List<ExcelLog> logList) {
        this.logList = logList;
    }

    public String getErrorLogFilePath() {
        return errorLogFilePath;
    }

    public void setErrorLogFilePath(String errorLogFilePath) {
        this.errorLogFilePath = errorLogFilePath;
    }
}
