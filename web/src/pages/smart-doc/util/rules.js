export const CONDITION_TYPES = [
  {
    id: "containsWords",
    name: "含有词语",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "notContainsWords",
    name: "不含有词语",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "containsTextMatchingRegex",
    name: "与正则表达式匹配",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "notContainsTextMatchingRegex",
    name: "不与正则表达式匹配",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "hasStyle",
    name: "拥有样式",
    hasArgument: true,
    argumentType: "STYLE",
  },
  {
    id: "notHasStyle",
    name: "不拥有样式",
    hasArgument: true,
    argumentType: "STYLE",
  },
  { id: "isEmpty", name: "为空", hasArgument: false, argumentType: "TEXT" },
  {
    id: "isNotEmpty",
    name: "不为空",
    hasArgument: false,
    argumentType: "TEXT",
  },
  { id: "isHeading", name: "是标题", hasArgument: false, argumentType: "TEXT" },
  {
    id: "isNotHeading",
    name: "不是标题",
    hasArgument: false,
    argumentType: "TEXT",
  },
  {
    id: "isListItem",
    name: "是列表项",
    hasArgument: false,
    argumentType: "TEXT",
  },
  {
    id: "isNotListItem",
    name: "不是列表项",
    hasArgument: false,
    argumentType: "TEXT",
  },
  { id: "isTable", name: "是表格", hasArgument: false, argumentType: "TEXT" },
  {
    id: "isNotTable",
    name: "不是表格",
    hasArgument: false,
    argumentType: "TEXT",
  },
];

export const ACTION_TYPES = [
  { id: "VALUE", name: "值", hasArgument: true, argumentType: "TEXT" },
  { id: "RICH_TEXT", name: "富文本", hasArgument: false, argumentType: "TEXT" },
  {
    id: "RICH_TEXT_WITHOUT_1ST_PARAGRAPH",
    name: "除去第一个段落后的富文本",
    hasArgument: false,
    argumentType: "TEXT",
  },
  {
    id: "RICH_TEXT_WITHOUT_REGEXP",
    name: "不满足正则表达式的富文本",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    Id: "RICH_TEXT_WITHOUT_STYLE",
    name: "不具有样式的富文本",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "RICH_TEXT_WITH_STYLE",
    name: "含有样式的富文本",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "TEXT_WITH_REGEXP",
    name: "符合正则表达式的文本",
    hasArgument: true,
    argumentType: "TEXT",
  },
  {
    id: "TEXT_WITH_1ST_PARAGRAPH",
    name: "第一个段落的文本",
    hasArgument: false,
    argumentType: "TEXT",
  },
  {
    id: "TABLE_COLUMN_MAP",
    name: "表格字段映射",
    hasArgument: false,
    argumentType: "TABLE",
  },
  {
    id: "TABLE_COLUMN_MAP",
    name: "表格字段映射",
    hasArgument: false,
    argumentType: "TEST_STEP",
  },
];

export function isConditionHasArgument(condition) {
  let condintionType = CONDITION_TYPES.filter(
    (item) => item.id === condition.conditionId
  )[0];
  if (condintionType) return condintionType.hasArgument;
  return false;
}

export function getConditionArgumentType(condition) {
  let condintionType = CONDITION_TYPES.filter(
    (item) => item.id === condition.conditionId
  )[0];
  if (condintionType) return condintionType.argumentType;
  return "";
}

export function isActionHasArgument(action) {
  let actionType = ACTION_TYPES.filter(
    (item) => item.id === action.actionType
  )[0];
  if (actionType) return actionType.hasArgument;
  return false;
}

export function getActionArgumentType(action) {
  let actionType = ACTION_TYPES.filter(
    (item) => item.id === action.actionType
  )[0];
  if (actionType) return actionType.argumentType;
  return "";
}
