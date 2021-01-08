package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    //生成激活码等随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    //MD5加密
    //只能加密不能解密  hello -> abc123def456  每次都是
    //hello  + 3e4a8 -> abc123def456abc  提高安全性

    public static String md5(String key){
        if (StringUtils.isBlank(key)){
            return null;
        }else {
            return DigestUtils.md5DigestAsHex(key.getBytes());
        }
    }
}
