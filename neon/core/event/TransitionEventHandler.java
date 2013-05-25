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
import neon.util.fsm.TransitionEvent;
import neon.util.fsm.TransitionListener;

public class TransitionEventHandler implements EventHandler<TransitionEvent, TransitionListener> {
	public void dispatch(TransitionEvent te, TransitionListener tl) {
		tl.transition(te);
	}

	public Class<? extends EventObject> getEventType() {
		return TransitionEvent.class;
	}

	public Class<? extends EventListener> getListenerType() {
		return TransitionListener.class;
	}
}
