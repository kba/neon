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

package neon.tools.resources;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import neon.graphics.shapes.JVShape;
import neon.graphics.svg.SVGLoader;
import neon.objects.resources.RData;
import neon.objects.resources.RItem;
import neon.tools.Editor;
import neon.tools.maps.MapEditor;
import neon.tools.resources.Instance;
import org.jdom2.Element;

public class IObject extends Instance {
	public final int uid;
	
	public IObject(RData resource, int x, int y, int z, int uid) {
		super(resource, x, y, z, 1, 1);
		this.uid = uid;
	}

	public IObject(Element properties) {
		super(properties);
		width = 1;
		height = 1;
		uid = Integer.parseInt(properties.getAttributeValue("uid"));
		String id = properties.getAttributeValue("id");
		resource = (RData)Editor.resources.getResource(id);
		if(properties.getName().equals("creature")) {
			z = Byte.MAX_VALUE - 1;
		} else {	// kan "item", "door" of "container" zijn
			z = Byte.MAX_VALUE - 2;
		}
	}
	
	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
		if(resource instanceof RItem && ((RItem)resource).svg != null) {
			if(MapEditor.isVisible(this)) {
				JVShape shape = SVGLoader.loadShape(((RItem)resource).svg);
				shape.setX(x);
				shape.setY(y);
				width = shape.getBounds().width;
				height = shape.getBounds().height;
				shape.paint(graphics, zoom, isSelected);
				if(isSelected) {
					graphics.setPaint(shape.getPaint());
					Rectangle2D rect = new Rectangle2D.Float(x*zoom, y*zoom, width*zoom, height*zoom);
					graphics.draw(rect);
				}
			}				
		} else {
			super.paint(graphics, zoom, isSelected);
		}
	}
	
	public Element toElement() {
		String type = (resource instanceof RItem) ? "item" : "creature";
		Element object = new Element(type);
		object.setAttribute("x", Integer.toString(x));
		object.setAttribute("y", Integer.toString(y));
		object.setAttribute("id", resource.id);
		object.setAttribute("uid", Integer.toString(uid));
		return object;
	}
}
