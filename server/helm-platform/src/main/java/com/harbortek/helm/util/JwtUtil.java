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


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

	/**签名的秘钥**/
	private static final String SECRET = "vux$6mcvE*ZZFL$6";

	/**过期的时间，单位：秒**/
	private static final long EXPIRATION = 1800L;

	/**生成token,并设置token的过期时间**/
	public static String createToken(String key,Long value) {

		/**过期时间**/
		Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");

		String token = JWT.create()
				.withHeader(map)
				.withClaim(key, value)
				.withExpiresAt(expireDate)
				.withIssuedAt(new Date())
				.sign(Algorithm.HMAC256(SECRET));
		return token;
	}


	/**校验token,并进行token的解析**/
	public static Map<String, Claim> verifyToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
			return null;
		}
		return jwt.getClaims();
	}
}
