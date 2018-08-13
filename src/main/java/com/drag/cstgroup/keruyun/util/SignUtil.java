package com.drag.cstgroup.keruyun.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.drag.cstgroup.utils.HttpsUtil;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class SignUtil {
	
	private static String keruyunurl;
	private static String appKey;
	private static String shopIdenty;
	private static String version;
	private static String secretKey;
	
	@Value("${keruyun.url.url}")
    public void setKeruyunurl(String value) {
		keruyunurl = value;
    }
	@Value("${keruyun.url.appKey}")
    public void seAppKey(String value) {
		appKey = value;
    }
	@Value("${keruyun.url.shopIdenty}")
    public void setShopIdenty(String value) {
		shopIdenty = value;
    }
	@Value("${keruyun.url.version}")
    public void setVersion(String value) {
		version = value;
    }
	@Value("${keruyun.url.secretKey}")
    public void setSecretKey(String value) {
		secretKey = value;
    }
	
	 /**
     * 获取Token
     * @return
     */
    public static String getToken(){
    	Date timestamp = new Date(System.currentTimeMillis());
    	String sign = SignUtil.signForToken();
    	String requestUrl = String.format("%s/open/v1/token/get?appKey=%s&shopIdenty=%s&version=%s&timestamp=%s&sign=%s",keruyunurl,appKey,shopIdenty,version,timestamp.getTime(),sign);
        JSONObject resultJson =null;
        JSONObject resultJs = null;
        String result = HttpsUtil.httpsRequest(requestUrl, "GET", null);
         try {
             resultJson = JSON.parseObject(result);
             String errmsg = (String) resultJson.get("errmsg");
             if(!"".equals(errmsg) && errmsg != null){
                 log.error("获取access_token失败："+errmsg);
                 return "error";
             }
             resultJs = (JSONObject) resultJson.get("result");
         } catch (JSONException e) {
             e.printStackTrace();
         }
         return (String) resultJs.get("token");
    }
	
    /**
     * 获取signForToken
     * @return
     */
    public static String signForToken() {
		Date timestamp = new Date(System.currentTimeMillis());
		Map<String, Object> params = new TreeMap<>();
		params.put("appKey", appKey);
		params.put("shopIdenty", shopIdenty);
		params.put("version", version);
		params.put("timestamp", timestamp.getTime());
		StringBuilder sortedParams = new StringBuilder();
		params.entrySet().stream().forEachOrdered(paramEntry -> sortedParams.append(paramEntry.getKey()).append(paramEntry.getValue()));
		sortedParams.append(secretKey);//请替换成真实的secretKey
		String SHA256Sign = null;
		try {
			SHA256Sign = SignUtil.getSign(sortedParams.toString());
		} catch (NoSuchAlgorithmException e) {
			log.info("获取签名出错" + e.getMessage(), e);
		}
		return SHA256Sign;
	}
    
    /**
     * 获取签名
     * @return
     */
    public static String getSign(){
    	String sign = "";
    	Date timestamp = new Date(System.currentTimeMillis());
    	Map<String, Object> params = new TreeMap<>();
		params.put("appKey", appKey);
		params.put("shopIdenty", shopIdenty);
		params.put("version", version);
		params.put("timestamp", timestamp.getTime());
		StringBuilder sortedParams = new StringBuilder();
		params.entrySet().stream().forEachOrdered(paramEntry -> sortedParams.append(paramEntry.getKey()).append(paramEntry.getValue()));
		String token = SignUtil.getToken();
		sortedParams.append(token);//请替换成真实的token
		System.out.println(sortedParams);
		try {
			sign = SignUtil.getSign(sortedParams.toString());
//			System.out.println(sign + "       " + sign.length());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return sign;
    }
    
    /**
	 * 获取公共参数
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getComParam() {
		Date timestamp = new Date(System.currentTimeMillis());
		String sign = SignUtil.getSign();
		String requestUrl = String.format("appKey=%s&shopIdenty=%s&version=%s&timestamp=%s&sign=%s",appKey,shopIdenty,version,timestamp.getTime(),sign);
		return requestUrl;
	}	
	
	/**
	 * @Description: SHA256加密字符串
	 * @param
	 * @return String
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSign(String sortedParams) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(sortedParams.getBytes());
		byte byteBuffer[] = messageDigest.digest();
		StringBuffer strHexString = new StringBuffer();
		for (int i = 0; i < byteBuffer.length; i++) {
			String hex = Integer.toHexString(0xff & byteBuffer[i]);
			if (hex.length() == 1) {
				strHexString.append('0');
			}
			strHexString.append(hex);
		}
		// 得到返回結果
		String SHA256Sign = strHexString.toString();
		return SHA256Sign;
	}
}
