package edu.kit.aifb.annotation;

import java.util.ArrayList;
import java.util.List;

import edu.kit.aifb.nlp.Language;

public class WikiAnnotation implements Comparable<WikiAnnotation> {

	private int id;
	private String wikiArticle;
	private String url;
	private Language lang;
	private int pageLen;
	private List<Mention> mentions;

	public WikiAnnotation(int id, String WikipediaArticle) {
		this.id = id;
		this.wikiArticle = WikipediaArticle;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setWikiArticle(String WikipediaArticle) {
		this.wikiArticle = WikipediaArticle;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setLanguage(Language lang) {
		this.lang = lang;
	}

	public void setPageLength(int len) {
		this.pageLen = len;
	}

	public void addMention(Mention mention) {
		if (mentions == null) {
			mentions = new ArrayList<Mention>();
		}
		mentions.add(mention);
	}

	public int getId() {
		return id;
	}

	public String getURL() {
		return url;
	}

	public Language getLanguage() {
		return lang;
	}

	public float getPageLength() {
		return pageLen;
	}

	public List<Mention> getMentions() {
		return mentions;
	}

	public String getWikiArticle() {
		return wikiArticle;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof WikiAnnotation) {
			WikiAnnotation anno = (WikiAnnotation) obj;
			if (anno.getId() == id && anno.getPageLength() == pageLen) 
				return true;
			else
				return false;
		}
		return false;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + wikiArticle.hashCode();
		return result;
	}

	public int compareTo(WikiAnnotation anno) {
		if (equals(anno))
			return 0;
		if (this.pageLen >= anno.getPageLength())
			return 1;
		else
			return -1;
	}

	public String toString() {
		return id + ":" + wikiArticle + ":[" + mentions + "]";
	}

}
