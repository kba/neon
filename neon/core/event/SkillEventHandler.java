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

package neon.core.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * A class that dispatches an incoming {@code SkillEvent} to a 
 * {@code SkillListener}.
 * 
 * @author mdriesen
 */
public class SkillEventHandler implements EventHandler<SkillEvent, SkillListener> {
	public void dispatch(SkillEvent se, SkillListener sl) {
		if(se.hasLevelled()) {
			sl.levelRaised(se);
		} else if(se.getStat() > 0){
			sl.statRaised(se);
		} else {
			sl.skillRaised(se);
		}
	}

	public Class<? extends EventObject> getEventType() {
		return SkillEvent.class;
	}

	public Class<? extends EventListener> getListenerType() {
		return SkillListener.class;
	}
}
