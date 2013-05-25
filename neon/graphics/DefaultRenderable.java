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

package neon.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import neon.util.TextureFactory;

/**
 * This class implements a minimal <code>Renderable</code>.
 * @author mdriesen
 */
public class DefaultRenderable implements Renderable {
	public int z;
	private Rectangle bounds;
	private String text;
	private Color color;
	
	/**
	 * Initializes this DefaultRenderable with the given parameters.
	 * 
	 * @param x			the x coordinate
	 * @param y			the y coordinate
	 * @param z			the z-order
	 * @param width		the width
	 * @param height	the height
	 * @param text		the type of texture
	 * @param color		the color of texture
	 */
	public DefaultRenderable(int x, int y, int z, int width, int height, String text, Color color) {
		bounds = new Rectangle(x, y, width, height);
		this.z = z;
		this.text = text;
		this.color = color;
	}
	
	public int getX() {
		return bounds.x;
	}

	public int getY() {
		return bounds.y;
	}

	public int getZ() {
		return z;
	}

	public void setX(int x) {
		bounds.x = x;
	}

	public void setY(int y) {
		bounds.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void paint(Graphics2D graphics, float zoomf, boolean isSelected) {
		int zoom = (int)zoomf;
		Rectangle rect = new Rectangle(bounds.x*zoom, bounds.y*zoom, bounds.width*zoom, bounds.height*zoom);
		TexturePaint paint = TextureFactory.getTexture(text, zoom, color);
		graphics.setPaint(paint);
		graphics.fill(rect);		
	}

	public Rectangle getBounds() {
		return bounds;
	}
}
