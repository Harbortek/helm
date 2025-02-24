/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.system.service.impl;

import com.harbortek.helm.common.config.MinIOConfig;
import com.harbortek.helm.system.service.FileService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.UUID;

@ConditionalOnBean(MinIOConfig.class)
@Service
@Slf4j
public class MinIOFileServiceImpl implements FileService {
	@Autowired
	MinioClient minioClient;

	@Autowired
	MinIOConfig minIOConfig;

	@Override
	public String upload(MultipartFile file, String destPath) throws Exception {
		BufferedInputStream bufferedInputStream = new BufferedInputStream(file.getInputStream());
		return upload(bufferedInputStream, file.getOriginalFilename(), destPath);
	}

	@Override
	public String upload(InputStream inputStream, String fileName, String destPath) throws Exception {
		String fileUrl = "";
		try {
			// 检查存储桶是否已经存在
			if(minioClient.bucketExists(BucketExistsArgs.builder().bucket(minIOConfig.getBucketName()).build())) {
				log.info("Bucket already exists.");
			} else {
				// 创建一个名为ota的存储桶
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(minIOConfig.getBucketName()).build());
				log.info("create a new bucket.");
			}
			// 获取文件名
			String saveFileName = UUID.randomUUID().toString() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
			if (!destPath.startsWith("/")) {
				destPath = "/"+destPath;
			}
			if (destPath.endsWith("/")){
				destPath = StringUtils.removeEnd(destPath,"/");
			}
			String objectName = destPath +"/"+saveFileName;

			PutObjectArgs objectArgs = PutObjectArgs.builder().object(objectName)
					.bucket(minIOConfig.getBucketName())
					.contentType("application/octet-stream")
					.stream(inputStream,inputStream.available(),-1).build();
			minioClient.putObject(objectArgs);
			inputStream.close();

			fileUrl = "/"+ minIOConfig.getBucketName()+objectName;
		}catch (Exception e){
			log.error(e.getMessage(), e);
		}
		return fileUrl;
	}

	@Override
	public InputStream download(String filePath) throws Exception {
		String innerPath = filePath;
		if (filePath.startsWith(minIOConfig.getBaseUrl())){
			innerPath = StringUtils.removeStart(filePath,StringUtils.removeEnd(minIOConfig.getBaseUrl(),"/"));
		}
		if (innerPath.startsWith("/")) {
			innerPath = StringUtils.removeStart(innerPath,"/");
		}

		if (innerPath.startsWith(minIOConfig.getBucketName())){
			innerPath = StringUtils.removeStart(innerPath,minIOConfig.getBucketName());
		}
		if (!innerPath.startsWith("/")) {
			innerPath = "/"+innerPath;
		}

		try {
			return minioClient.getObject(
					GetObjectArgs.builder().bucket(minIOConfig.getBucketName()).object(innerPath).build());
		}catch (Throwable t){
			log.error(t.getMessage());
		}
		return new NullInputStream();
	}
}
