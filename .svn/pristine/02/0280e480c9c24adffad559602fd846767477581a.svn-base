package com.jinr.core.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import com.jinr.core.config.UrlConfig;
 
import android.util.Base64;
 
public class MyDES {
 
    public final static String DES_KEY_STRING = "ABSujsuu";
//    public static final String iv_key = "12345678123456781234567812345678";
    
     
    public static String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); // ecb模式下不用iv
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        DESKeySpec desKeySpec = new DESKeySpec(UrlConfig.KEY.getBytes("UTF-8"));

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//        IvParameterSpec iv = new IvParameterSpec(KEY.getBytes("UTF-8")); // ecb模式下不用iv
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        MyLog.d("DES", "key值：~~~" + UrlConfig.KEY);

        return encodeBase64(cipher.doFinal(message.getBytes("UTF-8")));
        
//        SecretKeySpec key = new SecretKeySpec(getKey(encryptKey), "DES"); 
//        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding"); 
//        cipher.init(Cipher.ENCRYPT_MODE, key); 
//        byte[] encryptedData = cipher.doFinal(encryptString.getBytes()); 
//        return ConvertUtil.bytesToHexString(encryptedData);
    }
 
    public static String decrypt(String message) throws Exception {
 
        byte[] bytesrc = decodeBase64(message);//convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); //PKCS5Padding NoPadding位数不为8的倍数时报错
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(UrlConfig.KEY.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
//        IvParameterSpec iv = new IvParameterSpec(KEY.getBytes("UTF-8"));
        MyLog.d("DES", "key值：~~~" + UrlConfig.KEY);
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
 
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }
 
    public static byte[] convertHexString(String ss) {
        byte digest[] = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }
 
        return digest;
    }
 
    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }
 
        return hexString.toString();
    }
 
     
    public static String encodeBase64(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
     
    public static byte[] decodeBase64(String base64String) {
        return Base64.decode(base64String, Base64.DEFAULT);
    }
}
