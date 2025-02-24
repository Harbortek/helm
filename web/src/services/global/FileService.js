import { request, METHOD } from "@/utils/request";
import Cookies from "js-cookie";

export function uploadFile(file, fileType) {
  const fd = new FormData();
  if (fileType) {
    fd.append("uploadFile", file, fileType);
  } else {
    fd.append("uploadFile", file);
  }
  return request({
    method: METHOD.POST,
    url: "/uploadFile",
    timeout: 20000,
    data: fd,
  });
}

export function downloadFile(file) {
  // const accessToken = localStorage.get(ACCESS_TOKEN)
  const accessToken = Cookies.get("Authorization");
  const name = file.name;
  const url = file.url;
  const downloadUrl =
    process.env.VUE_APP_API_BASE_URL +
    `/download?access_token=${accessToken}&name=${name}&url=${url}`;
  var iframe = document.createElement("iframe");
  iframe.style.display = "none";
  iframe.src = downloadUrl;
  iframe.onload = function () {
    document.body.removeChild(iframe);
  };
  document.body.appendChild(iframe);
}
