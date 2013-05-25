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

package neon.tools.resources;

import java.util.*;
import neon.tools.Editor;
import org.jdom2.*;
import neon.graphics.Renderable;
import neon.objects.resources.RData;
import neon.objects.resources.RDungeonTheme;
import neon.systems.files.XMLTranslator;
import neon.tools.maps.*;

/*
 * levensloop van een map:
 * 	A. bestaande map:
 * 		1. map laden
 * 		2. zones laden
 * 	B. nieuwe map:
 * 		a. dungeon
 * 		b. random dungeon
 * 		c. outdoor
 */
public class RMap extends RData {
	// id van map = path
	public final static boolean DUNGEON = true;
	public HashMap<Integer, RZone> zones = new HashMap<Integer, RZone>();
	public String name;
	public RDungeonTheme theme;
	public short uid;
	private boolean type;
	private ArrayList<Integer> uids;
	
	// voor reeds bestaande maps tijdens loadMod
	public RMap(String id, Element properties, String... path) {
		super(id, path);	
		uid = Short.parseShort(properties.getChild("header").getAttributeValue("uid"));
		name = properties.getChild("header").getChildText("name");
		type = properties.getName().equals("dungeon");
		
		if(type == DUNGEON) {
			if(properties.getChild("header").getAttribute("theme") != null) {
				theme = (RDungeonTheme)Editor.resources.getResource(properties.getChild("header").getAttributeValue("theme"), "theme");
			} else {
				for(Element zone : properties.getChildren("level")) {
					zones.put(Integer.parseInt(zone.getAttributeValue("l")), new RZone(zone, this, path));
				}
			}
		} else {
			zones.put(0, new RZone(properties, this, path));
		}
	}

	// voor nieuwe aan te maken maps
	public RMap(short uid, String mod, MapDialog.Properties props) {
		super(props.getID(), mod);
		this.uid = uid;
		type = props.isDungeon();
		name = props.getName();

		if(!props.isDungeon()) { // bij outdoor altijd zone en base region instellen
			Element region = new Element("region");
			region.setAttribute("x", "0");
			region.setAttribute("y", "0");
			region.setAttribute("w", Integer.toString(props.getWidth()));
			region.setAttribute("h", Integer.toString(props.getHeight()));
			region.setAttribute("text", props.getTerrain());
			region.setAttribute("l", "0");
			Instance ri = new IRegion(region);
			RZone zone = new RZone(name, mod, ri, this);
			zones.put(0, zone);
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isDungeon() {
		return type;
	}
	
	public RZone getZone(int index) {
		if(zones == null) {
			load();
		}
		return zones.get(index);
	}
	
	public int getZone(RZone zone) {
		if(zones == null) {
			load();
		}
		
		for(Integer i : zones.keySet()) {
			if(zones.get(i) == zone) {
				return i;
			}
		}
		return 0;
	}
	
	public short getUID() {
		return uid;
	}
	
	public String toString() {
		return name;
	}
	
	// objecten indien nodig ook uit tree halen!!!
	public void removeObjectUID(int uid) {
		uids.remove((Integer)uid);	// omdat remove(int) de int'ste waarde verwijderd
	}
	
	// objecten niet vergeten in tree te steken!!!
	public int createUID(Element e) {
		int hash = e.hashCode();
		while(uids.contains(hash)) {
			hash++;
		}
		uids.add(hash);
		return hash;
	}
	
	public Element toElement() {
		System.out.println("save map: " + name);
		Element root = new Element(isDungeon() ? "dungeon" : "world");
		Element header = new Element("header");
		header.setAttribute("uid", Short.toString(uid));
		header.addContent(new Element("name").setText(name));
		root.addContent(header);
		if(type == DUNGEON) {
			for(Integer level : zones.keySet()) {
				root.addContent(zones.get(level).toElement().setAttribute("l", level.toString()));
			}
		} else {
			RZone zone = zones.get(0);
			Element creatures = new Element("creatures");
			Element items = new Element("items");
			Element regions = new Element("regions");
			for(Renderable r : zone.getScene().getElements()) {
				Instance i = (Instance)r;
				Element element = i.toElement();
				element.detach();
				if(element.getName().equals("region")) {
					regions.addContent(element);
				} else if(element.getName().equals("creature")) {
					creatures.addContent(element);
				} else if(element.getName().equals("item") || element.getName().equals("door") ||element.getName().equals("container")) {
					items.addContent(element);
				}
			}
			root.addContent(creatures);
			root.addContent(items);
			root.addContent(regions);
		}

		return root;
	}

	public void load() {
		if(uids == null) {	// vermijden dat map twee keer geladen wordt
			uids = new ArrayList<Integer>();
			try {
				String file = Editor.getStore().getMod(path[0]).getPath();
				Element root = Editor.files.getFile(new XMLTranslator(), file, "maps", id + ".xml").getRootElement();

				if(root.getName().equals("world")) {
					uids.addAll(zones.get(0).load(root));
				} else if(root.getName().equals("dungeon")) {
					for(Element level : root.getChildren("level")) {
						uids.addAll(zones.get(Integer.parseInt(level.getAttributeValue("l"))).load(level));
					}
				} else {
					System.out.println("fout in EditableMap.load(" + id + ")");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method removes a zone from this map.
	 * 
	 * @param level	the zone to remove
	 */
	public void removeZone(int level) {
		for(Renderable r : zones.get(level).getScene().getElements()) {
			Instance instance = (Instance)r;
			if(instance instanceof IObject) {	// uids verwijderen
				uids.remove(Integer.parseInt(instance.toElement().getAttributeValue("uid")));
				if(instance.toElement().getName().equals("container")) {	// container inhoud verwijderen
					for(Element e : instance.toElement().getChildren()) {
						uids.remove(Integer.parseInt(e.getAttributeValue("uid")));						
					}
				}
			}
		}
		zones.remove(level);
	}
}
