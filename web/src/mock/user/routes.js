import Mock from "mockjs";
import store from "@/store";
Mock.mock(`${process.env.VUE_APP_API_BASE_URL}/routes`, "get", () => {
  let result = {};
  result.code = 200;
  result.data = [
    {
      router: "root",
      children: [
        "demo",
        {
          router: "parent1",
          children: [
            {
              router: "demo",
              name: "demo1",
              authority: {
                permission: "demo",
                role: "admin",
              },
            },
          ],
        },
        {
          router: "parent2",
          children: [
            {
              router: "demo",
              name: "demo2",
            },
          ],
        },
        {
          router: "exception",
          children: ["exp404", "exp403", "exp500"],
        },
        {
          router: "demo",
          icon: "file-ppt",
          path: "auth/demo",
          name: "验权页面",
          authority: {
            permission: "form",
            role: "manager",
          },
        },
        {
          router: "config",
          icon: "setting",
          path: "config",
          name: "配置中心",
          authority: "*",
          meta: {
            appName: "config",
            appUrl: "http://localhost:3001",
            appRoute: "/config",
            currentUser: store.getters["account/user"],
          },
        },
      ],
    },
  ];
  return result;
});
