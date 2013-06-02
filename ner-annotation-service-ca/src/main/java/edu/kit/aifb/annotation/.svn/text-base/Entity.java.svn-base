package edu.kit.aifb.annotation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Entity implements Comparable<Entity> {

	private int id;
	private String type;
	private Set<Mention> mentions;

	public Entity(int id) {
		this.id = id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void addMention(Mention mention) {
		if (mentions == null) {
			mentions = new LinkedHashSet<Mention>();
		}
		mentions.add(mention);
	}

	public Set<Mention> getMentions() {
		return mentions;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof Entity) {
			Entity entity = (Entity) obj;
			if (entity.getId() == this.id) // entity.getDisplayName().equals(this.displayName)
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

	public int compareTo(Entity entity) {
		if (equals(entity))
			return 0;
		if (this.id > entity.getId())
			return 1;
		else
			return -1;
	}

	public String toString() {
		return id + ":" + type;
	}

}
