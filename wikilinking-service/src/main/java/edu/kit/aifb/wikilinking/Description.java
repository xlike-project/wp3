package edu.kit.aifb.wikilinking;

import edu.kit.aifb.nlp.Language;

public class Description {
	
	private String url;
	private Language lang;
	
	public Description(String url, Language lang) {
		this.url = url;
		this.lang = lang;
	} 
	
	public void setLanguage(Language lang) {
		this.lang = lang;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
	
	public Language getLanguage() {
		return lang;
	}
	
	public String getURL() {
		return url; 
	}
	
	public String toString() {
		return url;
	}

}
