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

import org.jdom2.Element;

public class RDungeonTheme extends RData {
	public int min, max, branching;
	public String zones;

	public RDungeonTheme(String id, String... path) {
		super(id, path);
	}

	public RDungeonTheme(Element props, String... path) {
		super(props.getAttributeValue("id"), path);
		min = Integer.parseInt(props.getAttributeValue("min"));
		max = Integer.parseInt(props.getAttributeValue("max"));
		branching = Integer.parseInt(props.getAttributeValue("b"));
		zones = props.getAttributeValue("zones");
	}

	public Element toElement() {
		Element theme = new Element("dungeon");
		theme.setAttribute("id", id);
		theme.setAttribute("min", Integer.toString(min));
		theme.setAttribute("max", Integer.toString(max));
		theme.setAttribute("b", Integer.toString(branching));
		theme.setAttribute("zones", zones);
		return theme;
	}
}
