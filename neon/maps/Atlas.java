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

import java.util.concurrent.ConcurrentMap;
import org.apache.jdbm.DB;
import org.apache.jdbm.DBMaker;

import neon.core.Engine;
import neon.entities.Door;
import neon.entities.Player;
import neon.systems.files.FileSystem;

/**
 * This class keeps track of all loaded maps and their connections.
 * 
 * @author mdriesen
 */
public class Atlas {
	private DB db = null;
	private ConcurrentMap<Integer, Map> maps;
	private int currentZone = 0;
	private int currentMap = 0;
	private FileSystem files;
	
	/**
	 * Initializes this {@code Atlas} with the given {@code FileSystem} and
	 * cache path. The cache is lazy initialised. 
	 * 
	 * @param files	a {@code FileSystem}
	 * @param path	the path to the file used for caching
	 */
	public Atlas(FileSystem files, String path) {
		this.files = files;
		db = DBMaker.openFile(path).disableLocking().make();
		
		if(db.getHashMap("maps") != null) {
			maps = db.getHashMap("maps");
		} else {
			maps = db.createHashMap("maps");
		}
	}
	
	public DB getCache() {
		return db;
	}
	
	/**
	 * @return	the current map
	 */
	public Map getCurrentMap() {
		return maps.get(currentMap);
	}
	
	/**
	 * @return	the current zone
	 */
	public Zone getCurrentZone() {
		return maps.get(currentMap).getZone(currentZone);
	}

	/**
	 * @return	the current zone
	 */
	public int getCurrentZoneIndex() {
		return currentZone;
	}
	
	/**
	 * @param uid	the unique identifier of a map
	 * @return		the map with the given uid
	 */
	public Map getMap(int uid) {
		if(!maps.containsKey(uid)) {
			Map map = MapLoader.loadMap(Engine.getStore().getMapPath(uid), uid, files);
			maps.put(uid, map);
		} 
		return maps.get(uid);
	}
	
	/**
	 * Sets the current zone.
	 * 
	 * @param i	the index of the current zone
	 */
	public void setCurrentZone(int i) {
		currentZone = i;
		Engine.getPhysicsEngine().clear();
		for(Region region : getCurrentZone().getRegions()) {
			if(region.isActive()) {
				Engine.getPhysicsEngine().register(region, region.getBounds(), true);
			}
		}
		// niet vergeten player terug te registreren
		Player player = Engine.getPlayer();
		Engine.getPhysicsEngine().register(player.physics);
	}
	
	/**
	 * Enter a new zone through a door.
	 * 
	 * @param door
	 * @param previousZone
	 */
	public void enterZone(Door door, Zone previousZone) {
		if(door.portal.getDestZone() > -1) {
			setCurrentZone(door.portal.getDestZone());
		} else {
			setCurrentZone(0);
		}
		
		if(getCurrentMap() instanceof Dungeon && getCurrentZone().isRandom()) {
			new DungeonGenerator(getCurrentZone()).generate(door, previousZone);
		}
	}

	/**
	 * Set the current map.
	 * 
	 * @param map	the new current map
	 */
	public void setMap(Map map) {
		if(!maps.containsKey(map.getUID())) {
			// kan een random map zijn die nog niet in db zit
			maps.put(map.getUID(), map);
		}
		currentMap = map.getUID();
	}
}
