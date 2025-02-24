export const systemFields = [
    // { id: 1, name: "父工作项", systemProperty: "parent", inputType: "WORK_ITEM" },
    { id: '2', name: "工作项编号", systemProperty: "itemNo", inputType: "WORK_ITEM_NO" },
    { id: '3', name: "标题", systemProperty: "name", inputType: "TEXT" },
    { id: '4', name: "描述", systemProperty: "description", inputType: "TEXT" },
    { id: '5', name: "优先级", systemProperty: "priority", inputType: "OPTIONS" },//severity
    { id: '6', name: "负责人", systemProperty: "ownerId", inputType: "USER" },
    { id: '7', name: "创建者", systemProperty: "createBy", inputType: "USER" },
    { id: '8', name: "创建日期", systemProperty: "createDate", inputType: "DATE" },
    { id: '9', name: "修改者", systemProperty: "lastModifiedBy", inputType: "USER" },
    { id: '10', name: "修改日期", systemProperty: "lastModifiedDate", inputType: "DATE" },
    { id: '11', name: "状态", systemProperty: "status", inputType: "STATUS" },
    { id: '12', name: "状态类型", systemProperty: "meaning", inputType: "STATUS_TYPE" },
    { id: '13', name: "分配给", systemProperty: "assignedToId", inputType: "USER" },
    { id: '14', name: "分配日期", systemProperty: "assignedDate", inputType: "DATE" },
    { id: '15', name: "所属项目", systemProperty: "projectId", inputType: "PROJECT" },
    { id: '16', name: "所属工作项类型", systemProperty: "trackerId", inputType: "WORK_ITEM_TYPE" },
    { id: '27', name: "所属迭代", systemProperty: "sprintId", inputType: "SPRINT" },
    { id: '18', name: "计划开始时间", systemProperty: "planStartDate", inputType: "DATE" },
    { id: '19', name: "计划结束时间", systemProperty: "planEndDate", inputType: "DATE" },
    { id: '20', name: "实际开始时间", systemProperty: "realStartDate", inputType: "DATE" },
    { id: '21', name: "实际结束时间", systemProperty: "realEndDate", inputType: "DATE" },
    { id: '22', name: "关闭时间", systemProperty: "closeDate", inputType: "DATE" },
    { id: '23', name: "预计花费工时", systemProperty: "estimateWorkingHours", inputType: "INTEGER" },
    { id: '24', name: "已登记工时", systemProperty: "registeredWorkingHours", inputType: "INTEGER" },
    { id: '25', name: "剩余工时", systemProperty: "remainingWorkingHours", inputType: "INTEGER" },
    { id: '26', name: "关注者", systemProperty: "watchers", inputType: "USER" },
]