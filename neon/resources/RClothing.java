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

import neon.entities.property.Slot;
import neon.magic.Effect;
import org.jdom2.Element;

public class RClothing extends RItem {	
	public enum ArmorType {
		LIGHT, MEDIUM, HEAVY, NONE;
	}
	
	// algemene dingen
	public ArmorType kind;
	public int rating;
	public Slot slot;
	
	// enchantment
	public int magnitude;
	public int mana;
	public Effect effect;

	public RClothing(Element cloth, String... path) {
		super(cloth, path);
		Element stats = cloth.getChild("stats");
		slot = Slot.valueOf(stats.getAttributeValue("slot").toUpperCase());
		
		if(cloth.getName().equals("armor")) {
			rating = Integer.parseInt(stats.getAttributeValue("ar"));
			kind = ArmorType.valueOf(stats.getAttributeValue("class").toUpperCase());			
		} else {
			rating = 0;
			kind = ArmorType.NONE;
		}
		
		if(cloth.getChild("enchant") != null) {
			Element enchantment = cloth.getChild("enchant");
			magnitude = Integer.parseInt(enchantment.getAttributeValue("mag"));
			mana = Integer.parseInt(enchantment.getAttributeValue("mana"));
			effect = Effect.valueOf(enchantment.getAttributeValue("effect").toUpperCase());
		} else {
			magnitude = 0;
			mana = 0;
			effect = null;
		}		
	}
	
	public RClothing(String id, Type type, String... path) {
		super(id, type, path);
	}

	public Element toElement() {
		Element clothing = super.toElement();
		Element stats = new Element("stats");
		stats.setAttribute("slot", slot.toString());
		if(type == Type.armor) {
			stats.setAttribute("class", kind.toString());
			stats.setAttribute("ar", Integer.toString(rating));
		}
		clothing.addContent(stats);
		return clothing;
	}
}
