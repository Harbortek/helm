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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReqIFFile {
	
	
	protected String path;
	protected String name;
	protected int numberOfReqIFDocuments;
	protected Map<String, String> picturesIS;
	protected Map<String, Map<String, String>> picturesInReqIFDocument = new LinkedHashMap<String, Map<String,
			String>>();
	protected Map<String, ReqIFDocument> reqIFDocuments = new LinkedHashMap<String, ReqIFDocument>();
	
	
	
	
	public String getPath() {
		return this.path;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getReqIFDocumentsCount() {
		return this.numberOfReqIFDocuments;
	}
	
	public Map<String, ReqIFDocument> getReqIFDocuments() {
		return this.reqIFDocuments;
	}
	
	public List<ReqIFDocument> getReqIFDocumentsList() {
		return new ArrayList<ReqIFDocument>(this.reqIFDocuments.values());
	}
	
	public Map<String, String> getPictures(String reqIFDocumentName) {
		return this.picturesInReqIFDocument.get(reqIFDocumentName.split("\\.")[0]);
	}
	
	public String getPicture(String reqIFDocumentName, String pictureFileName) {
		return this.picturesInReqIFDocument.get(reqIFDocumentName).get(pictureFileName);
	}
	
	
}
