/**
 * 未知文件类型图标
 */
export const unknownImg = require("@/assets/file/unknown.png");

/**
 * 文件类型图标 Map 映射
 */
export const fileImgMap = new Map([
  ["aac", require("@/assets/file/aac.png")],
  ["ai", require("@/assets/file/ai.png")],
  ["aiff", require("@/assets/file/aiff.png")],
  ["avi", require("@/assets/file/avi.png")],
  ["bmp", require("@/assets/file/bmp.png")],
  ["c", require("@/assets/file/c.png")],
  ["c#", require("@/assets/file/c#.png")],
  ["cpp", require("@/assets/file/cpp.png")],
  ["css", require("@/assets/file/css.png")],
  ["csv", require("@/assets/file/csv.png")],
  ["dat", require("@/assets/file/dat.png")],
  ["dir", require("@/assets/file/dir.png")],
  ["dmg", require("@/assets/file/dmg.png")],
  ["doc", require("@/assets/file/doc.png")],
  ["docx", require("@/assets/file/doc.png")],
  ["dotx", require("@/assets/file/dotx.png")],
  ["dwg", require("@/assets/file/dwg.png")],
  ["dxf", require("@/assets/file/dxf.png")],
  ["eps", require("@/assets/file/eps.png")],
  ["exe", require("@/assets/file/exe.png")],
  ["flv", require("@/assets/file/flv.png")],
  ["gif", require("@/assets/file/gif.png")],
  ["h", require("@/assets/file/h.png")],
  ["hpp", require("@/assets/file/hpp.png")],
  ["html", require("@/assets/file/html.png")],
  ["ics", require("@/assets/file/ics.png")],
  ["iso", require("@/assets/file/iso.png")],
  ["java", require("@/assets/file/java.png")],
  ["jpg", require("@/assets/file/jpg.png")],
  ["js", require("@/assets/file/js.png")],
  ["key", require("@/assets/file/key.png")],
  ["less", require("@/assets/file/less.png")],
  ["mid", require("@/assets/file/mid.png")],
  ["mp3", require("@/assets/file/mp3.png")],
  ["mp4", require("@/assets/file/mp4.png")],
  ["mpg", require("@/assets/file/mpg.png")],
  ["odf", require("@/assets/file/odf.png")],
  ["ods", require("@/assets/file/ods.png")],
  ["odt", require("@/assets/file/odt.png")],
  ["otp", require("@/assets/file/otp.png")],
  ["ots", require("@/assets/file/ots.png")],
  ["ott", require("@/assets/file/ott.png")],
  ["pdf", require("@/assets/file/pdf.png")],
  ["php", require("@/assets/file/php.png")],
  ["png", require("@/assets/file/png.png")],
  ["ppt", require("@/assets/file/ppt.png")],
  ["pptx", require("@/assets/file/ppt.png")],
  ["psd", require("@/assets/file/psd.png")],
  ["py", require("@/assets/file/py.png")],
  ["qt", require("@/assets/file/qt.png")],
  ["rar", require("@/assets/file/rar.png")],
  ["rb", require("@/assets/file/rb.png")],
  ["rtf", require("@/assets/file/rtf.png")],
  ["sass", require("@/assets/file/sass.png")],
  ["scss", require("@/assets/file/scss.png")],
  ["sql", require("@/assets/file/sql.png")],
  ["tga", require("@/assets/file/tga.png")],
  ["tgz", require("@/assets/file/tgz.png")],
  ["tiff", require("@/assets/file/tiff.png")],
  ["txt", require("@/assets/file/txt.png")],
  ["wav", require("@/assets/file/wav.png")],
  ["xls", require("@/assets/file/xls.png")],
  ["xlsx", require("@/assets/file/xlsx.png")],
  ["xml", require("@/assets/file/xml.png")],
  ["yml", require("@/assets/file/yml.png")],
  ["zip", require("@/assets/file/zip.png")],
]);

/**
 * 支持使用 onlyoffice 打开的文件格式
 */
export const officeFileType = ["ppt", "pptx", "doc", "docx", "xls", "xlsx"];

/**
 * markdown 文件后缀
 */
export const markdownFileType = ["markdown", "md"];

/**
 * 格式化文件大小
 * @param {number} size 文件大小
 * @param {boolean} isInteger 是否只显示整数位，默认不截取
 * @returns {string} 文件大小（带单位）
 */
export function calculateFileSize(size, isInteger = false) {
  const B = 1024;
  const KB = Math.pow(1024, 2);
  const MB = Math.pow(1024, 3);
  const GB = Math.pow(1024, 4);
  if (isInteger) {
    // 截取为整数
    if (size < B) {
      return `${size}B`;
    } else if (size < KB) {
      return `${(size / B).toFixed(0)}KB`;
    } else if (size < MB) {
      return `${(size / KB).toFixed(0)}MB`;
    } else if (size < GB) {
      return `${(size / MB).toFixed(0)}GB`;
    } else {
      return `${(size / GB).toFixed(0)}TB`;
    }
  } else {
    // 保留小数位
    if (size < B) {
      return `${size}B`;
    } else if (size < KB) {
      return `${(size / B).toFixed(0)}KB`;
    } else if (size < MB) {
      return `${(size / KB).toFixed(1)}MB`;
    } else if (size < GB) {
      return `${(size / MB).toFixed(2)}GB`;
    } else {
      return `${(size / GB).toFixed(3)}TB`;
    }
  }
}
