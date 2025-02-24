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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.attributes;

import java.util.ArrayList;
import java.util.List;

public class AttributeValueXHTMLElementList {
	
	
	private int size;
	private List<String> xhtmlElements;
	private List<List<String>> xhtmlContents;
	
	
	
	
	public int size() {
		return this.size;
	}
	
	public void add(String string, List<String> list) {
		this.xhtmlElements.add(string);
		this.xhtmlContents.add(list);
		this.size++;
	}
	
	public String getElementType(int index) {
		return xhtmlElements.get(index);
	}
	
	public List<String> getElementContentList(int index) {
		return this.xhtmlContents.get(index);
	}
	
	public List<String> getElementList() {
		return this.xhtmlElements;
	}
	
	
	
	
	public AttributeValueXHTMLElementList() {
		
		this.size = 0;
		this.xhtmlElements = new ArrayList<String>();
		this.xhtmlContents = new ArrayList<List<String>>();
	}

}
