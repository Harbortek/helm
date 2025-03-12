package com.harbortek.helm.tracker.entity.smartdoc.element.po.style;

import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.SlateNode;
import com.harbortek.helm.tracker.entity.smartdoc.element.po.text.SlateText;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Data
@Builder
public class IndentStyle extends SlateStyle {
    private String indent;

    public String styleToHtml(SlateNode node, String html) {
        if (node instanceof SlateText) {
            return html;
        }
        if (StringUtils.isEmpty(indent)) {
            return html;
        }
        // 解析 HTML
        Document doc = Jsoup.parse(html);
        Element elem = doc.body().child(0); // html 是一个单一的元素

        // 设置 text-indent 样式
        elem.attr("style", "text-indent: " + indent);
        // 返回修改后的 HTML
        return elem.outerHtml();
    }
}
