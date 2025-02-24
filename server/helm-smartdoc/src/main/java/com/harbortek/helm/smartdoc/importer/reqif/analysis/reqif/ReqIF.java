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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class ReqIF extends ReqIFFile {
	
	
	public ReqIFDocument getReqIFDocument() {
		return this.reqIFDocuments.get(this.name);
	}
	
	public ReqIFHeader getReqIFHeader() {
		return this.reqIFDocuments.get(this.name).getHeader();
	}
	
	public ReqIFCoreContent getReqIFCoreContent() {
		return this.reqIFDocuments.get(this.name).getCoreContent();
	}
	
	
	
	
	public ReqIF(String filePath) throws FileNotFoundException {
		
		this.path = filePath;
		this.name = this.path.substring(this.path.lastIndexOf(System.getProperty("file.separator"))+1);
		
		this.reqIFDocuments.put(this.name, new ReqIFDocument(filePath));
		this.numberOfReqIFDocuments = 1;
		
		this.picturesIS = new HashMap<String, String>();
		File picturesFolder = new File(this.path.split("\\.")[0]);
		if(picturesFolder.exists()) {
			
			File[] pictures = picturesFolder.listFiles();
			if (pictures != null) {
				for (File file : pictures) {

					if (file.getName().endsWith("png") ||
							file.getName().endsWith("jpg") ||
							file.getName().endsWith("jpeg")) {

						String base64 = Base64.encode(new FileInputStream(file));
						this.picturesIS.put(file.getName(), base64);
					}
				}
			}
		}
		this.picturesInReqIFDocument.put(this.name, this.picturesIS);
	}

}
