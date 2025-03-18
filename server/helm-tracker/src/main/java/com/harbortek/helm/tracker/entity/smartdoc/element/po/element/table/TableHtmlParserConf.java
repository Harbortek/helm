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

package com.harbortek.helm.tracker.entity.smartdoc.element.po.element.table;

import cn.hutool.core.util.NumberUtil;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.HtmlParser;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseElemHtmlConf;
import com.harbortek.helm.tracker.entity.smartdoc.element.parser.html2po.ParseElemHtmlFn;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.SlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.element.list.ListSlateElement;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TableHtmlParserConf extends ParseElemHtmlConf {
    private static final String SELECTOR = "table";

    public TableHtmlParserConf() {
        super(SELECTOR, new TableHtmlParserFn());
    }

    private static class TableHtmlParserFn implements ParseElemHtmlFn {

        @Override
        public SlateElement apply(Element elem, List<SlateNode> children) {
            final List<Element> trElements = new ArrayList<>();
            elem.getElementsByTag("tr").forEach(e -> trElements.add(e));
            TableSlateElement tableSlateElement = new TableSlateElement<>();
            for (Element tr : trElements) {
                boolean isHeader = tr.tagName().equals("th");
                TableSlateElement.TableRowSlateElement tableRowSlateElement = new TableSlateElement.TableRowSlateElement();
                List<Element> tdElements = new ArrayList<>();
                for (Element td : tr.getElementsByTag("td")) {
                    tdElements.add(td);
                }
                for (Element td : tr.getElementsByTag("th")) {
                    tdElements.add(td);
                }
                for (Element td : tdElements) {
                    String colSpan = td.attr("colspan");
                    String rowspan = td.attr("rowspan");
                    TableSlateElement.TableCellSlateElement tableCellSlateElement = new TableSlateElement.TableCellSlateElement();
                    if (NumberUtil.isNumber(colSpan)) {
                        tableCellSlateElement.setColSpan(Integer.parseInt(colSpan));
                    }
                    if (NumberUtil.isNumber(rowspan)) {
                        tableCellSlateElement.setRowSpan(Integer.parseInt(rowspan));
                    }
                    tableCellSlateElement.setIsHeader(isHeader);
                    tableCellSlateElement.getChildren().add(HtmlParser.parseElemHtml(td));
                    tableRowSlateElement.getChildren().add(tableCellSlateElement);
                }
                tableSlateElement.getChildren().add(tableRowSlateElement);
            }
            tableSlateElement.setWidth("100%");
            return tableSlateElement;
        }

    }


}
