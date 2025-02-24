package com.harbortek.helm.system.service.impl;

import com.harbortek.helm.common.config.LocalFileSystemConfig;
import com.harbortek.helm.system.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
@ConditionalOnBean(LocalFileSystemConfig.class)
@Service
@Slf4j
public class LocalFileServiceImpl implements FileService {
    @Override
    public String upload(MultipartFile file, String destPath) throws Exception {
        return "";
    }

    @Override
    public String upload(InputStream inputStream, String fileName, String destPath) throws Exception {
        return "";
    }

    @Override
    public InputStream download(String filePath) throws Exception {
        return null;
    }
}
