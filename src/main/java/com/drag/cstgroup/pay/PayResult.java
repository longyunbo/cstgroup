package com.drag.cstgroup.pay;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.drag.cstgroup.pay.common.StreamUtil;

/**
 * 接收支付结果
 */
public class PayResult{
	private static final Logger L = Logger.getLogger(PayResult.class);
       
	public static String payResult(HttpServletRequest request) {
		try {
			String reqParams = StreamUtil.read(request.getInputStream());
			L.info("-------支付结果:" + reqParams);
			StringBuffer sb = new StringBuffer("<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			L.error("-------", e);
		}
		return "";
	}

}
