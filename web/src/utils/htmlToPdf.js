// 页面导出为pdf格式
import html2Canvas from 'html2canvas'
import jsPDF from 'jspdf'
 
const htmlToPdf = {
    getPdf(title,url) {
        html2Canvas(document.querySelector('#pdfDom'), {
            allowTaint: false,
            taintTest: false,
            logging: false,
            useCORS: true,
            dpi: window.devicePixelRatio*4, //将分辨率提高到特定的DPI 提高四倍
            scale:4 //按比例增加分辨率
        }).then(canvas=>{
            var pdf = new jsPDF('p', 'mm', 'a4');    //A4纸，纵向
            var ctx = canvas.getContext('2d'),
                a4w = 190, a4h = 277,    //A4大小，210mm x 297mm，四边各保留10mm的边距，显示区域190x277
                imgHeight = Math.floor(a4h * canvas.width / a4w),    //按A4显示比例换算一页图像的像素高度
                renderedHeight = 0;
 
            while(renderedHeight < canvas.height) {
                var page = document.createElement("canvas");
                page.width = canvas.width;
                page.height = Math.min(imgHeight, canvas.height - renderedHeight);//可能内容不足一页
 
                //用getImageData剪裁指定区域，并画到前面创建的canvas对象中
                page.getContext('2d').putImageData(ctx.getImageData(0, renderedHeight, canvas.width, Math.min(imgHeight, canvas.height - renderedHeight)), 0, 0);
                pdf.addImage(page.toDataURL('image/jpeg', 1.0), 'JPEG', 10, 10, a4w, Math.min(a4h, a4w * page.height / page.width));    //添加图像到页面，保留10mm边距
                
                renderedHeight += imgHeight;
                if(renderedHeight < canvas.height) {
                    pdf.addPage();//如果后面还有内容，添加一个空页
                }
                // delete page;
            }
            //保存文件
            pdf.save(title + '.pdf')
        })
    }
};
 
export default htmlToPdf;