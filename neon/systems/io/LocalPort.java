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

package neon.systems.io;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

/**
 * A {@code Port} to connect client and server running locally.
 * 
 * @author mdriesen
 */
@Listener(references = References.Strong)	// strong, om gc te vermijden
public class LocalPort extends Port {
	private Collection<EventObject> buffer = Collections.synchronizedCollection(new ArrayDeque<EventObject>());
	private LocalPort peer;
	
	public LocalPort() {
		bus.subscribe(this);
	}
	
	/**
	 * Connects this {@code LocalPort} to another {@code LocalPort}.
	 * 
	 * @param peer	another {@code LocalPort}
	 */
	public void connect(LocalPort peer) {
		this.peer = peer;
	}

	@Override
	@Handler public void receive(EventObject event) {
		// zorgen dat al behandelde events niet nog eens worden teruggestuurd
		if(!buffer.remove(event)) {
			peer.write(event);
		}
	}

	private void write(EventObject event) {
		buffer.add(event);
		bus.publishAsync(event);
	}
}
