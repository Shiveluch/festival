package com.shiveluch.festival;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Refactor {
    public static byte[] refact(String message, SecretKey secret) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    public static String defact(byte[] cipherText, SecretKey secret) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    public static String keyToString(SecretKey secretKey) {
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }

    public static SecretKey stringToKey(String stringKey) {
        byte[] encodedKey = Base64.decode(stringKey.trim(), Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }


    // Кодируем строку
    public static String encodeXOR(String s, String key) {
        return Base64.encodeToString(xor(s.getBytes(), key.getBytes()), 0);
    }

    // Декoдируем строку
    public static String decodeXOR(String s, String key) {
        return new String(xor(Base64.decode(s, 0), key.getBytes()));
    }

    // Сама операция XOR
    private static byte[] xor(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

}
