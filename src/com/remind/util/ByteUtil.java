package com.remind.util;

/**
 * <b>function:</b>
 * 
 * @author lxl
 * @createDate 14-12-2 下午3:45
 * @file WThread
 * @package IM
 * @project groovy_test
 * @version 1.0
 */
public class ByteUtil {
    private static byte[] shortToByte(int num) {
        byte[] b = new byte[2];
        for (int i = 0; i < 2; i++) {
            b[i] = (byte) (num >>> (8 - i * 8));
        }
        return b;
    }

    private static byte[] intToByte(int num) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (num >>> (24 - i * 8));
        }
        return b;
    }

    public static int byteToShort(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int res = 0;
        for (int i = 0; i < 2; i++) {
            res <<= 8;
            temp = b[i] & mask;
            res |= temp;
        }
        return res;
    }

    public static int byteToInt(byte[] b) {
        int mask = 0xff;
        int temp = 0;
        int res = 0;
        for (int i = 0; i < 4; i++) {
            res <<= 8;
            temp = b[i] & mask;
            res |= temp;
        }
        return res;
    }

    public static byte[] toBytes(byte[] data, int type) throws Exception {
        byte[] result = null;
        if (type == 1) {
            result = new byte[data.length + 2];
            System.arraycopy(shortToByte(data.length), 0, result, 0, 2);
            System.arraycopy(data, 0, result, 2, data.length);
        } else {
            result = new byte[data.length + 4];
            System.arraycopy(intToByte(data.length), 0, result, 0, 4);
            System.arraycopy(data, 0, result, 4, data.length);
        }
        return result;
    }

}
