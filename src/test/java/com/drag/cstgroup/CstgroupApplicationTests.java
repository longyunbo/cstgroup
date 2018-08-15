package com.drag.cstgroup;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.drag.cstgroup.scoremall.service.OrderService;

import antlr.StringUtils;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CstgroupApplicationTests {

//	/**
//	 * @Description: 获取token时签名验证（只在获取token时调用一次）
//	 * @param
//	 * @throws
//	 */
//	public void signForToken() {
//			Map<String, Object> params = new TreeMap<>();
//			params.put("appKey", "301001");
//			params.put("shopIdenty", 247900001);
//			params.put("version", "1.0");
//			params.put("timestamp", 1425635264);
//			StringBuilder sortedParams = new StringBuilder();
//			params.entrySet().stream().forEachOrdered(paramEntry -> sortedParams.append(paramEntry.getKey()).append(paramEntry.getValue()));
//			sortedParams.append("secretKey");//请替换成真实的secretKey
//			String SHA256Sign = null;
//			try {
//				SHA256Sign = getSign(sortedParams.toString());
//			} catch (NoSuchAlgorithmException e) {
//				log.info("获取签名出错" + e.getMessage(), e);
//			}
//			if (!StringUtils.equals(sign, SHA256Sign)) {// 签名校验
//				String msg = String.format("sign=%s", sign);
//				System.out.println("签名校验不通过" + msg);
//
//	        }
//		}
	/**
	* 普通接口加密，获取到token之后
	**/
	public static void main(String[] args) {
		Map<String, Object> params = new TreeMap<>();
		params.put("appKey", "3e334f0d4f76c2eb47f5c142d1108787");
		params.put("shopIdenty", 810094162);
		params.put("version", "1.0");
		params.put("timestamp", "1534335861192");
		StringBuilder sortedParams = new StringBuilder();
		params.entrySet().stream().forEachOrdered(paramEntry -> sortedParams.append(paramEntry.getKey()).append(paramEntry.getValue()));
		sortedParams.append("6a74fce91ff60579e30891fea011a224");//请替换成真实的token
		System.out.println(sortedParams);
		try {
			String sign = getSign(sortedParams.toString());
			System.out.println(sign + "       " + sign.length());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Date d = new Date(System.currentTimeMillis());
		System.out.println(d.getTime());
	}

		/**
	 	* @Description: SHA256加密字符串
		 * @param
		 * @return String
		 * @throws NoSuchAlgorithmException
		 */
	private static String getSign(String sortedParams) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(sortedParams.getBytes());
        byte byteBuffer[] = messageDigest.digest();
        StringBuffer strHexString = new StringBuffer();
        for (int i = 0; i < byteBuffer.length; i++){
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
