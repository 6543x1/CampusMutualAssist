package com.jessie.campusmutualassist.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

@Component
public class JwcIdentifyUtil {
    @Autowired
    RedisUtil redisUtil;

    public void getVerifyCode(String path, int uid) throws Exception {
        Map<String, String> cookies = null;
        String urlLogin = "https://jwcjwxt2.fzu.edu.cn:82/plus/verifycode.asp?n=" + Math.random();
        System.out.println(urlLogin);
        Connection connect = Jsoup.connect(urlLogin);
        // 伪造请求头
        connect.header("Accept", "image/webp,*/*").header("Accept-Encoding",
                "gzip, deflate,br");
        connect.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2").header("Connection", "keep-alive");
        connect.header("Content-Length", "213").header("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");
        connect.header("Host", "jwcjwxt2.fzu.edu.cn:82").header("Referer", "https://jwch.fzu.edu.cn/html/login/1.html");
        connect.header("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        // 请求url获取响应信息
        Connection.Response res = connect.ignoreContentType(true).method(Connection.Method.POST).execute();// 执行请求
        // 获取返回的cookie
        cookies = res.cookies();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            System.out.println(entry.getKey() + "-" + entry.getValue());
        }
        redisUtil.hPutAll("User_Jwc_Cookie|" + uid, cookies, 5 * 60);
        //拿到cookie了！！！
        // 获取响应体
        String body = res.body();
        //System.out.println(body);
        byte[] bytes = res.bodyAsBytes();
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(path + "/imageYzm.png");
        if (!file2.exists()) {
            file2.createNewFile();
        } else if (file2.exists()) {
            file2.delete();
            file2.createNewFile();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        fileOutputStream.write(bytes);
    }

    public boolean login(String No, String Password, String VerifyCode, Map<String, String> cookies) throws Exception {

        String urlLogin = "https://jwcjwxt2.fzu.edu.cn:82/logincheck.asp";
        Connection connect = Jsoup.connect(urlLogin);
        connect.timeout(5 * 100000).followRedirects(false);
        // 伪造请求头
        connect.header("Accept", "image/webp,*/*").header("Accept-Encoding",
                "gzip, deflate,br");
        connect.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2").header("Connection", "keep-alive");
        connect.header("Content-Length", "213").header("Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8");
        connect.header("Host", "jwcjwxt2.fzu.edu.cn:82").header("Referer", "https://jwch.fzu.edu.cn/html/login/1.html");
        connect.header("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        // 输入验证码

        connect.data("muser", No).data("passwd", Password).data("Verifycode", VerifyCode);
        connect.cookies(cookies);
        // 请求url获取响应信息
        Connection.Response res = connect.ignoreContentType(true).method(Connection.Method.POST).execute();// 执行请求
        // 获取返回的cookie
        int StatusCode = res.statusCode();
        System.out.println(StatusCode);
        if (StatusCode == 302) {
            return true;
        } else {
            return false;
        }
    }
}
