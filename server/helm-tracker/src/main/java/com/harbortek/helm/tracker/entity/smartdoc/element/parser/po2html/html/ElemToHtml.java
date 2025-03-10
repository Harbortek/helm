package com.wangeditor.html;

import com.wangeditor.editor.Editor;
import com.wangeditor.editor.IDomEditor;
import com.wangeditor.slate.Descendant;
import com.wangeditor.slate.Element;

import java.util.List;
import java.util.Map;

/**
 * @description 元素节点转HTML
 * @author wangfupeng (Java version translator)
 */
public class ElemToHtml {

    /**
     * 默认的toHtml函数
     * @param elemNode 元素节点
     * @param childrenHtml 子节点HTML
     * @param editor 编辑器
     * @return HTML字符串
     */
    private static String defaultParser(Element elemNode, String childrenHtml, IDomEditor editor) {
        boolean isInline = editor.isInline(elemNode);
        String tag = isInline ? "span" : "div";
        return String.format("<%s>%s</%s>", tag, childrenHtml, tag);
    }

    /**
     * 根据type获取toHtml函数
     * @param type 节点类型
     * @return 转换函数
     */
    private static ElemToHtmlFunction getParser(String type) {
        ElemToHtmlFunction fn = HtmlConfigRegistry.ELEM_TO_HTML_CONF.get(type);
        return fn != null ? fn : ElemToHtml::defaultParser;
    }

    /**
     * 将元素节点转换为HTML
     * @param elemNode 元素节点
     * @param editor 编辑器
     * @return HTML字符串
     */
    public static String elemToHtml(Element elemNode, IDomEditor editor) {
        String type = elemNode.getType() != null ? elemNode.getType() : "";
        List<Descendant> children = elemNode.getChildren();
        boolean isVoid = Editor.isVoid(editor, elemNode);

        // 计算children html
        String childrenHtml = "";
        if (!isVoid) {
            // 非void node
            StringBuilder sb = new StringBuilder();
            for (Descendant child : children) {
                sb.append(Node2Html.node2Html(child, editor));
            }
            childrenHtml = sb.toString();
        }

        // 生成html
        ElemToHtmlFunction toHtmlFn = getParser(type);
        Object res = toHtmlFn.apply(elemNode, childrenHtml, editor);

        String elemHtml = "";
        if (res instanceof String) {
            elemHtml = (String) res;
        } else if (res instanceof ElemToHtmlResult) {
            ElemToHtmlResult result = (ElemToHtmlResult) res;
            elemHtml = result.getHtml() != null ? result.getHtml() : "";
        }

        // 添加样式（如text-align line-height等）
        if (!isVoid) {
            for (StyleToHtmlFunction fn : HtmlConfigRegistry.STYLE_TO_HTML_FN_LIST) {
                elemHtml = fn.apply(elemNode, elemHtml);
            }
        }

        // 直接返回html字符串
        if (res instanceof String) {
            return elemHtml;
        }

        // 解析prefix suffix（如list-item）
        ElemToHtmlResult result = (ElemToHtmlResult) res;
        String prefix = result.getPrefix() != null ? result.getPrefix() : "";
        String suffix = result.getSuffix() != null ? result.getSuffix() : "";
        
        if (!prefix.isEmpty()) {
            elemHtml = prefix + elemHtml;
        }
        if (!suffix.isEmpty()) {
            elemHtml = elemHtml + suffix;
        }
        
        return elemHtml;
    }
}