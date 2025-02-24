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

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class CompareUtils {

	public static int compare(Object obj1, Object obj2) {
		return compare(obj1, obj2, false);
	}

	public static int compare(Object obj1, Object obj2, boolean sort) {
		if (obj1 == obj2) {
			return 0;
		} else if (obj1 == null) {
			return (!(obj2 instanceof List) || !((List)obj2).isEmpty()) && (!(obj2 instanceof String) || !StringUtils.isBlank((String)obj2)) ? 1 : 0;
		} else if (obj1 instanceof List) {
			if (obj2 instanceof List) {
				return compareLists((List)obj1, (List)obj2, sort);
			} else {
				return obj2 == null ? (((List)obj1).isEmpty() ? 0 : -1) : 1;
			}
		} else if (obj1 instanceof String) {
			String str1 = (String)obj1;
			if (obj2 instanceof String) {
				return compareStrings(str1, (String)obj2,true);
			} else {
				return obj2 == null ? (StringUtils.isBlank(str1) ? 0 : -1) : 1;
			}
		} else if (obj1 instanceof Date) {
			if (obj2 instanceof Date) {
				return compareDates((Date)obj1, (Date)obj2);
			} else {
				return obj2 == null ? -1 : 1;
			}
		} else {
			boolean equals = obj1.equals(obj2);
			if (equals) {
				return 0;
			} else {
				if (obj1 instanceof Comparable && obj2 != null) {
					try {
						return ((Comparable)obj1).compareTo(obj2);
					} catch (Throwable var6) {
						compareStrings(obj1.toString(), obj2.toString(),true);
					}
				} else if (obj2 != null) {
					return 1;
				}

				return -1;
			}
		}
	}

	public static List sort(List list, boolean copy) {
		if (list != null && ((List)list).size() > 1) {
			if (copy) {
				list = new ArrayList((Collection)list);
			}

			Collections.sort((List)list);
		}

		return (List)list;
	}

	public static List sort(List list) {
		return sort(list, false);
	}

	protected static int compareLists(List list1, List list2, boolean sort) {
		int result = 0;
		if (list1 != list2 && (!list1.isEmpty() || !list2.isEmpty())) {
			if (sort) {
				list1 = sort(list1, true);
				list2 = sort(list2, true);
			}

			ListIterator it1 = list1.listIterator();

			ListIterator it2;
			for(it2 = list2.listIterator(); result == 0 && it1.hasNext() && it2.hasNext(); result = compare(it1.next(), it2.next())) {
			}

			if (result == 0) {
				result = compare(it1.hasNext() ? it1.next() : null, it2.hasNext() ? it2.next() : null, sort);
			}
		}

		return result;
	}

	protected static int compareDates(Date date1, Date date2) {
		long sec1 = date1.getTime() / 1000L;
		long sec2 = date2.getTime() / 1000L;
		return sec1 < sec2 ? -1 : (sec1 == sec2 ? 0 : 1);
	}

	protected static int compareStrings(String str1, String str2,boolean ignoreCase) {
		if (str1 == str2 || StringUtils.isBlank(str1) && StringUtils.isBlank(str2)) {
			return 0;
		} else {
			return str1 != null ? (str2 != null ? (ignoreCase ? str1.compareToIgnoreCase(str2) : str1.compareTo(str2)) : -1) : 1;
		}
	}

	public static int compareNumbers(Number x, Number y) {
		if (x != y) {
			if (x != null) {
				if (y != null) {
					return !isSpecial(x) && !isSpecial(y) ? toBigDecimal(x).compareTo(toBigDecimal(y)) : Double.compare(x.doubleValue(), y.doubleValue());
				} else {
					return -1;
				}
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	public static boolean isSpecial(Number number) {
		if (number instanceof Double) {
			double dblVal = (Double)number;
			return Double.isNaN(dblVal) || Double.isInfinite(dblVal);
		} else if (!(number instanceof Float)) {
			return false;
		} else {
			float fltVal = (Float)number;
			return Float.isNaN(fltVal) || Float.isInfinite(fltVal);
		}
	}

	public static BigDecimal toBigDecimal(Number number) throws NumberFormatException {
		BigDecimal result = null;
		if (number != null) {
			if (number instanceof BigDecimal) {
				result = (BigDecimal)number;
			} else if (number instanceof BigInteger) {
				result = new BigDecimal((BigInteger)number);
			} else if (!(number instanceof Byte) && !(number instanceof Short) && !(number instanceof Integer) && !(number instanceof Long)) {
				if (!(number instanceof Float) && !(number instanceof Double)) {
					result = new BigDecimal(number.toString());
				} else {
					result = new BigDecimal(number.doubleValue());
				}
			} else {
				result = new BigDecimal(number.longValue());
			}
		}

		return result;
	}
}
