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

import neon.objects.property.Ability;
import org.jdom2.Element;

public class RTattoo extends RData {
	public Ability ability;
	public int magnitude;
	public int cost;

	public RTattoo(String id, String... path) {
		super(id, path);
		name = id;
	}

	public RTattoo(Element tattoo, String... path) {
		super(tattoo, path);
		ability = Ability.valueOf(tattoo.getAttributeValue("ability").toUpperCase());
		magnitude =	Integer.parseInt(tattoo.getAttributeValue("size"));
		cost = Integer.parseInt(tattoo.getAttributeValue("cost"));
		if(tattoo.getAttribute("name") != null) {
			name = tattoo.getAttributeValue("name");
		} else {
			name = id;
		}
	}

	public Element toElement() {
		Element tattoo = new Element("tattoo");
		tattoo.setAttribute("id", id);
		tattoo.setAttribute("ability", ability.toString());
		tattoo.setAttribute("size", Integer.toString(magnitude));
		tattoo.setAttribute("cost", Integer.toString(cost));
		return tattoo;
	}
}
