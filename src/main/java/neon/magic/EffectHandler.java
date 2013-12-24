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

public interface EffectHandler {
	/**
	 * @return whether this effect can be used as a weapon enchantment
	 */
	public boolean isWeaponEnchantment();
	
	/**
	 * @return whether this effect can be used as a clothing enchantment
	 */
	public boolean isClothingEnchantment();
	
	/**
	 * @return	whether this effect can be cast on an item
	 */
	public boolean onItem();
	
	public void addEffect(Spell spell);
	public void repeatEffect(Spell spell);
	public void removeEffect(Spell spell);
}
