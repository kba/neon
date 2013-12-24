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

import net.engio.mbassy.bus.BusConfiguration;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

/**
 * A communication port used to send messages ({@code EventObject}s) between 
 * server and client parts of the engine.
 * 
 * @author mdriesen
 */
public abstract class Port {
	protected MBassador<EventObject> bus = new MBassador<EventObject>(BusConfiguration.Default());
	
	@Handler public abstract void receive(EventObject event);
	
	public MBassador<EventObject> getBus() {
		return bus;
	}
}
