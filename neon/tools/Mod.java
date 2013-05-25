/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jdom2.Element;

public class Mod {
	ArrayList<String> ccItems = new ArrayList<String>();
	ArrayList<String> ccRaces = new ArrayList<String>();
	ArrayList<String> ccSpells = new ArrayList<String>();
	private String path;
	private HashMap<String, String> info = new HashMap<String, String>();
	
	public Mod(String path, Element main, Element cc) {
		this.path = path;
		
		// main.xml
		info.put("id", main.getAttributeValue("id"));
		info.put("master", main.getChildText("master"));
		if(main.getChildText("title") != null) {
			info.put("title", main.getChildText("title"));
		}
		if(main.getChild("currency") != null) {
			info.put("big", main.getChild("currency").getAttributeValue("big"));
			info.put("small", main.getChild("currency").getAttributeValue("small"));
		}
		
		// cc.xml
		if(cc != null) {	// hier strings, want resources zijn nog niet geladen
			for(Element race : cc.getChildren("race")) {
				ccRaces.add(race.getText());
			}
			for(Element item : cc.getChildren("item")) {
				ccItems.add(item.getText());
			}
			for(Element spell : cc.getChildren("spell")) {
				ccSpells.add(spell.getText());				
			}
			if(cc.getChild("map") != null) {
				info.put("map", cc.getChild("map").getAttributeValue("path"));
				info.put("x", cc.getChild("map").getAttributeValue("x"));
				info.put("y", cc.getChild("map").getAttributeValue("y"));
				info.put("z", cc.getChild("map").getAttributeValue("z"));
			}
		}
	}
	
	public Element getMainElement() {
		Element main = new Element(isExtension() ? "extension" :  "master");
		main.setAttribute("id", info.get("id"));
		if(info.get("title") != null) {
			main.addContent(new Element("title").setText(info.get("title")));
		}
		if(info.get("big") != null || info.get("small") != null) {
			Element currency = new Element("currency");
			currency.setAttribute("big", info.get("big"));
			currency.setAttribute("small", info.get("small"));
			main.addContent("currency");
		}
		return main;
	}
	
	public Element getCCElement() {
		Element cc = new Element("cc");
		for(String item : ccItems) {
			cc.addContent(new Element("item").setText(item));
		}
		for(String spell : ccSpells) {
			cc.addContent(new Element("spell").setText(spell));
		}
		for(String race : ccRaces) {
			cc.addContent(new Element("race").setText(race));
		}
		if(info.get("map") != null) {
			Element map = new Element("map");
			map.setAttribute("path", info.get("map"));
			map.setAttribute("x", info.get("x"));
			map.setAttribute("y", info.get("y"));
			if(info.get("z") != null) {
				map.setAttribute("z", info.get("z"));
			}
			cc.addContent(map);
		}
		return cc;
	}
	
	public List<String> getList(String key) {
		ArrayList<String> list = new ArrayList<String>();
		if(Editor.getStore().getMod(info.get("master")) != null) {
			list.addAll(Editor.getStore().getMod(info.get("master")).getList(key));
		}
		if(key.equals("items")) {
			list.addAll(ccItems);
		} else if(key.equals("spells")) {
			list.addAll(ccSpells);
		} else if (key.equals("races")) {
			list.addAll(ccRaces);
		} 
		return list;
	}
	
	public boolean isExtension() {
		return info.get("master") != null;
	}
	
	public String get(String key) {
		if(info.get(key) != null) {
			return info.get(key);			
		} else if(Editor.getStore().getMod(info.get("master")) != null) {
			return Editor.getStore().getMod(info.get("master")).get(key);
		} else {
			return null;
		}
	}
	
	public void set(String key, String value) {
		if(Editor.getStore().getMod(info.get("master")) != null) {
			if(Editor.getStore().getMod(info.get("master")).get(key) != value) {
				info.put(key, value);
			} else {
				info.remove(key);
			}
		} else {
			info.put(key, value);
		}
	}
	
	public String getPath() {
		return path;
	}
}
