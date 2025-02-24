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

import lombok.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
@Data
public class ReqIFDocument {
	
	
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder builder;
	private Document reqifDocument;
	
	protected String filePath;
	private String fileName;
	
	private ReqIFHeader header;
	private ReqIFCoreContent coreContent;
    
    

	public ReqIFDocument(String filePath) throws FileNotFoundException {
		
		this.filePath = filePath;
		this.fileName = this.filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator"))+1);
		
		try {
			
			this.builder = factory.newDocumentBuilder();
			this.reqifDocument = this.builder.parse(this.filePath);
			
			if(this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).getLength() > 0 && this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).item(0).hasChildNodes()) {
				header = new ReqIFHeader((Element)this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).item(0));
			}
			coreContent = new ReqIFCoreContent((Element)this.reqifDocument.getElementsByTagName(ReqIFConst.CORE_CONTENT).item(0));
			
			this.builder = null;
			this.factory = null;
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public ReqIFDocument(InputStream is, String filePath) throws FileNotFoundException {
		
		this.filePath = filePath;
		this.fileName = this.filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator"))+1);
		
		try {
						
			this.builder = factory.newDocumentBuilder();
			this.reqifDocument = this.builder.parse(is);
			
			if(this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).getLength() > 0 && this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).item(0).hasChildNodes()) {
				header = new ReqIFHeader((Element)this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).item(0));
			}
			coreContent = new ReqIFCoreContent((Element)this.reqifDocument.getElementsByTagName(ReqIFConst.CORE_CONTENT).item(0));
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public ReqIFDocument(InputStream is, String zipFilePath, String fileName) {
		
		this.filePath = zipFilePath;		//		TODO
		this.fileName = fileName;
		
		try {
			
			this.builder = factory.newDocumentBuilder();
			
			this.reqifDocument = this.builder.parse(is);
			
			if(this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).getLength() > 0 && this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).item(0).hasChildNodes()) {
				header = new ReqIFHeader((Element)this.reqifDocument.getElementsByTagName(ReqIFConst.THE_HEADER).item(0));
			}
			coreContent = new ReqIFCoreContent((Element)this.reqifDocument.getElementsByTagName(ReqIFConst.CORE_CONTENT).item(0));
			
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
}
