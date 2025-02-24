/**
 * @description to html
 */

function titleToHtml(elem, childrenHtml) {
    return `<p data-w-e-type="title">${childrenHtml}</p>`
}

export const titleToHtmlConf = {
    type: 'title',
    elemToHtml: titleToHtml,
}