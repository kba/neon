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

package neon.resources.quest;

/**
 * A quest stage.
 * 
 * @author mdriesen
 *
 */
public class Stage {
	/** The resource ID of the quest this stage belongs to. */
	public final String questID;
	
	private int index;
	
	/**
	 * Initializes a quest stage.
	 * 
	 * @param questID	the resouce ID of the quest this stage belongs to
	 */
	public Stage(String questID, int index) {
		this.questID = questID;
		this.index = index;
	}
	
	/**
	 * @return	the stage index
	 */
	public int getIndex() {
		return index;
	}
}
