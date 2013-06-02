package edu.kit.aifb.annotation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.kit.aifb.nlp.Language;

public class WikifierService {

	private static Logger logger = Logger.getLogger(AnnotationServlet.class);

	private final static String RESULT_TAG = "Result";
	private final static String DETECTEDTOPIC_TAG = "DetectedTopic";
	private final static String DETECTEDTOPIC_ID_TAG = "id";
	private final static String DETECTEDTOPIC_TITLE_TAG = "title";
	private final static String DETECTEDTOPIC_WEIGHT_TAG = "weight";

	// Wikifier web service configuration information
	private final static String SERVICE_URL = "http://km.aifb.kit.edu/services/wpmservlet-en/web/service";
//	private final static String SERVICE_URL = "http://aifb-ls3-calc.aifb.uni-karlsruhe.de:8080/wpmservlet-en/web/service";
	private final static String PARAM_TASK = "task";
	private final static String DEFAULT_TASK = "wikify";
	private final static String PARAM_XML = "xml";
	private final static String DEFAULT_XML = "true";
	private final static String PARAM_MINWEIGHT = "minProbability";
	private final static String DEFAULT_MINWEIGHT = "0";
	private final static String PARAM_REPEATMODE = "repeatMode";
	private final static String DEFAULT_REPEATMODE = "0";
	private final static String PARAM_SOURCE = "source"; // text

	private final static String DELIMITER = " {{|}} ";
	private final static String DELIMITER_REGEX = "\\{\\{\\|\\}\\}";
	private final static String WIKI_URL = ".wikipedia.org/wiki/";
	public static final Pattern PATTERN_ANNOTATION = Pattern
			.compile("\\[\\[([\\p{L}\\p{N}][^\\[\\]]*[\\p{L}\\p{N}])\\]\\]");

	public static Map<Mention, WikiAnnotation> getWikiAnnotations(List<String> sentences, Set<WikiAnnotation> annoSet)
			throws SAXException, IOException, ParserConfigurationException {
		String[] paramName = { PARAM_TASK, PARAM_XML, PARAM_MINWEIGHT, PARAM_REPEATMODE, PARAM_SOURCE };
		String text = addSentenceDelimiter(sentences);
		logger.debug("text: " + text);
		String[] paramValue = { DEFAULT_TASK, DEFAULT_XML, DEFAULT_MINWEIGHT, DEFAULT_REPEATMODE, text };
		String xmlResponse = httpPost(SERVICE_URL, paramName, paramValue);
		logger.debug("wikifier output: " + xmlResponse);
		Document wikifierDoc = readXML(xmlResponse);
		Map<String, WikiAnnotation> annoMap = extractAnnotations(wikifierDoc);
		Map<Mention, WikiAnnotation> mentionAnnoMap = alignAnnotations(wikifierDoc, annoMap);
		if (annoSet == null)
			annoSet = new HashSet<WikiAnnotation>();
		annoSet.addAll(mentionAnnoMap.values());
		logger.debug("wiki annotation: " + annoMap);
		return mentionAnnoMap;
	}

	protected static String addSentenceDelimiter(List<String> sentences) {
		StringBuilder text = new StringBuilder();
		for (String sentence : sentences) {
			text.append(sentence + DELIMITER);
		}
		return text.toString();
	}

	protected static Document readXML(String xml) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
		return doc;
	}

	protected static Map<Mention, WikiAnnotation> alignAnnotations(Document doc, Map<String, WikiAnnotation> annoMap) {
		Map<Mention, WikiAnnotation> mentionAnnoMap = new HashMap<Mention, WikiAnnotation>();
		Node ResultNode = doc.getElementsByTagName(RESULT_TAG).item(0);
		String result = ResultNode.getFirstChild().getNodeValue();
		logger.debug("result: " + result);
		String[] sentences = result.split(DELIMITER_REGEX);
		for (int i = 0; i < sentences.length; i++) {
			Matcher matcher = PATTERN_ANNOTATION.matcher(sentences[i]);
			logger.debug("sentence " + i + ": " + sentences[i]);
			while (matcher.find()) {
				String matchs = matcher.group();
				matchs = matchs.substring(2, matchs.length() - 2);
				logger.debug("annotation matchs: " + matchs);
				String[] match = matchs.split("\\|");
				Mention mention = new Mention(i + 1);
				if (match.length == 1) {
					mention.setWords(match[0]);
				} else if (match.length == 2) {
					mention.setWords(match[1]);
				} else
					continue;
				String wikiArticle = match[0];
				WikiAnnotation anno = annoMap.get(wikiArticle.toLowerCase());
				anno.addMention(mention);
				mentionAnnoMap.put(mention, anno);
			}
		}
		return mentionAnnoMap;
	}

	protected static Map<String, WikiAnnotation> extractAnnotations(Document doc) {
		Map<String, WikiAnnotation> annoMap = new HashMap<String, WikiAnnotation>();

		NodeList topicNodeList = doc.getElementsByTagName(DETECTEDTOPIC_TAG);
		int i = 0, topiclength = topicNodeList.getLength();
		while (i < topiclength) {
			Element topicEle = (Element) topicNodeList.item(i++);
			int id = Integer.valueOf(topicEle.getAttribute(DETECTEDTOPIC_ID_TAG));
			String title = topicEle.getAttribute(DETECTEDTOPIC_TITLE_TAG);
			float weight = Float.valueOf(topicEle.getAttribute(DETECTEDTOPIC_WEIGHT_TAG));
			WikiAnnotation anno = new WikiAnnotation(id, title);
			anno.setWeight(weight);
			anno.setLanguage(Language.EN);
			anno.setURL("http://" + Language.EN.toString() + WIKI_URL + title.replace(" ", "_"));
			annoMap.put(title.toLowerCase(), anno);
		}
		return annoMap;
	}

	protected static String httpPost(String urlRequest, String[] paramName, String[] paramVal) {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(urlRequest);
		InputStream rstream = null;
		String response = "";
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
			BufferedReader br = new BufferedReader(new InputStreamReader(rstream));
			String line;
			while ((line = br.readLine()) != null) {
				response += line;
			}
			br.close();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		
		return response;
	}

}
