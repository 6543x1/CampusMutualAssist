package com.jessie.campusmutualassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class HttpTest2 {
    public static void main(String[] args) {
        URL url;
        try {
            //url=new URL("https://devapi.qweather.com/v7/weather/3d?key=619158f36ce646788a108dfa9dd88f5d&location=101230101");
            url = new URL("https://geoapi.qweather.com/v2/city/lookup?key=619158f36ce646788a108dfa9dd88f5d&location=福州");
            try {
                String encodedUrl = URLEncoder.encode("福州", "utf-8");
                url = new URL("https://geoapi.qweather.com/v2/city/lookup?key=619158f36ce646788a108dfa9dd88f5d&location=" + encodedUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                httpURLConnection.setInstanceFollowRedirects(false);
                httpURLConnection.setRequestProperty("Charset", "UTF-8");

                InputStream inputStream = httpURLConnection.getInputStream();

                String line;
                StringBuilder sb = new StringBuilder();
                GZIPInputStream gZIPInputStream = new GZIPInputStream(inputStream);
                BufferedReader br = new BufferedReader(new InputStreamReader(gZIPInputStream, StandardCharsets.UTF_8));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                String str = sb.toString();
                System.out.println(str);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
