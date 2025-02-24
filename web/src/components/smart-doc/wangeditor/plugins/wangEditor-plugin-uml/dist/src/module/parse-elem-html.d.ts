/**
 * @description parse elem html
 * @author wangfupeng
 */
import { DOMElement } from '../utils/dom';
import { IDomEditor, SlateDescendant, SlateElement } from '@wangeditor/editor';
declare function parseHtml(elem: DOMElement, children: SlateDescendant[], editor: IDomEditor): SlateElement;
declare const parseHtmlConf: {
    selector: string;
    parseElemHtml: typeof parseHtml;
};
export default parseHtmlConf;
