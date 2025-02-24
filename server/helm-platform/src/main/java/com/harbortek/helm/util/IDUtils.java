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

public class IDUtils {

	private static IdWorker mySQLId = new IdWorker(1);

	public static Long getId() {
		try {
			return mySQLId.nextId();
		} catch (Exception e) {
		}
		return 0L;
	}

	public static String getShortId(){
		long n = getId();
		String str = "ABCDEFGHIJKLNMOPQRSTUVWXYZ";
		String s = "";
		if(n==0L)
		{
			return str.charAt(0)+"";
		}
		while (n > 0){
			long m = n % 26;
			s = str.charAt((int)m) + s;
			n = (n - m) / 26;
		}
		return s;
	}

	public static String long2Short(Long n){
		String str = "0123456789abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ";
		String s = "";
		if(n==0L)
		{
			return str.charAt(0)+"";
		}
		while (n > 0){
			long m = n % 62;
			s = str.charAt((int)m) + s;
			n = (n - m) / 62;
		}
		return s;
	}

	public static Long short2Long(String data){
		String str = "0123456789abcdefghijklnmopqrstuvwxyzABCDEFGHIJKLNMOPQRSTUVWXYZ";
		int scale = str.length(); //转化目标进制
		String s = "";
		if("0".equalsIgnoreCase(data)) {
			return 0L;
		}
		long value = 0L;
		for (int i= data.length()-1; i>=0;i--){
			int m = str.indexOf( data.charAt(i) );
			value += Double.valueOf( m * Math.pow(62, data.length()-1-i) ).longValue() ;
		}

		return value;
	}

}
