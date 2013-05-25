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

import neon.objects.components.DRenderer;
import neon.objects.components.Lock;
import neon.objects.components.CPortal;
import neon.objects.components.Trap;
import neon.objects.resources.RItem;

public class Door extends Item {
	public final Lock lock;
	public final Trap trap;
	public final CPortal portal;
	private String sign;
	
	public Door(long uid, RItem resource) {
		super(uid, resource);
		lock = new Lock(uid);
		trap = new Trap(uid);
		portal = new CPortal(uid);
		renderer = new DRenderer(this);
	}
	
	/**
	 * Sets the door sign.
	 * 
	 * @param sign
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public boolean hasSign() {
		return sign != null;
	}
	
	public String toString() {
		if(sign != null) {
			return sign;
		} else {
			return super.toString();
		}
	}
}
