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

package neon.graphics;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Scene {
	private HashMap<Integer, Layer> layers = new HashMap<Integer, Layer>();
	
	/**
	 * @return	all renderables in this Scene
	 */
	public Collection<Renderable> getElements() {
		ArrayList<Renderable> elements = new ArrayList<Renderable>();
		for(Layer layer : layers.values()) {
			elements.addAll(layer.getElements().getElements());
		}
		return elements;
	}
	
	public void clear() {
		layers.clear();
	}

	public Collection<Layer> getLayers() {
		return layers.values();
	}
	
	public Collection<Renderable> getElements(Rectangle r) {
		ArrayList<Renderable> elements = new ArrayList<Renderable>();
		for(Layer layer : layers.values()) {
			if(layer.isVisible()) {
				elements.addAll(layer.getElements(r));
			}
		}
		return elements;
	}

	public void addElement(Renderable e, Rectangle bounds, int layer) {
		if(!layers.containsKey(layer)) {
			layers.put(layer, new Layer(layer));
		}
		layers.get(layer).getElements().insert(e, bounds);
	}

	public void removeElement(Renderable e) {
		for(Layer layer : layers.values()) {
			layer.getElements().remove(e);
		}
	}
	
	/**
	 * @return	the width of this Scene
	 */
	public int getWidth() {
		int w = 0;
		for(Layer layer : layers.values()) {
			w = Math.max(w, layer.getElements().getWidth());
		}
		return w;
	}
	
	/**
	 * @return	the heigth of this Scene
	 */
	public int getHeight() {
		int h = 0;
		for(Layer layer : layers.values()) {
			h = Math.max(h, layer.getElements().getHeight());
		}
		return h;
	}
}
