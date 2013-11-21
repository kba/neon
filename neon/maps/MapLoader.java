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

package neon.maps;

import org.jdom2.*;
import java.awt.Point;
import neon.core.*;
import neon.entities.Container;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.EntityFactory;
import neon.entities.Item;
import neon.entities.UIDStore;
import neon.entities.components.Enchantment;
import neon.entities.components.Lock;
import neon.resources.RDungeonTheme;
import neon.resources.RItem;
import neon.resources.RRegionTheme;
import neon.resources.RSpell;
import neon.resources.RTerrain;
import neon.resources.RZoneTheme;
import neon.systems.files.FileSystem;
import neon.systems.files.XMLTranslator;

/**
 * This class loads a map from an xml file.
 * 
 * @author mdriesen
 */
public class MapLoader {
	/**
	 * Returns a map described in an xml file with the given name.
	 * 
	 * @param path		the pathname of a map file
	 * @param uid		the unique identifier of this map
	 * @return		the <code>Map</code> described by the map file
	 */
	public static Map loadMap(String[] path, int uid, FileSystem files) {
		Document doc = files.getFile(new XMLTranslator(), path);
		Element root = doc.getRootElement();
//		System.out.println("loadmap(" + path + ")");
		if(root.getName().equals("world")) {
			return loadWorld(root, uid);						
		} else {
			return loadDungeon(root, uid);
		} 
	}
	
	/**
	 * Loads a dungeon behind a themed door.
	 * 
	 * @param theme
	 * @return
	 */
	public static Dungeon loadDungeon(String theme) {
		return loadThemedDungeon(theme, theme, Engine.getStore().createNewMapUID());
	}
	
	private static World loadWorld(Element root, int uid) {
//		System.out.println("maploader: " + uid);
		World world = new World(root.getChild("header").getChildText("name"), uid);
		loadZone(root, world, 0, uid);	// outdoor heeft maar 1 zone, namelijk 0
		return world;					
	}
	
	private static Dungeon loadDungeon(Element root, int uid) {
		if(root.getChild("header").getAttribute("theme") != null) {
			String name = root.getChild("header").getChildText("name");
			return loadThemedDungeon(name, root.getChild("header").getAttributeValue("theme"), uid);
		}
		
		Dungeon map = new Dungeon(root.getChild("header").getChildText("name"), uid);
		
		for(Element l : root.getChildren("level")) {
			int level = Integer.parseInt(l.getAttributeValue("l"));
			String name = l.getAttributeValue("name");
			if(l.getAttribute("theme") != null) {
				RZoneTheme theme = (RZoneTheme)Engine.getResources().getResource(l.getAttributeValue("theme"), "theme");
				map.addZone(level, name, theme);
				if(l.getAttribute("out") != null) {
					String[] connections = l.getAttributeValue("out").split(",");
					for(String connection : connections) {
						map.addConnection(level, Integer.parseInt(connection));						
					}
				}
			} else {
				map.addZone(level, name);
				loadZone(l, map, level, uid);
			}
		}
		
		return map;
	}
	
	private static Dungeon loadThemedDungeon(String name, String dungeon, int uid) {
		Dungeon map = new Dungeon(name, uid);
		RDungeonTheme theme = (RDungeonTheme)Engine.getResources().getResource(dungeon, "theme");
		
		int minZ = theme.min;
		int maxZ = theme.max;
		float branch = theme.branching;
		String[] types = theme.zones.split(";");
		
		int[] zones = new int[MapUtils.random(minZ, maxZ)];
		int z = zones.length - 1;
		while(z > -1) {
			int t = MapUtils.random(0, types.length - 1);
			zones[z] = 1;
			RZoneTheme rzt = (RZoneTheme)Engine.getResources().getResource(types[t], "theme");
			map.addZone(z, "zone " + z, rzt);
			z--;
		}
		
		zones[0] = 0;
		for(z = 1; z < zones.length; z++) {
			// verbinden met reeds bezocht zone
			int to = MapUtils.random((int)Math.max(0, z - branch), z - 1);
			map.addConnection(z, to);
			zones[z] = 0;
		}
		
		return map;
	}
	
	private static void loadZone(Element root, Map map, int l, int uid) {
		for(Element region : root.getChild("regions").getChildren()) {	// regions laden
			map.getZone(l).addRegion(loadRegion(region));
		}		
		if(root.getChild("creatures") != null) {	// creatures laden
			for(Element c : root.getChild("creatures").getChildren()) {
				String species = c.getAttributeValue("id");
				int x = Integer.parseInt(c.getAttributeValue("x"));
				int y = Integer.parseInt(c.getAttributeValue("y"));
				long creatureUID = UIDStore.getObjectUID(uid, Integer.parseInt(c.getAttributeValue("uid")));
				Creature creature = EntityFactory.getCreature(species, x, y, creatureUID);
				Engine.getStore().addEntity(creature);
				map.getZone(l).addCreature(creature);
			}			
		}
		if(root.getChild("items") != null) {		// items laden
			for(Element i : root.getChild("items").getChildren()) {
				long itemUID = UIDStore.getObjectUID(uid, Integer.parseInt(i.getAttributeValue("uid")));
				String id = i.getAttributeValue("id");
				int x = Integer.parseInt(i.getAttributeValue("x"));
				int y = Integer.parseInt(i.getAttributeValue("y"));
				Item item = null;
				if(i.getName().equals("container")) {
					item = loadContainer(i, id, x, y, itemUID, uid);	// omdat containers lastig zijn
				} else if(i.getName().equals("door")) {
					item = loadDoor(i, id, x, y, itemUID, uid);		// omdat deuren ook lastig zijn
				} else {
					item = EntityFactory.getItem(id, x, y, itemUID);
				}
				map.getZone(l).addItem(item);
				Engine.getStore().addEntity(item);
			}				
		}
	}
	
