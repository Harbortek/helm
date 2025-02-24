import AdminLayout from "@/layouts/AdminLayout";
import TopTitle from "@/layouts/header/TopTitle";
import TopMenu from "@/layouts/header/TopMenu";
import ConfigView from "@/layouts/ConfigView";
import ContentView from "@/layouts/ContentView";
import BlankPage from "@/layouts/BlankPage";
import BlankView from "@/layouts/BlankView";
// 路由配置
const options = {
  mode: "hash",
  routes: [
    {
      path: "",
      name: "root",
      component: AdminLayout,
      redirect: "/welcome",
      children: [
        {
          name: "welcom",
          path: "welcome",
          meta: {
            icon: "home",
            show: true,
            title: "menu.welcome",
          },
          components: {
            default: BlankPage,
            TopTitle,
          },
        },
        {
          path: "account",
          name: "Settings",
          redirect: "/account/settings",
          component: ContentView,
          meta: {
            title: "menu.account",
            icon: "setting",
            show: false,
          },
          children: [
            {
              path: "settings",
              name: "Setting",
              redirect: "/account/settings/basic",
              component: () => import("@/pages/account/settings/Index"),
              children: [
                {
                  path: "basic",
                  name: "BasicSetting",
                  meta: {
                    icon: "blank",
                    title: "menu.account.basic",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/account/settings/BasicSetting"),
                },
                {
                  path: "security",
                  name: "SecuritySetting",
                  meta: {
                    icon: "blank",
                    title: "menu.account.security",
                  },
                  component: () => import("@/pages/account/settings/Security"),
                },
                {
                  path: "language",
                  name: "LanguageSetting",
                  meta: {
                    icon: "blank",
                    title: "menu.account.language",
                  },
                  component: () => import("@/pages/account/settings/Language"),
                },
              ],
            },
          ],
        },
        //产品线管理
        {
          path: "productline",
          name: "productline",
          redirect: "/productline/list",
          components: {
            default: ContentView,
            TopTitle,
            TopMenu,
          },
          children: [
            {
              path: "list",
              name: "productlineList",
              component: () => import("@/pages/product-line/ProductLineList"),
            },
            {
              path: ":productLineId(\\d+)",
              name: "productlineMain",
              redirect: ":productLineId(\\d+)/products",
              component: () =>
                import("@/pages/product-line/ProudctLineMainPage"),
              children: [
                {
                  path: "products",
                  name: "productlineProducts",
                  component: () =>
                    import("@/pages/product-line/product/ProductList"),
                },
                {
                  path: "reports",
                  name: "productlineReports",
                  component: () =>
                    import("@/pages/product-line/report/ReportList"),
                },
                {
                  path: "reports/:reportId",
                  name: "productLineReportViewer",
                  component: () =>
                    import("@/pages/product-line/report/ReportViewer"),
                },
                {
                  path: "reports/:reportId",
                  name: "productLineReportEditor",
                  component: () =>
                    import("@/pages/product-line/report/ReportEditor"),
                },
                {
                  path: "config",
                  name: "productlineConfig",
                  component: () =>
                    import(
                      "@/pages/product-line/config/ProductPermissionConfigPage"
                    ),
                },
              ],
            },
          ],
        },
        //项目管理
        {
          path: "tracker",
          name: "tracker",
          redirect: "/tracker/projectList",
          meta: {
            title: "menu.tracker",
            show: true,
            icon: "project",
          },
          components: {
            default: ContentView,
            TopTitle,
            TopMenu,
          },
          children: [
            {
              path: "projectList",
              name: "projectList",
              meta: {
                icon: "blank",
                title: "menu.tracker.project",
              },
              component: () =>
                import("@/pages/tracker/project/ProjectListMainPage"),
            },
            {
              path: "createProject",
              name: "createProject",
              meta: {
                icon: "blank",
                title: "创建项目",
                show: false,
              },
              component: () => import("@/pages/tracker/project/CreateProject"),
            },
            {
              path: "project/:projectId(\\d+)",
              name: "project",
              meta: {
                icon: "blank",
                title: "menu.tracker.project",
                show: false,
              },
              redirect: "project/:projectId/summary",
              component: () =>
                import("@/pages/tracker/project/ProjectMainPage"),
              children: [
                {
                  path: "summary",
                  name: "projectSummary",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.summary",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/project/ProjectSummary"),
                },
                {
                  path: "wiki/:pageId",
                  name: "projectWiki",
                  component: () => import("@/pages/tracker/wiki/WikiPage"),
                },
                {
                  path: "wiki/:wikiId/importWord",
                  name: "importWord",
                  component: () => import("@/pages/smart-doc/word/ImportWord"),
                },
                {
                  path: "wiki/:wikiId/importReqIF",
                  name: "importReqIF",
                  component: () =>
                    import("@/pages/smart-doc/reqif/ImportReqIF"),
                },
                {
                  path: "wiki/:wikiId/exportReqIF",
                  name: "exportReqIF",
                  component: () =>
                    import("@/pages/smart-doc/reqif/ExportReqIF"),
                },
                {
                  path: "smartPage/:pageId",
                  name: "smartPage",
                  component: () =>
                    import("@/pages/tracker/report/ProjectReportViewer"),
                },
                {
                  path: "smartPage/:pageId/editor",
                  name: "smartPageEditor",
                  component: () =>
                    import("@/pages/tracker/report/ProjectReportEditor"),
                },
                {
                  path: "baseline/:pageId(\\d+)",
                  name: "projectBaseline",
                  component: () => import("@/pages/baseline/BaselineList"),
                },
                {
                  path: "matters/:pageId(\\d+)",
                  name: "projectMatters",
                  component: () =>
                    import("@/pages/tracker/matters/MattersList"),
                },
                {
                  path: "collection/:pageId(\\d+)",
                  name: "projectCollection",
                  component: () => import("@/pages/collection/CollectionList"),
                },
                {
                  path: "plan/:pageId(\\d+)",
                  name: "plan",
                  component: () => import("@/pages/plan/PlanList"),
                },
                {
                  path: "targetVersion/:pageId(\\d+)",
                  name: "targetVersion",
                  component: () => import("@/pages/plan/TargetVersionList"),
                },
                {
                  path: "milestones/:pageId(\\d+)",
                  name: "milestones",
                  component: () => import("@/pages/plan/MilestoneList"),
                },
                {
                  path: "milestones/:pageId(\\d+)/detail/:itemId(\\d+)",
                  name: "milestoneDetail",
                  component: () => import("@/pages/plan/MilestoneDetail"),
                },
                {
                  path: "deliverables/:pageId(\\d+)",
                  name: "deliverables",
                  component: () => import("@/pages/plan/DeliverableList"),
                },
                {
                  path: "tasks/:pageId(\\d+)",
                  name: "tasks",
                  component: () => import("@/pages/plan/TaskList"),
                },
                {
                  path: "tasks/:pageId(\\d+)/detail/:itemId(\\d+)",
                  name: "taskDetail",
                  component: () => import("@/pages/plan/TaskDetail"),
                },
                {
                  path: "sprints/:pageId(\\d+)",
                  name: "sprints",
                  component: () => import("@/pages/plan/SprintList"),
                },
                {
                  path: "repository/:pageId(\\d+)",
                  name: "repository",
                  component: () =>
                    import("@/pages/scm/repository/RepositoryList"),
                },
                {
                  path: "testRun/:pageId(\\d+)",
                  name: "testRun",
                  component: () => import("@/pages/test/TestRunList"),
                },
                // {
                //   path: "review",
                //   name: "review",
                //   component: () => import("@/pages/review/ReviewList"),
                // },
                {
                  path: "testReport/:pageId(\\d+)",
                  name: "testReport",
                  component: () => import("@/pages/test/TestReportList"),
                },
                {
                  path: "testReport/:pageId(\\d+)/testReportDetail/:reportId(\\d+)",
                  name: "testReportDetail",
                  component: () => import("@/pages/test/TestReportDetail"),
                },
                {
                  path: "testRun/:pageId(\\d+)/testRunCase/:testRunId(\\d+)",
                  name: "testRunCase",
                  component: () => import("@/pages/test/TestRunResultList"),
                },
                {
                  path: "testRun/:pageId(\\d+)/testRunCase/:testRunId(\\d+)/testRunExecute",
                  name: "testRunExecute",
                  component: () => import("@/pages/test/TestRunExecutePage"),
                  props: true,
                },
                {
                  path: "testRun/:pageId(\\d+)/testRunCase/:testRunId(\\d+)/testRunExecute/:caseId(\\d+)",
                  name: "testRunExecute",
                  component: () => import("@/pages/test/TestRunExecutePage"),
                  props: true,
                },
                {
                  path: "pipeline/:pageId(\\d+)",
                  name: "pipeline",
                  component: () => import("@/pages/pipeline/jobs/PipelineList"),
                },
                {
                  path: "trackerItems/:trackerId(\\d+)",
                  name: "trackerItems",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerItems",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/items/TrackerItemsMainPage"),
                  props: true,
                },
                {
                  path: "trackerItems/:trackerId(\\d+)/:itemId(\\d+)",
                  name: "trackerItems",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerItems",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/items/TrackerItemsMainPage"),
                  props: true,
                },

                {
                  path: "config/trackerConfig",
                  name: "trackerListConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerListConfig",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/TrackerListConfigPage"),
                },
                {
                  path: "config/trackerConfig/:trackerId(\\d+)",
                  name: "trackerConfigMain",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.trackerConfigMain",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/TrackerConfigNavPage"),
                  children: [
                    {
                      path: "trackerConfigPortal",
                      name: "trackerConfigPortal",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerConfigMain",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerConfigPortalPage"
                        ),
                    },
                    {
                      path: "trackerFieldsConfig",
                      name: "trackerFieldsConfig",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerFieldsConfig",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerFieldsConfigPage"
                        ),
                    },
                    {
                      path: "trackerLayoutConfig",
                      name: "trackerLayoutConfig",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerLayoutConfig",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerLayoutConfigPage"
                        ),
                    },
                    {
                      path: "trackerLayoutEditNew",
                      name: "trackerLayoutEditNew",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerLayoutEditNew",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerLayoutEditNewPage"
                        ),
                    },
                    {
                      path: "trackerLayoutEditDetail",
                      name: "trackerLayoutEditDetail",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerLayoutEditDetail",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerLayoutEditDetailPage"
                        ),
                    },
                    {
                      path: "trackerPermissionConfig",
                      name: "trackerPermissionConfig",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerPermissionConfig",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerPermissionConfigPage"
                        ),
                    },
                    {
                      path: "trackerStateTransitionConfig",
                      name: "trackerStateTransitionConfig",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerStateTransitionConfig",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerStateTransitionConfigPage"
                        ),
                    },
                    {
                      path: "trackerStateTransitionConfigEdit/:transitionId",
                      name: "trackerStateTransitionConfigEdit",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerStateTransitionEdit",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerStateTransitionEditPage"
                        ),
                    },
                    {
                      path: "trackerNotificationConfig",
                      name: "trackerNotificationConfig",
                      meta: {
                        icon: "blank",
                        title: "menu.tracker.trackerNotificationConfig",
                        show: false,
                      },
                      component: () =>
                        import(
                          "@/pages/tracker/config/TrackerNotificationConfigPage"
                        ),
                    },
                  ],
                },
                {
                  path: "config/projectPageConfig",
                  name: "projectPageConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.projectPageConfig",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/ProjectPageConfigPage"),
                },
                {
                  path: "config/projectPermmissionConfig",
                  name: "projectPermmissionConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.projectPermissionConfig",
                    show: false,
                  },
                  component: () =>
                    import(
                      "@/pages/tracker/config/ProjectPermissionConfigPage"
                    ),
                },
                {
                  path: "config/projectRoleConfig",
                  name: "projectRoleConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.projectRoleConfig",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/ProjectRoleConfigPage"),
                },
                {
                  path: "config/projectEnumConfig",
                  name: "projectEnumConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.projectEnumConfig",
                    show: false,
                  },
                  component: () => import("@/pages/system/enum/EnumList"),
                },
                {
                  path: "config/linkTypeConfig",
                  name: "linkTypeConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.linkTypeConfig",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/LinkTypeList"),
                },
                {
                  path: "config/projectCodeConfig",
                  name: "projectCodeConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.projectCodeConfig",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/ProjectCodeConfigPage"),
                },
                {
                  path: "config/projectOperationConfig",
                  name: "projectOperationConfig",
                  meta: {
                    icon: "blank",
                    title: "menu.tracker.projectOperationConfig",
                    show: false,
                  },
                  component: () =>
                    import("@/pages/tracker/config/ProjectOperationConfigPage"),
                },
              ],
            },
          ],
        },
        //集成
        // {
        //   path: "integration",
        //   name: "intergration",
        //   redirect: "/integration/repository",
        //   meta: {
        //     title: "menu.integration",
        //     icon: "api",
        //     show: true,
        //   },
        //   components: {
        //     default: ContentView,
        //     TopTitle,
        //   },
        //   children: [
        //     {
        //       path: "repository",
        //       name: "repository",
        //       meta: {
        //         // icon: 'database',
        //         title: "menu.integration.repository",
        //       },
        //       component: () =>
        //         import("@/pages/integration/repository/RepositoryPage"),
        //     },
        //     {
        //       path: "dataset",
        //       name: "dataset",
        //       meta: {
        //         // icon: 'bars',
        //         title: "menu.integration.dataset",
        //       },
        //       component: () =>
        //         import("@/pages/integration/dataset/DatasetPage"),
        //     },
        //     {
        //       path: "model",
        //       name: "model",
        //       meta: {
        //         // icon: 'bars',
        //         title: "menu.integration.model",
        //       },
        //       component: BlankPage,
        //     },
        //     {
        //       path: "job",
        //       name: "job",
        //       meta: {
        //         // icon: 'deployment-unit',
        //         title: "menu.integration.job",
        //       },
        //       component: () => import("@/pages/integration/job/JobPage"),
        //     },
        //   ],
        // },
        {
          path: "review",
          name: "review",
          redirect: "/review/index",
          components: {
            TopTitle,
            default: ContentView,
          },
          children: [
            {
              name: "review-index",
              path: "index",
              component: () => import("@/pages/review/ReviewList"),
            },
          ],
        },
        //配置项
        {
          name: "config",
          path: "config",
          meta: {
            title: "menu.config",
            icon: "setting",
            show: true,
          },
          components: {
            default: ConfigView,
            TopTitle,
          },
          // redirect:'/config/sys/org',
          children: [
            {
              name: "tracker",
              path: "tracker",
              meta: {
                title: "menu.config.tracker",
                icon: "setting",
                show: true,
              },
              component: BlankView,
              children: [
                {
                  name: "enum",
                  path: "enum",
                  component: () => import("@/pages/system/enum/EnumList"),
                  meta: {
                    show: true,
                    title: "menu.system.enum",
                    icon: "profile",
                    permission: "PROJECT_ENUM",
                  },
                },
              ],
            },
            {
              name: "scm",
              path: "scm",
              meta: {
                title: "menu.config.scm",
                icon: "setting",
                show: true,
              },
              component: BlankView,
              children: [
                {
                  name: "scmIntegration",
                  path: "scmIntegration",
                  component: () => import("@/pages/scm/setting/ScmIntegration"),
                  meta: {
                    title: "menu.scm.integration",
                    icon: "bank",
                    show: true,
                    permission: "PROJECT_SCM",
                  },
                },
              ],
            },
            {
              name: "pipeline",
              path: "pipeline",
              meta: {
                title: "menu.config.pipeline",
                icon: "setting",
                show: true,
              },
              component: BlankView,
              children: [
                {
                  name: "pipelineIntegration",
                  path: "pipelineIntegration",
                  component: () =>
                    import("@/pages/pipeline/setting/PipelineIntegration"),
                  meta: {
                    title: "menu.pipeline.integration",
                    icon: "bank",
                    show: true,
                    permission: "PROJECT_PIPELINE",
                  },
                },
              ],
            },
            {
              name: "sys",
              path: "sys",
              meta: {
                title: "menu.system",
                icon: "setting",
                show: true,
              },
              component: BlankView,
              children: [
                {
                  name: "dept",
                  path: "org",
                  component: () =>
                    import("@/pages/system/org/OrganizationList"),
                  meta: {
                    title: "menu.system.dept",
                    icon: "bank",
                    show: true,
                    permission: "SYSTEM_DEPT",
                  },
                },
                {
                  name: "user",
                  path: "user",
                  component: () => import("@/pages/system/user/UserList"),
                  meta: {
                    title: "menu.system.user",
                    icon: "team",
                    show: true,
                    permission: "SYSTEM_USER",
                  },
                },
                {
                  name: "role",
                  path: "role",
                  component: () => import("@/pages/system/role/RoleList"),
                  meta: {
                    title: "menu.system.role",
                    icon: "user",
                    show: true,
                    permission: "SYSTEM_ROLE",
                  },
                },
                {
                  name: "param",
                  path: "param",
                  component: () => import("@/pages/system/param/ParamList"),
                  redirect: "",
                  meta: {
                    show: true,
                    title: "menu.system.param",
                    icon: "calculator",
                    permission: "SYSTEM_PARAM",
                  },
                },

                {
                  name: "log",
                  path: "log",
                  component: () => import("@/pages/system/log/LogList"),
                  meta: {
                    show: true,
                    title: "menu.system.log",
                    icon: "zoom-in",
                    permission: "SYSTEM_LOG",
                  },
                },
                {
                  name: "setting",
                  path: "setting",
                  component: () => import("@/pages/system/setting/SettingPage"),
                  meta: {
                    show: true,
                    title: "menu.system.setting",
                    icon: "zoom-in",
                    permission: "system",
                  },
                },
              ],
            },
          ],
        },
      ],
    },
    {
      path: "/login",
      name: "login",
      component: () => import("@/pages/login"),
      meta: {
        title: "menu.login",
      },
    },
  ],
};
export default options;
