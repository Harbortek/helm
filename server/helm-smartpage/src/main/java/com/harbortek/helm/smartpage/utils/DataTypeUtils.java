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

package com.harbortek.helm.smartpage.utils;


import com.harbortek.helm.common.exception.ServiceException;
import com.harbortek.helm.tracker.constants.DatasetConstants;
import com.harbortek.helm.util.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;

import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class DataTypeUtils {
	private static final String DateFormatOne = "/";
	private static final String DateFormatTwo = "-";
	private static final String DateFormatThree = ":";
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String SHORT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
	private static final String DEFAULT_SQUOTE_DATE_FORMAT = "yyyy/MM/dd";
	private static SimpleDateFormat defaultSf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);


	private static final ConcurrentMap<Pattern, String> regPatternMap = new ConcurrentHashMap<Pattern, String>();
	static {
		regPatternMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$"), "yyyy-MM-dd");
		regPatternMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$"), "yyyy-MM-dd HH:mm:ss");
		regPatternMap.put(Pattern.compile("^\\d{4}\\d{1,2}\\d{1,2}$"), "yyyyMMdd");
		regPatternMap.put(Pattern.compile("^\\d{4}\\d{1,2}$"), "yyyyMM");
		regPatternMap.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$"), "yyyy/MM/dd");
		regPatternMap.put(Pattern.compile("^\\d{4}年\\d{1,2}月\\d{1,2}日$"), "yyyy年MM月dd日");
		regPatternMap.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$"), "yyyy/MM/dd HH:mm:ss");
		regPatternMap.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1}$"), "yyyy/MM/dd HH:mm:ss.S");
		regPatternMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1}$"), "yyyy-MM-dd HH:mm:ss.S");
	}

	public static String getDataType(Object rawData) {
		if (rawData ==null ){
			return DatasetConstants.COLUMN_STRING;
		}
		String rawString = rawData.toString();
		if (rawString.matches("-?\\d+(\\.\\d+)?") ) {
			return DatasetConstants.COLUMN_NUMBER;
		} /*else if (isValidDate(rawData.toString(), DEFAULT_DATETIME_FORMAT))*/
		else if (isValidDate(rawData.toString())) {
			return DatasetConstants.COLUMN_DATE;
		} else {
			return DatasetConstants.COLUMN_STRING;
		}
	}

	private static boolean isValidDate(String dateStr) {
		try {
			for (Map.Entry<Pattern, String> entry : regPatternMap.entrySet()) {
				boolean isMatch = entry.getKey().matcher(dateStr).matches();
				if (isMatch) {
					FastDateFormat.getInstance(entry.getValue()).parse(dateStr);
					return true;
				}
			}
			return false;
		} catch (ParseException e) {
			return false;
		}
	}

	private static boolean isValidDate(String dateStr, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			df.setLenient(false);
			df.parse(dateStr);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	private static String normalizeDate(String input) {
		if (StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(input, DateFormatOne)
				&& StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(input, DateFormatTwo)) {
			return input;
		}

		if (StringUtils.INDEX_NOT_FOUND != StringUtils.indexOf(input, DateFormatOne)) {
			SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_SQUOTE_DATE_FORMAT);
			try {
				input = defaultSf.format(sf.parse(input));
			} catch (ParseException e) {
				return input;
			}

		}

		if (StringUtils.INDEX_NOT_FOUND != StringUtils.indexOf(input, DateFormatTwo)
				&& StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(input, DateFormatThree)) {

			SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
			try {
				input = defaultSf.format(sf.parse(input));
			} catch (ParseException e) {
				return input;
			}

		}

		if (StringUtils.INDEX_NOT_FOUND != StringUtils.indexOf(input, DateFormatTwo)
				&& StringUtils.INDEX_NOT_FOUND != StringUtils.indexOf(input, DateFormatThree)) {

			SimpleDateFormat sf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
			try {
				input = defaultSf.format(sf.parse(input));
			} catch (ParseException e) {
				return input;
			}

		}

		return input;
	}

	public static String getDataTypeFromJDBC(int dataType) {
		if (dataType == Types.BIGINT || dataType == Types.DECIMAL || dataType == Types.DOUBLE || dataType == Types.FLOAT
				|| dataType == Types.INTEGER || dataType == Types.NUMERIC || dataType == Types.REAL || dataType == Types.ROWID
				|| dataType == Types.SMALLINT || dataType == Types.TINYINT){
			return DatasetConstants.COLUMN_NUMBER;
		}else if (dataType == Types.CHAR || dataType == Types.CLOB || dataType == Types.LONGNVARCHAR || dataType == Types.LONGVARCHAR
				|| dataType == Types.NCHAR || dataType == Types.NCLOB || dataType == Types.NVARCHAR || dataType == Types.VARCHAR){
			return DatasetConstants.COLUMN_STRING;
		}else if (dataType == Types.DATE || dataType == Types.TIME || dataType==Types.TIMESTAMP
				/*|| dataType == Types.TIMESTAMP_WITH_TIMEZONE|| dataType == Types.TIME_WITH_TIMEZONE*/  ){
			return DatasetConstants.COLUMN_DATE;
		}
		
		return null;
	}

	public static boolean isBlob(int dataType) {
		return  dataType== Types.BLOB || dataType== Types.CLOB
				|| dataType==Types.LONGNVARCHAR || dataType==Types.LONGVARBINARY
				|| dataType==Types.NCLOB || dataType==Types.VARBINARY
				|| dataType==Types.STRUCT || dataType==Types.SQLXML
				|| dataType==Types.JAVA_OBJECT || dataType==Types.DATALINK;
	}

	public static String getJDBCDataType(String type) {
		if (DatasetConstants.COLUMN_STRING.equals(type)){
			return "VARCHAR(500)";
		}else if (DatasetConstants.COLUMN_NUMBER.equals(type)){
			return "NUMERIC";
		}else if (DatasetConstants.COLUMN_DATE.equals(type)){
			return "DATE";
		}else if (DatasetConstants.COLUMN_TIME.equals(type)){
			return "TIME";
		}
		ServiceException.throwException("数据错误");
		return null;
	}
}
