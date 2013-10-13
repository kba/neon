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

package neon.narrative;

import java.util.ArrayList;
import java.util.Collection;
import neon.core.Engine;
import neon.resources.RQuest;
import neon.resources.quest.Topic;

public class QuestUtils {
	/**
	 * Checks whether the conditions for the given topic are fulfilled.
	 * 
	 * @param topic
	 */
	protected static boolean checkTopic(Topic topic) {
		String pre = topic.getCondition();
		return (pre == null) || (Engine.execute(pre).equals(true));
	}

	/**
	 * Checks whether the conditions for the given quest are fulfilled.
	 * 
	 * @param quest	
	 */
	protected static boolean checkQuest(RQuest quest) {		
		if(quest.conditions.isEmpty()) {
			return true;	// geen voorwaardes
		} else {
			boolean custom = true;
			for(String condition : quest.conditions) {
				custom = custom && Engine.execute(condition).equals(true);			
			}
			return custom;
		}
	}

	protected static Collection<Topic> replaceDialog(RQuest quest, String... vars) {
		ArrayList<Topic> list = new ArrayList<Topic>();
		for(Topic t : quest.getTopics()) {
			Topic topic = new Topic(t);
			QuestUtils.replaceString(topic, "$pcr$", Engine.getPlayer().species.getName());
			QuestUtils.replaceString(topic, "$pcn$", Engine.getPlayer().getName());
			QuestUtils.replaceString(topic, "$pcp$", Engine.getPlayer().getProfession());
			QuestUtils.replaceString(topic, "$quest$", quest.name);
			
			for(int i = 0; i < vars.length/2; i++) {
				QuestUtils.replaceString(topic, vars[2*i], vars[2*i + 1]);
			}
			
			list.add(topic);
		}
		return list;
	}

	protected static void replaceString(Topic topic, String old, String fresh) {
		String pre = topic.getCondition();
		if(pre != null) {
			topic.setCondition(pre.replace(old, fresh));
		}
		String answer = topic.getAnswer();
		if(answer != null) {
			topic.setAnswer(answer.replace(old, fresh));
		}
		String action = topic.getAction();
		if(action != null) {
			topic.setAction(action.replace(old, fresh));
		}
	} 
}
