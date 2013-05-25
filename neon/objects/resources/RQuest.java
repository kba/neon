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

package neon.objects.resources;

import java.util.ArrayList;
import java.util.Collection;
import neon.narrative.Topic;
import org.jdom2.Element;

public class RQuest extends RData {
	public Element variables;
	public int frequency;
	// random quests kunnen meer als eens draaien
	public boolean repeat = false;
	// enabled quest wordt toegevoegd van zodra spel start
	public boolean initial = false;
	public ArrayList<String> conditions = new ArrayList<String>();
	private ArrayList<Topic> topics = new ArrayList<Topic>();

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
		Element dialog = properties.getChild("dialog");
		if(dialog != null) {
			for(Element topic : dialog.getChildren("topic")) {
				topics.add(new Topic(topic));
			}
		}
	}

	public Collection<Topic> getTopics() {
		return topics;
	}
	
	public RQuest(String id, String... path) {
		super(id, path);
		repeat = true;
	}

	public RQuest(RQuest quest, String... path) {
		super(quest.id, path);
		name = quest.name;
		repeat = quest.repeat;
		initial = quest.initial;
		frequency = quest.frequency;
		topics.addAll(quest.getTopics());
		
		if(!quest.conditions.isEmpty()) {
			conditions.addAll(quest.conditions);
		}
		
		if(quest.variables != null) {
			variables = quest.variables.clone();
		}
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
		for(Topic topic : topics) {
			dialog.addContent(topic.toElement());
		}
		quest.addContent(dialog);

		return quest;
	}
}
