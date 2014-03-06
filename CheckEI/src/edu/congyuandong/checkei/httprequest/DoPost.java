package edu.congyuandong.checkei.httprequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

public class DoPost {
	private static String url_up = "http://elib.hrbeu.edu.cn/CheckEI/doPost.jsp";
	private static final String TAG_MESSAGE = "message";
	JSONParser jsonParser = new JSONParser();

	public String RequestPost(String searchWord1) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("SearchWord1", searchWord1));
		String message = "";

		try {
			JSONObject json = jsonParser
					.makeHttpRequest(url_up, "POST", params);
			 message = json.getString(TAG_MESSAGE);
			System.out.println(message);
			return message;

		} catch (Exception e) {
			e.printStackTrace();
			return "SORRY";
		}
	}

}
