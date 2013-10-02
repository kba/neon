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

package neon.ui.graphics;

import java.awt.Rectangle;
import java.util.Collection;
import neon.util.spatial.RTree;
import neon.util.spatial.SpatialIndex;

public class Layer {
	private SpatialIndex<Renderable> index;
	private int depth;
	private boolean visible = true;
	
	public Layer(int depth) {
		this.depth = depth;
		index = new RTree<Renderable>(20, 9);
	}
	
	public SpatialIndex<Renderable> getElements() {
		return index;
	}
	
	public Collection<Renderable> getElements(Rectangle r) {
		return index.getElements(r);
	}
	
	public int getDepth() {
		return depth;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
}
