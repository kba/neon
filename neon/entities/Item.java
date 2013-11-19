/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

package neon.entities;

import neon.entities.components.ItemRenderComponent;
import neon.entities.components.RenderComponent;
import neon.resources.RItem;

public class Item extends Entity {
	public final RItem resource;
	protected long owner = 0;
		
	public Item(long uid, RItem resource) {
		super(resource.id, uid);
		this.resource = resource;
		components.putInstance(RenderComponent.class, new ItemRenderComponent(this));
	}
	
	@Override
	public String toString() {
		if(resource.name != null) {
			return resource.name;
		} else {
			return resource.id;
		}
	}
	
	/**
	 * Sets the owner of this item.
	 * 
	 * @param owner
	 */
	public void setOwner(long owner) {
		this.owner = owner;
	}
	
	/**
	 * @return	the owner of this item
	 */
	public long getOwner() {
		return owner;
	}
	
	/**
	 * A light source.
	 * 
	 * @author mdriesen
	 */
	public static class Light extends Item {
		public Light(long uid, RItem resource) {
			super(uid, resource);
		}
	}

	/**
	 * A first aid item.
	 * 
	 * @author mdriesen
	 */
	public static class Aid extends Item {
		public Aid(long uid, RItem resource) {
			super(uid, resource);
		}
	}
	
	/**
	 * An edible item.
	 * 
	 * @author mdriesen
	 */
	public static class Food extends Item {	
		public Food(long uid, RItem resource) {
			super(uid, resource);
		}
	}
	
	/**
	 * Money.
	 * 
	 * @author mdriesen
	 */
	public static class Coin extends Item {
		public Coin(long uid, RItem resource) {
			super(uid, resource);
		}
	}
	
	/**
	 * A magic potion.
	 * 
	 * @author mdriesen
	 */
	public static class Potion extends Item {
		public Potion(long uid, RItem resource) {
			super(uid, resource);
		}
	}
	
	/**
	 * A magic scroll.
	 * 
	 * @author mdriesen
	 */
	public static class Scroll extends Item.Book {
		public Scroll(long uid, RItem.Text resource) {
			super(uid, resource);
		}
	}
	
	/**
	 * A readable item.
	 * 
	 * @author mdriesen
	 */
	public static class Book extends Item {
		public Book(long uid, RItem.Text resource) {
			super(uid, resource);
		}
	}
}
