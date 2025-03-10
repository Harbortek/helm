package com.wangeditor.html;

import com.wangeditor.editor.IDomEditor;
import com.wangeditor.slate.Descendant;
import com.wangeditor.slate.Element;

/**
 * @description node -> html
 * @author wangfupeng (Java version translator)
 */
public class Node2Html {
    
    /**
     * 将节点转换为HTML
     * @param node 节点
     * @param editor 编辑器
     * @return HTML字符串
     */
    public static String node2Html(Descendant node, IDomEditor editor) {
        if (Element.isElement(node)) {
            // 元素节点
            return ElemToHtml.elemToHtml((Element) node, editor);
        } else {
            // 文本节点
            return TextToHtml.textToHtml(node, editor);
        }
    }
}