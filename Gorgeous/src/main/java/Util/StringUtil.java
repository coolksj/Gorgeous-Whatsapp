package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class StringUtil {
    public static StringBuilder StringAppender(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        return sb;
    }

    public static String StringAppender(String str, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(i);
        return sb.toString();
    }

    public static boolean isEmpty(String str) {
        if ((str == null) || str.equals("")) {
            return true;
        }
        return false;
    }

    public static String BytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    public static byte HexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    public static byte[] HexToBytes(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]= HexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    public static byte[] ReadFileContent(String path) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            int len =  inputStream.available();
            byte[] buffer = new byte[len];
            inputStream.read(buffer);
            return buffer;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String GetFileExt(String filePath) {
        int index = filePath.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return filePath.substring(index + 1);
    }

    public static String GetFileBaseName(String filePath) {
        String fileName = new File(filePath).getName();
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }
}
