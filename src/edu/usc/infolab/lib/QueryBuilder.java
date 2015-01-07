package edu.usc.infolab.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class QueryBuilder {

	private static final String BASE_URL = "http://128.125.163.86/TDSP_Servlet/TDSPQuerySuper6";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	private static String getURL(GeoPoint start, GeoPoint end, int time,
			String day) {
		String url = String
				.format("%s?start=%s&end=%s&time=%d&update=False1&day=%s&Carpool=False",
						BASE_URL, start.toString(), end.toString(), time, day);
		return url;
	}

	public static String getResult(GeoPoint start, GeoPoint end, int time,
			String day) throws IllegalStateException, IOException {
		String result = null;
		String url = getURL(start, end, time, day);
		HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);
		HttpResponse response = client.execute(request);
		BufferedReader br = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		result = br.readLine();
		return result;
	}
}
