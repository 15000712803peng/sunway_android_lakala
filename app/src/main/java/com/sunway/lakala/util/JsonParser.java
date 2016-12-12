package com.sunway.lakala.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonParser {

	public static String ObjectToJsonStr(Object obj) {
		Gson gson = new Gson();
		return gson.toJson(obj).toString();
	}

	public static JSONObject ObjectToJson(Object obj) throws JSONException {
		Gson gson = new Gson();
		String str = gson.toJson(obj);
		return new JSONObject(str);
	}

	public static <T extends Object> String objectListToJsonStr(List<T> list)
			throws JSONException {
		JSONArray array = new JSONArray();
		if (list == null) {
			array.toString();
		}
		for (T obj : list)
			try {
				array.put(ObjectToJson(obj));
			} catch (JSONException e) {

				e.printStackTrace();
			}
		return array.toString();
	}
	public static <T extends Object> JSONArray objectListToJson(List<T> list)
			throws JSONException {
		JSONArray array = new JSONArray();
		if (list == null) {
			array.toString();
		}
		for (T obj : list)
			try {
				array.put(ObjectToJson(obj));
			} catch (JSONException e) {

				e.printStackTrace();
			}
		return array;
	}

	public static Object jsonToObject(String json, Class<?> cla) {
		try {
			if (json != null) {
				Gson gson = new Gson();
				return gson.fromJson(json, cla);
			}
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
