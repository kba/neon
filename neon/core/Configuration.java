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

package neon.core;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import de.muntjak.tinylookandfeel.Theme;
import neon.core.event.EventTracker;
import neon.systems.files.XMLTranslator;
import java.awt.Point;
import javax.swing.UIManager;

public class Configuration {
	public static final String version = "0.4.1";	// huidige versie
	public static boolean audio = false;	// audio aan of uit?
	public static boolean gThread = true;	// terrain generation threaded of niet?

	private ArrayList<String> playableRaces;
	private ArrayList<String> startingItems;
	private ArrayList<String> startingSpells;
	private Point startPos;
	private String[] startMap;
	private int startZone = 0;	// default
	private HashMap<String, String> properties = new HashMap<String, String>();
	private HashMap<String, Collection<String[]>> mods;
	private Properties strings;
	
	/**
	 * Loads a configuration file, containing a list of mods.
	 * 
	 * @param ini	a configuration file
	 */
	public Configuration(String ini, EventTracker tracker) {
		// configuratie inladen
		Document doc = new Document();
		try (FileInputStream in = new FileInputStream(ini)){
			doc = new SAXBuilder().build(in);
		} catch(Exception e) {
			e.printStackTrace();
		}

		// look and feel setten
		try {
			Theme.loadTheme(new File("data/neon.theme"));
			UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}					
		
		// taal
		String lang = doc.getRootElement().getChild("lang").getText();
		strings = new Properties();
		try {
			strings.load(new FileReader("data/locale/locale." + lang));
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		// logging
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		try {
			Handler handler = new FileHandler("neon.log");
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		} catch(Exception e) {
			e.printStackTrace();
		}
		String level = doc.getRootElement().getChildText("log");
		switch(level) {
		case "off": logger.setLevel(Level.OFF); break;
		case "severe": logger.setLevel(Level.SEVERE); break;
		case "warning": logger.setLevel(Level.WARNING); break;
		case "info": logger.setLevel(Level.INFO); break;
		case "config": logger.setLevel(Level.CONFIG); break;
		case "fine": logger.setLevel(Level.FINE); break;
		case "finer": logger.setLevel(Level.FINER);	break;
		case "finest": logger.setLevel(Level.FINEST); break;
		case "all": logger.setLevel(Level.ALL); break;
		}

		// alle data dirs en jars afgaan
		mods = new HashMap<String, Collection<String[]>>();
		Element files = doc.getRootElement().getChild("files");
		for(Element file : files.getChildren()) {
			new ModLoader(file, this, tracker).loadMod();
		}
		
		// keyboard inlezen
		KeyConfig.setKeys(doc.getRootElement().getChild("keys"));
		
		// threading
		gThread = doc.getRootElement().getChild("threads").getAttributeValue("generate").equals("on");
		logger.config("Map generation thread: " + gThread);
	}
	
	/**
	 * Adds a mod to list.
	 * 
	 * @param mod
	 * @param maps	a {@code Collection} of all maps in the given mod
	 */
	public void addMod(String mod, Collection<String[]> maps) {
		mods.put(mod, maps);
	}
	
	/**
	 * Returns all mods and all maps in these mods.
	 * 
	 * @return
	 */
	public HashMap<String, Collection<String[]>> getMods() {
		return mods;
	}
	
	/**
	 * @return	a list with all playable races
	 */
	public ArrayList<String> getPlayableRaces() {
		return playableRaces;
	}
		
	/**
	 * @return	a list of items the player starts with
	 */
	public ArrayList<String> getStartingItems() {
		return startingItems;
	}
	
	/**
	 * @return	a list of spells the player starts with
	 */
	public ArrayList<String> getStartingSpells() {
		return startingSpells;
	}

	/**
	 * @return	the starting position of the player character
	 */
	public Point getStartPosition() {
		return startPos;
	}
	
	/**
	 * @return	the starting map zone
	 */
	public int getStartZone() {
		return startZone;
	}
	
	/**
	 * @return	the starting map
	 */
	public String[] getStartMap() {
		return startMap;
	}
	
	/**
	 * @param property
	 * @return	the requested property
	 */
	public String getProperty(String property) {
		return properties.get(property);
	}
	
	/**
	 * Sets the string value of the given property.
	 * 
	 * @param property
	 * @param value
	 */
	public void setProperty(String property, String value) {
		properties.put(property, value);
	}
	
	/**
	 * Return the string value with the given name.
	 * 
	 * @param name
	 * @param backup
	 * @return
	 */
	public String getString(String name, String backup) {
		return strings.getProperty(name, backup);
	}
	
	/**
	 * Initializes all character creation data.
	 * 
	 * @param file
	 */
	public void initCC(String... file) {
		Element cc = Engine.getFileSystem().getFile(new XMLTranslator(), file).getRootElement();
		int x = Integer.parseInt(cc.getChild("map").getAttributeValue("x"));
		int y = Integer.parseInt(cc.getChild("map").getAttributeValue("y"));
		if(cc.getChild("map").getAttributeValue("z") != null) {
			startZone = Integer.parseInt(cc.getChild("map").getAttributeValue("z"));
		}
		startPos = new Point(x, y);
		String[] path = {file[0], "maps", cc.getChild("map").getAttributeValue("path") + ".xml"};
		startMap = path;
		playableRaces = new ArrayList<String>();
		for(Element e : cc.getChildren("race")) {
			playableRaces.add(e.getText());
		}
		startingItems = new ArrayList<String>();
		for(Element e : cc.getChildren("item")) {
			startingItems.add(e.getText());
		}
		startingSpells = new ArrayList<String>();
		for(Element e : cc.getChildren("spell")) {
			startingSpells.add(e.getText());
		}
	}
}