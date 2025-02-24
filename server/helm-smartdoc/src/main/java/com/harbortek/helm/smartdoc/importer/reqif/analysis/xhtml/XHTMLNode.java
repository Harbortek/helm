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

package com.harbortek.helm.smartdoc.importer.reqif.analysis.xhtml;

import org.w3c.dom.Node;



public class XHTMLNode {

	protected String tagName;
	protected XHTMLNode parent = null;
	
	
	/**
	 * @return the node name of this xhtml node
	 */
	public String getTagName() {
		return this.tagName;
	}
	
	/**
	 * @return the parent XHTMLNode of this XHTMLNode
	 */
	public XHTMLNode getParent() {
		return this.parent;
	}
	
	
	
	public XHTMLNode(Node xhtmlElement) {
		
		this.tagName = xhtmlElement.getNodeName();

		xhtmlElement.getAttributes();
	}
	
	public XHTMLNode(Node xhtmlElement, XHTMLNode parent) {
		
		this.tagName = xhtmlElement.getNodeName();
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return tagName;
	}
	
}
