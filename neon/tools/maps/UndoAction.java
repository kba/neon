/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

package neon.tools.maps;

import neon.graphics.Scene;
import neon.tools.resources.Instance;

public abstract class UndoAction {
	public abstract void undo();
	
	public static class Drop extends UndoAction {
		private Instance instance;
		private Scene model;
		
		public Drop(Instance instance, Scene model) {
			this.instance = instance;
			this.model = model;
		}
		
		public void undo() {
			model.removeElement(instance);
		}
	}
	
	public static class Move extends UndoAction {
		private Instance instance;
		private Scene scene;
		private int x, y;

		public Move(Instance instance, Scene scene, int x, int y) {
			this.instance = instance;
			this.scene = scene;
			this.x = x;
			this.y = y;
		}
		
		public void undo() {
			instance.setX(x);
			instance.setY(y);
			scene.removeElement(instance);
			scene.addElement(instance, instance.getBounds(), instance.z);
		}
	}
}
