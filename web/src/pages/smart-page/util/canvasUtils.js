import {
  COMMON_BACKGROUND,
  commonAttr,
  HYPERLINKS,
  PIC_STYLE,
} from "@/pages/smart-page/util/component-list"; // 左侧列表数据
export function deepCopy(target) {
  if (typeof target === "object" && target !== null) {
    const result = Array.isArray(target) ? [] : {};
    for (const key in target) {
      if (typeof target[key] === "object") {
        result[key] = deepCopy(target[key]);
      } else {
        result[key] = target[key];
      }
    }

    return result;
  }

  return target;
}

export function swap(arr, i, j) {
  const temp = arr[i];
  arr[i] = arr[j];
  arr[j] = temp;
}

export function moveUp(arr, i) {
  arr.splice(i + 1, 0, arr.splice(i, 1)[0]);
}

export function moveDown(arr, i) {
  arr.splice(i, 0, arr.splice(i - 1, 1)[0]);
}

export function toTop(arr, i, j) {
  arr.push(arr.splice(i, 1)[0]);
}

export function toBottom(arr, i) {
  arr.unshift(arr.splice(i, 1)[0]);
}

export function $(selector) {
  return document.querySelector(selector);
}

export function panelDataPrepare(componentData, componentStyle, callback) {
  // style初始化
  componentStyle.refreshTime = componentStyle.refreshTime || 5;
  componentStyle.refreshViewLoading =
    componentStyle.refreshViewLoading || false;
  componentStyle.refreshUnit = componentStyle.refreshUnit || "minute";
  componentStyle.refreshViewEnable =
    componentStyle.refreshViewEnable === undefined
      ? true
      : componentStyle.refreshViewEnable;
  // componentStyle.aidedDesign =
  //   componentStyle.aidedDesign || deepCopy(AIDED_DESIGN);
  // componentStyle.pdfPageLine =
  //   componentStyle.pdfPageLine || deepCopy(PAGE_LINE_DESIGN);
  componentStyle.chartInfo =
    componentStyle.chartInfo || deepCopy(PANEL_CHART_INFO);
  componentStyle.chartInfo.tabStyle =
    componentStyle.chartInfo.tabStyle || deepCopy(TAB_COMMON_STYLE);
  componentStyle.themeId = componentStyle.themeId || "NO_THEME";
  componentStyle.background.themeColor =
    componentStyle.background.themeColor || "light";
  // componentStyle.panel.mobileSetting =
  //   componentStyle.panel.mobileSetting || deepCopy(MOBILE_SETTING);

  // 主题增加组件背景设置
  if (componentStyle.chartCommonStyle) {
    componentStyle.chartCommonStyle.enable =
      componentStyle.chartCommonStyle.enable || false;
    componentStyle.chartCommonStyle.backgroundType =
      componentStyle.chartCommonStyle.backgroundType || "innerImage";
    componentStyle.chartCommonStyle.innerImageColor =
      componentStyle.chartCommonStyle.innerImageColor || "#1094E5";
    componentStyle.chartCommonStyle.innerImage =
      componentStyle.chartCommonStyle.innerImage || "board/blue_1.svg";
    componentStyle.chartCommonStyle.outerImage =
      componentStyle.chartCommonStyle.outerImage || null;
  } else {
    componentStyle.chartCommonStyle = deepCopy(COMMON_BACKGROUND);
  }
  componentData.forEach((item, index) => {
    if (item.component && item.component === "v-text") {
      item.propValue = xssCheck(item.propValue);
    }
    if (item.component && item.component === "de-date") {
      const widget = ApplicationContext.getService(item.serviceName);
      if (
        item.options.attrs &&
        (!item.options.attrs.default ||
          (item.serviceName === "timeYearWidget" &&
            item.options.attrs.default.dynamicInfill !== "year") ||
          (item.serviceName === "timeMonthWidget" &&
            item.options.attrs.default.dynamicInfill !== "month"))
      ) {
        if (widget && widget.defaultSetting) {
          item.options.attrs.default = widget.defaultSetting();
        }
      }
      if (
        item.options.attrs &&
        widget.isTimeWidget &&
        widget.isTimeWidget() &&
        !Object.prototype.hasOwnProperty.call(item.options.attrs, "showTime")
      ) {
        item.options.attrs.showTime = false;
        item.options.attrs.accuracy = "HH:mm";
      }
    }
    if (item.type === "de-tabs") {
      item.style.fontSize = item.style.fontSize || 16;
      item.style.activeFontSize = item.style.activeFontSize || 18;
      item.style.carouselEnable = item.style.carouselEnable || false;
      item.style.switchTime = item.style.switchTime || 5;
    }
    if (item.type === "custom") {
      item.options.manualModify = false;
    }
    if (item.filters && item.filters.length > 0) {
      item.filters = [];
    }
    item.linkageFilters = item.linkageFilters || [];
    item.auxiliaryMatrix = item.auxiliaryMatrix || false;
    item.x = item.x || 0;
    item.y = item.y || 0;
    item.sizex = item.sizex || 12;
    item.sizey = item.sizey || 6;
    // 初始化密度为最高密度
    // if (componentStyle.aidedDesign.matrixBase !== 4) {
    //   item.x = (item.x - 1) * 4 + 1;
    //   item.y = (item.y - 1) * 4 + 1;
    //   item.sizex = item.sizex * 4;
    //   item.sizey = item.sizey * 4;
    // }
    // item.mobileSelected = item.mobileSelected || false;
    // item.mobileStyle = item.mobileStyle || deepCopy(BASE_MOBILE_STYLE);
    item.hyperlinks = item.hyperlinks || deepCopy(HYPERLINKS);
    item.commonBackground =
      item.commonBackground || deepCopy(COMMON_BACKGROUND_NONE);
    item.commonBackground["innerImageColor"] =
      item.commonBackground["innerImageColor"] || "#1094E5";
    // Multi choice of colors and pictures
    if (item.commonBackground.backgroundType === "color") {
      item.commonBackground["backgroundColorSelect"] =
        item.commonBackground.enable;
      item.commonBackground.enable = false;
      item.commonBackground.backgroundType = "innerImage";
    }
    // picture component
    if (item.component && item.component === "Picture") {
      item.style.adaptation = item.style.adaptation || "adaptation";
    }
    // 增加所属画布ID（canvasId）当前所在画布的父ID（canvasPid） 主画布ID为main-canvas, PID = 0 表示当前所属canvas为最顶层
    item.canvasId = item.canvasId || "canvas-main";
    item.canvasPid = item.canvasPid || "0";
  });
  // 初始化密度为最高密度
  // componentStyle.aidedDesign.matrixBase = 4;
  callback({
    componentData: componentData,
    componentStyle: componentStyle,
  });
}

export function checkViewTitle(opt, id, tile) {
  try {
    const curPanelViewsData = store.state.componentViewsData;
    const curComponentViewNames = [];
    store.state.componentData.forEach((item) => {
      if (
        item.type === "view" &&
        item.propValue &&
        item.propValue.viewId &&
        curPanelViewsData[item.propValue.viewId]
      ) {
        // 更新时自己的title不加入比较
        if (
          (opt === "update" && id !== item.propValue.viewId) ||
          opt === "new"
        ) {
          curComponentViewNames.push(
            curPanelViewsData[item.propValue.viewId].title
          );
        }
      }
    });
    if (curComponentViewNames.includes(tile)) {
      return true;
    } else {
      return false;
    }
  } catch (e) {
    return false;
  }
}

export function imgUrlTrans(url) {
  if (url && typeof url === "string") {
    return "/static/fonts/svg/" + url;
  } else {
    return url;
  }
}
