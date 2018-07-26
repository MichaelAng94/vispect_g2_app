package com.vispect.android.vispect_g2_app.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import bean.BleBase;

/**
 * Created by shenglr on 16/1/22.
 */
public class CodeUtil {
    private static final String TAG = "CodeUtil";

    public static String transToCode(String value, int codeLength) {
        String temp = String.format("%" + codeLength / 2 + "s", value);
        temp = temp.replace(" ", "0");
        return new String(Hex.encodeHex(temp.getBytes()));
    }
    public static String codeToString(String value) {
        try {
            return new String(Hex.decodeHex(value.toCharArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 用于CRC校验（CRC校验计算由命令字开始到数据字结束，不包括起始字节、结束字和CRC校验字节）
     *
     * @param byteArray
     * @param intLength
     * @return
     */
    public static final byte[] generateCrc16(byte[] byteArray, int intLength) {
        int crc = 0xFFFF;
        for (int i = 0; i < intLength; i++) {
            crc ^= (byteArray[i] & 0xFF);
            crc = crc & 0xFFFF;
            // System.out.println(crc);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >> 1) ^ 0x8408;
                } else {
                    crc = (crc >> 1);
                }
                crc = crc & 0xFFFF;
                // System.out.println(crc);
            }
        }
        // System.out.println("crc=" + crc);
        return intToByteArray(crc);
    }

    public static int codeToInt(String value) {
        try {
            return Integer.valueOf(codeToString(value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * byte 数组与 int 的相互转换
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{(byte) ((a >> 24) & 0xFF), (byte) ((a >> 16) & 0xFF), (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)};
    }

    // 两个字节表示的最大数
    public static int MAX_2_BYTE_SIZE = 65535;

    /**
     * int 转 2个 字节 (先高字节再低字节)
     *
     * @param value
     * @return
     */
    public static byte[] intToBb(int value) {

        byte high = (byte) ((value & 0xff00) >> 8);
        byte low = (byte) (value & 0x00ff);

        return new byte[]{high, low};

    }



    /**
     * int 转 2个 字节  (先低字节再高字节)
     *
     * @param value
     * @return
     */
    public static byte[] intToBbLH(int value) {

        byte high = (byte) ((value & 0xff00) >> 8);
        byte low = (byte) (value & 0x00ff);

        return new byte[]{low,high};

    }
    /**
     * 1个byte转int  (无符号)
     *
     * @param b
     * @return
     */
    public static int oneByteToInt(byte b) {
        return (b & 0xFF);
    }
    /**
     * int 转 1个 字节
     *
     * @param num
     * @return
     */
    public static byte intToOneByte(int num) {
        return (byte) (num & 0x000000ff);
    }

    /**
     * 2个字节 转int (高位在前）
     *
     * @param b
     * @return
     */
    public static int bbToInt(byte[] b) {
        // ------------拼回int
        int result = ((b[0] << 8) & 0xff00) | (b[1] & 0xff);
        return result;
    }
    public static short byteToshort(byte[] b){
        return (short)(((b[0] & 0x00FF) << 8) | (0x00FF & b[1]));
    }

    public static byte[] intTobb(int value){
        byte[] buf = new byte[]{00,00};
        ByteArrayOutputStream boutput = new ByteArrayOutputStream();
        DataOutputStream doutput = new DataOutputStream(boutput);
        try {
            doutput.writeInt(value);
            buf = boutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            boutput.close();
            doutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }

    /**
     * 2个字节 转int（低位在前）
     *
     * @param b
     * @return
     */
    public static int bbToIntLH(byte[] b) {
        int result = ((b[1] << 8) & 0xff00) | (b[0] & 0xff);
        return result;
    }


    /**
     * 4个字节 转int
     *
     * @param buf
     * @return
     */
    public static int bbbbToint(byte[] buf) {
        // ------------拼回int
        return buf[3] & 0xFF | (buf[2] & 0xFF) << 8 | (buf[1] & 0xFF) << 16 | (buf[0] & 0xFF) << 24;
    }

    /**
     * 2个字节 转不带符号的int
     *
     * @param b
     * @return
     */
    public static int getUnsignedintbyBB(byte[] b){
        short data = (short)(((b[0] & 0x00FF) << 8) | (0x00FF & b[1]));
        return data&0x0FFFF ;
    }

    /**
     * 1个字节 转不带符号的int
     *
     * @param b
     * @return
     */
    public static int getUnsignedintbyB(byte b){
        return b&0xff ;
    }





    /**
     * 从一个byte[]数组中截取一部分
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);

        return bs;
    }

    /**
     * 从一个byte[]数组中截取一部分
     * @param src
     * @param begin
     * @param count
     * @return
     */
    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    /**
     * long 转 byte
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }
    /**
     *  byte 转 long
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        long temp = buffer.getLong();
        buffer.clear();
        return temp;
    }
    /**
     *  把16进制的string内容转为byte【】
     * @param str
     * @return
     */
    public static byte[] stringToByte(String str){
        byte[] data = new byte[str.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = Integer.valueOf(str.substring(0 + i * 2, 2 + i * 2), 16).byteValue();
        }
        return data;
    }
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    /**
     *  把byte[]以16进制格式打印出来
     * @param b
     * @return
     */

    public static String bytesToString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(byte temp : b){
            sb.append(String.format("%02x",temp));
        }
        return sb.toString();
    }

    /**
     *  把一个int转成二进制字符串  低位在前（八位）
     * @param b
     * @return
     */
    public static String toBitStr(byte b){
        String temp = Integer.toBinaryString(b & 0xFF);  //去掉符号位
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ;i < 8-temp.length(); i++){
            sb.append("0");
        }
        sb.append(temp);
        String bitstr = sb.reverse().toString();

        return bitstr;
    }

    public static String toBitStr(int b){
        String temp = Integer.toBinaryString(b & 0xFF);  //去掉符号位
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ;i < 8-temp.length(); i++){
            sb.append("0");
        }
        sb.append(temp);
        String bitstr = sb.reverse().toString();
        return bitstr;
    }

    /**
     *  把多个byte转成二进制字符串
     * @param bs
     * @return
     */
    public static String EBToBitStr(byte[] bs){
        StringBuffer value = new StringBuffer();
        for(byte temp : bs){
            value.append(toBitStr(temp));
        }
        return value.toString();
    }



    public static boolean checkBleData(byte[] bytes, String str) {
        //TODO 校验数据是否损坏

        if (bytes[0] != 0x1b) {
            return false;
        }
        XuLog.d(TAG, "头校验通过");

        if (bytes[bytes.length - 1] != 0x1e) {
            return false;
        }
        XuLog.d(TAG, "尾校验通过");
//        int sum = 0;
//        for (byte b : Arrays.copyOfRange(bytes, 0, bytes.length - 3)) {
//            sum += b;
//        }

        //先简单的判断下长度是否正确
        if (bytes.length < 5) {
            return false;
        }
        long length = bytes.length;
        int bytes_length = CodeUtil.bbToInt(new byte[]{bytes[2], bytes[3]});
        if(bytes[1] == BleBase.BLE_ADAS_PUSH || bytes[1] == BleBase.HEARTBEAT || bytes[1] == BleBase.OBD_CAN_VALUE){
            XuLog.d(TAG, "BLE推送的报警数据，不进行长度校验" );
            return  true;
        }
        if (bytes_length != str.length()/2) {
            XuLog.d(TAG, "长度校验不对" + "      length:" + length + "        bytes_length:" + bytes_length + "new String(new byte[]{bytes[1]})="+new String(new byte[]{bytes[1]}));
            return false;
        }
        ByteBuffer.wrap(bytes, 2, 2);

        int sum = 0;
        for(byte temp_b:subBytes(bytes,0,bytes.length - 5)){
            byte now_byte = temp_b;
            sum+=temp_b;
        }


        int check_code = byteToshort(new byte[]{bytes[bytes.length-3],bytes[bytes.length-2]});
        if (sum > 65535) {
            byte[] intToByteArray = Arrays.copyOfRange(bytes, 0, bytes.length - 3);
            ByteBuffer wrap = ByteBuffer.wrap(intToByteArray, 2, 2);
            sum = wrap.getShort();
        }

        if(check_code != sum){
            return  false;
        }


//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%02x", bytes[bytes.length-3]));
//        sb.append(String.format("%02x", bytes[bytes.length-2]));
//        ByteBuffer wrap = ByteBuffer.wrap(bytes, bytes[bytes.length-3], 2);
//        if(Integer.parseInt(sb.toString(), 16) != sum) {
//            XuLog.e(TAG, "和校验不通过" + "  Integer.parseInt(sb.toString(),16):" + Integer.parseInt(sb.toString(),16)+"    sum:"+sum+"  sb.toString():"+sb.toString());
//            return true;
//        }
//
//        int check_code = byteArrayToInt(bytes,bytes[bytes.length-3]);
//
//                if(sum != check_code){
//                    XuLog.e(TAG, "和校验不通过" + "  sum:" + sum+"    check_code:"+check_code);
//                }


        return true;
    }
    /**
     *  ascii转String字符串
     * @param ASCIIs
     * @return
     */
    public static String ascii2String(byte[] ASCIIs) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ASCIIs.length; i++) {
            sb.append((char) ascii2Char(ASCIIs[i]));
        }
        return sb.toString();
    }
    /**
     *  ascii转字符
     * @param ASCII
     * @return
     */
    public static char ascii2Char(int ASCII) {
        return (char) ASCII;
    }

}
