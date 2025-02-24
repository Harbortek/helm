/**
 * @description elem to html
 * @author wangfupeng
 */
import { SlateElement } from '@wangeditor/editor';
declare function mentionToHtml(elem: SlateElement, childrenHtml: string): string;
declare const conf: {
    type: string;
    elemToHtml: typeof mentionToHtml;
};
export default conf;
