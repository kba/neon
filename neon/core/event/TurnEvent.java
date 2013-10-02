/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

@SuppressWarnings("serial")
public class TurnEvent extends EventObject {
	private int time;
	private boolean start = false;
	
	public TurnEvent(int turn) {
		super(turn);
		this.time = turn;
	}
	
	public TurnEvent(int turn, boolean start) {
		super(turn);
		this.time = turn;
		this.start = true;
	}
	
	public int getTime() {
		return time;
	}
	
	/**
	 * @return	whether this {@code TurnEvent} indicates the start of a game
	 */
	public boolean isStart() {
		return start;
	}
	
	@Override
	public String toString() {
		return "turn " + time;
	}
}
