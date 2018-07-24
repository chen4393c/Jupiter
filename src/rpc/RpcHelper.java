package rpc;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class RpcHelper {
	/*
	 * Write a JSONObject to http response
	 * */
	public static void writeJSONObject(HttpServletResponse response, JSONObject obj) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(obj);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Write a JSONArray to http response 
	 * */
	public static void writeJSONArray(HttpServletResponse response, JSONArray array) {
		try {
			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print(array);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Parse HTTP request body into a JSONObject.
     * The input HTTP request looks like:
     * {
     *     "user_id": "1111",
     *     "favorite": [
     *          "abcd",
     *     ]
     * }
     * */
    public static JSONObject parseHttpRequest(HttpServletRequest request) {
        StringBuilder jsonBuilder = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            return new JSONObject(jsonBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
