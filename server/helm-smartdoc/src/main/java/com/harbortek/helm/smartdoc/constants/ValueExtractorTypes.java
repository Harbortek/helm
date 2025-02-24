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

package com.harbortek.helm.smartdoc.constants;

public interface ValueExtractorTypes {
    String VALUE_IS  = "VALUE";
    String RICH_TEXT = "RICH_TEXT";
    String RICH_TEXT_WITHOUT_1ST_PARAGRAPH="RICH_TEXT_WITHOUT_1ST_PARAGRAPH";
    String RICH_TEXT_WITHOUT_REGEX="RICH_TEXT_WITHOUT_REGEXP";

    String RICH_TEXT_WITHOUT_STYLE="RICH_TEXT_WITHOUT_STYLE";
    String RICH_TEXT_WITH_STYLE="RICH_TEXT_WITH_STYLE";
    String TEXT_WITH_REGEX="TEXT_WITH_REGEXP";
    String TEXT_WITh_1ST_PARAGRAPH="TEXT_WITHOUT_1ST_PARAGRAPH";
    String TABLE_COLUMN_MAP = "TABLE_COLUMN_MAP";
}
