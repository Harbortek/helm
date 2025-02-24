export default {
  "tracker.field.type.TEXT": "单行文本",
  "tracker.field.type.TEXT_AREA": "多行文本",
  "tracker.field.type.WIKI": "带格式文本",
  "tracker.field.type.OPTIONS": "单选菜单",
  "tracker.field.type.MULTI_OPTIONS": "多选菜单",
  "tracker.field.type.BOOL": "是否",
  "tracker.field.type.DATE": "日期",
  "tracker.field.type.TIME": "时间",
  "tracker.field.type.DURATION": "时间间隔",
  "tracker.field.type.INTEGER": "整数",
  "tracker.field.type.DECIMAL": "浮点数",
  "tracker.field.type.USER": "单选用户",
  "tracker.field.type.MEMBERS": "多选用户",
  "tracker.field.type.WORK_ITEM": "工作项",
  "tracker.field.type.WORK_ITEM_NO": "工作项编号",
  "tracker.field.type.WORK_ITEM_TYPE": "工作项类型",
  "tracker.field.type.TABLE": "表格",
  "tracker.field.type.STATUS": "状态",
  "tracker.field.type.STATUS_TYPE": "状态类型",
  "tracker.field.type.COUNTRY": "国家",
  "tracker.field.type.LANGUAGE": "语言",
  "tracker.field.type.COLOR": "颜色",
  "tracker.field.type.REFERENCE": "引用",
  "tracker.field.type.PROJECT": "工作项项目",
  "tracker.field.type.SPRINT": "迭代",
  "tracker.field.type.TEST_STEP": "测试步骤",

  "project.permission.PROJECT_ADMIN": "管理项目",
  "project.permission.desc.PROJECT_ADMIN":
    "将成员添加为项目管理员，管理当前项目并更新项目的配置信息",

  "project.permission.PROJECT_VIEW": "查看项目",
  "project.permission.desc.PROJECT_VIEW":
    "允许成员浏览当前项目，包括工作项，筛选器等信息",

  "tracker.permission.ITEM_VIEW": "查看{0}",
  "tracker.permission.desc.ITEM_VIEW": "允许成员查看{0}",

  "tracker.permission.ITEM_CREATE": "创建{0}",
  "tracker.permission.desc.ITEM_CREATE": "允许成员创建{0}",

  "tracker.permission.ITEM_EDIT": "编辑{0}",
  "tracker.permission.desc.ITEM_EDIT":
    "允许成员修改{0}，以及对{0}进行其他变更操作",

  "tracker.permission.ITEM_CHANGE_STATUS": "更新{0}状态",
  "tracker.permission.desc.ITEM_CHANGE_STATUS": "允许成员更新{0}状态",

  "tracker.permission.ITEM_DELETE": "删除{0}",
  "tracker.permission.desc.ITEM_DELETE": "允许成员删除{0}",
  "tracker.permission.ITEM_MANAGE_WATCHER": "管理关注者",
  "tracker.permission.desc.ITEM_MANAGE_WATCHER": "允许成员修改{0}的关注者",
  "tracker.permission.ITEM_EDIT_ASSOCIATIONS": "编辑关联工作项",
  "tracker.permission.desc.ITEM_EDIT_ASSOCIATIONS":
    "允许成员修改{0}的关联工作项",
  "tracker.permission.ITEM_EXPORT": "导出{0}列表",
  "tracker.permission.desc.ITEM_EXPORT": "允许成员将{0}列表导出为文件",
  "tracker.permission.ITEM_MANAGE_ESTIMATE_WORK_HOURS": "管理预估工时",
  "tracker.permission.desc.ITEM_MANAGE_ESTIMATE_WORK_HOURS":
    "允许成员添加、修改、删除{0}的预估工时",
  "tracker.permission.ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS":
    "管理所有登记工时",
  "tracker.permission.desc.ITEM_MANAGE_ALL_REGISTERED_WORK_HOURS":
    "允许成员在{0}下添加所有成员的登记工时，在成员工时记录中修改或删除所有成员的登记工时和剩余工时",
  "tracker.permission.ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS":
    "管理自己的登记工时",
  "tracker.permission.desc.ITEM_MANAGE_OWN_REGISTERED_WORK_HOURS":
    "允许成员在{0}下添加自己的登记工时，在成员工时记录中修改或删除自己的登记工时和剩余工时",

  "tracker.event.CREATE_ITEM": "创建{0}",
  "tracker.event.desc.CREATE_ITEM": "创建{0}时发送通知",

  "tracker.event.CHANGE_ITEM_OWNER": "更改负责人",
  "tracker.event.desc.CHANGE_ITEM_OWNER": "更改负责人时发送通知",

  "tracker.event.CHANGE_ITEM_STATUS": "更改状态",
  "tracker.event.desc.CHANGE_ITEM_STATUS": "更改{0}状态时发送通知",

  "tracker.event.CHANGE_ITEM_PRIORITY": "更改优先级",
  "tracker.event.desc.CHANGE_ITEM_PRIORITY": "更改{0}优先级时发送通知",

  "tracker.event.CHANGE_ITEM_TITLE": "更改标题",
  "tracker.event.desc.CHANGE_ITEM_TITLE": "更改{0}标题时发送通知",

  "tracker.event.CHANGE_ITEM_DESCRIPTION": "更改描述",
  "tracker.event.desc.CHANGE_ITEM_DESCRIPTION": "更改{0}描述时发送通知",

  "tracker.event.CREATE_COMMENTS": "新增评论",
  "tracker.event.desc.CREATE_COMMENTS": "新增评论时发送通知",

  "tracker.event.CHANGE_ITEM_WATCHER": "新增/取消关注工作项",
  "tracker.event.desc.CHANGE_ITEM_WATCHER": "成员新增/取消关注{0}时发送通知",

  "tracker.event.CHANGE_ITEM_ASSOCIATIONS": "设置关联工作项",
  "tracker.event.desc.CHANGE_ITEM_ASSOCIATIONS":
    "添加/修改关联工作项时发送通知",

  "tracker.event.CHANGE_ITEM_ATTACHMENT": "上传文件",
  "tracker.event.desc.CHANGE_ITEM_ATTACHMENT": "上传{0}文件时发送通知",

  "tracker.event.CHANGE_ITEM_PROGRESS": "进度变更",
  "tracker.event.desc.CHANGE_ITEM_PROGRESS": "进度变更时发送通知",

  "tracker.event.CHANGE_ITEM_ESTIMATE_WORKING_HOURS": "更新预估工时",
  "tracker.event.desc.CHANGE_ITEM_ESTIMATE_WORKING_HOURS":
    "添加、修改或删除预估工时时发送通知",

  "tracker.event.CHANGE_ITEM_REGISTERED_WORKING_HOURS": "更新剩余工时",
  "tracker.event.desc.CHANGE_ITEM_REGISTERED_WORKING_HOURS":
    "添加、修改或删除剩余工时时发送通知",

  "tracker.event.CHANGE_ITEM_REMAINING_WORKING_HOURS": "更新登记工时 ",
  "tracker.event.desc.CHANGE_ITEM_REMAINING_WORKING_HOURS":
    "添加、修改或删除登记工时时发送通知",

  "tracker.event.CHANGE_ITEM_PLAN_START_DATE": "设置计划开始日期",
  "tracker.event.desc.CHANGE_ITEM_PLAN_START_DATE":
    "修改计划开始日期时发送通知",

  "tracker.event.CHANGE_ITEM_PLAN_END_DATE": "设置计划结束日期",
  "tracker.event.desc.CHANGE_ITEM_PLAN_END_DATE":
    "修改计划结束日期日期时发送通知",

  "tracker.event.CHANGE_ITEM_WIKI": "设置关联WiKi页面",
  "tracker.event.desc.CHANGE_ITEM_WIKI": "关联/移除 Wiki 页面时发送通知",

  "tracker.event.CHANGE_ITEM_SPRINT": "设置所属迭代",
  "tracker.event.desc.CHANGE_ITEM_SPRINT": "添加/修改所属迭代时发送通知",

  "project.status.NOT_STARTED": "未开始",
  "project.status.ONGOING": "进行中",
  "project.status.ENDED": "已完成",
  "project.status.CLOSED": "已关闭",
  "project.status.DELAYED": "延迟中",

  "tracker.field.permission.type.Unrestricted": "无限制",
  "tracker.field.permission.type.Single": "单一权限",
  "tracker.field.permission.type.PerStatus": "按状态",

  "project.page.permission.PAGE_READ": "查看",
  "project.page.permission.PAGE_WRITE": "操作",

  ACTION_TYPE_UPDATE_ITEM_PROPERTIES: "修改属性值",
  ACTION_TYPE_TRIGGER_STATE_TRANSITION: "触发步骤执行",
  ACTION_TYPE_UPDATE_REFERRING_ITEMS: "更新关联工作项",
  ACTION_TYPE_CREATE_NEW_UP_DOWNSTREAM_REFERRING_ITEMS: "更新上/下游工作项",
  ACTION_TYPE_CREATE_A_NEW_SEQUENTIAL_ID: "创建顺序编号",
  ACTION_TYPE_EXECUTE_A_CUSTOM_SCRIPT: "执行自定义脚本",
  ACTION_TYPE_SEND_A_CUSTOMER_EMAIL_TO_SPECIFIC_RECIPIENTS: "发送邮件",
  ACTION_TYPE_START_A_NEW_REVIEW: "开启评审",
  ACTION_TYPE_WEBHOOK: "消息推送",

  "setMethod.SET_VALUE": "直接设值",
  "setMethod.VALUE_OF": "目标属性值",
  "setMethod.RESULT_OF": "表达式结果",

  "page.type.wiki": "智能文档",
  "page.type.report": "智能报表",
  "page.type.component": "组件",
  "page.type.tracker": "工作项",
  "page.type.url": "外部链接",
};
