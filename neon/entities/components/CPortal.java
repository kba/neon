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

import java.awt.Point;
import neon.maps.Map;

public class CPortal implements Component {
	private int destMapUID = -1;	// -1 is zelfde map
	private Point destPos;
	private int destZone = -1;		// -1 is zelfde zone
	private String theme;
	private long uid;
	
	public CPortal(long uid) {
		this.uid = uid;
	}
	
	/**
	 * Sets a destination.
	 * 
	 * @param destPos		the position of the destination
	 * @param destZone		the zone index of the destination
	 * @param destMapUID	the map UID of the destination
	 */
	public void setDestination(Point destPos, int destZone, int destMapUID) {
		this.destPos = destPos;
		this.destZone = destZone;
		this.destMapUID = destMapUID;
	}
	
	/**
	 * @return	the destination map
	 */
	public int getDestMap() {
		return destMapUID;
	}
	
	/**
	 * Sets the destination map of this portal.
	 * 
	 * @param map	the destination
	 */
	public void setDestMap(Map map) {
		destMapUID = map.getUID();
	}
	
	/**
	 * Sets the destination map of this portal.
	 * 
	 * @param map	the destination
	 */
	public void setDestMap(int map) {
		destMapUID = map;
	}
	
	/**
	 * @return	the destination position
	 */
	public Point getDestPos() {
		return destPos;
	}
	
	/**
	 * @return	the dungeon level this portal leads to
	 */
	public Integer getDestZone() {
		return destZone;
	}
	
	/**
	 * Sets the destination zone of this door.
	 * 
	 * @param zone	the index of the destination zone
	 */
	public void setDestZone(int zone) {
		destZone = zone;
	}
	
	/**
	 * Sets the destination position of this door.
	 * 
	 * @param p	the destination position
	 */
	public void setDestPos(Point p) {
		destPos = p;
	}
	
	/**
	 * @return	the theme of the dungeon this door leads to
	 */
	public String getDestTheme() {
		return theme;
	}
	
	/**
	 * Sets the theme of the dungeon this door leads to.
	 */
	public void setDestTheme(String theme) {
		this.theme = theme;
	}
	
	/**
	 * @return	whether this is a real portal or not
	 */
	public boolean isPortal() {
		return destZone > -1 || destPos != null || destMapUID > -1 || theme != null; 
	}

	@Override
	public long getUID() {
		return uid;
	}
}
