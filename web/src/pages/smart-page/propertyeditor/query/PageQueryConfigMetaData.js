export const DEFAULT_PAGE_QUERY_CONFIG = {
  datasetId: "", // 查询组件当前的数据集id
  fields: [
    {
      field: "id", // 字段名
      type: "TEXT", // 字段类型 TEXT, NUMBER, DATE
      datasetId: "", // 字段所属数据集id
      autoConfig: "AUTO", // 字段自动配置类型 AUTO, CUSTOMIZE
      searchConfig: {
        renderType: "DROPDOWN", // 渲染类型 DROPDOWN, INPUT_TEXT, INPUT_NUMBER, DATE_PICKER
        // 下拉框配置
        dropdownConfig: {
          dataSource: "DATASET", // 数据源类型 DATASET,MANUAL
          // 数据集配置
          datasetId: "", // 数据集id
          searchField: "", // 搜索字段
          displayField: "", // 显示字段
          sortField: "", // 排序字段
          sortDirection: "", // 排序方向 ASC, DESC
          conditionMethod: "SINGLE", // 搜索类型 SINGLE, MULTI
          searchTrigger: "ON_CLICK", // 搜索触发类型 ON_CLICK, PRE_LOAD
          //手工输入配置
          options: [{ key: "", value: "" }], // 手工输入值
          enableDefault: false, // 是否启用默认值
          defaultConfig: {
            defaultValue: "", // 默认值,多个值为数组
          },
        },
        // 文本输入框配置
        inputTextConfig: {
          conditionMethod: "", // 条件形式  SINGLE，AND, OR
          enableDefault: false, // 是否启用默认值
          defaultValue: [
            {
              matchMethod: "", // 匹配方法 EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL
              matchValue: "",
            },
          ], // 默认值,多个值为数组
        },
        // 数字输入框配置
        inputNumberConfig: {
          aggregationType: "", // 聚合类型 SUM, AVG, MAX, MIN, COUNT,
          conditionMethod: "", // 条件形式  SINGLE，AND, OR
          enableDefault: false, // 是否启用默认值
          defaultConfig: {
            defaultValue: [
              {
                matchMethod: "", // 匹配方法 EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL
                matchValue: "",
              },
            ], // 默认值,多个值为数组
          },
        },
        // 日期选择框配置
        datePickerConfig: {
          dateFormat: "YYYY-MM-DD", // 时间粒度
          dataSource: "DEFAULT", // 数据源类型 DEFAULT,DATASET
          conditionMethod: "SINGLE", // 搜索类型 SINGLE_DATE, DATE_RANGE
          rangeType: "", // 范围类型 START_WITH, END_WITH, BETWEEN,QUICK
          quickConfig: [
            "TODAY",
            "YESTERDAY",
            "LAST_7_DAYS",
            "LAST_30_DAYS",
            "LAST_90_DAYS",
            "THIS_MONTH",
            "LAST_MONTH",
            "THIS_YEAR",
          ],
          enableDefault: false, // 是否启用默认值
          defaultConfig: {
            defaultValue: [
              {
                matchMethod: "", // 匹配方法 EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL
                matchValue: "",
                relative: true,
                relativeUnit: "DAY", // DAY, MONTH, YEAR
                relativeValue: 1,
                relativeDirection: "", // + -
              },
            ], // 默认值,多个值为数组
          },
        },
      },
      linkConfig: [
        {
          chartId: "", // 图表id
          chartName: "", // 图表标题
          chartIcon: "", // 图表图标
          datasetId: "", // 数据集id
          datasetName: "", // 数据集名称
          linkageField: "", // 联动字段
        },
      ],
    },
  ], // 查询字段列表
};
