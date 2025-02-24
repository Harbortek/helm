/**
 * @description mention plugin
 * @author wangfupeng
 */
import { IDomEditor } from '@wangeditor/editor';
declare function withMention<T extends IDomEditor>(editor: T): T;
export default withMention;
