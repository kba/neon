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

package neon.graphics.shapes;

import java.awt.*;

public class JVEllipse extends JVShape {
	int x, y, radius;
	
	public JVEllipse(int radius, Paint paint) {
		this.paint = paint;
		this.radius = radius;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
		graphics.setPaint(paint);
		graphics.fillOval((int)(x*zoom), (int)(y*zoom), (int)(radius*zoom), (int)(radius*zoom));
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, radius, radius);
	}
}
