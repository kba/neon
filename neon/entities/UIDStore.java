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

package neon.entities;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentMap;
import org.apache.jdbm.DB;
import org.apache.jdbm.DBMaker;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import neon.entities.serialization.EntitySerializer;

/**
 * This class stores the UIDs of every object, map and mod currently in the 
 * game. It can give out new UIDs to objects created during gameplay. Positive
 * UIDs are used in resources loaded from a mod. Negative UIDs are reserved
 * for random generation.
 * 
 * @author mdriesen
 */
public class UIDStore {	
	// dummy uid voor objecten die eigenlijk niet bestaan
	public final static long DUMMY = 0;

	// uid database 
	private DB db;
	// uids van alle objecten in het spel
	private ConcurrentMap<Long, Entity> objects;
	// uids van alle geladen mods
	private ConcurrentMap<Short, Mod> mods;
	// uids van alle geladen maps
	private BiMap<Integer, String> maps = HashBiMap.create();
	
	/**
	 * Tells this UIDStore to use the given jdbm3 cache.
	 * 
	 * @param cache
	 */
	public UIDStore(String file) {
		db = DBMaker.openFile(file).disableLocking().make();

		if(db.getHashMap("objects") != null) {
			objects = db.getHashMap("objects");
			mods = db.getHashMap("mods");			
		} else {
			objects = db.createHashMap("objects", null, new EntitySerializer());
			mods = db.createHashMap("mods");
		}		
	}
	
	/**
	 * @return	the jdbm3 cache used by this UIDStore
	 */
	public DB getCache() {
		return db;
	}
	
	/**
	 * @param name	the name of a mod
	 * @return	the unique identifier of this mod
	 */
	public short getModUID(String name) {
		for(Mod mod : mods.values()) {
			if(mod.name.equals(name)) {
				return mod.uid;
			}
		}
		return 0;
	}
	
	/**
	 * Adds a {@code Map} with the given uid and path.
	 * 
	 * @param uid
	 * @param path
	 */
	public void addMap(Integer uid, String... path) {
		maps.put(uid, toString(path));
	}
	
	/**
	 * Adds an object to the list.
	 * 
	 * @param entity	the object to be added
	 */
	public void addEntity(Entity entity) {
		objects.put(entity.getUID(), entity);
		if(objects.size()%1000 == 0) {	// elke 1000 entities ne commit doen
			db.commit();
		}
	}
	
	/**
	 * Removes the object with the given UID.
	 * 
	 * @param uid	the UID of the object to be removed
	 */
	public void removeEntity(long uid) {
		objects.remove(uid);
	}
	
	/**
	 * Returns the entity with the given UID. If the UID is a {@code DUMMY},
	 * {@code null} is returned.
	 * 
	 * @param uid	the UID of an object
	 * @return		the object with the given UID
	 */
	public Entity getEntity(long uid) {
		return (uid == DUMMY ? null : objects.get(uid));
	}
	
	/**
	 * Adds a mod with the given id.
	 * 
	 * @param id
	 */
	public void addMod(String id) {
		short uid = (short)(Math.random()*Short.MAX_VALUE);
		while(mods.containsKey(uid) || uid == 0) {
			uid++;
		}
		Mod mod = new Mod(uid, id);
		mods.put(mod.uid, mod);	
	}
	
	/**
	 * @param uid	the unique identifier of a map
	 * @return		the full path of a map
	 */
	public String[] getMapPath(int uid) {
		if(maps.get(uid) != null) {
			return maps.get(uid).split(",");
		} else {
			return null;
		}
	}
	
	/**
	 * @param path	the path to a map
	 * @return	the uid of the given map
	 */
	public int getMapUID(String... path) {
		return maps.inverse().get(toString(path));
	}
	
	/**
	 * Creates a new uid for an entity.
	 * 
	 * @return
	 */
	public long createNewEntityUID() {
		// random objects hebben een random negatieve long als uid
		long uid = (long)(Math.random()*Long.MIN_VALUE);
		while(objects.containsKey(uid)) {
			uid = (uid >= 0) ? Long.MIN_VALUE : uid + 1;
		}
		return uid;		
	}
	
	/**
	 * Creates a new uid for a map.
	 * 
	 * @return
	 */
	public int createNewMapUID() {
		// random maps hebben een random negatieve int als uid
		int uid = (int)(Math.random()*Integer.MIN_VALUE);
		while(maps.containsKey(uid)) {
			uid = (uid >= 0) ? Integer.MIN_VALUE : uid + 1;
		}
		return uid;		
	}
	
	private static String toString(String... strings) {
		StringBuilder result = new StringBuilder();
		for(String s : strings) {
			result.append(s);
			result.append(",");
		}
		// laatste "," wegnemen
		result.replace(result.length(), result.length(), "");
		return result.toString();
	}
	
	/**
	 * @param map
	 * @param object
	 * @return	the full object UID
	 */
	public static long getObjectUID(long map, long object) {
		// dit om problemen met two's complement te vermijden
		return (map << 32) | ((object << 32) >>> 32);
	}
	
	/**
	 * @param mod
	 * @param map
	 * @return	the full map UID
	 */
	public static int getMapUID(int mod, int map) {
		// dit om problemen met two's complement te vermijden
		return (mod << 16) | ((map << 16) >>> 16);
	}
	
	private static class Mod implements Externalizable {
		private short uid;
		private String name;
		
		private Mod(short uid, String name) {
			this.uid = uid;
			this.name = name;
		}

		public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
			uid = input.readShort();
			name = input.readUTF();
		}

		public void writeExternal(ObjectOutput output) throws IOException {
			output.writeShort(uid);
			output.writeUTF(name);
		}
	}
}
