package com.wangeditor.html;

import com.wangeditor.editor.DomEditor;
import com.wangeditor.editor.IDomEditor;
import com.wangeditor.slate.Descendant;
import com.wangeditor.slate.Text;
import com.wangeditor.utils.HtmlUtils;

import java.util.List;

/**
 * @description 文本节点转HTML
 * @author wangfupeng (Java version translator)
 */
public class TextToHtml {

    /**
     * 将文本节点转换为HTML
     * @param textNode 文本节点
     * @param editor 编辑器
     * @return HTML字符串
     */
    public static String textToHtml(Descendant textNode, IDomEditor editor) {
        if (!Text.isText(textNode)) {
            throw new IllegalArgumentException("Current node is not slate Text " + textNode);
        }
        
        String text = ((Text) textNode).getText();
        if (text == null) {
            throw new IllegalArgumentException("Text content is null in node " + textNode);
        }
        
        String textHtml = text;
        
        // 替换HTML特殊字符
        textHtml = HtmlUtils.replaceHtmlSpecialSymbols(textHtml);
        
        // 替换\n为<br>（一定要在替换特殊字符之后）
        List<Descendant> parents = DomEditor.getParentsNodes(editor, textNode);
        boolean hasPre = parents.stream().anyMatch(p -> "pre".equals(DomEditor.getNodeType(p))); // 上级节点中是否存在<pre>
        
        // 在<pre>标签不替换，其他都替换
        if (!hasPre) {
            textHtml = textHtml.replaceAll("\r\n|\r|\n", "<br>");
        }
        
        // 在<pre>内部，&nbsp;替换为空格
        if (hasPre) {
            textHtml = textHtml.replace("&nbsp;", " ");
        }
        
        // 处理空字符串
        if (textHtml.isEmpty()) {
            Descendant parentNode = DomEditor.getParentNode(null, textNode);
            if (parentNode != null && ((Element) parentNode).getChildren().isEmpty()) {
                // textNode是唯一的子节点，则改为<br>
                textHtml = "<br>";
            } else {
                // 其他情况的空字符串，直接返回
                return textHtml;
            }
        }
        
        // 增加文本样式，如color bgColor
        for (StyleToHtmlFunction fn : HtmlConfigRegistry.STYLE_TO_HTML_FN_LIST) {
            textHtml = fn.apply(textNode, textHtml);
        }
        
        return textHtml;
    }
}