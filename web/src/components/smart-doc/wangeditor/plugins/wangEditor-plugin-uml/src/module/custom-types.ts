/**
 * @description attachment element
 * @author wangfupeng
 */

type EmptyText = {
  text: ''
}

export type UmlElement = {
  type: 'uml'
  xml: string
  src: string
  children: EmptyText[]
}
