package edu.kit.aifb.annotation;

public class Mention {

	private String words;
	private int sentenceId;

	// private int tokenId;

	public Mention(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	public Mention(String words) {
		this.words = words;
	}

	public Mention(int sentenceId, String words) {
		this.sentenceId = sentenceId;
		this.words = words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}

	public String getWords() {
		return this.words;
	}

	public int getSentenceId() {
		return this.sentenceId;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Mention) {
			Mention mention = (Mention) obj;
			if (mention.getSentenceId() == this.sentenceId && mention.getWords().equals(this.words))
				return true;
			else
				return false;
		}
		return false;
	}

	public int hashCode() {
		int result = 17;
		result = 37 * result + sentenceId;
		result = 37 * result + words.hashCode();
		return result;
	}

	public String toString() {
		return sentenceId + ":" + words;
	}

}
