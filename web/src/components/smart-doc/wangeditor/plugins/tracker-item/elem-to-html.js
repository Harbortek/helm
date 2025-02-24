/**
 * @description to html
 */
function trackerItemToHtml(elem, childrenHtml) {
    const { ref } = elem;
    return `<p data-x-ref=${ref}>${childrenHtml}</p>`
}
export const trackerItemToHtmlConf = {
    type: 'tracker-item',
    elemToHtml: trackerItemToHtml,
}

function trackerItemTitleToHtml(elem, childrenHtml) {
    const { ref } = elem;
    return `<p data-x-ref=${ref}>${childrenHtml}</p>`
}

export const trackerItemTitleToHtmlConf = {
    type: 'tracker-item-title',
    elemToHtml: trackerItemTitleToHtml,
}

function trackerItemDescToHtml(elem, childrenHtml) {
    const { ref } = elem;
    return `<p data-x-ref=${ref}>${childrenHtml}</p>`
}

export const trackerItemDescToHtmlConf = {
    type: 'tracker-item-description',
    elemToHtml: trackerItemDescToHtml,
}
function trackerItemExtraToHtml(elem, childrenHtml) {
    const { ref } = elem;
    return `<p data-x-ref=${ref}>${childrenHtml}</p>`
}

export const trackerItemExtraToHtmlConf = {
    type: 'tracker-item-extra',
    elemToHtml: trackerItemExtraToHtml,
}