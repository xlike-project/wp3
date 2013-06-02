package edu.kit.aifb.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.kit.aifb.concept.IConceptDescription;
import edu.kit.aifb.nlp.Language;

/**
 * Servlet implementation class ConfigurationServlet
 */
public class AnnotationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AnnotationServlet.class);

	private final static String ITEM_TAG = "item";

	private final static String SENTENCE_TAG = "sentence";
	private final static String TEXT_TAG = "text";

	private final static String ANNOS_TAG = "annotations";
	private final static String ANNO_TAG = "annotation";
	private final static String ANNO_URL_TAG = "URL";
	private final static String ANNO_DISPLAYNAME_TAG = "displayName";
	private final static String ANNO_LANG_TAG = "lang";
	private final static String ANNO_WEIGHT_TAG = "weight";
	private final static String ANNO_MENTIONS_TAG = "mentions";
	private final static String ANNO_MENTION_TAG = "mention";
	private final static String ANNO_SENTENCE_ID_TAG = "sentenceId";
	private final static String ANNO_WORDS_TAG = "words";
	private final static String ANNO_ID_TAG = "id";
	private final static String ANNO_DESCRIPTIONS_TAG = "descriptions";
	private final static String ANNO_DESCRIPTION_TAG = "description";

	private final static String WIKI_URL = ".wikipedia.org/wiki/";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			Document doc = readXML(request);
			List<String> sentences = extractSentences(doc);
			logger.debug("sentences: " + sentences);

			TreeSet<WikiAnnotation> annoSet = new TreeSet<WikiAnnotation>();
			Map<Mention, WikiAnnotation> mention2anno = WikifierService.getWikiAnnotations(sentences, annoSet);
			logger.debug("mention -> annotation: " + mention2anno);

			doc = addWikipediaAnnotations(doc, annoSet);
			writeXML(doc, response);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	public Document readXML(HttpServletRequest request) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(request.getInputStream());
		return doc;
	}

	public List<String> extractSentences(Document doc) {
		List<String> sentences = new ArrayList<String>();
		NodeList sentenceNodeList = doc.getElementsByTagName(SENTENCE_TAG);
		int i = 0, sentencelength = sentenceNodeList.getLength();
		logger.debug("number of sentences: " + sentencelength);
		while (i < sentencelength) {
			Element sentenceEle = (Element) sentenceNodeList.item(i++);
			NodeList textNodeList = sentenceEle.getElementsByTagName(TEXT_TAG);
			logger.debug("number of texts: " + textNodeList.getLength());
			String sentence = textNodeList.item(0).getFirstChild().getNodeValue();
			sentences.add(sentence);
		}
		return sentences;
	}

	public void extractDescriptions(int pageId, List<Description> descriptions) {
		ServletContext servletContext = getServletContext();
		IConceptDescription desc = (IConceptDescription) servletContext.getAttribute("description_service");
		if (desc == null) {
			ApplicationContext context = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());
			desc = (IConceptDescription) context.getBean("wikipedia_concept_description_de");
			servletContext.setAttribute("description_service", desc);
		}
		try {
			String title_en = desc.getDescription(String.valueOf(pageId), Language.EN);
			if (title_en != null) {
				String url = "http://" + Language.EN.toString() + WIKI_URL + title_en.replace(" ", "_");
				descriptions.add(new Description(url, Language.EN));
			}
			String title_es = desc.getDescription(String.valueOf(pageId), Language.ES);
			if (title_es != null) {
				String url = "http://" + Language.ES.toString() + WIKI_URL + title_es.replace(" ", "_");
				descriptions.add(new Description(url, Language.ES));
			}
			String title_sl = desc.getDescription(String.valueOf(pageId), Language.SL);
			if (title_sl != null) {
				String url = "http://" + Language.SL.toString() + WIKI_URL + title_sl.replace(" ", "_");
				descriptions.add(new Description(url, Language.SL));
			}
			String title_zh = desc.getDescription(String.valueOf(pageId), Language.ZH);
			if (title_zh != null) {
				String url = "http://" + Language.ZH.toString() + WIKI_URL + title_zh.replace(" ", "_");
				descriptions.add(new Description(url, Language.ZH));
			}
			String title_ca = desc.getDescription(String.valueOf(pageId), Language.CA);
			if (title_ca != null) {
				String url = "http://" + Language.CA.toString() + WIKI_URL + title_ca.replace(" ", "_");
				descriptions.add(new Description(url, Language.CA));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document addWikipediaAnnotations(Document doc, TreeSet<WikiAnnotation> annoSet) {
		NodeList nodeList = doc.getElementsByTagName(ITEM_TAG);
		if (nodeList.getLength() == 0) {
			return doc;
		}
		Node item = nodeList.item(0);
		Node NodeAnnos = doc.createElement(ANNOS_TAG);
		WikiAnnotation wikiAnnotation = null;
		int id = 1;
		while ((wikiAnnotation = annoSet.pollLast()) != null) {
			Node NodeAnno = doc.createElement(ANNO_TAG);
			// attributes
			String wikiArticle = wikiAnnotation.getWikiArticle();
			int pageId = wikiAnnotation.getId();
			float weight = wikiAnnotation.getWeight();

			NamedNodeMap AnnoAttrs = NodeAnno.getAttributes();
			Attr attrWikiArticle = doc.createAttribute(ANNO_DISPLAYNAME_TAG);
			attrWikiArticle.setValue(wikiArticle);
			AnnoAttrs.setNamedItem(attrWikiArticle);

			Attr attrWeight = doc.createAttribute(ANNO_WEIGHT_TAG);
			attrWeight.setValue(String.valueOf(weight));
			AnnoAttrs.setNamedItem(attrWeight);

			Attr attrEntityId = doc.createAttribute(ANNO_ID_TAG);
			attrEntityId.setValue(String.valueOf(id++));
			AnnoAttrs.setNamedItem(attrEntityId);

			// descriptions node
			List<Description> descriptions = new ArrayList<Description>();
			descriptions.add(new Description(wikiAnnotation.getURL(), wikiAnnotation.getLanguage()));
			extractDescriptions(pageId, descriptions);

			Node NodeDescriptions = doc.createElement(ANNO_DESCRIPTIONS_TAG);

			for (Description desc : descriptions) {
				Node NodeDesc = doc.createElement(ANNO_DESCRIPTION_TAG);
				String url = desc.getURL();
				Language lang = desc.getLanguage();

				NamedNodeMap DescAttrs = NodeDesc.getAttributes();

				Attr attrUrl = doc.createAttribute(ANNO_URL_TAG);
				attrUrl.setValue(url);
				DescAttrs.setNamedItem(attrUrl);

				Attr attrLang = doc.createAttribute(ANNO_LANG_TAG);
				attrLang.setValue(lang.toString());
				DescAttrs.setNamedItem(attrLang);

				NodeDescriptions.appendChild(NodeDesc);
			}

			NodeAnno.appendChild(NodeDescriptions);
			NodeAnnos.appendChild(NodeAnno);

			// mentions node
			Set<Mention> mentions = wikiAnnotation.getMentions();
			if (mentions == null || mentions.size() == 0)
				continue;
			Node NodeMentions = doc.createElement(ANNO_MENTIONS_TAG);

			for (Mention mention : mentions) {
				Node NodeMention = doc.createElement(ANNO_MENTION_TAG);
				int sentenceId = mention.getSentenceId();
				String words = mention.getWords();

				NamedNodeMap mentionAttrs = NodeMention.getAttributes();

				Attr attrSentenceId = doc.createAttribute(ANNO_SENTENCE_ID_TAG);
				attrSentenceId.setValue(String.valueOf(sentenceId));
				mentionAttrs.setNamedItem(attrSentenceId);

				Attr attrWords = doc.createAttribute(ANNO_WORDS_TAG);
				attrWords.setValue(words);
				mentionAttrs.setNamedItem(attrWords);

				NodeMentions.appendChild(NodeMention);
			}

			NodeAnno.appendChild(NodeMentions);
			NodeAnnos.appendChild(NodeAnno);
		}
		item.appendChild(NodeAnnos);
		return doc;
	}

	public void writeXML(Document doc, HttpServletResponse response) throws TransformerFactoryConfigurationError,
			TransformerException, IOException {
		response.setContentType("text/xml; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(response.getOutputStream());
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
	}

}
