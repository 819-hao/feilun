package com.seeease.flywheel.web.common.util;

import com.taobao.api.Constants;
import com.taobao.api.internal.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Tiro
 * @date 2023/10/13
 */
@Slf4j
public class TaoBaoSignUtil {

    /**
     * @param params
     * @param secret
     * @param signMethod
     * @return
     * @throws IOException
     */
    public static String signTopRequest(Map<String, String> params, String requestBody, String secret, String signMethod) {
        try {
            // 第一步：检查参数是否已经排序
            String[] keys = params.keySet().toArray(new String[0]);
            Arrays.sort(keys);

            // 第二步：把所有参数名和参数值串在一起
            StringBuilder query = new StringBuilder();
            if (Constants.SIGN_METHOD_MD5.equals(signMethod)) { //签名的摘要算法，可选值为：hmac，md5，hmac-sha256
                query.append(secret);
            }
            for (String key : keys) {
                //过滤sign
                if (key.equals("sign")) {
                    continue;
                }
                String value = params.get(key);
                if (StringUtils.areNotEmpty(key, value)) {
                    query.append(key).append(value);
                }
            }
            //连接request body
            query.append(requestBody);
            // 第三步：使用MD5/HMAC加密
            byte[] bytes;
            if (Constants.SIGN_METHOD_HMAC.equals(signMethod)) {
                bytes = encryptHMAC(query.toString(), secret);
            } else {
                query.append(secret);
                bytes = encryptMD5(query.toString());
            }
            // 第四步：把二进制转化为大写的十六进制(正确签名应该为32大写字符串，此方法需要时使用)
            return byte2hex(bytes);
        } catch (Exception e) {
            log.error("闲鱼签名计算异常：{}", e.getMessage(), e);
            return org.apache.commons.lang3.StringUtils.EMPTY;
        }
    }

    private static byte[] encryptHMAC(String data, String secret) throws IOException {
        byte[] bytes = null;
        try {
            SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacMD5");
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    private static byte[] encryptMD5(String data) throws IOException {
        return encryptMD5(data.getBytes(Constants.CHARSET_UTF8));
    }

    private static byte[] encryptMD5(byte[] data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(data);
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse.toString());
        }
        return bytes;
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

}
