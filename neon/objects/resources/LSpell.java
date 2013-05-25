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

public class LSpell extends RSpell {
	public HashMap<String, Integer> spells = new HashMap<String, Integer>();

	public LSpell(Element e, String... path) {
		super(e.getAttributeValue("id"), SpellType.SPELL, path);
		for(Element s : e.getChildren()) {
			spells.put(s.getAttributeValue("id"), Integer.parseInt(s.getAttributeValue("l")));
		}
	}

	public LSpell(String id, String path) {
		super(id, SpellType.SPELL, path);
	}

	public Element toElement() {
		Element list = new Element("list");
		list.setAttribute("id", id);
		
		for(String s : spells.keySet()) {
			Element spell = new Element("spell");
			spell.setAttribute("id", s);
			spell.setAttribute("l", spells.get(s).toString());
			list.addContent(spell);
		}
		
		return list;
	}
}
