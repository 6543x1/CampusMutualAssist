package com.jessie.campusmutualassist.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.jessie.campusmutualassist.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Service("aliyunGreenService")
@Slf4j
public class AliyunGreenService {

    @Autowired
    IAcsClient iAcsClient;
    @Autowired
    HashMap<String,String> textSafeDangerMap;

    public Result testTextSafe(String content) {
        TextScanRequest textScanRequest = new TextScanRequest();
//        textScanRequest.setAcceptFormat(FormatType.JSON);//指定API返回格式。textScanRequest.setHttpContentType (FormatType.JSON) ;
//        textScanRequest.setMethod(com.aliyuncs. http.MethodType.POST);//指定请求方法。textScanRequest.setEncoding("UTF-8") ;
//        textScanRequest.setRegionId(" cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<>();
        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
        task1.put("dataId", UUID.randomUUID().toString());
/**
 丰待检测的文本,长度不超过10000个字符。*/
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /*检测场景。文本垃圾检测请传递antispam 。**/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        System.out.println(JSON.toJSONString(data, true));
        try {
            textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Result.success("未能确定内容安全");
        }
        //请务必设置超时时间。
//        textScanRequest.setConnectTimeout (3000);
//        textScanRequest.setReadTimeout (6000) ;
        try {
            HttpResponse httpResponse = iAcsClient.doAction(textScanRequest);
            if (httpResponse.isSuccess()) {
                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
                System.out.println(JSON.toJSONString(scrResponse, true));

                if (200 == scrResponse.getInteger("code")) {
                    JSONArray taskResults = scrResponse.getJSONArray("data");
                    for (Object taskResult : taskResults) {
                        if (200 == ((JSONObject) taskResult).getInteger("code")) {
                            JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                            for (Object sceneResult :
                                    sceneResults) {
                                String scene = ((JSONObject) sceneResult).getString("scene");
                                String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                                // 根据scene和suggetion做相关处理。
                                // suggestion == pass表示未命中垃圾。suggestion == block表示命中了垃圾，可以通过1abe1字段:
//                                System.out.println("args = [" + scene + "]");
//                                System.out.println("args = [" + suggestion + "]");
                                if("block".equals(suggestion)){
                                    String label=((JSONObject)sceneResult).getString("label");
//                                    System.out.println(textSafeDangerMap);
                                    log.info("Hit TextDangerMap:"+label);
                                    if(textSafeDangerMap.containsKey(label)){
                                        if(((JSONObject) taskResult).get("filteredContent")!=null){
                                            return Result.error("内容命中关键词",400,((JSONObject) taskResult).get("filteredContent"));
                                        }
                                    return Result.error("内容涉嫌:"+textSafeDangerMap.get(label),403,"自行检查");
                                    }
                                    //为啥返回结果有时候没有违禁词在哪的提示?
                                }
                            }
                        } else {
                            log.error("task process fail:" + ((JSONObject) taskResult).getInteger(" code"));
                        }
                    }
                } else {
                    log.error("detect not success. code:" + scrResponse.getInteger(" code"));
                }
            } else {
                log.error("response not success. status:" + httpResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.success("未能确定内容安全");
        }
        return Result.success("OK");
    }
}
