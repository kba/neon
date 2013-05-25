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

package neon.systems.physics;

import java.awt.Rectangle;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.*;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.strategies.QuadSpaceStrategy;

public class PhysicsSystem {
	private World world;
	
	public PhysicsSystem() {
		world = new World(new Vector2f(0,0), 1, new QuadSpaceStrategy(50, 15));
	}
	
	public void clear() {
		world.clear();
	}
	
	public void register(Body body) {
		world.add(body);
	}
	
	public void register(Object object, Rectangle bounds, boolean isStatic) {
		// -1 en -0.5f om afrondingsfouten met floats te voorkomen
		Box box = new Box(bounds.width - 1, bounds.height - 1);
		Body body = isStatic ? new StaticBody(box) : new Body(box, 1);
		body.setUserData(object);
		body.setPosition((float)bounds.getCenterX() - 0.5f, (float)bounds.getCenterY() - 0.5f);
		world.add(body);
	}
	
	public void addListener(CollisionListener cl) {
		world.addListener(cl);
	}
	
	public void removeListener(CollisionListener cl) {
		world.removeListener(cl);
	}

	public void update() {
		world.step();
	}
}
