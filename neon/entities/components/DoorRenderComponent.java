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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import neon.entities.Door;
import neon.resources.RItem;
import neon.util.TextureFactory;

/**
 * Class used to render doors. Rendering takes into account the state of the
 * door (open, locked or closed).
 * 
 * @author mdriesen
 */
public class DoorRenderComponent extends ItemRenderComponent {
	public DoorRenderComponent(Door door) {
		super(door);
	}

	@Override
	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
		Rectangle2D rect = new Rectangle2D.Float(item.getBounds().x*zoom, item.getBounds().y*zoom, zoom, zoom);
		String text = item.resource.text;
		Lock lock = ((Door)item).lock;
		if(lock != null) {
			if(lock.isLocked()) {
				text = ((RItem.Door)item.resource).locked;
			} else if(lock.isClosed()) {
				text = ((RItem.Door)item.resource).closed;
			}
		}
		graphics.setPaint(TextureFactory.getTexture(text, (int)zoom, item.resource.color));
		graphics.fill(rect);		
	}
}
