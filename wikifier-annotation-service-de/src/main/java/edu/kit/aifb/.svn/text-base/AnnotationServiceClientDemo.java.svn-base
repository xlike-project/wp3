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
		String strXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><item><sentences><sentence id=\"1\"><text>Vorfeld von Olympia hat nun auch Tino Sehgal wieder zugeschlagen.</text><tokens><token id=\"1.1\" pos=\"NN\" lemma=\"Vorfeld\" start=\"0\" end=\"7\">Vorfeld</token><token id=\"1.2\" pos=\"APPR\" lemma=\"von\" start=\"8\" end=\"11\">von</token><token id=\"1.3\" pos=\"NE\" lemma=\"Olympia\" start=\"12\" end=\"19\">Olympia</token><token id=\"1.4\" pos=\"VAFIN\" lemma=\"haben\" start=\"20\" end=\"23\">hat</token><token id=\"1.5\" pos=\"ADV\" lemma=\"nun\" start=\"24\" end=\"27\">nun</token><token id=\"1.6\" pos=\"ADV\" lemma=\"auch\" start=\"28\" end=\"32\">auch</token><token id=\"1.7\" pos=\"NE\" lemma=\"Tino\" start=\"33\" end=\"37\">Tino</token><token id=\"1.8\" pos=\"NE\" lemma=\"Sehgal\" start=\"38\" end=\"44\">Sehgal</token><token id=\"1.9\" pos=\"ADV\" lemma=\"wieder\" start=\"45\" end=\"51\">wieder</token><token id=\"1.10\" pos=\"VVPP\" lemma=\"zuschlagen\" start=\"52\" end=\"64\">zugeschlagen</token><token id=\"1.11\" pos=\"$.\" lemma=\".\" start=\"64\" end=\"65\">.</token></tokens></sentence></sentences><entities><entity id=\"1\" displayName=\"Olympia\" type=\"I-ORG\"><mentions><mention id=\"1.3\" sentenceId=\"1\" words=\"Olympia\"/></mentions></entity><entity id=\"2\" displayName=\"Tino\" type=\"I-PER\"><mentions><mention id=\"1.7\" sentenceId=\"1\" words=\"Tino\"/></mentions></entity><entity id=\"3\" displayName=\"Sehgal\" type=\"I-PER\"><mentions><mention id=\"1.8\" sentenceId=\"1\" words=\"Sehgal\"/></mentions></entity></entities></item>";		
		InputStream strXMLStream=new ByteArrayInputStream(strXML.getBytes());
		
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
