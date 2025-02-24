import enquireJs from "enquire.js";
import Cookies from "js-cookie";
export function isDef(v) {
  return v !== undefined && v !== null;
}

/**
 * Remove an item from an array.
 */
export function remove(arr, item) {
  if (arr.length) {
    const index = arr.indexOf(item);
    if (index > -1) {
      return arr.splice(index, 1);
    }
  }
}

export function isRegExp(v) {
  return _toString.call(v) === "[object RegExp]";
}

export function enquireScreen(call) {
  const handler = {
    match: function () {
      call && call(true);
    },
    unmatch: function () {
      call && call(false);
    },
  };
  enquireJs.register("only screen and (max-width: 767.99px)", handler);
}

export function iconUrl(icon) {
  if (icon) {
    const accessToken = Cookies.get("Authorization");
    return (
      process.env.VUE_APP_API_BASE_URL +
      `/file?path=${icon}&access_token=${accessToken}`
    );
  }
  return null;
}

export function filePath(filePath) {
  if (filePath) {
    const accessToken = Cookies.get("Authorization");
    return (
      process.env.VUE_APP_API_BASE_URL +
      `/file?path=${filePath}&access_token=${accessToken}`
    );
  }
  return null;
}

export function debounce(func, wait) {
  let timeout;

  return function () {
    const context = this;
    const args = arguments;

    clearTimeout(timeout);

    timeout = setTimeout(function () {
      func.apply(context, args);
    }, wait);
  };
}

export function deepEqual(obj1, obj2) {
  // 检查两个对象是否是相同的引用
  if (obj1 === obj2) {
    return true;
  }

  // 检查两个对象是否都是对象类型
  if (
    typeof obj1 !== "object" ||
    obj1 === null ||
    typeof obj2 !== "object" ||
    obj2 === null
  ) {
    return false;
  }

  // 检查两个对象的属性数量是否相同
  const keys1 = Object.keys(obj1);
  const keys2 = Object.keys(obj2);

  if (keys1.length !== keys2.length) {
    return false;
  }

  // 递归地比较每个属性
  for (const key of keys1) {
    if (!keys2.includes(key) || !deepEqual(obj1[key], obj2[key])) {
      return false;
    }
  }

  return true;
}

const _toString = Object.prototype.toString;
