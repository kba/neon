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

package neon.magic;

import neon.entities.Entity;
import neon.resources.RSpell;
import neon.resources.RSpell.SpellType;

/**
 * A spell contains all information about its casting.
 * 
 * @author 	mdriesen
 */
public class Spell {
	private Effect effect;
	private float magnitude;
	private String script;
	private SpellType type;
	private Entity caster;
	private Entity target;
	
	public Spell(Entity target, Entity caster, Effect effect, float magnitude, String script, SpellType type) {
		this.caster = caster;
		this.target = target;
		this.effect = effect;
		this.magnitude = magnitude;
		this.script = script;
		this.type = type;
	}
	
	public Spell(RSpell spell, float modifier, Entity target, Entity caster) {
		this(spell, modifier);
		this.caster = caster;
		this.target = target;
	}
	
	public Spell(RSpell spell, float modifier) {
		this.effect = spell.effect;
		this.magnitude = spell.size*modifier;
		this.script = spell.script;
		this.type = spell.type;
	}
	
	public Effect getEffect() {
		return effect;
	}
	
	public float getMagnitude() {
		return magnitude;
	}
	
	public String getScript() {
		return script;
	}
	
	public SpellType getType() {
		return type;
	}
	
	public Entity getCaster() {
		return caster;
	}
	
	public Entity getTarget() {
		return target;
	}
	
	/**
	 * Convenience method to get the {@code EffectHandler} of the effect 
	 * associated with this spell.
	 * 
	 * @return	an {@code EffectHandler}
	 */
	public EffectHandler getHandler() {
		return effect.getHandler();
	}
}
