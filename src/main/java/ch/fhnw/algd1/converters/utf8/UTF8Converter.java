package ch.fhnw.algd1.converters.utf8;

/*
 * Created on 05.09.2014
 */

import java.util.Arrays;

/**
 * @author 
 */
public class UTF8Converter {
	public static byte[] codePointToUTF(int x) {

		int len = 4;
		if (x < 1 << 7) len = 1;
		else if (x < 1 << 11) len = 2;
		else if (x < 1 << 16) len = 3;
		if(x >= 1 << 22) throw new IllegalArgumentException();

		byte[] b = new byte[len];
		for (int i = len - 1; i > 0; i--) {
			b[i] = (byte) (0b1000_0000 | (x & 0b0011_1111));
			x >>= 6;
		}

		b[0] = (byte) x;

		if(len > 1){
			for(int i = 0; i < len; i++){
				b[0] |= (byte) (1 << 8 - len + i);
			}
		}
		return b;
	}

	public static int UTFtoCodePoint(byte[] bytes) {
		if (isValidUTF8(bytes)) {
			int len = bytes.length;
			if (len == 1) return bytes[0];
			int x = 0;
			x |= bytes[0] & 0xFF;
			x <<= 25 + len;
			x >>>= 25 + len;
			for (int i = 1; i < len; i++) {
				x <<= 6;
				x |= bytes[i] & 0b0011_1111;
			}
			return x;
		} else return 0;
	}

	private static boolean isValidUTF8(byte[] bytes) {
		if (bytes.length == 1) return (bytes[0] & 0b1000_0000) == 0;
		else if (bytes.length == 2) return ((bytes[0] & 0b1110_0000) == 0b1100_0000)
				&& isFollowup(bytes[1]);
		else if (bytes.length == 3) return ((bytes[0] & 0b1111_0000) == 0b1110_0000)
				&& isFollowup(bytes[1]) && isFollowup(bytes[2]);
		else if (bytes.length == 4) return ((bytes[0] & 0b1111_1000) == 0b1111_0000)
				&& isFollowup(bytes[1]) && isFollowup(bytes[2]) && isFollowup(bytes[3]);
		else return false;
	}

	private static boolean isFollowup(byte b) {
		return (b & 0b1100_0000) == 0b1000_0000;
	}
}
