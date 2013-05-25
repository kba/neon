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

import java.util.EventObject;
import neon.core.Engine;
import neon.magic.Effect;
import neon.magic.MagicUtils;
import neon.magic.Spell;
import neon.objects.entities.Creature;
import neon.util.fsm.Action;

public class MagicTask implements Action {
	private Spell spell;
	private int stop;
	
	public MagicTask(Spell spell, int stop) {
		this.spell = spell;
		this.stop = stop;
	}
	
	public Spell getSpell() {
		return spell;
	}
	
	public void run(EventObject e) {
		Creature target = (Creature)spell.getTarget();
		if(target.getActiveSpells().contains(spell)) {
			if(stop == Engine.getTimer().getTime()) {
				MagicUtils.removeSpell(target, spell);
			} else if(spell.getEffect().getDuration() == Effect.REPEAT) {
				spell.getHandler().repeatEffect(spell);
			}
		}
	}
}
