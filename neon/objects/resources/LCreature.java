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

import java.util.HashMap;
import org.jdom2.Element;

public class LCreature extends RCreature {
	public HashMap<String, Integer> creatures = new HashMap<String, Integer>();
	
	public LCreature(Element e, String... path) {
		super(e.getAttributeValue("id"), path);
		for(Element c : e.getChildren()) {
			creatures.put(c.getAttributeValue("id"), Integer.parseInt(c.getAttributeValue("l")));
		}
	}
	
	public LCreature(String id, String... path) {
		super(id, path);
	}

	public Element toElement() {
		Element list = new Element("list");
		list.setAttribute("id", id);
		
		for(String s : creatures.keySet()) {
			Element creature = new Element("creature");
			creature.setAttribute("id", s);
			creature.setAttribute("l", creatures.get(s).toString());
			list.addContent(creature);
		}
		
		return list;
	}
}
