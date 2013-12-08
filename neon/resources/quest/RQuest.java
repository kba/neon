/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - Maarten Driesen
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

import java.util.ArrayList;
import java.util.Collection;

import neon.resources.RData;

import org.jdom2.Element;

/**
 * A resource representing a quest.
 * 
 * @author mdriesen
 */
public class RQuest extends RData {
	public Element variables;
	public int frequency;
	// repeat quests kunnen meer als eens draaien
	public boolean repeat = false;
	// initial quest wordt toegevoegd van zodra spel start
	public boolean initial = false;
	
	private ArrayList<String> conditions = new ArrayList<String>();
	private ArrayList<Conversation> conversations = new ArrayList<Conversation>();

	public RQuest(String id, Element properties, String... path) {
		super(id, path);
		name = properties.getAttributeValue("name");
		if(properties.getChild("pre") != null) {
			for(Element condition : properties.getChild("pre").getChildren()) {
				conditions.add(condition.getTextTrim());
			}
		}
		if(properties.getChild("objects") != null) {
			variables = properties.getChild("objects").detach();			
		}
		repeat = properties.getName().equals("repeat");
		if(repeat) {
			frequency = Integer.parseInt(properties.getAttributeValue("f"));
		}
		initial = (properties.getAttribute("init") != null);
		
		if(properties.getChild("dialog") != null) {
			initDialog(properties.getChild("dialog"));
		}
	}

	public RQuest(String id, String... path) {
		super(id, path);
	}

	private void initDialog(Element dialog) {
		for(Element ce : dialog.getChildren("conversation")) {
			Conversation conversation = new Conversation(id, ce.getAttributeValue("id"));
			Topic root = new Topic(id, conversation.id, ce.getChild("root"));
			conversation.setRootTopic(root);
			for(Element te : ce.getChild("root").getChildren("topic")) {
				initTopic(conversation, root, te);
			}
			conversations.add(conversation);
		}
	}
	
	private void initTopic(Conversation conversation, Topic parent, Element te) {
		Topic topic = new Topic(id, conversation.id, te);
		conversation.addSubTopic(parent, topic);
		// recursief alle child topics toevoegen
		for(Element ce : te.getChildren("topic")) {
			initTopic(conversation, topic, ce);
		}
	}
	
	/**
	 * @return	all dialog topics in this quest
	 */
	public Collection<Conversation> getConversations() {
		return conversations;
	}
	
	/**
	 * @return	all preconditions for this quest
	 */
	public Collection<String> getConditions() {
		return conditions;
	}
	
	public Element toElement() {
		Element quest = new Element(repeat ? "repeat" : "quest");
		quest.setAttribute("name", name != null ? name : id);
		if(initial) {
			quest.setAttribute("init", "1");
		}
		
		if(!conditions.isEmpty()) {
			Element pre = new Element("pre");
			for(String condition : conditions) {
				pre.addContent(new Element("condition").setText(condition));
			}
			quest.addContent(pre);
		}
		
		if(variables != null) {
			quest.addContent(variables);
		}
		
		Element dialog = new Element("dialog");
//		for(Topic topic : topics) {
//			dialog.addContent(topic.toElement());
//		}
		quest.addContent(dialog);

		return quest;
	}
}
