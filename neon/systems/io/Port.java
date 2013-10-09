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

import java.util.ArrayList;
import java.util.EventObject;

/**
 * A communication port used to send messages ({@code EventObject}s) between 
 * server and client parts of the engine.
 * 
 * @author mdriesen
 */
public abstract class Port {
	public enum Mode {
		POLL, EVENT;
	}
	
	protected ArrayList<PortListener> listeners = new ArrayList<>();
	
	/**
	 * Writes a new event to this port.
	 * 
	 * @param event	an {@code EventObject}
	 */
	public abstract void write(EventObject event);
	
	public abstract EventObject read();
	
	/**
	 * Returns the {@code Mode} this port supports: {@code POLL}ed or 
	 * {@code EVENT}-based.
	 * 
	 * @return	
	 */
	public abstract Mode getMode();
	
	/**
	 * Adds a listener to this port. {@code EVENT}-based implementations
	 * should use these listeners.
	 * 
	 * @param listener
	 */
	public void addListener(PortListener listener) {
		listeners.add(listener);
	}
}
