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

package com.harbortek.helm.tracker.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public interface FieldTypes {
    String BOOLEAN = "BOOL";
    String CHOICE = "CHOICE";
    String DATE = "DATE";

    String TIME = "TIME";
    String DECIMAL = "DECIMAL";
    String DURATION = "DURATION";

    String INTEGER = "INTEGER";
    String SINGLE_OPTIONS = "OPTIONS";

    String MULTI_OPTIONS = "MULTI_OPTIONS";
    String STATUS = "STATUS";

    String TABLE = "TABLE";

    String TEST_STEP = "TEST_STEP";

    String TEXT = "TEXT";

    String TEXT_AREA = "TEXT_AREA";

    String USER = "USER";
    String MEMBERS = "MEMBERS";

    String WIKITEXT = "WIKI";

    String WORK_ITEM = "WORK_ITEM";

    String REFERENCE = "REFERENCE";

//	String PRINCIPAL = "PRINCIPAL";

    String LANGUAGE = "LANGUAGE";

    String COUNTRY = "COUNTRY";

    String COLOR = "COLOR";

    String URL = "URL";

    String CALC = "CALC";

    String LABEL = "LABEL";


    Set<String> ALL_TYPES = Collections.unmodifiableSet(new TreeSet<>(
            Arrays.asList(TEXT,TEXT_AREA, INTEGER, DECIMAL, DATE, BOOLEAN, CHOICE, REFERENCE, LANGUAGE, COUNTRY, WIKITEXT,
                          DURATION, COLOR, URL)));
    Set<String> ORDERED_TYPES =
            Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(INTEGER, DECIMAL, DATE, BOOLEAN, CHOICE, DURATION)));
    Set<String> NUMERIC_TYPES = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(INTEGER, DECIMAL, DURATION)));


    String WORK_ITEM_NO = "WORK_ITEM_NO";
    String SPRINT = "SPRINT";

    String STATUS_TYPE = "STATUS_TYPE";
    String PROJECT = "PROJECT";
    String WORK_ITEM_TYPE = "WORK_ITEM_TYPE";
}
