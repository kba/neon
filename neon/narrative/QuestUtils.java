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

import neon.core.Engine;
import neon.resources.quest.RQuest;
import neon.resources.quest.Topic;

public class QuestUtils {
	/**
	 * Checks whether the conditions for the given topic are fulfilled.
	 * 
	 * @param topic
	 */
	protected static boolean checkTopic(Topic topic) {
		String pre = topic.condition;
		return (pre == null) || (Engine.execute(pre).equals(true));
	}

	/**
	 * Checks whether the conditions for the given quest are fulfilled.
	 * 
	 * @param quest	
	 */
	protected static boolean checkQuest(RQuest quest) {		
		if(quest.getConditions().isEmpty()) {
			return true;	// geen voorwaardes
		} else {
			boolean custom = true;
			for(String condition : quest.getConditions()) {
				custom = custom && Engine.execute(condition).equals(true);			
			}
			return custom;
		}
	}
}
