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


public class IdWorker {

	private final long workerId;
	private final long epoch = 1403854494756L; // 鏃堕棿璧峰鏍囪鐐癸紝浣滀负鍩哄噯锛屼竴鑸彇绯荤粺鐨勬渶杩戞椂闂�
	private final long workerIdBits = 10L; // 鏈哄櫒鏍囪瘑浣嶆暟
	private final long maxWorkerId = -1L ^ -1L << this.workerIdBits;// 鏈哄櫒ID鏈�澶у��:
																	// 1023
	private long sequence = 0L; // 0锛屽苟鍙戞帶鍒�
	private final long sequenceBits = 12L; // 姣鍐呰嚜澧炰綅

	private final long workerIdShift = this.sequenceBits; // 12
	private final long timestampLeftShift = this.sequenceBits + this.workerIdBits;// 22
	private final long sequenceMask = -1L ^ -1L << this.sequenceBits; // 4095,111111111111,12浣�
	private long lastTimestamp = -1L;

	public IdWorker(long workerId) {
		if (workerId > this.maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
		}
		this.workerId = workerId;
	}

	public synchronized long nextId() throws Exception {
		long timestamp = timeGen();
		if (this.lastTimestamp == timestamp) { // 濡傛灉涓婁竴涓猼imestamp涓庢柊浜х敓鐨勭浉绛夛紝鍒檚equence鍔犱竴(0-4095寰幆);
												// 瀵规柊鐨則imestamp锛宻equence浠�0寮�濮�
			this.sequence = this.sequence + 1 & this.sequenceMask;
			if (this.sequence == 0) {
				timestamp = this.tilNextMillis(this.lastTimestamp);// 閲嶆柊鐢熸垚timestamp
			}
		} else {
			this.sequence = 0;
		}

		if (timestamp < this.lastTimestamp) {
			throw new Exception(String.format("clock moved backwards.Refusing to generate id for %d milliseconds",
					(this.lastTimestamp - timestamp)));
		}

		this.lastTimestamp = timestamp;
		return timestamp - this.epoch << this.timestampLeftShift | this.workerId << this.workerIdShift | this.sequence;
	}


	/**
	 * 绛夊緟涓嬩竴涓绉掔殑鍒版潵, 淇濊瘉杩斿洖鐨勬绉掓暟鍦ㄥ弬鏁發astTimestamp涔嬪悗
	 */
	private long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 鑾峰緱绯荤粺褰撳墠姣鏁�
	 */
	private static long timeGen() {
		return System.currentTimeMillis();
	}

}
