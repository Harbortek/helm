import moment from "moment";
import "moment/locale/zh-cn";
moment.locale("zh-cn");

export function shortDate(d) {
  let m = moment(new Date(d));
  return m.calendar();
  // let now = moment();
  // if (m.year() != now.year()) {
  //   return m.format("YYYY-MM-DD");
  // } else if (m.month() != now.month()) {
  //   return m.format("MM月DD日");
  // } else if (m.date() != now.date()) {
  //   return m.format("DD日");
  // } else if (m.hour() != now.hour()) {
  //   return m.format("今日hh:mm");
  // } else if (m.minute() != now.minute()) {
  //   return m.format("今日hh:mm");
  // } else {
  //   return "刚刚";
  // }
}

export function longDate(d) {
  let m = moment(new Date(d));
  return m.format("YYYY年MMM月DD日 hh时mm分ss秒");
}

export function formatDate(d) {
  if (d) {
    let m = moment(d);
    return m.format("YYYY-MM-DD");
  }
  return "-";
}
export function formatLongDate(d) {
  if (d) {
    let m = moment(d);
    return m.format("YYYY-MM-DD HH:mm:ss");
  }
  return "-";
}

export function formatElipse(d) {
  let du = moment.duration(d, "milliseconds");
  let hours = du.get("hours");
  let mins = du.get("minutes");
  let ss = du.get("seconds");
  if (hours > 0) {
    return hours + "时" + mins + "分" + ss + "秒";
  } else if (mins > 0) {
    return mins + "分" + ss + "秒";
  } else if (ss > 0) {
    return ss + "秒";
  } else {
    return "<1秒";
  }
}

export function fromNow(d) {
  return moment(d).fromNow();
}

export function isWeekend(day) {
  if (moment(day).weekday() === 5 || moment(day).weekday() === 6) {
    return true;
  }
}

export function getTimer(stringTime) {
  var minute = 1000 * 60;
  var hour = minute * 60;
  var day = hour * 24;
  var week = day * 7;
  var month = day * 30;

  var time1 = new Date().getTime(); //当前的时间戳
  var time2 = stringTime * 1000; //指定时间的时间戳
  var time = time1 - time2;

  var result = null;
  if (time < 0) {
    alert("设置的时间不能早于当前时间！");
  } else if (time / month >= 1) {
    result = parseInt(time / month) + "月前";
  } else if (time / week >= 1) {
    result = parseInt(time / week) + "周前";
  } else if (time / day >= 1) {
    result = parseInt(time / day) + "天前";
  } else if (time / hour >= 1) {
    result = parseInt(time / hour) + "小时前";
  } else if (time / minute >= 1) {
    result = parseInt(time / minute) + "分钟前";
  } else {
    result = "刚刚";
  }
  return result;
}
export function getDefaultValue(key) {
  switch (key) {
    case "TODAY":
      return getToday();
    case "YESTERDAY":
      return getYesterday();
    case "THIS_WEEK_START":
      return getThisWeekStart();
    case "THIS_WEEK_END":
      return getThisWeekEnd();
    case "LAST_WEEK_START":
      return getLastWeekStart();
    case "LAST_WEEK_END":
      return getLastWeekEnd();
    case "THIS_MONTH_START":
      return getThisMonthStart();
    case "THIS_MONTH_END":
      return getThisMonthEnd();
    case "LAST_MONTH_START":
      return getLastMonthStart();
    case "LAST_MONTH_END":
      return getLastMonthEnd();
    case "THIS_QUARTER_START":
      return getThisQuarterStart();
    case "THIS_QUARTER_END":
      return getThisQuarterEnd();
    case "LAST_QUARTER_START":
      return getLastQuarterStart();
    case "LAST_QUARTER_END":
      return getLastQuarterEnd();
    case "THIS_YEAR_START":
      return getThisYearStart();
    case "THIS_YEAR_END":
      return getThisYearEnd();
    case "LAST_YEAR_START":
      return getLastYearStart();
    case "LAST_YEAR_END":
      return getLastYearEnd();
  }
  return null;
}

export function getToday() {
  return moment();
}

export function getYesterday() {
  return moment().subtract(1, "days");
}

export function getThisWeekStart() {
  return moment().isoWeekday(1);
}

export function getThisWeekEnd() {
  return moment().isoWeekday(7);
}

export function getLastWeekStart() {
  return moment().date(-7).isoWeekday(1);
}

export function getLastWeekEnd() {
  return moment().date(-7).isoWeekday(7);
}

export function getThisMonthStart() {
  return moment().startOf("month");
}

export function getThisMonthEnd() {
  return moment().endOf("month");
}

export function getLastMonthStart() {
  return moment().month(-1).startOf("month");
}

export function getLastMonthEnd() {
  return moment().month(-1).endOf("month");
}

export function getThisQuarterStart() {
  return moment().startOf("quarter");
}

export function getThisQuarterEnd() {
  return moment().endOf("quarter");
}

export function getLastQuarterStart() {
  return moment().quarter(-1).startOf("quarter");
}

export function getLastQuarterEnd() {
  return moment().quarter(-1).endOf("quarter");
}

export function getThisYearStart() {
  return moment().startOf("year");
}

export function getThisYearEnd() {
  return moment().endOf("year");
}

export function getLastYearStart() {
  return moment().year(-1).startOf("montyearh");
}

export function getLastYearEnd() {
  return moment().year(-1).endOf("year");
}

// moment("2021-08-01").diff("2021-07-01", "days")
export function calcDiffDays(endDate,startDate) {
  return moment(endDate).diff(startDate, "days");
}

export function getDayStart(date) {
  //返回该日期当前0点的日期
  return moment(date).startOf("day");
}

export function getDayEnd(date) {
  return moment(date).endOf("day");
}