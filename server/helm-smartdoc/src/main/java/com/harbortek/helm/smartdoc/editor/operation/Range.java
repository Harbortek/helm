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

package com.harbortek.helm.smartdoc.editor.operation;

public class Range {
    private Point anchor; // 选区的起点
    private Point focus;  // 选区的终点

    public Range(Point anchor, Point focus) {
        this.anchor = anchor;
        this.focus = focus;
    }

    public Point getAnchor() {
        return anchor;
    }

    public void setAnchor(Point anchor) {
        this.anchor = anchor;
    }

    public Point getFocus() {
        return focus;
    }

    public void setFocus(Point focus) {
        this.focus = focus;
    }

    @Override
    public String toString() {
        return "Range{anchor=" + anchor + ", focus=" + focus + "}";
    }
}