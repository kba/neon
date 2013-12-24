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

import java.util.EventObject;
import javax.swing.SwingConstants;

/**
 * An event used to signal the client that a message should be shown.
 * 
 * @author mdriesen
 */
@SuppressWarnings("serial")
public class MessageEvent extends EventObject {
	private String message;
	private int time;
	private int position;

	public MessageEvent(Object source, String message, int duration, int position) {
		super(source);
		this.message = message;
		time = duration;
		this.position = position;
	}
	
	public MessageEvent(Object source, String message, int duration) {
		this(source, message, duration, SwingConstants.CENTER);
	}	
	
	@Override
	public String toString() {
		return message;
	}
	
	/**
	 * @return	how long the message should be shown
	 */
	public int getDuration() {
		return time;
	}
	
	/**
	 * @return	the screen position where the message should be shown
	 */
	public int getPosition() {
		return position;
	}
}
