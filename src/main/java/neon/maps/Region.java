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

package neon.maps;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import neon.core.Engine;
import neon.resources.RRegionTheme;
import neon.resources.RTerrain;
import neon.systems.scripting.Activator;
import neon.ui.graphics.Renderable;
import neon.util.ColorFactory;
import neon.util.TextureFactory;

/**
 * This class represents a region on the map. 
 * 
 * @author mdriesen
 */
public class Region implements Renderable, Activator, Externalizable {
	public enum Modifier {
		NONE, SWIM, CLIMB, BLOCK, ICE, FIRE;
	}

	// TODO: destructable muren (opgeven welk terrein het wordt na destructie)
	protected String id, label;
	protected int x, y, z, width, height;
	private ArrayList<String> scripts = new ArrayList<String>();
	protected RRegionTheme theme;
	private RTerrain terrain;
	
	/**
	 * Initializes a new map region.
	 * 
	 * @param id		the id of this region
	 * @param x			the x coordinate of the upper left corner
	 * @param y			the y coordinate of the upper left corner
	 * @param width		the width
	 * @param height	the height
	 * @param theme		the region theme, in case random generation is necessary
	 * @param z			the z-order
	 */
	public Region(String id, int x, int y, int width, int height, RRegionTheme theme, int z, RTerrain terrain) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.theme = theme;
		this.id = id;
		this.z = z;
		this.terrain = terrain;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Sets whether this region should be random generated, or can be used as it is.
	 */
	public void fix() {
		theme = null;
	}
		
	public String getTextureType() {
		return id;
	}
	
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return <code>false</code> if this region should be randomly generated, <code>false</code> otherwise
	 */
	public boolean isFixed() {
		return theme == null;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getY() {
		return y;
	}
	
	public int getX() {
		return x;
	}
	
	public Color getColor() {
		return ColorFactory.getColor(terrain.color);
	}
	
	/**
	 * @return	the type of this region for random generation
	 */
	public RRegionTheme getTheme() {
		return theme;
	}
	
	/**
	 * @return	the movement modifier, determining how creatures move over this type of terrain
	 */
	public Modifier getMovMod() {
		return terrain.modifier;
	}
	
	/**
	 * An active region is one which has scripts added to it.
	 * 
	 * @return
	 */
	public boolean isActive() {
		return !scripts.isEmpty();
	}
	
	public void addScript(String id, boolean once) {
		scripts.add(id);
	}
	
	public void removeScript(String id) {
		scripts.remove(id);
	}
	
	public Collection<String> getScripts() {
		return scripts;
	}

	public void paint(Graphics2D graphics, float zoomf, boolean isSelected) {
		int zoom = (int)zoomf;
		TexturePaint paint = TextureFactory.getTexture(terrain.text, zoom, getColor());
		graphics.setPaint(paint);
		graphics.fillRect(x*zoom, y*zoom, width*zoom, height*zoom);
	}
	
	public String toString() {
		return terrain.description.isEmpty() ? id : terrain.description;
	}

	public void setZ(int z) {
		this.z = z;
	}

	/**
	 * @return	the bounding rectangle of this region
	 */
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public String getLabel() {
		return label;
	}
	
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		theme = (RRegionTheme)Engine.getResources().getResource(input.readUTF(), "theme");
		label = input.readUTF();
		if(label.isEmpty()) {
			label = null;
		}
		id = input.readUTF();
		terrain = (RTerrain)Engine.getResources().getResource(id, "terrain");
		x = input.readInt();
		y = input.readInt();
		z = input.readInt();
		width = input.readInt();
		height = input.readInt();
		int size = input.readInt();
		scripts = new ArrayList<String>();
		for(int i = 0; i < size; i++) {
			scripts.add(input.readUTF());
		}
	}

	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeUTF(theme != null ? theme.id : "");
		output.writeUTF(label != null ? label : "");
		output.writeUTF(id);
		output.writeInt(x);
		output.writeInt(y);
		output.writeInt(z);
		output.writeInt(width);
		output.writeInt(height);
		output.writeInt(scripts.size());
		for(String script : scripts) {
			output.writeUTF(script);
		}
	}
}
