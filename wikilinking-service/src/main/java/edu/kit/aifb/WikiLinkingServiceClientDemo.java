package edu.kit.aifb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

public class WikiLinkingServiceClientDemo {

	public final static String PARAM_PAGE_ID = "id";
	public final static String PARAM_LANGUAGE = "lang";

	public static void main(String[] args) throws HttpException, IOException {
		String serviceURL = "http://km.aifb.kit.edu/services/wikiLinking/";

		String language = "en";
		int pageId = 8210131;
//		String language = "de";
//		int pageId = 740797;
//		String language = "es";
//		int pageId = 892660;
		
		String[] paramName = { PARAM_PAGE_ID, PARAM_LANGUAGE };
		String[] paramValue = { String.valueOf(pageId), language };

		String httpPostResponse = httpPost(serviceURL, paramName, paramValue);
		System.out.println("Http Post Response:");
		System.out.println(httpPostResponse);
	}

	public static String httpPost(String urlRequest, String[] paramName, String[] paramVal) {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(urlRequest);
		InputStream rstream = null;
		// Add POST parameters
		for (int i = 0; i < paramName.length; i++) {
			method.addParameter(paramName[i], paramVal[i]);
		}
		// Send POST request
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			// Get the response body
			rstream = method.getResponseBodyAsStream();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return getResponse(rstream);
	}

	protected static String getResponse(InputStream rstream) {
		String response = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				response += line + "\n";
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
