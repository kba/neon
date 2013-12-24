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

package neon.core.event;

import java.awt.Point;
import java.util.EventObject;
import neon.entities.Creature;
import neon.entities.Item;
import neon.resources.RSpell;

@SuppressWarnings("serial")
public class MagicEvent extends EventObject {
	public MagicEvent(Object source) {
		super(source);
	}
	
	/**
	 * Used when a trap casts a spell on a creature.
	 * 
	 * @author mdriesen
	 */
	public static class OnCreature extends MagicEvent {
		private RSpell spell;
		private Creature target;
		
		public OnCreature(Object source, RSpell spell, Creature target) {
			super(source);
			this.spell = spell;
			this.target = target;
		}
		
		public RSpell getSpell() {
			return spell;
		}
		
		public Creature getTarget() {
			return target;
		}
	}
	
	/**
	 * Used when a trap casts a spell on a point.
	 * 
	 * @author mdriesen
	 */
	public static class OnPoint extends MagicEvent {
		private RSpell spell;
		private Point target;
		
		public OnPoint(Object source, RSpell spell, Point target) {
			super(source);
			this.spell = spell;
			this.target = target;
		}
		
		public RSpell getSpell() {
			return spell;
		}
		
		public Point getTarget() {
			return target;
		}
	}
	
	/**
	 * Used when a creature casts a spell on itself.
	 * 
	 * @author mdriesen
	 */
	public static class OnSelf extends MagicEvent {
		private RSpell spell;
		private Creature caster;
		
		public OnSelf(Object source, Creature caster, RSpell spell) {
			super(source);
			this.spell = spell;
			this.caster = caster;
		}
		
		public RSpell getSpell() {
			return spell;
		}
		
		public Creature getCaster() {
			return caster;
		}
	}
	
	/**
	 * Used when a creature casts a spell on itself using an enchanted item.
	 * 
	 * @author mdriesen
	 */
	public static class ItemOnSelf extends MagicEvent {
		private Item item;
		private Creature caster;
		
		public ItemOnSelf(Object source, Creature caster, Item item) {
			super(source);
			this.item = item;
			this.caster = caster;
		}
		
		public Item getItem() {
			return item;
		}
		
		public Creature getCaster() {
			return caster;
		}
	}
	
	/**
	 * Used when a creature casts a spell on a point.
	 * 
	 * @author mdriesen
	 */
	public static class CreatureOnPoint extends MagicEvent {
		private Point target;
		private Creature caster;
		
		public CreatureOnPoint(Object source, Creature caster, Point target) {
			super(source);
			this.target = target;
			this.caster = caster;
		}
		
		public Point getTarget() {
			return target;
		}
		
		public Creature getCaster() {
			return caster;
		}
	}
	
	/**
	 * Used when a creature uses an enchanted item to cast a spell on a point.
	 * 
	 * @author mdriesen
	 */
	public static class ItemOnPoint extends MagicEvent {
		private Point target;
		private Creature caster;
		private Item item;
		
		public ItemOnPoint(Object source, Creature caster, Item item, Point target) {
			super(source);
			this.target = target;
			this.caster = caster;
			this.item = item;
		}
		
		public Point getTarget() {
			return target;
		}
		
		public Creature getCaster() {
			return caster;
		}
		
		public Item getItem() {
			return item;
		}
	}
	
	public static class Result extends MagicEvent {
		private Creature caster;
		private int result;
		
		public Result(Object source, Creature caster, int result) {
			super(source);
			this.caster = caster;
			this.result = result;
		}
		
		public Creature getCaster() {
			return caster;
		}
		
		public int getResult() {
			return result;
		}
	}
}
