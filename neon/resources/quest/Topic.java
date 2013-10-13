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

import org.jdom2.Element;

public class Topic {
	private String questID;
	private String id;
	private String condition;
	private String answer;
	private String action;
	
	public Topic(Element topic) {
		id = topic.getAttributeValue("id");
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
	
	public Topic(String id, String pre, String answer, String action) {
		this.id = id;
		this.condition = pre;
		this.answer = answer;
		this.action = action;
	}
	
	/**
	 * Copy constructor. 
	 * 
	 * @param topic
	 */
	public Topic(Topic topic) {
		id = topic.getID();
		condition = topic.getCondition();
		answer = topic.getAnswer();
		action = topic.getAction();
	}

	public String getID() {
		return id;
	}
	
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
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
