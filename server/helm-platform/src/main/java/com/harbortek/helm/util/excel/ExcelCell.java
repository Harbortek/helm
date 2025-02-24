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


import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>ExcelCell</code><br>
 * 数值型的栏位只能使用Double
 * @see {@link com.sargeraswang.util.ExcelUtil.ExcelUtil#exportExcel}
 * @author sargeras.wang
 * @version 1.0, Created at 2013年9月14日
 * @version 1.1, Updated at 2013年10月20日 ,添加了专用于验证的子注解:Valid
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelCell {
    /**
     * 顺序 default 100
     *
     * @return index
     */
    int index();

    /**
     * 当值为null时要显示的值 default StringUtils.EMPTY
     *
     * @return defaultValue
     */
    String defaultValue() default StringUtils.EMPTY;

    /**
     * 用于验证
     *
     * @return valid
     */
    Valid valid() default @Valid();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Valid {
        /**
         * 必须与in中String相符,目前仅支持String类型
         *
         * @return e.g. {"key","value"}
         */
        String[] in() default {};

        /**
         * 是否允许为空,用于验证数据 default true
         *
         * @return allowNull
         */
        boolean allowNull() default true;

        /**
         * Apply a "greater than" constraint to the named property
         *
         * @return gt
         */
        double gt() default Double.NaN;

        /**
         * Apply a "less than" constraint to the named property
         * @return lt
         */
        double lt() default Double.NaN;

        /**
         * Apply a "greater than or equal" constraint to the named property
         *
         * @return ge
         */
        double ge() default Double.NaN;

        /**
         * Apply a "less than or equal" constraint to the named property
         *
         * @return le
         */
        double le() default Double.NaN;
    }
}
