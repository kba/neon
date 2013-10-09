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

import java.util.EventObject;

public class LocalPort extends Port {
	private LocalPort peer;
	
	@Override
	public void write(EventObject event) {
		peer.receive(event);
	}

	@Override
	public EventObject read() {
		return null;
	}
	
	/**
	 * Connects this {@code LocalPort} to another {@code LocalPort}.
	 * 
	 * @param peer	another {@code LocalPort}
	 */
	public void connect(LocalPort peer) {
		this.peer = peer;
	}
	
	private void receive(EventObject event) {
		for(PortListener listener : listeners) {
			listener.receive(event);
		}
	}

	@Override
	public Mode getMode() {
		return Mode.EVENT;
	}
}
