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
import java.awt.Rectangle;
import java.util.HashMap;
import neon.core.Engine;
import neon.core.event.CombatEvent;
import neon.core.handlers.*;
import neon.entities.Clothing;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Item;
import neon.entities.Weapon;
import neon.entities.components.FactionComponent;
import neon.entities.property.Condition;
import neon.entities.property.Skill;
import neon.entities.property.Slot;
import neon.magic.Effect;
import neon.resources.RItem;
import neon.resources.RSpell;
import neon.resources.RWeapon.WeaponType;

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
		byte disposition = (byte)(40 + other.getStatsComponent().getCha());
		if(creature.species.id.equals(other.species.id)) {
			disposition += 5;		// zelfde soort is ok
		}
		FactionComponent factions = creature.getFactionComponent();
		for(String faction : factions.getFactions().keySet()) {
			if(factions.isMember(faction)) {
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
			Rectangle cBounds = creature.getShapeComponent();
			Rectangle oBounds = other.getShapeComponent();
			return Point.distance(cBounds.x, cBounds.y, oBounds.x, oBounds.y) < 16;
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
			Rectangle bounds = creature.getShapeComponent();
			return p.distance(bounds.x, bounds.y) < 16;
		}
	}
	
	/*
	 * heal: kijken of er spells/scrolls/potions zijn om te healen
	 */
	protected boolean heal() {
		// eerst potions en scrolls bekijken?
		for(long uid : creature.getInventoryComponent()) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item instanceof Item.Scroll || item instanceof Item.Potion) {
				RSpell formula = item.getMagicComponent().getSpell();
				
				if(formula.effect.equals(Effect.RESTORE_HEALTH) && formula.range == 0) {
					MagicHandler.cast(creature, item);
					InventoryHandler.removeItem(creature, item.getUID());
					return true;
				}
			}
		}
		int time = Engine.getTimer().getTime();
		for(RSpell.Power power : creature.getMagicComponent().getPowers()) {
			if(power.effect.equals(Effect.RESTORE_HEALTH) && power.range == 0 && 
					creature.getMagicComponent().canUse(power, time)) {
				MagicHandler.cast(creature, power);
				return true;
			}
		}
		for(RSpell spell : creature.getMagicComponent().getSpells()) {
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
		for(long uid : creature.getInventoryComponent()) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item instanceof Item.Scroll || item instanceof Item.Potion) {
				RSpell formula = item.getMagicComponent().getSpell();
				if(formula.effect.equals(effect) && formula.range == 0) {
					MagicHandler.cast(creature, item);
					InventoryHandler.removeItem(creature, item.getUID());
					return true;
				}
			}
		}
		int time = Engine.getTimer().getTime();
		for(RSpell.Power power : creature.getMagicComponent().getPowers()) {
			if(power.effect.equals(effect) && power.range == 0 && 
					creature.getMagicComponent().canUse(power, time)) {
				MagicHandler.cast(creature, power);
				return true;
			}
		}
		for(RSpell spell : creature.getMagicComponent().getSpells()) {
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
		for(long uid : creature.getInventoryComponent()) {
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
		Rectangle cBounds = creature.getShapeComponent();
		Rectangle hBounds = hunter.getShapeComponent();

		int dx = 0;
		int dy = 0;
		if(cBounds.x < hBounds.x) { 
			dx = -1; 
		} else if(cBounds.x > hBounds.x) { 
			dx = 1; 
		}
		if(cBounds.y < hBounds.y) { 
			dy = -1; 
		} else if(cBounds.y > hBounds.y) { 
			dy = 1; 
		}				
		Point p = new Point(cBounds.x + dx, cBounds.y + dy);

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
		Rectangle bounds = creature.getShapeComponent();
		// prooi aanvallen
		hunt(prey);			
		// indien te ver verwijderd van home, terugkeren
		if(home.distance(bounds.getLocation()) > range) {
			MotionHandler.move(creature, bounds.getLocation());				
		}
	}

	/*
	 * wander(range): rondwandelen binnen territorium
	 */
	protected void wander(int range, Point home) {
		// huidige positie creature opslaan
		Rectangle bounds = creature.getShapeComponent();
		double oldDistance = home.distance(bounds.getLocation());
		// wandel willekeurig rond
		wander();
		// indien te ver verwijderd van home, terugkeren
		double newDistance = home.distance(bounds.getLocation());
		if(newDistance > range && newDistance > oldDistance) {
			MotionHandler.move(creature, bounds.getLocation());				
		}
	}

	/*
	 * wander: wandel gewoon wat rond
	 */
	protected void wander() {
		Rectangle cBounds = creature.getShapeComponent();
		Rectangle pBounds = Engine.getPlayer().getShapeComponent();
		
		int dx = 1 - (int)(Math.random()*3);
		int dy = 1 - (int)(Math.random()*3);
		Point p = new Point(cBounds.x + dx, cBounds.y + dy);
		Point player = pBounds.getLocation();

		if(Engine.getAtlas().getCurrentZone().getCreature(p) == null && !player.equals(p)) {
			MotionHandler.move(creature, p);
		}
	}

	/*
	 * wander(point): naar een bepaald punt wandelen
	 */
	protected void wander(Point destination) {
		Rectangle pBounds = Engine.getPlayer().getShapeComponent();
		Rectangle cBounds = creature.getShapeComponent();

		Point player = pBounds.getLocation();
		Point next = PathFinder.findPath(creature, cBounds.getLocation(), destination)[0];
		if(Engine.getAtlas().getCurrentZone().getCreature(next) == null && !player.equals(next)) {
			MotionHandler.move(creature, next);
		}
	}
	
	/*
	 * hunt: jaag op een prooi
	 */
	protected void hunt(Creature prey) {
		int dice = neon.util.Dice.roll(1,2,0);
		Rectangle creaturePos = creature.getShapeComponent();
		Rectangle preyPos = prey.getShapeComponent();
		
		if(dice == 1) {
			int time = Engine.getTimer().getTime();
			for(RSpell.Power power : creature.getMagicComponent().getPowers()) {
				if(power.effect.getSchool().equals(Skill.DESTRUCTION) && 
						creature.getMagicComponent().canUse(power, time) && 
						power.range >= Point.distance(creaturePos.x, creaturePos.y, 
						preyPos.x, preyPos.y)) {
					creature.getMagicComponent().equipSpell(power);
					Rectangle bounds = prey.getShapeComponent();
					MagicHandler.cast(creature, bounds.getLocation());
					return;	// hunt afbreken van zodra er een spell is gecast
				}
			}
			for(RSpell spell : creature.getMagicComponent().getSpells()) {
				if(spell.effect.getSchool().equals(Skill.DESTRUCTION) && 
						spell.range >= Point.distance(creaturePos.x, creaturePos.y, 
						preyPos.x, preyPos.y)) {
					creature.getMagicComponent().equipSpell(spell);
					Rectangle bounds = prey.getShapeComponent();
					MagicHandler.cast(creature, bounds.getLocation());
					return;	// hunt afbreken van zodra er een spell is gecast
				}
			}
		} 

		Point p;
		if(creature.getStatsComponent().getInt() < 5) {		// als wezen lomp is, gewoon kortste weg proberen
			int dx = 0;
			int dy = 0;
			if(creaturePos.x < preyPos.x) { 
				dx = 1; 
			} else if(creaturePos.x > preyPos.x) { 
				dx = -1; 
			}
			if(creaturePos.y < preyPos.y) { 
				dy = 1; 
			} else if(creaturePos.y > preyPos.y) { 
				dy = -1; 
			}				
			p = new Point(creaturePos.x + dx, creaturePos.y + dy);
		} else {						// als wezen slimmer is, A* proberen
			Rectangle cBounds = creature.getShapeComponent();
			Rectangle pBounds = prey.getShapeComponent();
			p = PathFinder.findPath(creature, cBounds.getLocation(), pBounds.getLocation())[0];
		}

		if(p.distance(preyPos.x, preyPos.y) < 1) {
			long uid = creature.getInventoryComponent().get(Slot.WEAPON);
			Weapon weapon = (Weapon)Engine.getStore().getEntity(uid);
			if(creature.getInventoryComponent().hasEquiped(Slot.WEAPON) && weapon.isRanged()) {
				if(!(CombatUtils.getWeaponType(creature).equals(WeaponType.THROWN) || equip(Slot.AMMO))) {
					InventoryHandler.unequip(weapon.getUID(), creature);
				}
			} else if(!creature.getInventoryComponent().hasEquiped(Slot.WEAPON)) {
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
		for(long uid : creature.getInventoryComponent()) {
			if(Engine.getStore().getEntity(uid).getID().equals(item.id)) {
				return true;
			}
		}
		return false;
	}
}
