package com.wangeditor.html;

import com.wangeditor.slate.Descendant;

/**
 * @description 样式转HTML函数接口
 * @author wangfupeng (Java version translator)
 */
@FunctionalInterface
public interface StyleToHtmlFunction {
    
    /**
     * 将节点样式转换为HTML
     * @param node 节点
     * @param elemHtml 元素HTML
     * @return 处理后的HTML字符串
     */
    String apply(Descendant node, String elemHtml);
}