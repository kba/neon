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

package neon.ui.states;

import java.awt.Point;
import neon.core.*;
import neon.core.event.CombatEvent;
import neon.core.handlers.*;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Item;
import neon.entities.Player;
import neon.entities.Weapon;
import neon.entities.property.Slot;
import java.awt.event.*;
import neon.maps.Zone;
import java.util.*;
import javax.swing.Popup;
import neon.resources.CClient;
import neon.resources.RWeapon.WeaponType;
import neon.systems.animation.Translation;
import neon.ui.Client;
import neon.ui.GamePanel;
import neon.ui.graphics.DefaultRenderable;
import neon.util.fsm.*;

/**
 * Implements all methods to aim at something. This is done by showing a cursor 
 * on the main game field.
 * 
 * @author mdriesen
 */
public class AimState extends State implements KeyListener {
	private Point target;
	private Player player;
	private DefaultRenderable cursor;
	private Popup popup;
	private GamePanel panel;
	private CClient keys;
	
	/**
	 * Constructs a new AimModule.
	 */
	public AimState(State state) {
		super(state);
		keys = (CClient)Engine.getResources().getResource("client", "config");
		target = new Point();
	}

	@Override
	public void enter(TransitionEvent e) {
		panel = (GamePanel)getVariable("panel");
		player = Engine.getPlayer();
		target.setLocation(player.bounds.getLocation());
		panel.print("Use arrow keys to move cursor. Press L to cancel, " +
				"F to shoot, T to talk and G to cast a spell.");
		panel.addKeyListener(this);
		cursor = panel.showCursor();
	}
	
	@Override
	public void exit(TransitionEvent e) {
		panel.removeKeyListener(this);
		if(popup != null) {	// kan gebeuren als popup nog niet geset is in look()
			popup.hide();
		}
		panel.hideCursor();
		panel.repaint();
	}
	
	public void keyReleased(KeyEvent key) { }
	public void keyTyped(KeyEvent key) { }
	public void keyPressed(KeyEvent key) {
		int code = key.getKeyCode();
		if(code == keys.up) {
			target.y--;
			look();
		} else if(code == keys.upright) {
			target.x++; target.y--;
			look();
		} else if(code == keys.right) {
			target.x++;
			look();
		} else if(code == keys.downright) {
			target.x++; target.y++;
			look();
		} else if(code == keys.down) {
			target.y++;
			look();
		} else if(code == keys.downleft) {
			target.x--;	target.y++;
			look();
		} else if(code == keys.left) {
			target.x--;
			look();
		} else if(code == keys.upleft) {
			target.x--; target.y--;
			look();
		} else if(code == keys.act) {
			act();
		} else if(code == keys.look) {
			transition(new TransitionEvent("return", "message", "Aiming cancelled."));
		} else if(code == keys.shoot) {
			shoot();
		} else if(code == keys.magic) {
			cast();
		} else if(code == keys.talk) {
			talk();
		}
	}

	private void shoot() {
		if(target.distance(player.getBounds().x, player.getBounds().y) < 5) {
			Creature victim = Engine.getAtlas().getCurrentZone().getCreature(target);
			if(victim != null) {
				Weapon ammo = (Weapon)Engine.getStore().getEntity(player.inventory.get(Slot.AMMO));
				if(player.inventory.hasEquiped(Slot.AMMO) && ammo.getWeaponType() == WeaponType.THROWN) {
					shoot(ammo, victim);
					Engine.post(new CombatEvent(CombatEvent.FLING, player, victim));
				} else if(CombatUtils.getWeaponType(player) == WeaponType.BOW) {
					if(player.inventory.hasEquiped(Slot.AMMO) && ammo.getWeaponType() == WeaponType.ARROW) {
						shoot(ammo, victim);
						Engine.post(new CombatEvent(CombatEvent.SHOOT, player, victim));
					} else {
						Client.getUI().showMessage("No arrows equiped!", 1);
					}
				} else if(CombatUtils.getWeaponType(player) == WeaponType.CROSSBOW) {
					if(player.inventory.hasEquiped(Slot.AMMO) && ammo.getWeaponType() == WeaponType.BOLT) {
						Engine.post(new CombatEvent(CombatEvent.SHOOT, player, victim));
					} else {
						Client.getUI().showMessage("No bolts equiped!", 1);
					}
				} else {
					Client.getUI().showMessage("No ranged weapon equiped!", 1);
				}
			} else {
				Client.getUI().showMessage("No target!", 1);
			}
		} else {
			Client.getUI().showMessage("Out of range!", 1);
		}		
		transition(new TransitionEvent("return"));			
	}
	
