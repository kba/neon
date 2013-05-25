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

package neon.objects.entities;

import neon.objects.property.Slot;
import neon.objects.resources.RClothing;

public class Clothing extends Item {
	public Clothing(long uid, RClothing resource) {
		super(uid, resource);
	}
	
	/**
	 * @return	the equipment slot this clothing item occupies when worn
	 */
	public Slot getSlot() {
		return ((RClothing)resource).slot;
	}
}
