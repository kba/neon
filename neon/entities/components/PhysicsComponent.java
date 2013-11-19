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

package neon.entities.components;

import java.awt.Rectangle;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Box;

public class PhysicsComponent extends Body implements Component {
	private long uid;

	public PhysicsComponent(long uid, Rectangle bounds) {
		super(new Box(bounds.width, bounds.height), 1);
		setUserData(uid);
		setEnabled(true);
		setPosition((float)bounds.getCenterX(), (float)bounds.getCenterY());
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public long getUID() {
		return uid;
	}
}
