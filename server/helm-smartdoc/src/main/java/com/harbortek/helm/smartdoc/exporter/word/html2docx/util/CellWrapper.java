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

package com.harbortek.helm.smartdoc.exporter.word.html2docx.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.docx4j.wml.*;

import java.math.BigInteger;
import java.util.Optional;

/**
 * A utility class for wrapping and managing table cell properties for docx4j library.
 */
public class CellWrapper {
    private String width;
    private String content;
    private String style;
    private String merge;
    private BigInteger colspan;

    /**
     * Gets the width of the table cell.
     *
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * Gets the content of the table cell.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the table cell.
     *
     * @param content the content to set
     * @return the current instance of the CellWrapper
     */
    public CellWrapper setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Gets the style of the table cell.
     *
     * @return the style
     */
    public String getStyle() {
        return style;
    }


    /**
     * Sets the style of the table cell.
     * Extract and set the width of the table cell.
     *
     * @param style the style to set
     * @return the current instance of the CellWrapper
     */
    public CellWrapper setStyle(String style) {
        this.style = style;
        this.width = ConverterUtils.getPercentWidthFromStyle(style);
        return this;
    }

    /**
     * Gets the merge status of the table cell.
     *
     * @return the merge status
     */
    public String getMerge() {
        return merge;
    }

    /**
     * Sets the merge status of the table cell.
     *
     * @param merge the merge status to set
     * @return the current instance of the CellWrapper
     */
    public CellWrapper setMerge(String merge) {
        this.merge = merge;
        return this;
    }

    /**
     * Gets the colspan of the table cell.
     *
     * @return the colspan
     */
    public BigInteger getColspan() {
        return colspan;
    }

    /**
     * Sets the colspan of the table cell.
     *
     * @param colspan the colspan to set
     * @return the current instance of the CellWrapper
     */
    public CellWrapper setColspan(BigInteger colspan) {
        this.colspan = colspan;
        return this;
    }

    /**
     * Sets the cell parameters for the given table cell.
     *
     * @param tableCell the table cell to set the parameters for
     */
    public void setCellParams(Tc tableCell) {
        TcPr tableCellProperties = RunUtils.getObjectFactory().createTcPr();
        setTableCellWidth(tableCellProperties);
        setTableCellStyle(tableCellProperties);
        setTableCellMerge(tableCellProperties);
        setTableCellColspan(tableCellProperties);
        tableCell.setTcPr(tableCellProperties);
    }

    /**
     * Sets the table cell width for the given table cell properties.
     *
     * @param tableCellProperties the table cell properties to set the width for
     */
    private void setTableCellWidth(TcPr tableCellProperties) {
        Optional.ofNullable(width)
                .filter(w -> !w.isEmpty())
                .ifPresent(w -> {
                    if (NumberUtils.isCreatable(width)) {
                        TblWidth tableWidth = RunUtils.getObjectFactory().createTblWidth();
                        tableWidth.setW(BigInteger.valueOf(Integer.parseInt(w) * 50L));
                        tableWidth.setType("pct");
                        tableCellProperties.setTcW(tableWidth);
                    }
                });
    }

    /**
     * Sets the table cell style for the given table cell properties.
     *
     * @param tableCellProperties the table cell properties to set the style for
     */
    private void setTableCellStyle(TcPr tableCellProperties) {
        Optional.ofNullable(style)
                .filter(s -> !s.isEmpty())
                .ifPresent(s -> {
                    CTShd shd = RunUtils.getObjectFactory().createCTShd();
                    shd.setVal(STShd.CLEAR);
                    shd.setColor("auto");
                    shd.setFill(StringUtils.substringBetween(s, "background-color: ", ";"));
                    tableCellProperties.setShd(shd);
                });
    }

    /**
     * Sets the table cell merge for the given table cell properties.
     *
     * @param tableCellProperties the table cell properties to set the merge for
     */
    private void setTableCellMerge(TcPr tableCellProperties) {
        Optional.ofNullable(merge)
                .filter(m -> !m.isEmpty())
                .ifPresent(m -> {
                    TcPrInner.VMerge vMerge = new TcPrInner.VMerge();
                    if ("restart".equals(m) || "continue".equals(m)) {
                        vMerge.setVal(m);
                    } else {
                        throw new IllegalArgumentException("Merge value must be restart or continue");
                    }
                    tableCellProperties.setVMerge(vMerge);
                });
    }

    /**
     * Sets the table cell colspan for the given table cell properties.
     *
     * @param tableCellProperties the table cell properties to set the colspan for
     */
    private void setTableCellColspan(TcPr tableCellProperties) {
        Optional.ofNullable(colspan)
                .ifPresent(c -> {
                    TcPrInner.GridSpan gridSpan = new TcPrInner.GridSpan();
                    gridSpan.setVal(c);
                    tableCellProperties.setGridSpan(gridSpan);
                });
    }
}