	private void shoot(Item projectile, Creature victim) {
		projectile.getBounds().setLocation(victim.getBounds().x, victim.getBounds().y);
		Engine.getAtlas().getCurrentZone().addItem(projectile);
		new Thread(new Translation(projectile, player.getBounds().x, player.getBounds().y, 
				victim.getBounds().x, victim.getBounds().y, 100, panel)).start();
	}
	
	private void talk() {
		if(target.distance(player.getBounds().getLocation()) < 2) {
			Creature creature = Engine.getAtlas().getCurrentZone().getCreature(target);
			if(creature != null) {
				if(creature.hasDialog()) {
					// dialog module
					TransitionEvent te = new TransitionEvent("dialog", "speaker", creature);
					Engine.post(te);
					transition(te);
				} else {
					transition(new TransitionEvent("return", "message", "Creature can't talk."));
				}
			} else {
				transition(new TransitionEvent("return", "message", "No person to talk to selected."));
			}
		} else {
			Client.getUI().showMessage("Too far away.", 1);
		}		
	}
	
	private void cast() {
		int out = MagicHandler.NULL;

		if(player.animus.getSpell() != null) {
			out = MagicHandler.cast(player, target);
		} else if(player.inventory.hasEquiped(Slot.MAGIC)) {
			Item item = (Item)Engine.getStore().getEntity(player.inventory.get(Slot.MAGIC));
			out = MagicHandler.cast(player, target, item);
		}

		switch(out) {
		case MagicHandler.MANA: Client.getUI().showMessage("Not enough mana to cast this spell.", 1); break;
		case MagicHandler.RANGE: Client.getUI().showMessage("Target out of range.", 1); break;
		case MagicHandler.NONE: Client.getUI().showMessage("No spell equiped.", 1); break;
		case MagicHandler.SKILL: Client.getUI().showMessage("Casting failed.", 1); break;
		case MagicHandler.OK: Client.getUI().showMessage("Spell cast.", 1); break;
		case MagicHandler.NULL: Client.getUI().showMessage("No target selected.", 1); break;
		}
		transition(new TransitionEvent("return"));
	}
	
	private void look() {
		cursor.setX(target.x);
		cursor.setY(target.y);
		panel.repaint();
		// beschrijving van waar naar gekeken wordt
		if(target.distance(player.getBounds().getLocation()) < 20) {
			Zone zone = Engine.getAtlas().getCurrentZone();
			String items = "";
			String actors = "";
			ArrayList<Long> things = new ArrayList<Long>(zone.getItems(target));
			if(things.size() == 1) {
				items = ", " + Engine.getStore().getEntity(things.get(0));
			} else if(things.size() > 1) {
				items = ", several items";
			}
			Creature creature = Engine.getAtlas().getCurrentZone().getCreature(target);
			if(creature != null) {
				actors = ", " + creature.toString();
			}
			popup = Client.getUI().showPopup(zone.getRegion(target) + items + actors);
		} else {
			popup = Client.getUI().showPopup("Too far away.");			
		}
	}
	
	private void act() {
		for(long uid : Engine.getAtlas().getCurrentZone().getItems(target)) {
			if(Engine.getStore().getEntity(uid) instanceof Door) {
				transition(new TransitionEvent("door", "door", Engine.getStore().getEntity(uid)));
				break;
			}
		}
	}
}
