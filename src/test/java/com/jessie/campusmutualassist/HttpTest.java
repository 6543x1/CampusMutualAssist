package com.jessie.campusmutualassist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTest {
    @Test
    public void httpTest() {
        HttpClient httpClient = new HttpClient();//注意一下这玩意最后更新时间2007年.....
        HttpGet httpGet = new HttpGet("https://geoapi.qweather.com/v2/city/lookup?key=619158f36ce646788a108dfa9dd88f5d&location=福州");
        CloseableHttpClient httpClient2 = HttpClients.createDefault();//返回的就是Closeable的
        try {
            HttpResponse httpResponse = httpClient2.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            InputStream gzipInputStream = entity.getContent();
//            GZIPInputStream gzipInputStream =new GZIPInputStream(entity.getContent());
            StringBuilder res = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                res.append(line);
            }
            System.out.println(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        GetMethod getMethod=new GetMethod("https://devapi.qweather.com/v7/weather/3d?key=619158f36ce646788a108dfa9dd88f5d&location=101230101");
//        try {
//            httpClient.executeMethod(getMethod);
//            GZIPInputStream gzipInputStream =new GZIPInputStream(getMethod.getResponseBodyAsStream());
//            StringBuilder res=new StringBuilder();
//            String line;
//            BufferedReader br = new BufferedReader(new InputStreamReader(gzipInputStream, StandardCharsets.UTF_8));
//            while ((line = br.readLine()) != null) {
//                res.append(line);
//            }
//            System.out.println(res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void okHttpTest() {
        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request=new Request.Builder().url("https://devapi.qweather.com/v7/weather/3d?key=619158f36ce646788a108dfa9dd88f5d&location=101230101")
//                .get()//默认为GET请求，也可以不写，写了更清晰
//                .build();
        Request request = new Request.Builder().url("https://geoapi.qweather.com/v2/city/lookup?key=619158f36ce646788a108dfa9dd88f5d&location=福州").build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
//            System.out.println(response.body().string());
            JSONObject jsonObject = JSON.parseObject(response.body().string());

            System.out.println(jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray("location");
            System.out.println(jsonArray);
            System.out.println(jsonArray.getJSONObject(0).getString("name"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TimeTest() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String stime = "2020-11-18T04:31";
        System.out.println(LocalDateTime.parse(stime, df));
        System.out.println(ZonedDateTime.parse("2022-01-16T14:35+08:00"));
        System.out.println(LocalDateTime.from(ZonedDateTime.parse("2022-01-16T14:35+08:00")));
    }
}
