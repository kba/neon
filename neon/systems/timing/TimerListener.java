/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2007 - Maarten Driesen
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

package neon.systems.timing;

/**
 * This interface describes a timer listener. 
 * 
 * @author mdriesen
 */
public interface TimerListener {
	/**
	 * Notifies that a new clock tick has passed.
	 * 
	 * @param time	the current in-game time
	 */
	public void tick(int time);

	/**
	 * Notifies that the timer has started.
	 * 
	 * @param time	the current in-game time
	 */
	public void start(int time);
}
