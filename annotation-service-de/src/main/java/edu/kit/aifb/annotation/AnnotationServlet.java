package edu.kit.aifb.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
	private final static String ENTITY_TAG = "entity";
	private final static String ENTITY_ID_TAG = "id";
	private final static String ENTITY_TYPE_TAG = "type";
	private final static String MENTION_TAG = "mention";
	private final static String MENTION_SENTENCE_ID_TAG = "sentenceId";
	private final static String MENTION_WORDS_TAG = "words";

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
	private final static String ANNO_ENTITY_ID_TAG = "entityId";
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
			Map<Entity, List<Mention>> entity2mentions = extractEntities(doc);
			logger.debug("entity -> mentions: " + entity2mentions);

			List<String> sentences = extractSentences(doc);
			logger.debug("sentences: " + sentences);

			HashSet<WikiAnnotation> annoSet = new HashSet<WikiAnnotation>();
			Map<Mention, WikiAnnotation> mention2anno = WikifierService.getWikiAnnotations(sentences, annoSet);
			logger.debug("mention -> annotation: " + mention2anno);

			Map<Entity, WikiAnnotation> entity2anno = alignEntityAnnotation(entity2mentions, mention2anno, annoSet);
			logger.debug("entity -> annotation: " + entity2anno);

			doc = addWikipediaAnnotations(doc, entity2anno);
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

	public Map<Entity, List<Mention>> extractEntities(Document doc) {
		Map<Entity, List<Mention>> entity2mentions = new TreeMap<Entity, List<Mention>>();

		NodeList entityNodeList = doc.getElementsByTagName(ENTITY_TAG);
		int i = 0, entitylength = entityNodeList.getLength();
		while (i < entitylength) {
			Element entityEle = (Element) entityNodeList.item(i++);
			String entityId = entityEle.getAttribute(ENTITY_ID_TAG);
			String entityType = entityEle.getAttribute(ENTITY_TYPE_TAG);
			Entity entity = new Entity(Integer.valueOf(entityId));
			entity.setType(entityType);
			List<Mention> mentions = entity2mentions.get(entity);
			if (mentions == null) {
				mentions = new ArrayList<Mention>();
				entity2mentions.put(entity, mentions);
			}
			NodeList mentionNodeList = entityEle.getElementsByTagName(MENTION_TAG);
			int j = 0, mentionslength = mentionNodeList.getLength();
			while (j < mentionslength) {
				Element mentionEle = (Element) mentionNodeList.item(j++);
				String sentenceId = mentionEle.getAttribute(MENTION_SENTENCE_ID_TAG);
				String words = mentionEle.getAttribute(MENTION_WORDS_TAG);
				Mention mention = new Mention(Integer.valueOf(sentenceId), words.trim());
				mentions.add(mention);
				entity.addMention(mention);
			}
		}
		return entity2mentions;
	}

	public Map<Entity, WikiAnnotation> alignEntityAnnotation(Map<Entity, List<Mention>> entity2mentions,
			Map<Mention, WikiAnnotation> mention2Anno, Set<WikiAnnotation> annoSet) {
		Map<Entity, WikiAnnotation> entity2anno = new TreeMap<Entity, WikiAnnotation>();
		for (Entity entity : entity2mentions.keySet()) {
			List<Mention> mentions = entity2mentions.get(entity);
			TreeSet<WikiAnnotation> entityAnnoSet = new TreeSet<WikiAnnotation>();
			for (Mention mention : mentions) {
				WikiAnnotation anno = mention2Anno.get(mention);
				if(anno != null)
					entityAnnoSet.add(anno);
			}

			if (entityAnnoSet.size() != 0) {
				WikiAnnotation anno = entityAnnoSet.last();
				entity2anno.put(entity, anno);
				annoSet.remove(anno);
			}
		}

		int id = entity2mentions.keySet().size();
		for (WikiAnnotation anno : annoSet) {
			Entity entity = new Entity(++id);
			for (Mention mention : anno.getMentions()) {
				entity.addMention(mention);
			}
			entity2anno.put(entity, anno);
		}

		return entity2anno;
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

	public Document addWikipediaAnnotations(Document doc, Map<Entity, WikiAnnotation> entity2anno) {
		NodeList nodeList = doc.getElementsByTagName(ITEM_TAG);
		if (nodeList.getLength() == 0) {
			return doc;
		}
		Node item = nodeList.item(0);
		Node NodeAnnos = doc.createElement(ANNOS_TAG);
		for (Entity entity : entity2anno.keySet()) {
			Node NodeAnno = doc.createElement(ANNO_TAG);
			WikiAnnotation wikiAnnotation = entity2anno.get(entity);
			if (wikiAnnotation == null)
				continue;

			// attributes
			String wikiArticle = wikiAnnotation.getWikiArticle();
			int pageId = wikiAnnotation.getId();
			int entityId = entity.getId();
			float weight = wikiAnnotation.getWeight();

			NamedNodeMap AnnoAttrs = NodeAnno.getAttributes();
			Attr attrWikiArticle = doc.createAttribute(ANNO_DISPLAYNAME_TAG);
			attrWikiArticle.setValue(wikiArticle);
			AnnoAttrs.setNamedItem(attrWikiArticle);

			Attr attrWeight = doc.createAttribute(ANNO_WEIGHT_TAG);
			attrWeight.setValue(String.valueOf(weight));
			AnnoAttrs.setNamedItem(attrWeight);

			Attr attrEntityId = doc.createAttribute(ANNO_ENTITY_ID_TAG);
			attrEntityId.setValue(String.valueOf(entityId));
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
			Set<Mention> mentions = entity.getMentions();
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
