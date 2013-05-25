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

public class CombatEventHandler implements EventHandler<CombatEvent, CombatListener> {
	public Class<CombatEvent> getEventType() {
		return CombatEvent.class;
	}

	public Class<CombatListener> getListenerType() {
		return CombatListener.class;
	}

	public void dispatch(CombatEvent ce, CombatListener cl) {
		if(ce.isFinished()) {
			cl.combatEnded(ce);
		} else {
			cl.combatStarted(ce);
		}
	}
}
