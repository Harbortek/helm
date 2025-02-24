/**
 * @description elem to html
 * @author wangfupeng
 */
import { SlateElement } from '@wangeditor/editor';
declare function umlToHtml(elem: SlateElement, childrenHtml: string): string;
declare const conf: {
    type: string;
    elemToHtml: typeof umlToHtml;
};
export default conf;
