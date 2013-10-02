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

package neon.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jdom2.Element;

public class RZoneTheme extends RData {
	public String type, floor, walls, doors;
	public int min, max;
	public HashMap<String, Integer> creatures = new HashMap<String, Integer>();
	public HashMap<String, Integer> items = new HashMap<String, Integer>();
	public ArrayList<Object[]> features = new ArrayList<Object[]>();

	public RZoneTheme(String id, String... path) {
		super(id, path);
	}

	public RZoneTheme(Element props, String... path) {
		super(props.getAttributeValue("id"), path);
		String[] params = props.getAttributeValue("type").split(";");
		type = params[0];
		floor = params[1];
		walls = params[2];
		doors = params[3];
		min = Integer.parseInt(props.getAttributeValue("min"));
		max = Integer.parseInt(props.getAttributeValue("max"));

		for(Element creature : props.getChildren("creature")) {
			creatures.put(creature.getText(), Integer.parseInt(creature.getAttributeValue("n")));
		}
		
		for(Element item : props.getChildren("item")) {
			items.put(item.getText(), Integer.parseInt(item.getAttributeValue("n")));
		}
		
		for(Element feature : props.getChildren("feature")) {
			Object[] data = {feature.getAttributeValue("t"), feature.getText(), 
					Integer.parseInt(feature.getAttributeValue("s")), Integer.parseInt(feature.getAttributeValue("n"))};
			features.add(data);
		}	
	}

	public Element toElement() {
		Element theme = new Element("zone");
		theme.setAttribute("id", id);
		theme.setAttribute("min", Integer.toString(min));
		theme.setAttribute("max", Integer.toString(max));
		theme.setAttribute("type", type.toString() + ";" + floor + ";" + walls + ";" + doors);
		
		for(Map.Entry<String, Integer> entry : creatures.entrySet()) {
			Element creature = new Element("creature");
			creature.setText(entry.getKey());
			creature.setAttribute("n", Integer.toString(entry.getValue()));
			theme.addContent(creature);
		}
		for(Map.Entry<String, Integer> entry : items.entrySet()) {
			Element item = new Element("item");
			item.setText(entry.getKey());
			item.setAttribute("n", Integer.toString(entry.getValue()));
			theme.addContent(item);
		}
		for(Object[] data : features) {
			Element feature = new Element("feature");
			feature.setAttribute("t", data[0].toString());
			feature.setText(data[1].toString());
			feature.setAttribute("s", data[2].toString());
			feature.setAttribute("n", data[3].toString());
			theme.addContent(feature);
		}
		
		return theme;
	}
}
