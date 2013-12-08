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

package neon.core.handlers;

import java.util.ArrayList;
import java.util.Collection;
import neon.core.Engine;
import neon.core.event.StoreEvent;
import neon.entities.Clothing;
import neon.entities.Creature;
import neon.entities.Item;
import neon.entities.Weapon;
import neon.entities.components.Inventory;
import neon.entities.property.Slot;
import neon.magic.Effect;
import neon.magic.MagicUtils;
import neon.magic.Spell;
import neon.resources.RWeapon.WeaponType;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

@Listener(references = References.Strong)
public class InventoryHandler {
	/**
	 * Adds or removes an {@code Entity} from the {@code UIDStore}.
	 * 
	 * @param event
	 */
	@Handler public void handle(StoreEvent event) {
		switch(event.getMode()) {
		case ADD:
			Engine.getStore().addEntity(event.getEntity());
			break;
		case REMOVE:
			Engine.getStore().removeEntity(event.getUID());
			break;
		}
	}
	
	/**
	 * Adds an item to a creature's inventory.
	 * 
	 * @param creature	a creature
	 * @param uid	the uid of the item to add
	 */
	public static void addItem(Creature creature, long uid) {
		Item item = (Item)Engine.getStore().getEntity(uid);
		if(item instanceof Item.Coin) {
			creature.getInventoryComponent().addMoney(item.resource.cost);
		} else {
			creature.getInventoryComponent().addItem(uid);
		}
	}

	/**
	 * Removes an item from an inventory.
	 * 
	 * @param uid	the uid of the the item to remove
	 */
	public static void removeItem(Creature creature, long uid) {
		if(creature.getInventoryComponent().hasEquiped(uid)) {
			unequip(uid, creature);	// eerst unequipen als ge dit nog aanhebt
		}
		creature.getInventoryComponent().removeItem(uid);
	}
	
	/*
	 * Removes items from an inventory.
	 * 
	 * @param id		the name of the item to remove
	 * @param amount	the number of items to remove
	 */
	public static Collection<Long> removeItems(Creature creature, String id, int amount) {
		ArrayList<Long> removal = new ArrayList<Long>();
		for(Long uid : creature.getInventoryComponent()) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item.getID().equals(id)) {
				InventoryHandler.removeItem(creature, item.getUID());
				removal.add(uid);
				amount--;
			}
			if(amount < 1) {
				break;
			}
		} 
		return removal;
	}
	
	public static void equip(Item item, Creature creature) {
		Inventory inventory = creature.getInventoryComponent();
		if(item instanceof Clothing) {
			Clothing c = (Clothing)item;
			switch(c.getSlot()) {
			case RING: 
				if(inventory.get(Slot.RING_LEFT) == 0) {
					inventory.put(Slot.RING_LEFT, item.getUID());					
				} else {
					inventory.remove(Slot.RING_RIGHT);					
					inventory.put(Slot.RING_RIGHT, item.getUID());					
				}
				break;
			case SHOES: 
				inventory.remove(Slot.BOOTS);
				inventory.remove(Slot.SHOES); 
				inventory.put(c.getSlot(), item.getUID());
				break;
			case GLOVES:
				inventory.remove(Slot.GAUNTLETS);
				inventory.remove(Slot.GLOVES); 
				inventory.put(c.getSlot(), item.getUID());
				break;
			case GAUNTLETS:
				inventory.remove(Slot.GLOVES); 
				inventory.remove(Slot.GAUNTLETS);
				inventory.put(c.getSlot(), item.getUID());
				break;
			default:
				break;
			}
			if(c.getMagicComponent().getSpell() != null) {
				MagicUtils.equip(creature, (Clothing)item);
			}
		} else if(item instanceof Weapon) {
			Weapon weapon = (Weapon)Engine.getStore().getEntity((inventory.get(Slot.WEAPON)));
			Weapon ammo = (Weapon)Engine.getStore().getEntity((inventory.get(Slot.AMMO)));
			
			switch(((Weapon)item).getWeaponType()) {
			case THROWN:
				if(inventory.hasEquiped(Slot.WEAPON) && (weapon.getWeaponType() == WeaponType.BOW ||
						 weapon.getWeaponType() == WeaponType.CROSSBOW)) {
					inventory.remove(Slot.WEAPON);
				}
				inventory.put(Slot.AMMO, item.getUID()); 
				break;
			case BOLT:
			case ARROW:
				if(inventory.hasEquiped(Slot.WEAPON) && weapon.getWeaponType() == WeaponType.THROWN) {
					inventory.remove(Slot.WEAPON);
				}
				inventory.put(Slot.AMMO, item.getUID());
				break;
			case BOW:
				inventory.put(Slot.WEAPON, item.getUID()); 
				if(inventory.hasEquiped(Slot.AMMO) && ammo.getWeaponType() != WeaponType.ARROW) {
					inventory.remove(Slot.AMMO);
				} 
				break;
			case CROSSBOW:
				inventory.put(Slot.WEAPON, item.getUID()); 
				if(inventory.hasEquiped(Slot.AMMO) && ammo.getWeaponType() != WeaponType.BOLT) {
					inventory.remove(Slot.AMMO);
				} 
				break;
			default:
				inventory.put(Slot.WEAPON, item.getUID()); break;
			}
		} else if(item instanceof Item.Scroll) {
			inventory.put(Slot.MAGIC, item.getUID());
			creature.getMagicComponent().equipSpell(null);
		}
	}

	public static void unequip(long uid, Creature creature) {
		Inventory inventory = creature.getInventoryComponent();
		Item item = (Item)Engine.getStore().getEntity(uid);
		if(item instanceof Clothing) {
			Clothing c = (Clothing)item;
			if(c.getSlot().equals(Slot.RING)) {
				if(inventory.get(Slot.RING_LEFT) == c.getUID()) {
					inventory.remove(Slot.RING_LEFT);
				} else if(inventory.get(Slot.RING_RIGHT) == c.getUID()) {
					inventory.remove(Slot.RING_RIGHT);
				}
			} else {
				inventory.remove(c.getSlot());
			}
			if(c.getMagicComponent().getSpell() != null) {
				MagicUtils.unequip(creature, (Clothing)item);
			}
		} else if(item instanceof Weapon) {
			if(((Weapon)item).getWeaponType() == WeaponType.THROWN || 
					((Weapon)item).getWeaponType() == WeaponType.BOLT ||
					((Weapon)item).getWeaponType() == WeaponType.ARROW) {
				inventory.remove(Slot.AMMO);
			} else {
				inventory.remove(Slot.WEAPON);
			}
		} else if(item instanceof Item.Scroll) {
			inventory.remove(Slot.MAGIC);
		}
	}
	
	/**
	 * @param creature
	 * @param id
	 * @return	the number of items with the given id the given creature owns
	 */
	public static int getAmount(Creature creature, String id) {
		int count = 0;
		for(long uid : creature.getInventoryComponent()) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item.getID().equals(id)) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * @return	a creature's weight
	 */
	public static int getWeight(Creature creature) {
		float sum = 0;
		for(long uid : creature.getInventoryComponent()) {
			sum += ((Item)Engine.getStore().getEntity(uid)).resource.weight;
		}
		// in geval van 'burden' spell
		for(Spell s : creature.getActiveSpells()) {
			if(s.getEffect() == Effect.BURDEN) {
				sum += s.getMagnitude();
			}
		}
		return (int)sum;
	}
}
