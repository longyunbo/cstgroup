package com.drag.cstgroup.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CertUtil {

	public static void main(String[] args) {
		CertUtil util = new CertUtil();
		util.test();
	}
	
	public static void test() {
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			//读取本机存放的PKCS12证书文件
			FileInputStream instream = new FileInputStream(new File("/Users/longyunbo/Downloads/cert/apiclient_cert.p12"));
			try {
			//指定PKCS12的密码(商户ID)
			keyStore.load(instream, "1441830302".toCharArray());
			} finally {
			instream.close();
			}
			SSLContext sslcontext = SSLContexts.custom()
			.loadKeyMaterial(keyStore, "1441830302".toCharArray()).build();
			//指定TLS版本
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
			sslcontext,new String[] { "TLSv1" },null,
			SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
			//设置httpclient的SSLSocketFactory
			CloseableHttpClient httpclient = HttpClients.custom()
			.setSSLSocketFactory(sslsf)
			.build();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}
}
