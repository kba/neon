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

package neon.resources;

/**
 * A class that represents a resource that is loaded from disk. Two abstract
 * methods are provided to {@code load()} and {@code unload()} a resource. 
 * Beware that some resources are not read from disk until their are explicitly
 * {@code load}ed.
 * 
 * @author mdriesen
 */
public abstract class Resource {
	public final String id;
	protected String[] path;
	
	public Resource(String id, String... path) {
		this.id = id;
		this.path = path;
	}
	
	public abstract void load();
	public abstract void unload();
	
	public String[] getPath() {
		return path;
	}
	
	public void setPath(String... path) {
		this.path = path;
	}
	
	public String toString() {
		return id;
	}
}
