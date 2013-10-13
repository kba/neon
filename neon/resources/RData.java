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

import org.jdom2.Element;

/**
 * A resource that is loaded from/saved to an XML data file.
 * 
 * @author mdriesen
 *
 */
public abstract class RData extends Resource {
	// dit dient eigenlijk alleen voor items en creatures
	public String text = "x";
	public String color = "white";
	public String name;

	/**
	 * Initializes this resource from a JDOM {@code Element}.
	 * 
	 * @param data
	 * @param path
	 */
	public RData(Element data, String... path) {
		super(data.getAttributeValue("id"), path);
		color = data.getAttributeValue("color");
		text = data.getAttributeValue("char");
		name = data.getAttributeValue("name");
	}
	
	/**
	 * Initializes an empty resource with the given id.
	 * 
	 * @param id
	 * @param path
	 */
	public RData(String id, String... path) {
		super(id, path);
	}
	
	/**
	 * Creates a JDOM {@code Element} from this resource.
	 * 
	 * @return
	 */
	public abstract Element toElement();
	
	@Override
	public void load() {}	// RData heeft niets om te laden buiten wat er in de constructor staat
	
	@Override
	public void unload() {}	// RData heeft niets om te ontladen
}
