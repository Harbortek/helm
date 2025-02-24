/**
 * @description menu entry
 * @author wangfupeng
 */
import InsertUMLMenu from './InsertUml';
import EditUmlMenu from './EditUml';
export declare const insertUmlMenuConf: {
    key: string;
    factory(): InsertUMLMenu;
    config: {
        onInsertedUml(elem: import("../custom-types").UmlElement): void;
    };
};
export declare const editUmlMenuConf: {
    key: string;
    factory(): EditUmlMenu;
    config: {
        onInsertedUml(elem: import("../custom-types").UmlElement): void;
    };
};
