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

import neon.core.event.CombatEvent;
import neon.core.event.SkillEvent;
import neon.util.fsm.TransitionEvent;
import net.engio.mbassy.listener.Handler;

public class EventAdapter {
	private QuestTracker tracker;
	
	public EventAdapter(QuestTracker tracker) {
		this.tracker = tracker;
	}
	
	@Handler public void handleSkill(SkillEvent se) {

	}

	@Handler public void handleCombat(CombatEvent ce) {
		
	}
	
	@Handler public void transition(TransitionEvent event) {
		if(event.toString().equals("dialog")) {
			tracker.checkTransition((TransitionEvent)event);
		}
	}
}
