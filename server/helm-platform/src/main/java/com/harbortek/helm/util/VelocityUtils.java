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

package com.harbortek.helm.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class VelocityUtils {
	public static void export(String templateName, Map parameters, Writer writer) throws Exception {
		VelocityEngine ve = new VelocityEngine();
		//File file = templateFile.getFile();
		//String folder= file.getParent();
		//String fileName = file.getName();
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//		properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,folder);
		properties.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
		properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		properties.setProperty("runtime.log.logsystem.class","org.apache.velocity.runtime.log.NullLogChute");

		try {
			ve.init(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Template t = ve.getTemplate(templateName,"UTF-8");

		VelocityContext context = new VelocityContext();
		for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
			Entry entry = (Entry)iter.next();
			context.put(entry.getKey().toString(), entry.getValue());
		}

		t.merge(context, writer);
		writer.flush();
	}

}
