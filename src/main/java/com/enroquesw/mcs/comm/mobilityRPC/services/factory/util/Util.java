package com.enroquesw.mcs.comm.mobilityRPC.services.factory.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.security.MessageDigest.*;

/**
 * La clase <code>Util</code>, contiene metodos utilitarios utilizados por las clases de este paquete
 * @author Julio Morales
 */
public class Util {
    static MessageDigest digest;
    static {
        try {
            digest = getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    public static String sha256(String base) {
        try{
            if(digest != null){
                byte[] hash = digest.digest(base.getBytes("UTF-8"));
                StringBuilder hexString = new StringBuilder();
                for (byte aHash : hash) {
                    String hex = Integer.toHexString(0xff & aHash);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
                /*for (int i = 0; i < hash.length; i++) { String hex = Integer.toHexString(0xff & hash[i]); if(hex.length() == 1) hexString.append('0'); hexString.append(hex); }*/
            } else {
                return base;
            }
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
