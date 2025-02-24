/**
 * @description mention element
 * @author wangfupeng
 */
type EmptyText = {
    text: '';
};
export type MentionElement = {
    type: 'mention';
    value: string;
    info: any;
    children: EmptyText[];
};
export {};
