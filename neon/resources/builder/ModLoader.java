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

package neon.resources.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import neon.core.Engine;
import neon.core.event.TaskQueue;
import neon.resources.*;
import neon.resources.quest.RQuest;
import neon.systems.files.FileSystem;
import neon.systems.files.StringTranslator;
import neon.systems.files.XMLTranslator;

import org.jdom2.*;

public class ModLoader {
	private String path;
	private TaskQueue queue;
	private FileSystem files;
	
	public ModLoader(String mod, TaskQueue queue, FileSystem files) {
		this.queue = queue;
		this.files = files;
		
		try {
			path = files.mount(mod);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public RMod loadMod(CGame game, CClient client) {
		// main.xml laden
		Element mod = files.getFile(new XMLTranslator(), path, "main.xml").getRootElement();

		// cc.xml laden
		Element cc = null;
		if(files.exists(path, "cc.xml")) {
			cc = files.getFile(new XMLTranslator(), path, "cc.xml").getRootElement();
		} 

		RMod rmod = new RMod(mod, cc);
		rmod.addMaps(initMaps(path, "maps"));

		initMain(client, mod);
		if(mod.getName().equals("extension")) {
			ResourceManager resources = Engine.getResources();
			if(!resources.hasResource(mod.getChild("master").getText(), "mods")) {
				System.err.println("Extension master not found: " + path + ".");
			}
		}

		// terrain
		if(files.exists(path, "terrain.xml")) {
			initTerrain(path, "terrain.xml");
		}

		// books
		if(files.listFiles(path, "books") != null) {
			initBooks(path, "books");	// laden voor items, anders vindt book zijn text niet
		}
		
		// items
		initItems(path, "objects", "items.xml");		// items
		initItems(path, "objects", "crafting.xml");		// crafting
		
		// themes (na terrain en items, want themes bevatten terrain en items)
		initThemes(path, "themes", "dungeons.xml");	// dungeons
		initThemes(path, "themes", "zones.xml");	// zones
		initThemes(path, "themes", "regions.xml");	// regions
		
		// creatures
		initCreatures(path, "objects", "monsters.xml");	// species
		initCreatures(path, "objects", "npc.xml");		// people
		
		
		// scripts
		if(files.listFiles(path, "scripts") != null) {
			initScripts(path, "scripts");
		}
		
		// events
		if(files.exists(path, "events.xml")) {
			initTasks(path, "events.xml");
		}
		
		// character creation
		if(files.exists(path, "cc.xml")) {
			initCC(game, path, "cc.xml");
		}
		
		// random quests
		if(files.listFiles(path, "quests") != null) {
			initQuests(path, "quests");
		}
		
		// magic
		initMagic(path, "spells.xml");				// spells
		initMagic(path, "objects", "alchemy.xml");	// alchemy
		initMagic(path, "signs.xml");				// birth signs
		initMagic(path, "tattoos.xml");				// tattoos
		
		return rmod;
	}

	private void initMain(CClient client, Element info) {
		if(info.getChild("title") != null) {
			client.setTitle(info.getChild("title").getText());			
		}
		if(info.getChild("currency") != null) {
			if(info.getChild("currency").getAttributeValue("big") != null) {
				client.setBig(info.getChild("currency").getAttributeValue("big"));
			}
			if(info.getChild("currency").getAttributeValue("small") != null) {
				client.setSmall(info.getChild("currency").getAttributeValue("small"));
			}
		}		
	}
	
	private void initQuests(String... file) {
		try {
			for(String s : files.listFiles(file)) {
				s = s.substring(s.lastIndexOf("/") + 1);
				String quest = s.substring(s.lastIndexOf(File.separator) + 1);
				Document doc = files.getFile(new XMLTranslator(), path, "quests", quest);
				Engine.getResources().addResource(new RQuest(quest, doc.getRootElement()), "quest");
			}
		} catch(Exception e) {	// gebeurt bij .svn directory
			e.printStackTrace();
			Engine.getLogger().fine("Error loading quest in mod " + path);
		}
	}

	private void initBooks(String... file) {
		try {
			for(String s : files.listFiles(file)) {
				s = s.substring(s.lastIndexOf("/") + 1);
				String id = s.substring(s.lastIndexOf(File.separator) + 1);
				Resource book = new RText(id, files, path, "books", id);
				Engine.getResources().addResource(book, "text");
			}
		} catch(Exception e) { 
			Engine.getLogger().fine("No books in mod " + path);
		}
	}

	private ArrayList<String[]> initMaps(String... file) {
		ArrayList<String[]> maps = new ArrayList<String[]>();
		for(String s : files.listFiles(file)) {
			/* gefoefel met separators om jar of folder files te krijgen:
			 * substrings moeten er allebei instaan als het om jars gaat
			 */
			s = s.substring(s.lastIndexOf("/") + 1);
			s = s.substring(s.lastIndexOf(File.separator) + 1);
			String[] map = {path, "maps", s};
			maps.add(map);
		}
		return maps;
	}

	private void initCreatures(String... file) {
		if(files.exists(file)) {
			Element creatures = files.getFile(new XMLTranslator(), file).getRootElement();
			for(Element c : creatures.getChildren()) {
				switch(c.getName()) {
				case "npc": Engine.getResources().addResource(new RPerson(c)); break;
				case "list": Engine.getResources().addResource(new LCreature(c)); break;
				default: Engine.getResources().addResource(new RCreature(c)); break;
				}
			}
		}
	}

	private void initItems(String... file) {
		if(files.exists(file)) {
			Element items = files.getFile(new XMLTranslator(), file).getRootElement();
			for(Element e : items.getChildren()) {
				switch(e.getName()) {
				case "book":
				case "scroll": Engine.getResources().addResource(new RItem.Text(e)); break;
				case "weapon": Engine.getResources().addResource(new RWeapon(e)); break;
				case "craft": Engine.getResources().addResource(new RCraft(e)); break;
				case "door": Engine.getResources().addResource(new RItem.Door(e)); break;
				case "potion": Engine.getResources().addResource(new RItem.Potion(e)); break;
				case "container": Engine.getResources().addResource(new RItem.Container(e)); break;
				case "list": Engine.getResources().addResource(new LItem(e)); break;
				case "armor":
				case "clothing": Engine.getResources().addResource(new RClothing(e)); break;
				default: Engine.getResources().addResource(new RItem(e)); break;
				}
			}
		}
	}

	private void initTerrain(String... file) {
		Element terrain = files.getFile(new XMLTranslator(), file).getRootElement();
		for(Element e : terrain.getChildren()) {
			Engine.getResources().addResource(new RTerrain(e), "terrain");
		}		
	}
	
	private void initThemes(String... file) {
		if(files.exists(file)) {
			Element themes = files.getFile(new XMLTranslator(), file).getRootElement();
			for(Element theme : themes.getChildren()) {
				switch(theme.getName()) {
				case "dungeon": Engine.getResources().addResource(new RDungeonTheme(theme), "theme"); break;
				case "zone": Engine.getResources().addResource(new RZoneTheme(theme), "theme"); break;
				case "region": Engine.getResources().addResource(new RRegionTheme(theme), "theme"); break;
				}
			}
		}
	}
	
	private void initMagic(String... file) {
		if(files.exists(file)) {
			Element resources = files.getFile(new XMLTranslator(), file).getRootElement();
			for(Element resource : resources.getChildren()) {
				switch(resource.getName()) {
				case "sign": Engine.getResources().addResource(new RSign(resource), "magic"); break;
				case "tattoo": Engine.getResources().addResource(new RTattoo(resource), "magic"); break;
				case "recipe": Engine.getResources().addResource(new RRecipe(resource), "magic"); break;
				case "list": Engine.getResources().addResource(new LSpell(resource), "magic"); break;
				case "power": Engine.getResources().addResource(new RSpell.Power(resource), "magic"); break;
				case "enchant": Engine.getResources().addResource(new RSpell.Enchantment(resource), "magic"); break;
				default: Engine.getResources().addResource(new RSpell(resource), "magic"); break;				
				}
			}
		}
	}

	private void initScripts(String... file) {
		try {
			for(String s : files.listFiles(file)) {
				s = s.substring(s.lastIndexOf("/") + 1);
				s = s.substring(s.lastIndexOf(File.separator) + 1);
				String[] path = new String[file.length + 1];
				path[file.length] = s;
				System.arraycopy(file, 0, path, 0, file.length);
				RScript script = new RScript(s.replaceAll(".js", ""), files.getFile(new StringTranslator(), path));
				Engine.getResources().addResource(script, "script");
			}
		} catch(Exception e) { 
			Engine.getLogger().fine("No scripts in mod " + path);
		}
	}
	
	/*
	 * Initializes all character creation data.
	 * 
	 * @param file
	 */
	private void initCC(CGame game, String... file) {
		Element cc = files.getFile(new XMLTranslator(), file).getRootElement();
		int x = Integer.parseInt(cc.getChild("map").getAttributeValue("x"));
		int y = Integer.parseInt(cc.getChild("map").getAttributeValue("y"));
		if(cc.getChild("map").getAttributeValue("z") != null) {
			game.setStartZone(Integer.parseInt(cc.getChild("map").getAttributeValue("z")));
		}
		game.getStartPosition().setLocation(x, y);
		String[] path = {file[0], "maps", cc.getChild("map").getAttributeValue("path") + ".xml"};
		game.setStartMap(path);
		for(Element e : cc.getChildren("race")) {
			game.getPlayableRaces().add(e.getText());
		}
		for(Element e : cc.getChildren("item")) {
			game.getStartingItems().add(e.getText());
		}
		for(Element e : cc.getChildren("spell")) {
			game.getStartingSpells().add(e.getText());
		}
	}
	
	private void initTasks(String... file) {
		Document doc = files.getFile(new XMLTranslator(), file);
		for(Element e : doc.getRootElement().getChildren()) {
			String[] ticks = e.getAttributeValue("tick").split(":");
			RScript rs = (RScript)Engine.getResources().getResource(e.getAttributeValue("script"), "script");
			if(ticks.length == 1) {	// ene tick: gewoon toevoegen op dat tijdstip
				queue.add(rs.script, Integer.parseInt(ticks[0]), 0, 0);
			} else if(ticks.length == 2) {	// twee ticks
				if(!ticks[0].isEmpty()) { 
					ticks[0] = "0"; 
				}
				if(!ticks[1].isEmpty()) {	// indien periode 0, maar 1 keer uitvoeren
					queue.add(rs.script, Integer.parseInt(ticks[0]), 0, 0);
				} else {	// anders met periode vanaf start
					queue.add(rs.script, Integer.parseInt(ticks[0]), Integer.parseInt(ticks[1]), 0);
				}
			} else if(ticks.length == 3) {	// drie ticks
				if(!ticks[2].isEmpty()) { 
					ticks[2] = "0"; 
				}
				if(!ticks[1].isEmpty() || ticks[1].equals("0")) {	// indien periode 0, enkel op begin en eind uitvoeren
					queue.add(rs.script, Integer.parseInt(ticks[0]), 0, 0);
					queue.add(rs.script, Integer.parseInt(ticks[2]), 0, 0);
				} else {	// anders met periode vanaf start tot stop
					queue.add(rs.script, Integer.parseInt(ticks[0]), 
							Integer.parseInt(ticks[1]), Integer.parseInt(ticks[2]));
				}
			}
		}		
	}
}
