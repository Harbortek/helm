package com.wangeditor.html.element;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.html.ElemToHtmlFunction;
import com.wangeditor.html.HtmlConfigRegistry;
import com.wangeditor.slate.Element;

/**
 * @description 分割线元素转HTML
 * @author wangfupeng (Java version translator)
 */
public class DividerToHtml implements HtmlConfigRegistry.ElemToHtmlConf {
    
    /**
     * 获取元素类型
     * @return 元素类型
     */
    @Override
    public String getType() {
        return "divider";
    }
    
    /**
     * 获取元素转HTML函数
     * @return 转换函数
     */
    @Override
    public ElemToHtmlFunction getElemToHtml() {
        return (elemNode, childrenHtml, editor) -> {
            return "<hr/>";
        };
    }
}