package edu.kit.aifb.annotation;

public class Token {

	private String token;
	private int id;
	private int sentenceId;

	public Token(String token) {
		this.token = token;
	}

	public Token(String token, int id) {
		this.token = token;
		this.id = id;
	}

	public Token(String token, int id, int sentenceId) {
		this.token = token;
		this.id = id;
		this.sentenceId = sentenceId;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	public int getSentenceId() {
		return sentenceId;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Token) {
			Token token = (Token) obj;
			if (token.getId() == this.id && token.getSentenceId() == this.sentenceId)
				return true;
			else
				return false;
		}
		return false;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + id;
		result = 37 * result + sentenceId;
		return result;
	}

	public String toString() {
		return sentenceId + "." + id + ":" + token;
	}

}
