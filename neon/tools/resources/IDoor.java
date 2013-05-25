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

package neon.tools.resources;

import java.awt.Point;
import neon.objects.resources.RData;
import neon.objects.resources.RDungeonTheme;
import neon.objects.resources.RItem;
import neon.objects.resources.RSpell;
import neon.tools.Editor;
import org.jdom2.Element;

public class IDoor extends IObject {
	public int trap = 0;
	public int lock = 0;
	public RItem key;
	public State state;
	public RMap destMap;
	public RZone destZone;
	public Point destPos;
	public RDungeonTheme destTheme;
	public String text;
	public RSpell.Enchantment spell;
	
	public IDoor(RData resource, int x, int y, int z, int uid) {
		super(resource, x, y, z, uid);
	}
	
	public IDoor(Element properties, RZone zone) {
		super(properties);
		if(properties.getAttribute("state") != null) {
			state = State.valueOf(properties.getAttributeValue("state"));			
		} else {
			state = State.open;
		}
		if(properties.getAttribute("lock") != null) {
			lock = Integer.parseInt(properties.getAttributeValue("lock"));
			key = (RItem)Editor.resources.getResource(properties.getAttributeValue("key"));
		}
		if(properties.getAttribute("trap") != null) {
			trap = Integer.parseInt(properties.getAttributeValue("trap"));
			spell = (RSpell.Enchantment)Editor.resources.getResource(properties.getAttributeValue("spell"), "magic");
		}
		if(properties.getChild("dest") != null) {
			Element dest = properties.getChild("dest");
			if(dest.getAttribute("theme") != null) {
				destTheme = (RDungeonTheme)Editor.resources.getResource(dest.getAttributeValue("theme"), "theme");
			} else {
				if(dest.getAttribute("map") != null) {
					int uid = Integer.parseInt(dest.getAttributeValue("map"));
					for(RMap map : Editor.resources.getResources(RMap.class)) {
						if(map.uid == uid) {
							destMap = map;
						}
					}
				} else {
					destMap = zone.map;
				}
				if(dest.getAttribute("z") != null) {
						destZone = destMap.getZone(Integer.parseInt(dest.getAttributeValue("z")));
				} else {
					destZone = destMap.getZone(0);
				}
				if(dest.getAttribute("x") != null && dest.getAttribute("y") != null) {
					int x = Integer.parseInt(dest.getAttributeValue("x"));
					int y = Integer.parseInt(dest.getAttributeValue("y"));
					destPos = new Point(x, y);
				}
			}
			text = dest.getAttributeValue("sign");
		}
	}
	
	public enum State {
		open, closed, locked;
	}
	
	public boolean isPortal() {
		return destMap != null || destZone != null || destPos != null || destTheme != null;
	}
	
	public Element toElement() {
		Element door = super.toElement();
		door.setName("door");
		door.setAttribute("state", state.toString());
		
		// bestemming
		Element dest = new Element("dest");
		if(text != null) {
			dest.setAttribute("sign", text);
		}
		if(destTheme != null) {
			door.addContent(dest);
			dest.setAttribute("theme", destTheme.id);
		} else if(destPos != null || destZone != null || destMap != null){
			door.addContent(dest);
			if(destPos !=  null) {
				dest.setAttribute("x", Integer.toString(destPos.x));
				dest.setAttribute("y", Integer.toString(destPos.y));
			}
			if(destZone != null) {
				dest.setAttribute("z", Integer.toString(destZone.map.getZone(destZone)));					
			}
			if(destMap != null) {
				dest.setAttribute("map", Integer.toString(destMap.uid));
			}
		}
		
		// lock
		if(lock > 0) {
			door.setAttribute("lock", Integer.toString(lock));
			if(key != null) {
				door.setAttribute("key", key.id);
			}
		}
		
		// trap
		if(trap > 0) {
			door.setAttribute("trap", Integer.toString(trap));
			if(spell != null) {
				door.setAttribute("spell", spell.id);
			}
		}
		
		return door;
	}
}
