package com.drag.cstgroup.pay;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drag.cstgroup.pay.common.Configure;
import com.drag.cstgroup.pay.common.HttpRequest;
import com.drag.cstgroup.pay.common.RandomStringGenerator;
import com.drag.cstgroup.pay.common.Signature;
import com.drag.cstgroup.pay.model.OrderInfo;
import com.drag.cstgroup.pay.model.OrderReturnInfo;
import com.drag.cstgroup.utils.IpUtils;
import com.thoughtworks.xstream.XStream;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一下单接口
 */
@Slf4j
@Service
public class Xiadan {
	private static final Logger L = Logger.getLogger(Xiadan.class);

	public static JSONObject wxPay(HttpServletRequest request,String openid,int price) {
		JSONObject json = new JSONObject();
		try {
			String out_trade_no = RandomStringGenerator.getRandomStringByLength(32);
			OrderInfo order = new OrderInfo();
			order.setAppid(Configure.appid);
			order.setMch_id(Configure.mch_id);
			order.setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
			order.setBody("星旅-付款");
			order.setOut_trade_no(out_trade_no);
			order.setTotal_fee(price);
			order.setSpbill_create_ip(IpUtils.getIpAddr(request));
			order.setNotify_url("https://zlj.wisdomdr.com/cstgroup/pay/payresult");
			order.setTrade_type("JSAPI");
			order.setOpenid(openid);
			order.setSign_type("MD5");
			// 生成签名
			String sign = Signature.getSign(order);
			order.setSign(sign);

			String result = HttpRequest.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", order);
			System.out.println(result);
			L.info("---------下单返回:" + result);
			XStream xStream = new XStream();
			xStream.alias("xml", OrderReturnInfo.class);

			OrderReturnInfo returnInfo = (OrderReturnInfo) xStream.fromXML(result);
			returnInfo.setOut_trade_no(out_trade_no);
			JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(returnInfo));
			return itemJSONObj;
		} catch (Exception e) {
			e.printStackTrace();
			L.error("-------", e);
		}
		return json;

	}

}
