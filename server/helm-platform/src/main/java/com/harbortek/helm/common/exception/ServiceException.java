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

package com.harbortek.helm.common.exception;

import org.springframework.core.NestedRuntimeException;

public class ServiceException extends NestedRuntimeException {

    /**
     *
     */
    private String message;

    private static final long serialVersionUID = -871158005964778467L;

    public ServiceException(String msg, Throwable ex) {
        super(msg, ex);
        this.message = msg;
    }

    public ServiceException(String msg) {
        super(msg);
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }


    public static void throwException(String message) {
        throw new ServiceException(message);
    }

    public static void throwException(Throwable ex) {
        throw new ServiceException(ex.getMessage(),ex);
    }

}
