/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - Maarten Driesen
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

package neon.maps;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import neon.objects.resources.RZoneTheme;
import neon.util.Graph;

/**
 * A dungeon. It can contain several interconnected zones.
 * 
 * @author	mdriesen
 */
public class Dungeon implements Map {
	private String name;
	private int uid;
	private Graph<Zone> zones = new Graph<Zone>();
	
	/**
	 * Initialize a dungeon.
	 * 
	 * @param name	the name of this dungeon
	 * @param uid	the unique identifier of this dungeon
	 */
	public Dungeon(String name, int uid) {
		this.name = name;
		this.uid = uid;
	}
	
	public Zone getZone(int i) {
		return zones.getNode(i);
	}

	public int getUID() {
		return uid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Adds an empty zone to this dungeon.
	 */
	public void addZone(int zone, String name) {
		zones.addNode(zone, new Zone(name, uid, zone));
	}
	
	/**
	 * Adds an empty zone to this dungeon.
	 */
	public void addZone(int zone, String name, RZoneTheme theme) {
		zones.addNode(zone, new Zone(name, uid, theme, zone));
	}
	
	public Collection<Zone> getZones() {
		return zones.getNodes();
	}
	
	public String getZoneName(int zone) {
		return zones.getNode(zone).getName();
	}
	
	/**
	 * Adds a connection between two zones in this dungeon.
	 * 
	 * @param from
	 * @param to
	 */
	public void addConnection(int from, int to) {
		zones.addConnection(from, to, true);
	}
	
	/**
	 * @param from
	 * @return	all connections originating from the given zone 
	 */
	public Collection<Integer> getConnections(int from) {
		return zones.getConnections(from);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		name = in.readUTF();
		uid = in.readInt();
		zones = (Graph<Zone>)in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeUTF(name);
		out.writeInt(uid);
		out.writeObject(zones);
	}
}