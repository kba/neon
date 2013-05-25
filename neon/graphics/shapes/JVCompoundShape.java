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

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class JVCompoundShape extends JVShape {
	public int getX() {
		return 0;
	}

	public int getY() {
		return 0;
	}

	public void setX(int x) {
	}

	public void setY(int y) {
	}

	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
	}

	public Rectangle getBounds() {
		return null;
	}
}
