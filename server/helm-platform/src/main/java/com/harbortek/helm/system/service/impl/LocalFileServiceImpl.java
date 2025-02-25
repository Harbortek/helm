package com.harbortek.helm.system.service.impl;

import com.harbortek.helm.common.config.LocalFileSystemConfig;
import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

@ConditionalOnBean(LocalFileSystemConfig.class)
@Service
@Slf4j
public class LocalFileServiceImpl implements FileService {
    @Autowired
    private LocalFileSystemConfig localFileSystemConfig;

    @Override
    public String upload(MultipartFile file, String destPath) throws Exception {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(file.getInputStream());
        return upload(bufferedInputStream, Objects.requireNonNull(file.getOriginalFilename()), destPath);
    }

    @Override
    public String upload(InputStream inputStream, String fileName, String destPath) throws Exception {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        return upload(bufferedInputStream, fileName, destPath);
    }

    public String upload(BufferedInputStream bufferedInputStream, String fileName, String destPath) throws Exception {
        if (StringUtils.isEmpty(localFileSystemConfig.getRootPath())) {
            throw new ServiceException("未配置本地文件系统根目录oss.rootPath");
        }
        String rootPath = StringUtils.removeEnd(localFileSystemConfig.getRootPath(), "/");
        String absolutePath = rootPath + File.separator + StringUtils.removeEnd(StringUtils.removeStart(destPath,
                                                                                                        "/"), "/");
        String saveFileName = UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        String outPath = absolutePath + File.separator + saveFileName;
        File file = new File(absolutePath);
        if (!file.exists()) { // 如果目录不存在则创建目录
            file.mkdirs();
        }
        try (OutputStream ot = new FileOutputStream(outPath)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((-1 != (len = bufferedInputStream.read(buffer)))) {
                ot.write(buffer, 0, len);
            }
            ot.flush();
            ot.close();
            log.debug("上传文件成功，文件路径：" + outPath);
            String url = StringUtils.removeEnd(StringUtils.removeStart(destPath, "/"), "/") + "/" + saveFileName;
            log.debug("上传文件成功，文件访问地址：" + url);
            return url;
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new ServiceException("上传文件失败");
        }
    }

    @Override
    public InputStream download(String filePath) throws Exception {
        String directory = filePath.substring(0,filePath.lastIndexOf("/"));
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        filePath = localFileSystemConfig.getRootPath() + File.separator + directory+File.separator+fileName;
        return new FileInputStream(filePath);
    }
}
