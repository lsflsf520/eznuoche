package com.ujigu.exam.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Test {

	public static void main(String[] args) throws IOException {
		Map<String, String> cookieMap = new HashMap<>();
		cookieMap.put("zg_did", "%7B%22did%22%3A%20%221658f5ad9a8388-0eebfd5c8591ec-5e4b2519-1fa400-1658f5ad9a9ac3%22%7D");
		cookieMap.put("_uab_collina", "153570842584875885375778");
		cookieMap.put("_umdata", "ED82BDCEC1AA6EB9A886101184594F557DF85CC97CD36124F00AA97B6A655E1E367975C9454E6E89CD43AD3E795C914C9EC0E66A45C3B32173EF4386F0D07720");
		cookieMap.put("saveFpTip", "true");
		cookieMap.put("UM_distinctid", "1695ae6a79e3df-05b8afda1d5b53-5e4b2519-1fa400-1695ae6a79f1c4");
		cookieMap.put("acw_tc", "7ce8aa4315602245888882639e2cc49ad2a190ed4466e2f9a9e02c1e23");
		cookieMap.put("QCCSESSID", "1arf6fap89di0hbvnp7nfrcfp2");
		cookieMap.put("CNZZDATA1254842228", "319903403-1535705612-https%253A%252F%252Fwww.baidu.com%252F%7C1562661644");
		cookieMap.put("hasShow", "1");
		cookieMap.put("Hm_lvt_3456bee468c83cc63fb5147f119f1075", "1560924230,1562140516,1562315988,1562663969");
		cookieMap.put("Hm_lpvt_3456bee468c83cc63fb5147f119f1075", "1562664147");
		cookieMap.put("zg_de1d1a35bfa24ce29bbf2c7eb17e6c4f", "%7B%22sid%22%3A%201562663968901%2C%22updated%22%3A%201562664671019%2C%22info%22%3A%201562140515385%2C%22superProperty%22%3A%20%22%7B%7D%22%2C%22platform%22%3A%20%22%7B%7D%22%2C%22utm%22%3A%20%22%7B%7D%22%2C%22referrerDomain%22%3A%20%22www.baidu.com%22%2C%22cuid%22%3A%20%22e0d2664fe1c064a724f85831d192204c%22%7D");
				
		Document doc = Jsoup.connect("https://www.qichacha.com/search_index?key=%E7%81%AB%E9%BE%99%E6%9E%9C&ajaxflag=1&p=2&industrycode=A").cookies(cookieMap).get();
	
	    System.out.println(doc);
	
	}

}
