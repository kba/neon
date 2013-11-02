/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
 * 
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neon.resources.quest;

import java.util.Collection;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A conversation tree. Before adding {@code Topic}s to a conversation, a root 
 * topic must be present.
 * 
 * @author mdriesen
 */
public class Conversation {
	/** The resource ID of the quest this conversation belongs to. */
	public final String questID;
	public final String id;
	
	// vieze manier om een tree structuur te maken met een multimap
	private Multimap<Topic, Topic> topics = ArrayListMultimap.create();
	private Topic root;
	
	/**
	 * Initializes an empty conversation.
	 * 
	 * @param questID	the resource ID of the quest this conversation belongs to
	 */
	public Conversation(String questID, String id) {
		this.questID = questID;
		this.id = id;
	}
	
	/**
	 * Sets the root topic of this conversation.
	 * 
	 * @param topic
	 */
	public void setRootTopic(Topic topic) {
		root = topic;
	}
	
	/**
	 * Adds a topic to this conversation.
	 * 
	 * @param parent
	 * @param child
	 * @throws IllegalArgumentException	if the parent topic does not exist
	 */
	public void addSubTopic(Topic parent, Topic child) throws IllegalArgumentException {
		if(topics.values().contains(parent) || parent.equals(root)) {
			topics.put(parent, child);			
		} else {
			throw new IllegalArgumentException("Parent topic not found!");
		}
	}
	
	/**
	 * @return	the root topic
	 */
	public Topic getRootTopic() {
		return root;
	}
	
	/**
	 * @param parent
	 * @return	all child topics of the parent
	 */
	public Collection<Topic> getTopics(Topic parent) {
		return topics.get(parent);
	}
}
