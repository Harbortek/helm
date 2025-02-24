/**
 * @description upload attachment menu
 * @author wangfupeng
 */
import { IDomEditor } from '@wangeditor/editor';
import { IButtonMenu } from '@wangeditor/editor';
declare class InsertUMLMenu implements IButtonMenu {
    readonly title: string;
    readonly iconSvg = "<svg t=\"1695797780258\" class=\"icon\" viewBox=\"0 0 1024 1024\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" p-id=\"5167\" id=\"mx_n_1695797780259\" width=\"200\" height=\"200\"><path d=\"M780.007475 65.267493 243.903497 65.267493c-98.642624 0-178.61298 80.013335-178.61298 178.615027l0 536.102954c0 98.774631 79.969333 178.744987 178.61298 178.744987l536.105001 0c98.728582 0 178.700985-79.969333 178.700985-178.744987L958.709483 243.88252C958.70846 145.280828 878.73708 65.267493 780.007475 65.267493zM869.44546 735.353974c0 73.994248-60.119239 134.069485-134.113487 134.069485L288.667004 869.423459c-74.082252 0-134.071532-60.075237-134.071532-134.069485l0-89.307002 178.744987-178.744987 178.615027 223.37751 268.051989-268.050965-89.307002-44.6315L511.955486 556.608987 333.340459 333.319481 154.595472 511.932462 154.595472 288.555975c0-73.992201 59.989279-133.981481 134.071532-133.981481l446.664969 0c73.994248 0 134.113487 59.989279 134.113487 133.981481L869.44546 735.353974z\" p-id=\"5168\"></path></svg>";
    readonly tag = "button";
    getValue(editor: IDomEditor): string | boolean;
    isActive(editor: IDomEditor): boolean;
    exec(editor: IDomEditor, value: string | boolean): void;
    isDisabled(editor: IDomEditor): boolean;
    private getMenuConfig;
}
export default InsertUMLMenu;
