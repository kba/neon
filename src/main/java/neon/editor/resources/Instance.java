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

import neon.editor.maps.MapEditor;
import neon.resources.RData;
import neon.ui.graphics.Renderable;
import neon.util.ColorFactory;
import neon.util.TextureFactory;
import org.jdom2.Element;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public abstract class Instance implements Renderable {
	private static AlphaComposite alphaOn = AlphaComposite.getInstance(AlphaComposite.SRC_OVER).derive(0.5f);
	private static AlphaComposite alphaOff = AlphaComposite.getInstance(AlphaComposite.SRC);
	
	public int x, y, z, width, height;
	public RData resource;
	public boolean isCut = false;

	public Instance(RData resource, int x, int y, int z, int w, int h) {
		this.resource = resource;
		this.x = x;
		this.y = y;
		this.z = z;
		width = w;
		height = h;
	}
	
	public Instance(Element properties) {
		x = Integer.parseInt(properties.getAttributeValue("x"));
		y = Integer.parseInt(properties.getAttributeValue("y"));
		if(properties.getAttribute("w") != null) {
			width = Integer.parseInt(properties.getAttributeValue("w"));
		} 
		if(properties.getAttribute("h") != null) {
			height = Integer.parseInt(properties.getAttributeValue("h"));
		} 
		if(properties.getAttribute("l") != null) {
			z = Byte.parseByte(properties.getAttributeValue("l"));
		}
	}
	
	public void setCut(boolean isCut) {
		this.isCut = isCut;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
		
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	

	public void setZ(int z) {
		this.z = z;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
		if(MapEditor.isVisible(this)) {
			Color color = ColorFactory.getColor(resource.color);
			Rectangle2D rect = new Rectangle2D.Float(x*zoom, y*zoom, width*zoom, height*zoom);

			if(zoom < 1) {
				graphics.setPaint(color);
			} else {
				graphics.setPaint(TextureFactory.getTexture(resource.text, (int)zoom, resource.color));
			}
			graphics.fill(rect);

			if(isCut) {
				graphics.setComposite(alphaOn);
				graphics.setPaint(Color.GRAY);
				graphics.fill(rect);
				graphics.setComposite(alphaOff);
			}

			if(isSelected) {
				graphics.setPaint(color);
				graphics.draw(rect);
			}
		}		
	}
	
	public abstract Element toElement();
}
