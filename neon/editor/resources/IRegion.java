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

package neon.editor.resources;

import java.util.ArrayList;

import neon.editor.Editor;
import neon.resources.RData;
import neon.resources.RRegionTheme;
import neon.resources.RScript;
import neon.resources.RTerrain;

import org.jdom2.Element;

public class IRegion extends Instance {
	public RRegionTheme theme;
	public ArrayList<RScript> scripts = new ArrayList<RScript>();
	public String label;
	
	public IRegion(RTerrain terrain, int x, int y, int z, int w, int h) {
		super(terrain, x, y, z, w, h);
	}
	
	public IRegion(Element properties) {
		super(properties);
		resource = (RData)Editor.resources.getResource(properties.getAttributeValue("text"), "terrain");		
		theme = (RRegionTheme)Editor.resources.getResource(properties.getAttributeValue("random"), "theme");
		for(Element script : properties.getChildren("script")) {
			scripts.add(Editor.getStore().getScripts().get(script.getAttributeValue("id")));
		}
		label = properties.getAttributeValue("label");
	}

	public Element toElement() {
		Element region = new Element("region");
		region.setAttribute("x", Integer.toString(x));
		region.setAttribute("y", Integer.toString(y));
		region.setAttribute("w", Integer.toString(width));
		region.setAttribute("h", Integer.toString(height));
		region.setAttribute("l", Integer.toString(z));
		region.setAttribute("text", resource.id);
		if(theme != null) {
			region.setAttribute("random", theme.id);
		}
		for(RScript rs : scripts) {
			Element script = new Element("script");
			script.setAttribute("id", rs.id);
			region.addContent(script);
		}
		if(label != null && !label.isEmpty()) {
			region.setAttribute("label", label);
		}
		return region;
	}
}
