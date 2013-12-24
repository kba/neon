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

package neon.entities.components;

import java.util.HashMap;

/**
 * Keeps track of the factions a creature belongs to and their standing with 
 * these factions.
 * 
 * @author mdriesen
 */
public class FactionComponent implements Component {
	private final long uid;
	private HashMap<String, Integer> factions = new HashMap<>();
	
	public FactionComponent(long uid) {
		this.uid = uid;
	}
	
	public HashMap<String, Integer> getFactions() {
		return factions;
	}
	
	public void addFaction(String faction, int rank) {
		factions.put(faction, rank);
	}
	
	public int getRank(String faction) {
		return factions.get(faction);
	}

	public boolean isMember(String faction) {
		return factions.containsKey(faction);
	}	
		
	@Override
	public long getUID() {
		return uid;
	}
}
