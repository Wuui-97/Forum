package com.wuui.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author Dee
 * @create 2022-05-09-23:37
 * @describe 生成随机码及加密工具类
 */
public class CommunityUtil {

    //生成UUID
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //会为用户的password + salt(随机生成)，再进行加密，以提高密码的安全性
    public static String md5(String key){
        if(StringUtils.isAllBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 将要返回的信息封装为JSON
     * @param code 状态码
     * @param msg 返回的提示信息
     * @param map 要封装的数据信息
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null){
            for(String key : map.keySet()){
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg){
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code){
        return getJSONString(code, null, null);
    }
}
