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
import neon.core.event.CombatListener;
import neon.core.event.DeathEvent;
import neon.core.event.DeathListener;
import neon.core.event.SkillEvent;
import neon.core.event.SkillListener;
import neon.util.fsm.TransitionEvent;
import neon.util.fsm.TransitionListener;

public class EventAdapter implements CombatListener, DeathListener, SkillListener, TransitionListener {
	private QuestTracker tracker;
	
	public EventAdapter(QuestTracker tracker) {
		this.tracker = tracker;
	}
	
	public void skillRaised(SkillEvent se) {
		
	}

	public void statRaised(SkillEvent se) {
		
	}

	public void levelRaised(SkillEvent se) {
		
	}

	public void died(DeathEvent de) {
		
	}

	public void combatStarted(CombatEvent ce) {
		
	}

	public void combatEnded(CombatEvent ce) {
		
	}

	public void transition(TransitionEvent event) {
		if(event.toString().equals("dialog")) {
			tracker.checkTransition((TransitionEvent)event);
		}
	}
}
