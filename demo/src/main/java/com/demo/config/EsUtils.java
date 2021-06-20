package com.demo.config;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;


@Component
public class EsUtils {

    private static final Logger logger = LoggerFactory.getLogger(EsUtils.class);

    @Autowired
    private RestClient restClient;

    private  JSONObject rest(String url,String json,String type) {
        try {
            if(StringUtil.isBlank(json)){
                json="{}";
            }
            HttpEntity entity = new NStringEntity(json, ContentType.APPLICATION_JSON);
            Response response = restClient.performRequest(type, url, Collections.<String, String>emptyMap(), entity);
            logger.debug(type.toUpperCase()+"  "+url+"\n"+json);
            InputStream content = response.getEntity().getContent();
            String result = IOUtils.toString(content, "UTF-8");
            return JSONObject.parseObject(result);
        }catch (IOException e){
            logger.debug(type.toUpperCase()+"  "+url+"\n"+json);
            throw new RuntimeException(e.getMessage());
        }
    }

    public JSONObject put(String url,String json){
        return rest(url,json,"put");
    }

    public JSONObject put(String url){
        return rest(url,null,"put");
    }

    public JSONObject get(String url,String json){
        return rest(url,json,"get");
    }

    public JSONObject get(String url){
        return rest(url,null,"get");
    }

    public JSONObject post(String url,String json){
        return rest(url,json,"post");
    }

    public JSONObject post(String url){
        return rest(url,null,"post");
    }

    public JSONObject delete(String url,String json){
        return rest(url,json,"delete");
    }

    public JSONObject delete(String url){
        return rest(url,null,"delete");
    }
}
