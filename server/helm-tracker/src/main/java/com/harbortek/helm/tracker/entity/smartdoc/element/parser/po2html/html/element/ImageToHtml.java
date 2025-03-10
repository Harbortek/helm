package com.wangeditor.html.element;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.html.ElemToHtmlFunction;
import com.wangeditor.html.HtmlConfigRegistry;
import com.wangeditor.slate.Element;

import java.util.Map;

/**
 * @description 图片元素转HTML
 * @author wangfupeng (Java version translator)
 */
public class ImageToHtml implements HtmlConfigRegistry.ElemToHtmlConf {
    
    /**
     * 获取元素类型
     * @return 元素类型
     */
    @Override
    public String getType() {
        return "image";
    }
    
    /**
     * 获取元素转HTML函数
     * @return 转换函数
     */
    @Override
    public ElemToHtmlFunction getElemToHtml() {
        return (elemNode, childrenHtml, editor) -> {
            Map<String, Object> properties = elemNode.getProperties();
            String src = properties.containsKey("src") ? properties.get("src").toString() : "";
            String alt = properties.containsKey("alt") ? properties.get("alt").toString() : "";
            String href = properties.containsKey("href") ? properties.get("href").toString() : "";
            
            // 处理图片样式
            StringBuilder styleStr = new StringBuilder();
            
            // 处理宽度
            if (properties.containsKey("width")) {
                String width = properties.get("width").toString();
                styleStr.append("width: ").append(width).append(";\n");
            }
            
            // 处理高度
            if (properties.containsKey("height")) {
                String height = properties.get("height").toString();
                styleStr.append("height: ").append(height).append(";\n");
            }
            
            String style = styleStr.length() > 0 ? String.format(" style=\"%s\"" , styleStr.toString()) : "";
            
            // 生成图片HTML
            String imgHtml = String.format("<img src=\"%s\" alt=\"%s\"%s/>", src, alt, style);
            
            // 如果有链接，则包装在<a>标签中
            if (!href.isEmpty()) {
                imgHtml = String.format("<a href=\"%s\" target=\"_blank\">%s</a>", href, imgHtml);
            }
            
            return imgHtml;
        };
    }
}