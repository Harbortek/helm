export const PAGE_COMPONENTS = [
  {
    id: "plans",
    name: "项目计划",
    group: "计划",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/plan/${menuItem.id}`;
    },
  },
  {
    id: "targetVersions",
    name: "版本",
    group: "计划",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/targetVersion/${menuItem.id}`;
    },
  },
  {
    id: "sprints",
    name: "迭代",
    group: "计划",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/sprints/${menuItem.id}`;
    },
  },
  {
    id: "milestones",
    name: "里程碑",
    group: "计划",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/milestones/${menuItem.id}`;
    },
  },
  {
    id: "deliverables",
    name: "交付物",
    group: "计划",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/deliverables/${menuItem.id}`;
    },
  },
  {
    id: "tasks",
    name: "计划执行",
    group: "计划",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/tasks/${menuItem.id}`;
    },
  },
  {
    id: "testRun",
    name: "测试运行",
    group: "测试",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/testRun/${menuItem.id}`;
    },
  },
  {
    id: "testReport",
    name: "测试报告",
    group: "测试",
    icon: "",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/testReport/${menuItem.id}`;
    },
  },
  // {
  //   id: "review",
  //   name: "评审",
  //   group: "评审",
  //   icon: "checkbox",
  //   path: (menuItem, projectId) => {
  //     return `/tracker/project/${projectId}/review`;
  //   },
  // },
  {
    id: "repository",
    name: "代码仓库",
    group: "代码",
    icon: "code",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/repository/${menuItem.id}`;
    },
  },
  {
    id: "pipeline",
    name: "流水线",
    group: "流水线",
    icon: "pipeline",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/pipeline/${menuItem.id}`;
    },
  },
  {
    id: "baseline",
    name: "基线",
    group: "基线",
    icon: "history",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/baseline/${menuItem.id}`;
    },
  },
  {
    id: "matters",
    name: "我的事项",
    group: "我的事项",
    icon: "history",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/matters/${menuItem.id}`;
    },
  },
  {
    id: "collection",
    name: "集合",
    group: "集合",
    icon: "history",
    path: (menuItem, projectId) => {
      return `/tracker/project/${projectId}/collection/${menuItem.id}`;
    },
  },
];

export default PAGE_COMPONENTS;
