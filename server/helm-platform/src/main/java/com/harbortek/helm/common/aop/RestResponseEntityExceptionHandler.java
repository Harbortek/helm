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

package com.harbortek.helm.common.aop;

import com.harbortek.helm.common.entity.ApiResponseMessage;
import com.harbortek.helm.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleDataIntegrity(RuntimeException ex, WebRequest request) {
        logger.error("DataIntegrityViolationException", ex);
        if (ex instanceof DataIntegrityViolationException) {
            DataIntegrityViolationException divex = (DataIntegrityViolationException) ex;
            if (divex.getMessage().toLowerCase().contains("delete ")) {
                ApiResponseMessage message =
                        new ApiResponseMessage("DataIntegrityViolationException", "数据正在使用中，无法删除!");
                return handleExceptionInternal(ex, message,
                                               new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
            }
        }
        ApiResponseMessage message = new ApiResponseMessage("DataIntegrityViolationException", "数据访问错误!");
        return handleExceptionInternal(ex, message,
                                       new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    protected ResponseEntity<Object> handleDuplicateKey(RuntimeException ex, WebRequest request) {
        logger.error("DataAccessException", ex);
        ApiResponseMessage message = new ApiResponseMessage("DuplicateKeyException", "禁止插入重复数据!");
        return handleExceptionInternal(ex, message,
                                       new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    @ExceptionHandler(value = {DataAccessException.class})
    protected ResponseEntity<Object> handleDataAccess(RuntimeException ex, WebRequest request) {
        logger.error("DataAccessException", ex);
        ApiResponseMessage message = new ApiResponseMessage("DataAccessException", "数据访问错误!");
        return handleExceptionInternal(ex, message,
                                       new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> handleServerException(RuntimeException ex, WebRequest request) {
        logger.error("RuntimeException", ex);
        if (ex instanceof ServiceException) {
            ApiResponseMessage message = new ApiResponseMessage("ServiceException", ex.getMessage());
            return handleExceptionInternal(ex, message,
                                           new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        } else {
            ApiResponseMessage message = new ApiResponseMessage("RuntimeException", ex.getMessage());
            return handleExceptionInternal(ex, message,
                                           new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }


}
