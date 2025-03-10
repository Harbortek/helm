package com.wangeditor.html.element;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.html.ElemToHtmlFunction;
import com.wangeditor.html.HtmlConfigRegistry;
import com.wangeditor.slate.Element;

/**
 * @description 标题元素转HTML
 * @author wangfupeng (Java version translator)
 */
public class HeaderToHtml implements HtmlConfigRegistry.ElemToHtmlConf {
    private final String type;
    private final String tag;
    
    /**
     * 构造函数
     * @param type 元素类型
     * @param tag HTML标签
     */
    public HeaderToHtml(String type, String tag) {
        this.type = type;
        this.tag = tag;
    }
    
    /**
     * 获取元素类型
     * @return 元素类型
     */
    @Override
    public String getType() {
        return type;
    }
    
    /**
     * 获取元素转HTML函数
     * @return 转换函数
     */
    @Override
    public ElemToHtmlFunction getElemToHtml() {
        return (elemNode, childrenHtml, editor) -> {
            if (childrenHtml == null || childrenHtml.isEmpty()) {
                return String.format("<%s><br></%s>", tag, tag);
            }
            return String.format("<%s>%s</%s>", tag, childrenHtml, tag);
        };
    }
}