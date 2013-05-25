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

package neon.ai;

import java.awt.Point;
import java.util.HashMap;
import neon.core.Engine;
import neon.core.event.CombatEvent;
import neon.core.handlers.*;
import neon.magic.Effect;
import neon.objects.entities.*;
import neon.objects.property.Condition;
import neon.objects.property.Skill;
import neon.objects.property.Slot;
import neon.objects.resources.RItem;
import neon.objects.resources.RSpell;
import neon.objects.resources.RWeapon.WeaponType;

/**
 * This class implements a creature's AI. 
 * 
 * @author mdriesen
 */
public abstract class AI {
	protected byte aggression;
	protected byte confidence;
	protected Creature creature;
	protected HashMap<Long, Integer> dispositions = new HashMap<Long, Integer>();
	
	/**
	 * Initializes a new AI.
	 * 
	 * @param creature
	 * @param aggression
	 * @param confidence
	 */
	public AI(Creature creature, byte aggression, byte confidence) {
		this.aggression = aggression;
		this.confidence = confidence;
		this.creature = creature;
	}
	
	/**
	 * Lets the creature with this AI act.
	 */
	public abstract void act();
	
	/**
	 * @return	whether the creature with this AI is hostile towards the player
	 */
	public boolean isHostile() {
		if(creature.hasCondition(Condition.CALM)) {
			return false;
		} else {
			return aggression > getDisposition(Engine.getPlayer());
		}
	}
	
	/**
	 * Reduces the aggression of the creature with this AI
	 */
	public void calm() {
		aggression -= aggression/4;
	}
	
	/**
	 * Reduces the aggression of the creature with this AI
	 */
	public void charm(Creature other, int magnitude) {
		if(dispositions.containsKey(other.getUID())) {
			magnitude += dispositions.get(other.getUID());
		}
		dispositions.put(other.getUID(), magnitude);
	}
	
	/**
	 * @param other
	 * @return	the disposition of the creature with this AI towards the given creature
	 */
	public byte getDisposition(Creature other) {
		byte disposition = (byte)(40 + other.getCha());
		if(creature.species.id.equals(other.species.id)) {
			disposition += 5;		// zelfde soort is ok
		}
		for(String faction : creature.getFactions().keySet()) {
			if(creature.isMember(faction)) {
				disposition += 10;	// zelfde faction is nog meer ok
			}
		}
		if(dispositions.containsKey(other.getUID())) {
			disposition += dispositions.get(other.getUID());
		}
		return disposition;
	}
	
	/**
	 * Increases the aggression of the creature with this AI towards the given creature.
	 * 
	 * @param other
	 */
	public void makeHostile(Creature other) {
		aggression = (byte)(getDisposition(other) + 10);
	}
	
	/**
	 * @return	the aggression of the creature with this AI
	 */
	public byte getAggression() {
		return aggression;
	}
	
	/**
	 * @return	the confidence of the creature with this AI
	 */
	public byte getConfidence() {
		return confidence;
	}
	
	/**
	 * Checks if this creature can see another one.
	 * 
	 * @param other	the creature to check
	 * @return	<code>true</code> if this creature can see the other one, <code>false</code> otherwise
	 */
	public boolean sees(Creature other) {
		if(creature.hasCondition(Condition.BLIND)) {
			return false;
		} else {
			// TODO: rekening houden met sneaken en lichtjes
			return Point.distance(creature.getBounds().x, creature.getBounds().y, 
					other.getBounds().x, other.getBounds().y) < 16;
		}
	}
	
	/**
	 * Checks if this creature can see the given position.
	 * 
	 * @param p	the position to check
	 * @return	<code>true</code> if this creature can see the position, <code>false</code> otherwise
	 */
	public boolean sees(Point p) {
		if(creature.hasCondition(Condition.BLIND)) {
			return false;
		} else {
			return p.distance(creature.getBounds().x, creature.getBounds().y) < 16;
		}
	}
	
