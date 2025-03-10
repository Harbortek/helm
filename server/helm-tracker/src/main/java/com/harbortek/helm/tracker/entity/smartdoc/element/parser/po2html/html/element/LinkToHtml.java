package com.wangeditor.html.element;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.html.ElemToHtmlFunction;
import com.wangeditor.html.HtmlConfigRegistry;
import com.wangeditor.slate.Element;

import java.util.Map;

/**
 * @description 链接元素转HTML
 * @author wangfupeng (Java version translator)
 */
public class LinkToHtml implements HtmlConfigRegistry.ElemToHtmlConf {
    
    /**
     * 获取元素类型
     * @return 元素类型
     */
    @Override
    public String getType() {
        return "link";
    }
    
    /**
     * 获取元素转HTML函数
     * @return 转换函数
     */
    @Override
    public ElemToHtmlFunction getElemToHtml() {
        return (elemNode, childrenHtml, editor) -> {
            Map<String, Object> properties = elemNode.getProperties();
            String url = properties.containsKey("url") ? properties.get("url").toString() : "";
            String target = properties.containsKey("target") ? properties.get("target").toString() : "_blank";
            
            if (childrenHtml == null || childrenHtml.isEmpty()) {
                childrenHtml = url; // 如果没有子内容，则使用URL作为链接文本
            }
            
            return String.format("<a href=\"%s\" target=\"%s\">%s</a>", url, target, childrenHtml);
        };
    }
}