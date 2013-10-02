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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import neon.editor.Editor;
import neon.editor.maps.MapEditor;
import neon.resources.RCreature;
import neon.resources.RPerson;
import neon.util.ColorFactory;
import neon.util.TextureFactory;
import org.jdom2.Element;

public class IPerson extends IObject {
	private RCreature species;
	
	public IPerson(Element properties) {
		super(properties);
		resource = (RPerson)Editor.resources.getResource(properties.getAttributeValue("id"));
		species = (RCreature)Editor.resources.getResource(((RPerson)resource).species);
	}
	
	public IPerson(RPerson resource, int x, int y, int z, Element e) {
		super(resource, x, y, z, 0);
		this.resource = resource;
	}

	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
		if(MapEditor.isVisible(this)) {
			Color color = ColorFactory.getColor(species.color);
			Rectangle2D rect = new Rectangle2D.Float(x*zoom, y*zoom, width*zoom, height*zoom);
			if(zoom < 1) {
				graphics.setPaint(color);
			} else {
				graphics.setPaint(TextureFactory.getTexture(species.text, (int)zoom, species.color));
			}
			graphics.fill(rect);
			if(isSelected) {
				graphics.setPaint(color);
				graphics.draw(rect);
			}
		}		
	}
}
