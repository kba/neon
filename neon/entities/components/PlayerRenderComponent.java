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

package neon.entities.components;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import neon.entities.Player;
import neon.util.ColorFactory;

public class PlayerRenderComponent extends CreatureRenderComponent {
	public PlayerRenderComponent(Player player) {
		super(player);
	}
	
	@Override
	public void paint(Graphics2D graphics, float zoomf, boolean isSelected) {
		super.paint(graphics, zoomf, isSelected);
		Rectangle bounds = creature.getShapeComponent();
		int x = bounds.x;
		int y = bounds.y;
		int zoom = (int)zoomf;
		graphics.setPaint(ColorFactory.getColor(creature.species.color));
		graphics.drawLine(x*zoom + 2, y*zoom + zoom, x*zoom + zoom - 4, y*zoom + zoom);
		if(((Player)creature).isSneaking()) {
			graphics.drawLine(x*zoom + 2, y*zoom, x*zoom + zoom - 4, y*zoom);
		}		
	}
	
	@Override
	public int getZ() {
		return Byte.MAX_VALUE - 1;
	}
}

