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

import com.harbortek.helm.util.DateUtils;
import lombok.Data;
import org.w3c.dom.Element;

import java.util.Date;

@Data
public class ReqIFHeader {
	
	
	private String id;
	private String author = "";
	private String title = "";
	private String toolID;
	private String sourceToolID = "";
	private String reqifVersion = "";
	//private String comment = "";
	private Date creationDate;
	
	

	
	public ReqIFHeader(Element theHeader) {
		
		this.id = theHeader.getElementsByTagName(ReqIFConst.REQ_IF_HEADER).item(0).getAttributes().getNamedItem(ReqIFConst.IDENTIFIER).getTextContent();
		this.toolID = theHeader.getElementsByTagName(ReqIFConst.REQ_IF_TOOL_ID).item(0).getTextContent();
		
		if(theHeader.getElementsByTagName(ReqIFConst.SOURCE_TOOL_ID).getLength() > 0) {
			this.sourceToolID = theHeader.getElementsByTagName(ReqIFConst.SOURCE_TOOL_ID).item(0).getTextContent();
		}
		if(theHeader.getElementsByTagName(ReqIFConst.REQ_IF_VERSION).getLength() > 0) {
			this.reqifVersion = theHeader.getElementsByTagName(ReqIFConst.REQ_IF_VERSION).item(0).getTextContent();
		}
		if(theHeader.getElementsByTagName(ReqIFConst.COMMENT).getLength() > 0) {
			//this.comment = theHeader.getElementsByTagName(ReqIFConst.COMMENT).item(0).getTextContent();
			this.author = theHeader.getElementsByTagName(ReqIFConst.COMMENT).item(0).getTextContent().split("Created by: ")[1];
		}
		if(theHeader.getElementsByTagName(ReqIFConst.CREATION_TIME).getLength() > 0) {
			String date = theHeader.getElementsByTagName(ReqIFConst.CREATION_TIME).item(0).getTextContent();
			this.creationDate = DateUtils.parseISO8601Date(date);
		}
		if(theHeader.getElementsByTagName(ReqIFConst.TITLE).getLength() > 0) {
			this.title = theHeader.getElementsByTagName(ReqIFConst.TITLE).item(0).getTextContent().replace("_Template", "");
		}
	}

}
