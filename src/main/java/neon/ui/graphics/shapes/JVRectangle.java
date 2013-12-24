/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

package neon.ui.graphics.shapes;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

public class JVRectangle extends JVShape {
	private Rectangle bounds;
	
	public JVRectangle(Paint paint, Rectangle bounds) {
		this.paint = paint;
		this.bounds = bounds;
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	public void paint(Graphics2D g, float zoom, boolean isSelected) {
		g.setPaint(paint);
		g.drawRect((int)(getBounds().x*zoom), (int)(getBounds().y*zoom), 
				(int)(getBounds().width*zoom), (int)(getBounds().height*zoom));
	}

	public int getX() {
		return bounds.x;
	}

	public int getY() {
		return bounds.y;
	}

	public void setX(int x) {
		bounds.x = x;
	}

	public void setY(int y) {
		bounds.y = y;
	}
}
