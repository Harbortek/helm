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

public class XHTMLElementObject extends XHTMLElement {

	private String data;
	private int width;

	private int height;

	private String name;

	private String type;


	
	public String getData() {
		return data;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public XHTMLElementObject(Node xhtmlElement) {
		super(xhtmlElement);
		
		this.data = xhtmlElement.getAttributes().getNamedItem("data").getTextContent().trim().replace("/", System.getProperty("file.separator"));
		if (xhtmlElement.getAttributes().getNamedItem("name") != null) {
			this.name = xhtmlElement.getAttributes().getNamedItem("name").getTextContent().trim();
		}
		if (xhtmlElement.getAttributes().getNamedItem("type") != null) {
			this.type = xhtmlElement.getAttributes().getNamedItem("type").getTextContent().trim();
		}

		if (xhtmlElement.getAttributes().getNamedItem("width") != null) {
			this.width = Integer.parseInt(xhtmlElement.getAttributes().getNamedItem("width").getTextContent().trim());
		}
		if ( xhtmlElement.getAttributes().getNamedItem("height") != null) {
			this.height = Integer.parseInt(xhtmlElement.getAttributes().getNamedItem("height").getTextContent().trim());
		}
	}

	public XHTMLElementObject(Node xhtmlElement, XHTMLNode parent) {
		super(xhtmlElement, parent);

		this.data = xhtmlElement.getAttributes().getNamedItem("data").getTextContent().trim().replace("/", System.getProperty("file.separator"));

		if (xhtmlElement.getAttributes().getNamedItem("name") != null) {
			this.name = xhtmlElement.getAttributes().getNamedItem("name").getTextContent().trim();
		}
		if (xhtmlElement.getAttributes().getNamedItem("type") != null) {
			this.type = xhtmlElement.getAttributes().getNamedItem("type").getTextContent().trim();
		}

		if (xhtmlElement.getAttributes().getNamedItem("width") != null) {
			this.width = Integer.parseInt(xhtmlElement.getAttributes().getNamedItem("width").getTextContent().trim());
		}
		if ( xhtmlElement.getAttributes().getNamedItem("height") != null) {
			this.height = Integer.parseInt(xhtmlElement.getAttributes().getNamedItem("height").getTextContent().trim());
		}

	}
	
	@Override
	public String toString() {
		return tagName + " {" + data +  "}" + (!children.isEmpty() ? "\t" + children.toString() : "");
	}

}
