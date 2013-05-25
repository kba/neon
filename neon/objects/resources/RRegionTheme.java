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

package neon.objects.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Element;

public class RRegionTheme extends RData {
	public String floor;
	public Type type;
	public String door, wall;
	public HashMap<String, Integer> creatures = new HashMap<String, Integer>();
	public List<Element> features = new ArrayList<Element>();
	public HashMap<String, Integer> vegetation = new HashMap<String, Integer>();
	
	public RRegionTheme(String id, String... path) {
		super(id, path);
	}

	public RRegionTheme(Element theme, String... path) {
		super(theme.getAttributeValue("id"), path);
		String[] data = theme.getAttributeValue("random").split(";");

		for(Element creature : theme.getChildren("creature")) {
			creatures.put(creature.getText(), Integer.parseInt(creature.getAttributeValue("n")));
		}
		
		// nieuwe arraylist om concurrentmodificationexceptions te vermijden
		for(Element feature : new ArrayList<Element>(theme.getChildren("feature"))) {
			features.add(feature.detach());			
		}
		
		floor = theme.getAttributeValue("floor");
		type = Type.valueOf(data[0]);
		for(Element plant : theme.getChildren("plant")) {
			int abundance = Integer.parseInt(plant.getAttributeValue("a"));
			vegetation.put(plant.getText(), abundance);
		}
		
		switch(type) {	// mottig switch met ontbrekende breaks
		case town:
		case town_big: 
		case town_small: 
			wall = data[1];
			door = data[2];
			break;
		default:
			break;
		}
	}

	public Element toElement() {
		Element theme = new Element("region");
		theme.setAttribute("id", id);

		if(floor != null) {
			theme.setAttribute("floor", floor);
		}

		for(Map.Entry<String, Integer> entry : creatures.entrySet()) {
			Element creature = new Element("creature");
			creature.setText(entry.getKey());
			creature.setAttribute("n", Integer.toString(entry.getValue()));
			theme.addContent(creature);
		}

		for(Map.Entry<String, Integer> plant : vegetation.entrySet()) {
			Element veg = new Element("plant");
			veg.setText(plant.getKey());
			veg.setAttribute("a", Integer.toString(plant.getValue()));
			theme.addContent(veg);
		}
		
		String random = type.toString() + ";";
		switch(type) {
		case town:
		case town_big: 
		case town_small: 
			random += (wall + ";" + door.toString());
			break;
		default:
			break;
		}
		theme.setAttribute("random", random);
		return theme;
	}
	
	public enum Type {
		town, town_small, town_big, PLAIN, TERRACE, RIDGES, CHAOTIC, BEACH;
	}
}
