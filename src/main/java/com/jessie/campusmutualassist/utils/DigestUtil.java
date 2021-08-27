package com.jessie.campusmutualassist.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class DigestUtil {
    //用于判断文件是不是同一个文件
    public static String getSHA256(File file){
        String value = null;
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
            MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(byteBuffer);
            BigInteger bigInteger = new BigInteger(1, messageDigest.digest());
            value = bigInteger.toString(16);

        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }
}
