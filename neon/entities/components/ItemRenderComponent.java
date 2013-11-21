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
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import neon.entities.Item;
import neon.util.TextureFactory;

/**
 * Class used to render {@code Item}s.
 * 
 * @author mdriesen
 */
public class ItemRenderComponent extends RenderComponent {
	protected Item item;
	
	public ItemRenderComponent(Item item) {
		this.item = item;
	}
	
	@Override
	public void paint(Graphics2D graphics, float zoom, boolean isSelected) {
		Rectangle bounds = item.getShapeComponent();
		Rectangle2D rect = new Rectangle2D.Float(bounds.x*zoom, bounds.y*zoom, zoom, zoom);
		graphics.setPaint(TextureFactory.getTexture(item.resource.text, (int)zoom, item.resource.color));
		graphics.fill(rect);
	}

	@Override
	public Rectangle getBounds() {
		return item.getShapeComponent();
	}

	@Override
	public long getUID() {
		return item.getUID();
	}
}

