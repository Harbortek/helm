/**
 * @description attachment element
 * @author wangfupeng
 */
declare type EmptyText = {
    text: '';
};
export declare type UmlElement = {
    type: 'uml';
    xml: string;
    src: string;
    children: EmptyText[];
};
export {};
