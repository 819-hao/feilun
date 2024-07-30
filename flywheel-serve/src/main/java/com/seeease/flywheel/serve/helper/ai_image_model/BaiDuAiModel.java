package com.seeease.flywheel.serve.helper.ai_image_model;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baidu.aip.imagesearch.AipImageSearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Component
@Slf4j
public class BaiDuAiModel implements AiImageModel{

    @NacosValue(value = "${baidu.aiImage.appId}", autoRefreshed = true)
    private String appId;
    @NacosValue(value = "${baidu.aiImage.appKey}", autoRefreshed = true)
    private String appKey;
    @NacosValue(value = "${baidu.aiImage.appSecretKey}", autoRefreshed = true)
    private String appSecretKey;

    private AipImageSearch baiduClient;

    @PostConstruct
    public void init(){
        try {
            AipImageSearch aipImageSearch = new AipImageSearch(appId, appKey, appSecretKey);
            baiduClient = aipImageSearch;
        }catch (Exception e){
            log.error("init baidu model error :{}",e.getMessage());
        }

    }

    @Override
    public void submitFormUrl(String url, String brief) {
        HashMap<String, String> options = new HashMap<String, String>();
        //后期增加分类这里要处理
        options.put("class_id1", "1");
        options.put("class_id2", "1");
        // 参数为本地路径
        baiduClient.productAddUrl(url, brief, options);
    }

    @Override
    public String match(byte[] imageBytes) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("class_id1", "1");
        options.put("class_id2", "1");
        options.put("pn", "0"); //开始页面
        options.put("rn", "1"); //limit

        com.alibaba.fastjson.JSONObject respJson = com.alibaba.fastjson.JSONObject.parseObject(baiduClient.productSearch(imageBytes, options).toString(2), com.alibaba.fastjson.JSONObject.class);
        return  respJson.getJSONArray("result")
                .toJavaList(com.alibaba.fastjson.JSONObject.class)
                .stream()
                .findFirst()
                .map(v-> v.getJSONObject("brief").getString("keyword"))
                .orElse("");
    }

    @Override
    public Model getModel() {
        return Model.BAIDU;
    }
}
