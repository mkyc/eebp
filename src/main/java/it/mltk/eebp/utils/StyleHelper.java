package it.mltk.eebp.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mateusz on 10.07.2017.
 */
public class StyleHelper {

    public static String getRandomPostCategoryStyle(String name) throws NoSuchAlgorithmException {
        MessageDigest msg = MessageDigest.getInstance("MD5");
        msg.update(name.getBytes(), 0, name.length());
        String digest1 = new BigInteger(1, msg.digest()).toString(16);
        digest1 = digest1.substring(0, 6);
        return digest1;
    }
}
