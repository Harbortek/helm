package com.wangeditor.html.element;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.html.ElemToHtmlFunction;
import com.wangeditor.html.HtmlConfigRegistry;
import com.wangeditor.slate.Element;

import java.util.Map;

/**
 * @description 代码元素转HTML
 * @author wangfupeng (Java version translator)
 */
public class CodeToHtml implements HtmlConfigRegistry.ElemToHtmlConf {
    
    /**
     * 获取元素类型
     * @return 元素类型
     */
    @Override
    public String getType() {
        return "code";
    }
    
    /**
     * 获取元素转HTML函数
     * @return 转换函数
     */
    @Override
    public ElemToHtmlFunction getElemToHtml() {
        return (elemNode, childrenHtml, editor) -> {
            Map<String, Object> properties = elemNode.getProperties();
            String language = properties.containsKey("language") ? properties.get("language").toString() : "";
            
            if (childrenHtml == null || childrenHtml.isEmpty()) {
                childrenHtml = "<br>";
            }
            
            // 如果有语言属性，添加到class中
            String classAttr = language.isEmpty() ? "" : String.format(" class=\"language-%s\"", language);
            
            return String.format("<code%s>%s</code>", classAttr, childrenHtml);
        };
    }
}