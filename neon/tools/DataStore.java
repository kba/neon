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

package neon.tools;

import java.io.File;
import java.util.*;
import neon.systems.files.StringTranslator;
import neon.systems.files.XMLTranslator;
import neon.tools.resources.RFaction;
import neon.tools.resources.RMap;
import neon.util.MultiMap;
import org.jdom2.Document;
import org.jdom2.Element;
import neon.objects.resources.*;

public class DataStore {
	private HashMap<String, RScript> scripts = new HashMap<String, RScript>();
	private MultiMap<String, String> events = new MultiMap<String, String>();
	private HashMap<String, Mod> mods = new HashMap<String, Mod>();
	private Mod active;
	
	public Mod getActive() {
		return active;
	}
	
	public Mod getMod(String id) {
		return mods.get(id);
	}
	
	public HashMap<String, RScript> getScripts() {
		return scripts;
	}

	public MultiMap<String, String> getEvents() {
		return events;
	}

	public void loadData(String root, boolean active, boolean extension) {
		Mod mod = new Mod(root, loadInfo(root, "main.xml"), loadCC(root, "cc.xml"));
		if(active) {
			this.active = mod;
		}
		
		loadScripts(mod, root, "scripts");
		loadEvents(mod, root, "events.xml");
		loadQuests(mod, root, "quests");
		loadMagic(mod, root, "spells.xml");
		loadItems(mod, root, "objects", "items.xml");
		loadMagic(mod, root, "objects", "alchemy.xml");
		loadFactions(mod, root, "factions.xml");
		loadMagic(mod, root, "signs.xml");
		loadMagic(mod, root, "tattoos.xml");
		loadTerrain(mod, root, "terrain.xml");
		loadItems(mod, root, "objects", "crafting.xml");
		loadThemes(mod, root, "themes", "zones.xml");
		loadThemes(mod, root, "themes", "regions.xml");
		loadThemes(mod, root, "themes", "dungeons.xml");
		loadMaps(mod, root, "maps");	// maps moeten na themes geladen worden
		loadCreatures(mod, root, "objects", "monsters.xml");
		loadCreatures(mod, root, "objects", "npc.xml");
		
		mods.put(mod.get("id"), mod);
	}
	
	private void loadEvents(Mod mod, String... file) {
		try {
			for(Element event : Editor.files.getFile(new XMLTranslator(), file).getRootElement().getChildren()) {
				events.put(event.getAttributeValue("script"), event.getAttributeValue("tick"));
			}
		} catch (NullPointerException e) {}
	}

	private void loadScripts(Mod mod, String... file) {
		String[] path = new String[file.length + 1];
		try {
			for(String id : Editor.files.listFiles(file)) {
				System.arraycopy(file, 0, path, 0, file.length);
				id = id.substring(id.lastIndexOf("/") + 1);
				id = id.substring(id.lastIndexOf(File.separator) + 1);
				path[file.length] = id;
				String script = Editor.files.getFile(new StringTranslator(), path);
				id = id.replace(".js", "");
				scripts.put(id, new RScript(id, script, mod.get("id")));
			}
		} catch (NullPointerException e) {}
	}
	
	private Element loadInfo(String... file) {
		Element info;
		try {
			info = Editor.files.getFile(new XMLTranslator(), file).getRootElement();
			info.detach();
		} catch (NullPointerException e) {	// file bestaat niet
			info = new Element("master");
			info.setAttribute("id", file[0]);
			info.addContent(new Element("title"));
			info.addContent(new Element("currency"));
		}
		return info;
	}
	
	private Element loadCC(String... file) {
		Element cc;
		try {
			cc = Editor.files.getFile(new XMLTranslator(), file).getRootElement();
			cc.detach();
		} catch (NullPointerException e) {	// file bestaat niet
			cc = new Element("root");
			cc.addContent(new Element("races"));
			cc.addContent(new Element("items"));
			cc.addContent(new Element("spells"));
			cc.addContent(new Element("map"));
		}
		return cc;
	}
	
	private void loadMaps(Mod mod, String... file) {
		String[] path = new String[file.length + 1];
		try {
			for(String s : Editor.files.listFiles(file)) {
				System.arraycopy(file, 0, path, 0, file.length);
				// substrings moeten er alebei instaan voor jars
				s = s.substring(s.lastIndexOf("/") + 1);
				s = s.substring(s.lastIndexOf(File.separator) + 1);
				path[file.length] = s;
				Element map = Editor.files.getFile(new XMLTranslator(), path).getRootElement();
				Editor.resources.addResource(new RMap(s.replace(".xml", ""), map, mod.get("id")), "maps");
			}
		} catch (NullPointerException e) {}
	}