	/*
	 * heal: kijken of er spells/scrolls/potions zijn om te healen
	 */
	protected boolean heal() {
		// eerst potions en scrolls bekijken?
		for(long uid : creature.inventory) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item instanceof Item.Scroll || item instanceof Item.Potion) {
				RSpell formula = null;
				if(item instanceof Item.Scroll) {
					formula = ((Item.Scroll)item).enchantment.getSpell();
				} else if(item instanceof Item.Potion) {
					formula = ((Item.Potion)item).enchantment.getSpell();
				}
				
				if(formula.effect.equals(Effect.RESTORE_HEALTH) && formula.range == 0) {
					MagicHandler.cast(creature, item);
					InventoryHandler.removeItem(creature, item.getUID());
					return true;
				}
			}
		}
		int time = Engine.getTimer().getTime();
		for(RSpell.Power power : creature.animus.getPowers()) {
			if(power.effect.equals(Effect.RESTORE_HEALTH) && power.range == 0 && creature.animus.canUse(power, time)) {
				MagicHandler.cast(creature, power);
				return true;
			}
		}
		for(RSpell spell : creature.animus.getSpells()) {
			if(spell.effect.equals(Effect.RESTORE_HEALTH) && spell.range == 0) {
				MagicHandler.cast(creature, spell);
				return true;
			}
		}
		return false;
	}

	/*
	 * cure: kijken of er iets gecured moet worden
	 */
	protected boolean cure() {
		if(creature.hasCondition(Condition.CURSED) && cure(Effect.LIFT_CURSE)) {
			return true;
		} else if(creature.hasCondition(Condition.DISEASED) && cure(Effect.CURE_DISEASE)) {
			return true;
		} else if(creature.hasCondition(Condition.POISONED) && cure(Effect.CURE_POISON)) {
			return true;
		} else if(creature.hasCondition(Condition.PARALYZED) && cure(Effect.CURE_PARALYZATION)) {
			return true;
		} else if(creature.hasCondition(Condition.BLIND) && cure(Effect.CURE_BLINDNESS)) {
			return true;
		}
		return false;
	}
	
	/*
	 * cure(effect): disease, curse of poison selectief healen
	 */
	private boolean cure(Effect effect) {
		// eerst potions en scrolls bekijken?
		for(long uid : creature.inventory) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item instanceof Item.Scroll || item instanceof Item.Potion) {
				RSpell formula = ((Item.Scroll)item).enchantment.getSpell();
				if(formula.effect.equals(effect) && formula.range == 0) {
					MagicHandler.cast(creature, item);
					InventoryHandler.removeItem(creature, item.getUID());
					return true;
				}
			}
		}
		int time = Engine.getTimer().getTime();
		for(RSpell.Power power : creature.animus.getPowers()) {
			if(power.effect.equals(effect) && power.range == 0 && creature.animus.canUse(power, time)) {
				MagicHandler.cast(creature, power);
				return true;
			}
		}
		for(RSpell spell : creature.animus.getSpells()) {
			if(spell.effect.equals(effect) && spell.range == 0) {
				MagicHandler.cast(creature, spell);
				return true;
			}
		}
		return false;
	}

	/*
	 * equip(slot): proberen iets te equipen dat in dit slot past
	 */
	private boolean equip(Slot slot) {
		for(long uid : creature.inventory) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item instanceof Weapon && slot.equals(((Weapon)item).getSlot())) {
				WeaponType type = ((Weapon)item).getWeaponType();
				InventoryHandler.equip(item, creature);
				if((type.equals(WeaponType.BOW) || type.equals(WeaponType.CROSSBOW)) && !equip(Slot.AMMO)) {
					continue;
				} else if(type.equals(WeaponType.ARROW) && !CombatUtils.getWeaponType(creature).equals(WeaponType.BOW)) {
					continue;
				} else if(type.equals(WeaponType.BOLT) && !CombatUtils.getWeaponType(creature).equals(WeaponType.CROSSBOW)) {
					continue;
				}
				return true;
			} else if(item instanceof Clothing && slot.equals(((Clothing)item).getSlot())) {
				InventoryHandler.equip(item, creature);
				return true;
			} else if(item instanceof Item.Scroll && slot.equals(Slot.MAGIC)) {
				InventoryHandler.equip(item, creature);
				return true;
			}
		}
		return false;
	}

	/*
	 * flee: wegvluchten van de jager
	 */
	protected void flee(Creature hunter) {
		int dx = 0;
		int dy = 0;
		if(creature.getBounds().x < hunter.getBounds().x) { 
			dx = -1; 
		} else if(creature.getBounds().x > hunter.getBounds().x) { 
			dx = 1; 
		}
		if(creature.getBounds().y < hunter.getBounds().y) { 
			dy = -1; 
		} else if(creature.getBounds().y > hunter.getBounds().y) { 
			dy = 1; 
		}				
		Point p = new Point(creature.getBounds().x + dx, creature.getBounds().y + dy);

		if(Engine.getAtlas().getCurrentZone().getCreature(p) == null) {
			byte result = MotionHandler.move(creature, p);
			if(result == MotionHandler.BLOCKED) {
				// eenmaal willekeurige andere richting proberen (of meerdere?)
				wander();
			} else if(result == MotionHandler.DOOR) {
				// deur proberen opendoen, anders willekeurige richting
				if(!open(p)) {
					wander();
				}
			}
		}
	}
	
	private boolean open(Point p) {
		Door door = null;
		for(long uid : Engine.getAtlas().getCurrentZone().getItems(p)) {
			if(Engine.getStore().getEntity(uid) instanceof Door) {
				door = (Door)Engine.getStore().getEntity(uid);
			}
		}
		if(door != null) {
			if(door.lock.isLocked() && door.lock.getKey() != null && hasItem(creature, door.lock.getKey())) {
				door.lock.unlock();
				return true;
			} else if(door.lock.isClosed()) {
				door.lock.open();
				return true;
			}	
		}
		return false;
	}

	/*
	 * hunt(range): val prooi aan zolang die binnen territorium zit
	 */
	protected void hunt(int range, Point home, Creature prey) {
		// huidige positie van actor even bijhouden
		Point p = creature.bounds.getLocation();
		// prooi aanvallen
		hunt(prey);			
		// indien te ver verwijderd van home, terugkeren
		if(home.distance(p) > range) {
			MotionHandler.move(creature, p);				
		}
	}

	/*
	 * wander(range): rondwandelen binnen territorium
	 */
	protected void wander(int range, Point home) {
		// huidige positie creature opslaan
		Point p = creature.bounds.getLocation();
		double oldDistance = home.distance(p);
		// wandel willekeurig rond
		wander();
		// indien te ver verwijderd van home, terugkeren
		double newDistance = home.distance(creature.bounds.getLocation());
		if(newDistance > range && newDistance > oldDistance) {
			MotionHandler.move(creature, p);				
		}
	}

	/*
	 * wander: wandel gewoon wat rond
	 */
	protected void wander() {
		int dx = 1 - (int)(Math.random()*3);
		int dy = 1 - (int)(Math.random()*3);
		Point p = new Point(creature.getBounds().x + dx, creature.getBounds().y + dy);
		Point player = Engine.getPlayer().bounds.getLocation();

		if(Engine.getAtlas().getCurrentZone().getCreature(p) == null && !player.equals(p)) {
			MotionHandler.move(creature, p);
		}
	}

	/*
	 * wander(point): naar een bepaald punt wandelen
	 */
	protected void wander(Point destination) {
		Point player = Engine.getPlayer().bounds.getLocation();
		Point next = PathFinder.findPath(creature, creature.bounds.getLocation(), destination)[0];
		if(Engine.getAtlas().getCurrentZone().getCreature(next) == null && !player.equals(next)) {
			MotionHandler.move(creature, next);
		}
	}
	
	/*
	 * hunt: jaag op een prooi
	 */
	protected void hunt(Creature prey) {
		int dice = neon.util.Dice.roll(1,2,0);
		
		if(dice == 1) {
			int time = Engine.getTimer().getTime();
			for(RSpell.Power power : creature.animus.getPowers()) {
				if(power.effect.getSchool().equals(Skill.DESTRUCTION) && creature.animus.canUse(power, time) && 
						power.range >= Point.distance(creature.getBounds().x, creature.getBounds().y, 
						prey.getBounds().x, prey.getBounds().y)) {
					creature.animus.equipSpell(power);
					MagicHandler.cast(creature, prey.bounds.getLocation());
					return;	// hunt afbreken van zodra er een spell is gecast
				}
			}
			for(RSpell spell : creature.animus.getSpells()) {
				if(spell.effect.getSchool().equals(Skill.DESTRUCTION) && 
						spell.range >= Point.distance(creature.getBounds().x, creature.getBounds().y, 
						prey.getBounds().x, prey.getBounds().y)) {
					creature.animus.equipSpell(spell);
					MagicHandler.cast(creature, prey.bounds.getLocation());
					return;	// hunt afbreken van zodra er een spell is gecast
				}
			}
		} 

		Point p;
		if(creature.getInt() < 5) {		// als wezen lomp is, gewoon kortste weg proberen
			int dx = 0;
			int dy = 0;
			if(creature.getBounds().x < prey.getBounds().x) { 
				dx = 1; 
			} else if(creature.getBounds().x > prey.getBounds().x) { 
				dx = -1; 
			}
			if(creature.getBounds().y < prey.getBounds().y) { 
				dy = 1; 
			} else if(creature.getBounds().y > prey.getBounds().y) { 
				dy = -1; 
			}				
			p = new Point(creature.getBounds().x + dx, creature.getBounds().y + dy);
		} else {						// als wezen slimmer is, A* proberen
			p = PathFinder.findPath(creature, creature.bounds.getLocation(), prey.bounds.getLocation())[0];
		}

		if(p.distance(prey.getBounds().x, prey.getBounds().y) < 1) {
			if(creature.inventory.hasEquiped(Slot.WEAPON) && creature.getWeapon().isRanged()) {
				if(!(CombatUtils.getWeaponType(creature).equals(WeaponType.THROWN) || equip(Slot.AMMO))) {
					InventoryHandler.unequip(creature.getWeapon().getUID(), creature);
				}
			} else if(!creature.inventory.hasEquiped(Slot.WEAPON)) {
				equip(Slot.WEAPON);
			} 
			Engine.post(new CombatEvent(creature, prey));
		} else if(Engine.getAtlas().getCurrentZone().getCreature(p) == null) {
			if(MotionHandler.move(creature, p) == MotionHandler.DOOR) {
				open(p);	// deur opendoen indien nodig
			}
		} else {	// als een ander creature in de weg staat
			wander();
		}
	}
	
	private boolean hasItem(Creature creature, RItem item) {
		for(long uid : creature.inventory) {
			if(Engine.getStore().getEntity(uid).getID().equals(item.id)) {
				return true;
			}
		}
		return false;
	}
}
