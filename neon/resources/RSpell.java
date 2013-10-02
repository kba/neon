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

import neon.magic.Effect;
import org.jdom2.Element;

public class RSpell extends RData {
	public enum SpellType {
		SPELL, DISEASE, POISON, CURSE, POWER, ENCHANT;
	}
	
	public SpellType type;
	public Effect effect;
	public int size, range, duration, radius, cost;
	public String script;
	
	public RSpell(String id, SpellType type, String... path) {
		super(id, path);
		this.type = type;
	}

	/**
	 * Initializes a spell with the given parameters.
	 * 
	 * @param id		the name of this spell
	 * @param range		the range of this spell
	 * @param duration	the duration of this spell
	 * @param effect	the <code>Effect</code> of this spell
	 * @param area		the area affected by this spell
	 * @param size		the size of this spell
	 * @param type		the type of this spell
	 */
	public RSpell(String id, int range, int duration, String effect, int radius, int size, String type) {
		super(id);
		this.range = range;
		this.duration = duration;
		this.effect = Effect.valueOf(effect.toUpperCase());
		this.size = size;
		this.type = SpellType.valueOf(type.toUpperCase());
		this.radius = radius;
		script = null;
	}
	
	public RSpell(Element spell, String... path) {
		super(spell, path);
		type = SpellType.valueOf(spell.getName().toUpperCase());
		effect = Effect.valueOf(spell.getAttributeValue("effect").toUpperCase());
		script = spell.getText();
		
		if(spell.getAttribute("size") != null) {
			size = Integer.parseInt(spell.getAttributeValue("size"));
		} else {
			size = 0;
		}
		if(spell.getAttribute("range") != null) {
			range = Integer.parseInt(spell.getAttributeValue("range"));
		} else {
			range = 0;
		}
		if(spell.getAttribute("duration") != null) {
			duration = Integer.parseInt(spell.getAttributeValue("duration"));
		} else {
			duration = 0;
		}
		if(spell.getAttribute("area") != null) {
			radius = Integer.parseInt(spell.getAttributeValue("area"));
		} else {
			radius = 0;
		}
	}

	public Element toElement() {
		Element spell = new Element(type.toString());
		spell.setAttribute("id", id);
		spell.setAttribute("effect", effect.name());
		
		if(script != null && !script.isEmpty()) {
			spell.setText(script);
		}
		if(size > 0) {
			spell.setAttribute("size", Integer.toString(size));
		}
		if(range > 0) {
			spell.setAttribute("range", Integer.toString(range));
		}
		if(duration > 0) {
			spell.setAttribute("duration", Integer.toString(duration));
		}
		if(radius > 0) {
			spell.setAttribute("area", Integer.toString(radius));
		}
		
		return spell;
	}
	
	// scrolls/books hebben een gewone spell
	public static class Enchantment extends RSpell {
		public String item;	// geldig: clothing/armor, weapon, container/door, food/potion
		
		public Enchantment(Element enchantment, String... path) {
			super(enchantment, path);
			item = enchantment.getAttributeValue("item");
		}

		public Enchantment(String id, String... path) {
			super(id, SpellType.ENCHANT, path);
		}
		
		public Element toElement() {
			Element enchantment = super.toElement();
			enchantment.setAttribute("item", item);
			return enchantment;
		}
	}
	
	public static class Power extends RSpell {
		public int interval;
		
		public Power(Element power, String... path) {
			super(power, path);
			interval = Integer.parseInt(power.getAttributeValue("int"));
		}

		public Power(String id, String... path) {
			super(id, SpellType.POWER, path);
			interval = 0;
		}
		
		public Element toElement() {
			Element power = super.toElement();
			power.setAttribute("int", Integer.toString(interval));
			return power;
		}
	}
}
