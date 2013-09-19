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

import neon.entities.Container;
import neon.entities.Door;
import neon.entities.Item;

public class LockHandler implements EffectHandler {
	public boolean isWeaponEnchantment() {
		return false;
	}

	public boolean isClothingEnchantment() {
		return false;
	}

	public boolean onItem() {
		return true;
	}

	public void addEffect(Spell spell) {
		Item target = (Item)spell.getTarget();
		switch(spell.getEffect()) {
		case OPEN: 
			if(target instanceof Door) {
				((Door)target).lock.unlock();
			} else if(target instanceof Container) {
				((Container)target).lock.unlock();				
			}
			break;
		case LOCK:
			if(target instanceof Door) {
				((Door)target).lock.lock();
			} else if(target instanceof Container) {
				((Container)target).lock.lock();				
			}
			break;
		case DISARM:
			if(target instanceof Door) {
				((Door)target).trap.disarm();
			} else if(target instanceof Container) {
				((Container)target).trap.disarm();				
			}
			break;
		default:
			throw new IllegalArgumentException("The given spell does not have a lock-related effect.");
		}
	}

	public void repeatEffect(Spell spell) {}
	public void removeEffect(Spell spell) {}
}
