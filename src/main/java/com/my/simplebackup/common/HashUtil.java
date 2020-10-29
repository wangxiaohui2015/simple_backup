package com.my.simplebackup.common;

import java.security.MessageDigest;

/**
 * Hash utility.
 * 
 * @author Administrator
 */
public class HashUtil {

	/**
	 * Get SHA256 hash value.
	 * 
	 * @param bytes
	 *            given bytes
	 * @return SHA256 hash value
	 * @throws Exception
	 *             Exception
	 */
	public static byte[] getSHA256HashValue(byte[] bytes) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(bytes);
		return md.digest();
	}

	/**
	 * Get SHA256 hash string value.
	 * 
	 * @param bytes
	 *            given bytes
	 * @return SHA256 hash string value
	 * @throws Exception
	 *             Exception
	 */
	public static String getSHA256HashStringValue(byte[] bytes)
			throws Exception {
		byte[] hashBytes = getSHA256HashValue(bytes);
		StringBuffer sb = new StringBuffer();
		for (byte hashByte : hashBytes) {
			String str = Integer.toHexString(hashByte & 0xFF);
			if (str.length() == 1) {
				sb.append(0);
			}
			sb.append(str);
		}
		return sb.toString().toUpperCase();
	}
}