	/*
	 * dit gaat mottig worden, met ganse if-then-else mesthoop
	 */
	private static Door loadDoor(Element door, String id, int x, int y, long itemUID, int mapUID) {
		Door d = (Door)EntityFactory.getItem(id, x, y, itemUID);

		// lock difficulty
		int lock = 0;
		if(door.getAttribute("lock") != null) {
			lock = Integer.parseInt(door.getAttributeValue("lock"));			
			d.lock.setLockDC(lock);
		}
		// sleutel
		if(door.getAttribute("key") != null) {
			RItem key = (RItem)Engine.getResources().getResource(door.getAttributeValue("key"));
			d.lock.setKey(key);
		}
		// state van de deur (open, dicht of gesloten)
		if(door.getAttributeValue("state").equals("locked")) {
			if(lock > 0) {
				d.lock.setState(Lock.LOCKED);
			} else {	// als er geen lock is, state in closed veranderen
				d.lock.setState(Lock.CLOSED);
			}
		} else if(door.getAttributeValue("state").equals("closed")) {
			d.lock.setState(Lock.CLOSED);
		}
		
		// trap
		int trap = 0;
		if(door.getAttribute("trap") != null) {
			trap = Integer.parseInt(door.getAttributeValue("trap"));			
			d.trap.setTrapDC(trap);
		}
		// spell
		if(door.getAttribute("spell") != null) {
			String spell = door.getAttributeValue("spell");
			RSpell.Enchantment enchantment = (RSpell.Enchantment)Engine.getResources().getResource(spell, "magic");
			d.setMagicComponent(new Enchantment(enchantment, 0, d.getUID()));
		}
		
		// bestemming van de deur
		Element dest = door.getChild("dest");
		Point destPos = null;
		int destLevel = 0;
		int destMapUID = 0;
		String theme = null;
		String sign = null;
		if(door.getChild("dest") != null) {
			int destX = -1;
			int destY = -1;
			if(dest.getAttribute("x") != null) {
				destX = Integer.parseInt(dest.getAttributeValue("x"));
			}
			if(dest.getAttribute("y") != null) {
				destY = Integer.parseInt(dest.getAttributeValue("y"));
			}
			if(destX > -1 && destY > -1) {
				destPos = new Point(destX, destY);
			}
			if(dest.getAttributeValue("z") != null) {
				destLevel = Integer.parseInt(dest.getAttributeValue("z"));
			}
			if(dest.getAttributeValue("map") != null) {
				destMapUID = (mapUID & 0xFFFF0000) + Integer.parseInt(dest.getAttributeValue("map"));
			}
			theme = dest.getAttributeValue("theme");
			sign = dest.getAttributeValue("sign");
		}

		if(dest != null) {
			d.portal.setDestination(destPos, destLevel, destMapUID);
		}
		d.portal.setDestTheme(theme);
		d.setSign(sign);
		return d;
	}

	private static Container loadContainer(Element container, String id, int x, int y, long itemUID, int mapUID) {
		Container cont = (Container)EntityFactory.getItem(id, x, y, itemUID);
		
		// lock difficulty
		if(container.getAttribute("lock") != null) {
			int lock = Integer.parseInt(container.getAttributeValue("lock"));	
			cont.lock.setLockDC(lock);
			cont.lock.setState(Lock.LOCKED);
		}
		// sleutel
		RItem key = null;
		if(container.getAttribute("key") != null) {
			key = (RItem)Engine.getResources().getResource(container.getAttributeValue("key"));
			cont.lock.setKey(key);
		}

		// trap
		int trap = 0;
		if(container.getAttribute("trap") != null) {
			trap = Integer.parseInt(container.getAttributeValue("trap"));			
			cont.trap.setTrapDC(trap);
		}
		// spell
		if(container.getAttribute("spell") != null) {
			String spell = container.getAttributeValue("spell");
			RSpell.Enchantment enchantment = (RSpell.Enchantment)Engine.getResources().getResource(spell, "magic");
			cont.setMagicComponent(new Enchantment(enchantment, 0, cont.getUID()));
		}
		
		if(!container.getChildren("item").isEmpty()) {	// indien items in map file
			for(Element e : container.getChildren("item")) {
				long contentUID = UIDStore.getObjectUID(mapUID, Integer.parseInt(e.getAttributeValue("uid")));
				Engine.getStore().addEntity(EntityFactory.getItem(e.getAttributeValue("id"), contentUID));
				cont.addItem(contentUID);
			}			
		} else {	// en anders default items
			for(String s : ((RItem.Container)cont.resource).contents) {
				Item i = EntityFactory.getItem(s, Engine.getStore().createNewEntityUID());
				Engine.getStore().addEntity(i);
				cont.addItem(i.getUID());
			}
		}
		
		return cont;
	}
	
	private static Region loadRegion(Element element) {
		int x = Integer.parseInt(element.getAttributeValue("x"));
		int y = Integer.parseInt(element.getAttributeValue("y"));
		int w = Integer.parseInt(element.getAttributeValue("w"));
		int h = Integer.parseInt(element.getAttributeValue("h"));
		byte order = Byte.parseByte(element.getAttributeValue("l"));
		
		String text = element.getAttributeValue("text");
		RRegionTheme theme = (RRegionTheme)Engine.getResources().getResource(element.getAttributeValue("random"), "theme");
		
		RTerrain rt = (RTerrain)Engine.getResources().getResource(text, "terrain");
		Region r = new Region(text, x, y, w, h, theme, order, rt);
		r.setLabel(element.getAttributeValue("label"));
		for(Element e : element.getChildren("script")) {
			r.addScript(e.getAttributeValue("id"), false);
		}
		
		return r;
	}
}