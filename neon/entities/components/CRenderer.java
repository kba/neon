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

package neon.entities.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import neon.entities.Creature;
import neon.entities.property.Condition;
import neon.resources.RCreature;
import neon.resources.RCreature.Size;
import neon.ui.graphics.Renderable;
import neon.util.ColorFactory;
import neon.util.TextureFactory;

/**
 * Class used to render creatures. Rendering takes into account the size of a 
 * creature, whether it has any active spells, and whether it's dead or alive.
 * 
 * @author mdriesen
 */
public class CRenderer implements Renderable, Component {
	public int z = Byte.MAX_VALUE - 1;
	protected Creature creature;
	
	public CRenderer(Creature creature) {
		this.creature = creature;
	}
	
	@Override
	public void paint(Graphics2D graphics, float zoomf, boolean isSelected) {
		RCreature species = creature.species;
		int x = creature.getBounds().x;
		int y = creature.getBounds().y;
		String text = creature.hasCondition(Condition.DEAD) ? "%" : creature.species.text;
		int zoom = (int)zoomf;
		Color color = ColorFactory.getColor(species.color);
		if(species.size == Size.tiny) {
			graphics.drawImage(TextureFactory.getImage(text, zoom*2/3, color), x*zoom + zoom/6, y*zoom + zoom/6, zoom*2/3, zoom*2/3, null);
		} else if(species.size == Size.huge) {
			graphics.drawImage(TextureFactory.getImage(text, zoom*3/2, color), x*zoom - zoom/4, y*zoom - zoom/4, zoom*3/2, zoom*3/2, null);
		} else {
			graphics.drawImage(TextureFactory.getImage(text, zoom, color), x*zoom, y*zoom, zoom, zoom, null);
		}
		if(creature.getActiveSpells().size() != 0) {
			graphics.setPaint(Color.blue);
			graphics.drawOval(x*zoom, y*zoom, creature.bounds.width*zoom, creature.bounds.height*zoom);
		} 		
	}

	@Override
	public int getZ() {
		return z;
	}
	
	@Override
	public void setZ(int z) {
		this.z = z;
	}

	@Override
	public Rectangle getBounds() {
		return creature.getBounds();
	}

	@Override
	public long getUID() {
		return creature.getUID();
	}
}
