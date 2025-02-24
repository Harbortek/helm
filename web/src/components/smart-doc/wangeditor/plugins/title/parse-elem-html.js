/**
 * @description parse html
 */


function parseTitleHtml(elem, children, editor) {
    return {
        type: 'title',
        children: children,
    }
}

export const parseTitleHtmlConf = {
    selector: 'p[data-w-e-type="title"]', 
    parseElemHtml: parseTitleHtml,
}
