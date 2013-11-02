/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

import org.jdom2.Element;

/**
 * A single topic in a conversation branch.
 * 
 * @author mdriesen
 */
public class Topic {
	/** The resource ID of the quest this topic belongs to. */
	public final String questID;
	public final String conversationID;
	public final String id;		// unieke id string

	public String phrase;		// hetgeen de player zegt
	public String condition;	// script voorwaarden
	public String answer;		// antwoord van NPC
	public String action;		// script om achteraf uit te voeren
	
	/**
	 * Initializes a topic from a JDOM {@code Element}.
	 * 
	 * @param topic
	 */
	public Topic(String questID, String conversationID, Element topic) {
		this.questID = questID;
		this.conversationID = conversationID;
		
		// id en phrase moeten altijd bestaan
		id = topic.getAttributeValue("id");
		phrase = topic.getChildText("phrase");
		
		if(topic.getChild("pre") != null) {
			condition = topic.getChildText("pre");
		}
		if(topic.getChild("answer") != null) {
			answer = topic.getChildText("answer");
		}
		if(topic.getChild("action") != null) {
			action = topic.getChildText("action");
		}
	}
	
	/**
	 * Initializes a new topic.
	 * 
	 * @param questID	the resource ID of the quest this topic belongs to
	 * @param id		a unique ID for this topic
	 * @param pre		script preconditions
	 * @param phrase	the phrase the player says
	 * @param answer	the NPC's response
	 * @param action	script that is executed after the answer
	 */
	public Topic(String questID, String conversationID, String id, String pre, 
			String phrase, String answer, String action) {
		this.questID = questID;
		this.conversationID = conversationID;
		this.id = id;
		this.phrase = phrase;
		this.condition = pre;
		this.answer = answer;
		this.action = action;
	}
	
	public Element toElement() {
		Element topic = new Element("topic");
		topic.setAttribute("id", id);
		if(condition != null) {
			Element pre = new Element("pre");
			pre.setText(condition);
			topic.addContent(pre);
		}
		if(answer != null) {
			Element ae = new Element("answer");
			ae.setText(answer);
			topic.addContent(ae);
		}
		if(action != null) {
			Element ae = new Element("action");
			ae.setText(action);
			topic.addContent(ae);
		}
		return topic;
	}
}
