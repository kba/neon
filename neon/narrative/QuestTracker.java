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

package neon.narrative;

import java.util.*;
import neon.core.Engine;
import neon.core.event.TurnEvent;
import neon.entities.Creature;
import neon.resources.RQuest;
import neon.resources.quest.Conversation;
import neon.resources.quest.Topic;
import neon.util.fsm.TransitionEvent;
import net.engio.mbassy.listener.Handler;

public class QuestTracker {
	private LinkedList<String> objects = new LinkedList<>();
	private HashMap<String, Quest> quests = new HashMap<>();
	// tijdelijke map voor quests die voor dialogmodule zijn geladen
	private HashMap<String, Quest> temp = new HashMap<>();
	
	public QuestTracker() {
		
	}
	
	/**
	 * Return all dialog topics for the given creature. The caller of this
	 * method should take care to properly initialize the scripting engine:
	 * the {@code NPC} variable should be made to refer to the given creature 
	 * before calling this method.
	 * 
	 * @param speaker	the creature that is spoken to
	 * @return	a {@code Vector} with all {@code Topic}s for the given creature
	 */
	public Vector<Topic> getDialog(Creature speaker) {
		Vector<Topic> dialog = new Vector<Topic>();
		
		for(Quest quest : getQuests(speaker)) {
			for(Conversation conversation : quest.getConversations()) {
				dialog.add(conversation.getRootTopic());
			}
		}
		
		return dialog;
	}
	
	public Vector<Topic> getSubtopics(Topic topic) {
		Vector<Topic> dialog = new Vector<Topic>();
		
		Quest quest = quests.get(topic.questID);
		for(Conversation c : quest.getConversations()) {
			if(c.id.equals(topic.conversationID)) {
				dialog.addAll(c.getTopics(topic));
				break;
			}
		}
		
		return dialog;
	}
	
	public void doAction(Topic topic) {
		HashMap<String, Object> objects = new HashMap<>();
//		if(quests.containsKey(topic.quest)) {
//			objects.putAll(quests.get(topic.quest).getObjects());
//		} else if(temp.containsKey(topic.quest)) {
//			objects.putAll(temp.get(topic.quest).getObjects());			
//		}
		for(Map.Entry<String, Object> entry : objects.entrySet()) {
			Engine.getScriptEngine().put(entry.getKey(), entry.getValue());
		}
		
		if(topic.action != null) {
			Engine.execute(topic.action);
		}
	}

	/**
	 * Start the quest with the given id.
	 * 
	 * @param id
	 */
	public void startQuest(String id) {
		if(temp.containsKey(id)) {
			Quest quest = temp.remove(id);
			quests.put(id, quest);			
		} else if(!quests.containsKey(id)) {
			RQuest quest = (RQuest)Engine.getResources().getResource(id, "quest");
			quests.put(id, new Quest(quest));
		}
	}
	
	/**
	 * Finish the quest with the given id.
	 * 
	 * @param id
	 */
	public void finishQuest(String id) {
		if(quests.containsKey(id)) {
			Quest quest = quests.get(id);
			quest.finish();
			if(quest.template.repeat) {
				// repeat quests worden verwijderd om opnieuw kunnen starten
				quests.remove(id);
			}
		}
	}

	private Collection<Quest> getQuests(Creature speaker) {
		ArrayList<Quest> list = new ArrayList<Quest>();
		for(Quest quest : quests.values()) {
			if(QuestUtils.checkQuest(quest.template)) {
				list.add(quest);
			}
		}
		for(Quest quest : temp.values()) {
			if(QuestUtils.checkQuest(quest.template)) {
				list.add(quest);
			}
		}
		return list;
	}
	
	public String getNextRequestedObject() {
		return objects.poll();
	}
	
	void addObject(String object) {
		objects.add(object);
	}
	
	void checkTransition(TransitionEvent te) {

	}

	@Handler public void start(TurnEvent te) {
		if(te.isStart()) {
			for(RQuest quest : Engine.getResources().getResources(RQuest.class)) {
				if(quest.initial) {
					startQuest(quest.id);
				}
			}
		}
	}
}
