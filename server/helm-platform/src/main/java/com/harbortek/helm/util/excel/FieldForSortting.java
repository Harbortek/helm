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

import java.lang.reflect.Field;

/**
 * The <code>FieldForSortting</code>
 *
 * @author sargeras.wang
 * @version 1.0, Created at 2013年9月17日
 */
public class FieldForSortting {
    private Field field;
    private int index;

    /**
     * @param field
     */
    public FieldForSortting(Field field) {
        super();
        this.field = field;
    }

    /**
     * @param field
     * @param index
     */
    public FieldForSortting(Field field, int index) {
        super();
        this.field = field;
        this.index = index;
    }

    /**
     * @return the field
     */
    public Field getField() {
        return field;
    }

    /**
     * @param field
     *            the field to set
     */
    public void setField(Field field) {
        this.field = field;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     *            the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

}
