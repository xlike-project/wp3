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
		String strURL = "http://km.aifb.kit.edu/services/annotation-es/";
		
		// XML to be posted
		String strXML = "<item><sentences><sentence id=\"1\"><text>Alcan es una de las mayores empresas del mundo dedicada a la producci¨®n de aluminio.</text><tokens><token pos=\"NP00O00\" end=\"5\" lemma=\"alcan\" id=\"1.1\" start=\"0\">Alcan</token><token pos=\"VSIP3S0\" end=\"8\" lemma=\"ser\" id=\"1.2\" start=\"6\">es</token><token pos=\"DI0FS0\" end=\"12\" lemma=\"uno\" id=\"1.3\" start=\"9\">una</token><token pos=\"SPS00\" end=\"15\" lemma=\"de\" id=\"1.4\" start=\"13\">de</token><token pos=\"DA0FP0\" end=\"19\" lemma=\"el\" id=\"1.5\" start=\"16\">las</token><token pos=\"AQ0CP0\" end=\"27\" lemma=\"mayor\" id=\"1.6\" start=\"20\">mayores</token><token pos=\"NCFP000\" end=\"36\" lemma=\"empresa\" id=\"1.7\" start=\"28\">empresas</token><token pos=\"SPS00\" end=\"38\" lemma=\"de\" id=\"1.8\" start=\"37\">de</token><token pos=\"DA0MS0\" end=\"40\" lemma=\"el\" id=\"1.9\" start=\"39\">el</token><token pos=\"NCMS000\" end=\"46\" lemma=\"mundo\" id=\"1.10\" start=\"41\">mundo</token><token pos=\"VMP00SF\" end=\"55\" lemma=\"dedicar\" id=\"1.11\" start=\"47\">dedicada</token><token pos=\"SPS00\" end=\"57\" lemma=\"a\" id=\"1.12\" start=\"56\">a</token><token pos=\"DA0FS0\" end=\"60\" lemma=\"el\" id=\"1.13\" start=\"58\">la</token><token pos=\"NCMS000\" end=\"69\" lemma=\"producci\" id=\"1.14\" start=\"61\">producci</token><token pos=\"Fz\" end=\"70\" lemma=\"¨\" id=\"1.15\" start=\"69\">¨</token><token pos=\"Fz\" end=\"71\" lemma=\"®\" id=\"1.16\" start=\"70\">®</token><token pos=\"NCFS000\" end=\"72\" lemma=\"n\" id=\"1.17\" start=\"71\">n</token><token pos=\"SPS00\" end=\"75\" lemma=\"de\" id=\"1.18\" start=\"73\">de</token><token pos=\"NCMS000\" end=\"84\" lemma=\"aluminio\" id=\"1.19\" start=\"76\">aluminio</token><token pos=\"Fp\" end=\"85\" lemma=\".\" id=\"1.20\" start=\"84\">.</token></tokens></sentence></sentences><entities><entity type=\"organization\" displayName=\"alcan\" id=\"1\"><mentions><mention SentenceId=\"1\" id=\"1.1\" words=\"Alcan\"></mention></mentions></entity></entities></item>";		
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
