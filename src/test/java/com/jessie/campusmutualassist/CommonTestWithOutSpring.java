package com.jessie.campusmutualassist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.Stack;

public class CommonTestWithOutSpring {
    @Test
    public void testSHA256(){
        String value = null;
        File file=new File("D:\\camProject\\test.xlsx");
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
        System.out.println(value);
        System.out.println(value.length());//以十六进制存储64位，如果是二进制为256位
    }

    @Test
    public void testJSON() {
        String menu = "{\n" +
                "     \"button\":[\n" +
                "     {    \n" +
                "        \"name\":\"一键查询\",\n" +
                "           \"sub_button\":[\n" +
                "           {\t\n" +
                "               \"type\":\"view\",\n" +
                "               \"name\":\"四六级查询\",\n" +
                "               \"url\":\"http://www.soso.com/\"\n" +
                "            },\n" +
                "            {\n" +
                "                 \"type\":\"miniprogram\",\n" +
                "                 \"name\":\"选课指导\",\n" +
                "                 \"url\":\"http://wechat.west2online.com\",\n" +
                "             },\n" +
                "            {\n" +
                "               \"type\":\"view\",\n" +
                "               \"name\":\"抽奖\",\n" +
                "                \"url\":\"http://37x366u444.wicp.vip/wechat/wx/oauth\"\n" +
                "            }]\n" +
                "      },\n" +
                "     {    \n" +
                "          \"type\":\"view\",\n" +
                "          \"name\":\"福大助手\",\n" +
                "          \"url\":\"http://fzuhelper.w2fzu.com\"\n" +
                "      },\n" +
                "     {    \n" +
                "          \"type\":\"view\",\n" +
                "          \"name\":\"西二官网\",\n" +
                "          \"url\":\"http://www.w2fzu.com/\"\n" +
                "      }\n" +
                " ]\n" +
                "}\n";
        JSONObject jsonObject = JSON.parseObject(menu);
        System.out.println(jsonObject.toJSONString());
    }

    @Test
    public void sayReverse() {
        Stack<String> stack = new Stack<>();
        String s;
        Scanner in = new Scanner(System.in);
        while ((s = in.next()) != null) {
            stack.push(s);
        }
        while (stack.size() != 0) {
            System.out.print(stack.pop());
            if (stack.size() != 0) {
                System.out.print(" ");
            }
        }
        in.close();
    }
}
