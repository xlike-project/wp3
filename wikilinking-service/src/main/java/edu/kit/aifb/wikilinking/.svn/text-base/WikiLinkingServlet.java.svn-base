package edu.kit.aifb.wikilinking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import edu.kit.aifb.concept.IConceptDescription;
import edu.kit.aifb.nlp.Language;

/**
 * Servlet implementation class ConfigurationServlet
 */
public class WikiLinkingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(WikiLinkingServlet.class);

	private final static String ANNO_URL_TAG = "URL";
	private final static String ANNO_LANG_TAG = "lang";
	private final static String ANNO_DESCRIPTIONS_TAG = "descriptions";
	private final static String ANNO_DESCRIPTION_TAG = "description";

	private final static String WIKI_URL = ".wikipedia.org/wiki/";

	public final static String PARAM_PAGE_ID = "id";
	public final static String PARAM_LANGUAGE = "lang";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			int pageId = 0;
			Language lang = null;
			
			@SuppressWarnings("unchecked")
			Map<String, String[]> parameterMap = request.getParameterMap();
			for (String parameter : parameterMap.keySet()) {
				if (parameter.equals(PARAM_LANGUAGE)) {
					lang = Language.getLanguage(parameterMap.get(PARAM_LANGUAGE)[0]);
				} else if (parameter.equals(PARAM_PAGE_ID)) {
					pageId = Integer.parseInt(parameterMap.get(PARAM_PAGE_ID)[0]);
				}
			}
			List<Description> descriptions = new ArrayList<Description>();
			extractDescriptions(pageId, descriptions, lang);
			writeXML(descriptions, response);
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

	public void extractDescriptions(int pageId, List<Description> descriptions, Language lang) {
		ServletContext servletContext = getServletContext();
		IConceptDescription desc = (IConceptDescription) servletContext.getAttribute("description_service_"
				+ lang.toString());
		if (desc == null) {
			ApplicationContext context = WebApplicationContextUtils
					.getRequiredWebApplicationContext(getServletContext());
			desc = (IConceptDescription) context.getBean("wikipedia_concept_description_" + lang.toString());
			servletContext.setAttribute("description_service_" + lang.toString(), desc);
		}
		try {
			String title_en = desc.getDescription(String.valueOf(pageId), Language.EN);
			if (title_en != null) {
				String url = "http://" + Language.EN.toString() + WIKI_URL + title_en.replace(" ", "_");
				descriptions.add(new Description(url, Language.EN));
			}
			String title_de = desc.getDescription(String.valueOf(pageId), Language.DE);
			if (title_de != null) {
				String url = "http://" + Language.DE.toString() + WIKI_URL + title_de.replace(" ", "_");
				descriptions.add(new Description(url, Language.DE));
			}
			String title_es = desc.getDescription(String.valueOf(pageId), Language.ES);
			if (title_es != null) {
				String url = "http://" + Language.ES.toString() + WIKI_URL + title_es.replace(" ", "_");
				descriptions.add(new Description(url, Language.ES));
			}
			String title_fr = desc.getDescription(String.valueOf(pageId), Language.FR);
			if (title_fr != null) {
				String url = "http://" + Language.FR.toString() + WIKI_URL + title_fr.replace(" ", "_");
				descriptions.add(new Description(url, Language.FR));
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

	public void writeXML(List<Description> descriptions, HttpServletResponse response)
			throws TransformerFactoryConfigurationError, TransformerException, IOException,
			ParserConfigurationException {
		response.setContentType("text/xml; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StreamResult result = new StreamResult(response.getOutputStream());
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

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
		doc.appendChild(NodeDescriptions);
		
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
	}

}
