package edu.kit.aifb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;

public class AnnotationServiceClientDemo {

	public static void main(String[] args) throws HttpException, IOException {
		// Target URL
		String strURL = "http://km.aifb.kit.edu/services/annotation-de/";
		
		// XML to be posted
		String strXML = "<item><sentences><sentence id=\"1\"><text>Vorfeld von Olympia hat nun auch Tino Sehgal wieder zugeschlagen.</text></sentence></sentences></item>";		InputStream strXMLStream=new ByteArrayInputStream(strXML.getBytes());
		
		// Prepare HTTP post
		PostMethod post = new PostMethod(strURL);
		post.setRequestEntity(new InputStreamRequestEntity(strXMLStream, strXML.length()));
		post.setRequestHeader("Content-type", "text/xml; charset=UTF-8");

		// HTTP client
		HttpClient httpclient = new HttpClient();

		// // Execute request
		try {
			int result = httpclient.executeMethod(post);
			System.out.println("Response status code: " + result);

			System.out.println("Response body: ");
			System.out.println(post.getResponseBodyAsString());
		} finally {
			post.releaseConnection();
		}
	}
}
