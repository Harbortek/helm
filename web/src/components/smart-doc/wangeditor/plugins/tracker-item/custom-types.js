/**
 * @description 自定义 element
 */

//【注意】需要把自定义的 Element 引入到最外层的 custom-types.d.ts


export const TrackerItemTitleElement = {
    type: 'tracker-item-title',
    ref: '',
    children: []
}

export const TrackerItemDescElement = {
    type: 'tracker-item-description',
    ref: '',
    children: []
}
export const TrackerItemExtraElement = {
    type: 'tracker-item-extra',
    ref: '',
    rendered: false,
    children: []
}
export const TrackerItemElement = {
    type: 'tracker-item',
    ref: '',
    trackerItem: {},
    children: [TrackerItemTitleElement, TrackerItemDescElement, TrackerItemExtraElement]
}
