package com.harbortek.helm.tracker.entity.smartdoc.element.po.style;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;

import java.io.Serializable;

public abstract class SlateStyle implements Serializable {
    protected String type;

    public SlateStyle() {
    }

    public abstract String styleToHtml(SlateNode node, String html);
}
