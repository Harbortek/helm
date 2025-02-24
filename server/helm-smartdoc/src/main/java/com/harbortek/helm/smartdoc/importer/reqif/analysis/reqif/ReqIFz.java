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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.reqif;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.file.FileNameUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ReqIFz extends ReqIFFile {
	
	
	public ReqIFz(String filePath) {
		File file = new File(filePath);
		this.path = filePath;
		this.name = file.getName();
		if (file.exists() && file.length()>0){
			try {
				ZipFile zipFile =  new ZipFile(new File(filePath));
				List<String>  fileNames = listFiles(zipFile);

				for (String fileName: fileNames) {
					if(fileName.endsWith("reqif")) {
						this.numberOfReqIFDocuments++;
						HashMap<String,String> pictures = new HashMap<String, String>();
						String documentName = FileNameUtil.mainName(fileName);
						this.picturesInReqIFDocument.put(documentName, pictures);
						this.reqIFDocuments.put(fileName, new ReqIFDocument(zipFile.getInputStream(new ZipEntry(fileName)),
																			fileName, documentName));
						for (String imageFile:fileNames) {
							if (imageFile.endsWith("png")   ||
									imageFile.endsWith("jpeg")  ||
									imageFile.endsWith("jpg")          ) {
								String base64 = Base64.encode(zipFile.getInputStream(zipFile.getEntry(imageFile)));
								pictures.put(imageFile,base64);
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private List<String> listFiles(ZipFile zipFile) {
		//列出zip文件中的所有文件
		List<String> list = new ArrayList<String>();
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		while(enumeration.hasMoreElements()){
            ZipEntry entry = enumeration.nextElement();
			list.add(entry.getName());
        }
		return list;
	}

}
