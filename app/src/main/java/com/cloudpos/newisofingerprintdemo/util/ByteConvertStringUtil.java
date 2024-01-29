package com.cloudpos.newisofingerprintdemo.util;

import java.util.Scanner;

/**
 *  @author john
 *  Convert byte[] to hex string
 * */
public class ByteConvertStringUtil {
	/**
	 *  Change byte to int. Then use Integer.toHexString(int) change to Hex String.
	 *  
	 *  @param src byte[] data  
	 *  @param hex string  
	 *  
	 * */
	public static String bytesToHexString(byte[] src){
		StringBuilder stringBuilder = new StringBuilder("");
	    if (src == null || src.length <= 0) {  
	        return null;  
	    }  
	    for (int i = 0; i < src.length; i++) {  
	        int v = src[i] & 0xFF;  
	        String hv = Integer.toHexString(v);
	        if (hv.length() < 2) {  
	            stringBuilder.append(0);  
	        }  
	        stringBuilder.append(hv);  
	    }  
	    return stringBuilder.toString();  
	}
	
	public static String byteToHexString(byte src){
		StringBuilder stringBuilder = new StringBuilder("");
		int v = src & 0xFF;
		String hv = Integer.toHexString(v);
	    return hv;  
	}

	public static byte[] hexToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] bytes = new byte[length];
		String hexDigits = "0123456789abcdef";
		for (int i = 0; i < length; i++) {
			int pos = i * 2; // 两个字符对应一个byte
			int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
			int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
			if (h == -1 || l == -1) { // 非16进制字符
				return null;
			}
			bytes[i] = (byte) (h | l);
		}
		return bytes;
	}

	/**
	 * int to byte[]
	 */
	public static byte[] intToBytes(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (i & 0xff);
		bytes[1] = (byte) ((i >> 8) & 0xff);
		bytes[2] = (byte) ((i >> 16) & 0xff);
		bytes[3] = (byte) ((i >> 24) & 0xff);
		return bytes;
	}

}
