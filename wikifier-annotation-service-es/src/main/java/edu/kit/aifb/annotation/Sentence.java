package edu.kit.aifb.annotation;

import java.util.ArrayList;
import java.util.List;

public class Sentence {

	private int id;
	private String text;
	private List<Token> tokens;

	public Sentence(String text) {
		this.text = text;
	}

	public Sentence(String text, int id) {
		this.text = text;
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void addToken(Token token) {
		if (tokens == null) {
			tokens = new ArrayList<Token>();
		}
		tokens.add(token);
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Sentence) {
			Sentence sentence = (Sentence) obj;
			if (sentence.getId() == this.id)
				return true;
			else
				return false;
		}
		return false;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + id;
		return result;
	}

	public String toString() {
		return id + ":" + text;
	}

}
