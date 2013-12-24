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

package neon.editor.resources;

import java.util.ArrayList;

import neon.editor.Editor;
import neon.resources.RData;
import neon.resources.RItem;
import neon.resources.RSpell;

import org.jdom2.Element;

public class IContainer extends IObject {
	public int lock = 0;
	public int trap = 0;
	public RItem key;
	public RSpell.Enchantment spell;
	public ArrayList<IObject> contents = new ArrayList<IObject>();
	
	public IContainer(RData resource, int x, int y, int z, int uid) {
		super(resource, x, y, z, uid);
	}
	
	public IContainer(Element properties) {
		super(properties);
		for(Element e : properties.getChildren("item")) {
			RItem ri = (RItem)Editor.resources.getResource(e.getAttributeValue("id"));
			contents.add(new IObject(ri, 0, 0, 0, Integer.parseInt(e.getAttributeValue("uid"))));
		}
		if(properties.getAttribute("lock") != null) {
			lock = Integer.parseInt(properties.getAttributeValue("lock"));
			key = (RItem)Editor.resources.getResource(properties.getAttributeValue("key"));
		}
		if(properties.getAttribute("trap") != null) {
			trap = Integer.parseInt(properties.getAttributeValue("trap"));
			spell = (RSpell.Enchantment)Editor.resources.getResource(properties.getAttributeValue("spell"), "magic");
		}
	}
	
	public Element toElement() {
		Element container = super.toElement();
		container.setName("container");
		for(IObject io : contents) {
			Element item = new Element("item");
			item.setAttribute("id", io.resource.id);
			item.setAttribute("uid", Integer.toString(io.uid));
			container.addContent(item);
		}
		// lock
		if(lock > 0) {
			container.setAttribute("lock", Integer.toString(lock));
			if(key != null) {
				container.setAttribute("key", key.id);
			}
		}
		// trap
		if(trap > 0) {
			container.setAttribute("trap", Integer.toString(trap));
			if(spell != null) {
				container.setAttribute("spell", spell.id);
			}
		}
		return container;
	}
}