	private void loadQuests(Mod mod, String... file) {
		String[] path = new String[file.length + 1];
		try {
			Collection<String> files = Editor.files.listFiles(file);
			for(String quest : files) {
				System.arraycopy(file, 0, path, 0, file.length);
				quest = quest.substring(quest.lastIndexOf("/") + 1);
				quest = quest.substring(quest.lastIndexOf(File.separator) + 1);
				path[file.length] = quest;
				Element root = Editor.files.getFile(new XMLTranslator(), path).getRootElement();
				String id = quest.replace(".xml", "");
				Editor.resources.addResource(new RQuest(id, root, mod.get("id")), "quest");
			}
		} catch (NullPointerException e) {}
	}

	private void loadMagic(Mod mod, String... path) {
		try {
			Document doc = Editor.files.getFile(new XMLTranslator(), path);
			for(Element e : doc.getRootElement().getChildren()) {
				switch(e.getName()) {
				case "sign": Editor.resources.addResource(new RSign(e, mod.get("id")), "magic"); break;
				case "tattoo": Editor.resources.addResource(new RTattoo(e, mod.get("id")), "magic"); break;
				case "recipe": Editor.resources.addResource(new RRecipe(e, mod.get("id")), "magic"); break;
				case "list": Editor.resources.addResource(new LSpell(e, mod.get("id")), "magic"); break;
				case "power": Editor.resources.addResource(new RSpell.Power(e, mod.get("id")), "magic"); break;
				case "enchant": Editor.resources.addResource(new RSpell.Enchantment(e, mod.get("id")), "magic"); break;
				default: Editor.resources.addResource(new RSpell(e, mod.get("id")), "magic"); break;
				}
			}
		} catch (NullPointerException e) {}
	}

	private void loadCreatures(Mod mod, String... path) {
		try {
			Document doc = Editor.files.getFile(new XMLTranslator(), path);
			for(Element e : doc.getRootElement().getChildren()) {
				switch(e.getName()) {
				case "list": Editor.resources.addResource(new LCreature(e, mod.get("id"))); break;
				case "npc": Editor.resources.addResource(new RPerson(e, mod.get("id"))); break;
				case "group": break;
				default: Editor.resources.addResource(new RCreature(e, mod.get("id"))); break;
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void loadFactions(Mod mod, String... path) {
		try {
			Document doc = Editor.files.getFile(new XMLTranslator(), path);
			for(Element e : doc.getRootElement().getChildren()) {
				Editor.resources.addResource(new RFaction(e, mod.get("id")), "faction");
			}
		} catch (NullPointerException e) {}
	}

	private void loadTerrain(Mod mod, String... path) {
		try {
			Document doc = Editor.files.getFile(new XMLTranslator(), path);
			for(Element e : doc.getRootElement().getChildren()) {
				Editor.resources.addResource(new RTerrain(e, mod.get("id")), "terrain");
			}
		} catch (NullPointerException e) {}
	}

	private void loadItems(Mod mod, String... path) {
		try {
			Document doc = Editor.files.getFile(new XMLTranslator(), path);
			for(Element e : doc.getRootElement().getChildren()) {
				switch(e.getName()) {
				case "list": Editor.resources.addResource(new LItem(e, mod.get("id"))); break;
				case "book": 
				case "scroll": Editor.resources.addResource(new RItem.Text(e, mod.get("id"))); break;
				case "armor":
				case "clothing": Editor.resources.addResource(new RClothing(e, mod.get("id"))); break;
				case "weapon": Editor.resources.addResource(new RWeapon(e, mod.get("id"))); break;
				case "craft": Editor.resources.addResource(new RCraft(e, mod.get("id"))); break;
				case "door": Editor.resources.addResource(new RItem.Door(e, mod.get("id"))); break;
				case "potion": Editor.resources.addResource(new RItem.Potion(e, mod.get("id"))); break;
				case "container": Editor.resources.addResource(new RItem.Container(e, mod.get("id"))); break;
				default: Editor.resources.addResource(new RItem(e, mod.get("id"))); break;
				}
			}
		} catch (NullPointerException e) {}
	}

	private void loadThemes(Mod mod, String... path) {
		try {
			Document doc = Editor.files.getFile(new XMLTranslator(), path);
			for(Element e : doc.getRootElement().getChildren()) {
				switch(e.getName()) {
				case "dungeon": Editor.resources.addResource(new RDungeonTheme(e, mod.get("id")), "theme"); break;
				case "region": Editor.resources.addResource(new RRegionTheme(e, mod.get("id")), "theme"); break;
				case "zone": Editor.resources.addResource(new RZoneTheme(e, mod.get("id")), "theme"); break;
				}
			}
		} catch (NullPointerException e) {}
	}
}
