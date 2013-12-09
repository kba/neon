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
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.*;
import javax.swing.Popup;
import neon.core.*;
import neon.core.event.CombatEvent;
import neon.core.event.MagicEvent;
import neon.core.handlers.*;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Item;
import neon.entities.Player;
import neon.entities.Weapon;
import neon.entities.property.Slot;
import neon.maps.Zone;
import neon.resources.CClient;
import neon.resources.RWeapon.WeaponType;
import neon.systems.animation.Translation;
import neon.ui.GamePanel;
import neon.ui.UserInterface;
import neon.ui.graphics.DefaultRenderable;
import neon.util.fsm.*;
import net.engio.mbassy.bus.MBassador;

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
	private MBassador<EventObject> bus;
	private UserInterface ui;
	
	/**
	 * Constructs a new AimModule.
	 */
	public AimState(State state, MBassador<EventObject> bus, UserInterface ui) {
		super(state);
		this.bus = bus;
		this.ui = ui;
		keys = (CClient)Engine.getResources().getResource("client", "config");
		target = new Point();
	}

	@Override
	public void enter(TransitionEvent e) {
		panel = (GamePanel)getVariable("panel");
		player = Engine.getPlayer();
		Rectangle bounds = player.getShapeComponent();
		target.setLocation(bounds.getLocation());
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
			bus.publishAsync(new TransitionEvent("return", "message", "Aiming cancelled."));
		} else if(code == keys.shoot) {
			shoot();
		} else if(code == keys.magic) {
			cast();
		} else if(code == keys.talk) {
			talk();
		}
	}

	private void shoot() {
		Rectangle bounds = player.getShapeComponent();
		if(target.distance(bounds.x, bounds.y) < 5) {
			Creature victim = Engine.getAtlas().getCurrentZone().getCreature(target);
			if(victim != null) {
				Weapon ammo = (Weapon)Engine.getStore().getEntity(player.getInventoryComponent().get(Slot.AMMO));
				if(player.getInventoryComponent().hasEquiped(Slot.AMMO) && ammo.getWeaponType() == WeaponType.THROWN) {
					shoot(ammo, victim);
					bus.publishAsync(new CombatEvent(CombatEvent.FLING, player, victim));
				} else if(CombatUtils.getWeaponType(player) == WeaponType.BOW) {
					if(player.getInventoryComponent().hasEquiped(Slot.AMMO) && ammo.getWeaponType() == WeaponType.ARROW) {
						shoot(ammo, victim);
						bus.publishAsync(new CombatEvent(CombatEvent.SHOOT, player, victim));
					} else {
						ui.showMessage("No arrows equiped!", 1);
					}
				} else if(CombatUtils.getWeaponType(player) == WeaponType.CROSSBOW) {
					if(player.getInventoryComponent().hasEquiped(Slot.AMMO) && ammo.getWeaponType() == WeaponType.BOLT) {
						bus.publishAsync(new CombatEvent(CombatEvent.SHOOT, player, victim));
					} else {
						ui.showMessage("No bolts equiped!", 1);
					}
				} else {
					ui.showMessage("No ranged weapon equiped!", 1);
				}
			} else {
				ui.showMessage("No target!", 1);
			}
		} else {
			ui.showMessage("Out of range!", 1);
		}		
		bus.publishAsync(new TransitionEvent("return"));			
	}
	
	private void shoot(Item projectile, Creature victim) {
		// get bounds of all involved entities
		Rectangle prBounds = projectile.getShapeComponent();
		Rectangle vBounds = victim.getShapeComponent();
		Rectangle plBounds = player.getShapeComponent();

		// shoot
		prBounds.setLocation(vBounds.x, vBounds.y);
		Engine.getAtlas().getCurrentZone().addItem(projectile);
		new Thread(new Translation(projectile, plBounds.x, plBounds.y, 
				vBounds.x, vBounds.y, 100, panel)).start();
	}
	
	private void talk() {
		Rectangle bounds = player.getShapeComponent();
		if(target.distance(bounds.getLocation()) < 2) {
			Creature creature = Engine.getAtlas().getCurrentZone().getCreature(target);
			if(creature != null) {
				if(creature.hasDialog()) {
					// dialog module
					bus.publishAsync(new TransitionEvent("dialog", "speaker", creature));
				} else {
					bus.publishAsync(new TransitionEvent("return", "message", "Creature can't talk."));
				}
			} else {
				bus.publishAsync(new TransitionEvent("return", "message", "No person to talk to selected."));
			}
		} else {
			ui.showMessage("Too far away.", 1);
		}		
	}
	
	private void cast() {
		if(player.getMagicComponent().getSpell() != null) {
			bus.publishAsync(new MagicEvent.CreatureOnPoint(this, player, target));
		} else if(player.getInventoryComponent().hasEquiped(Slot.MAGIC)) {
			Item item = (Item)Engine.getStore().getEntity(player.getInventoryComponent().get(Slot.MAGIC));
			bus.publishAsync(new MagicEvent.ItemOnPoint(this, player, item, target));
		}

		bus.publishAsync(new TransitionEvent("return"));
	}
	
	private void look() {
		cursor.setX(target.x);
		cursor.setY(target.y);
		panel.repaint();
		// beschrijving van waar naar gekeken wordt
		Rectangle bounds = player.getShapeComponent();
		if(target.distance(bounds.getLocation()) < 20) {
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
			popup = ui.showPopup(zone.getRegion(target) + items + actors);
		} else {
			popup = ui.showPopup("Too far away.");			
		}
	}
	
	private void act() {
		for(long uid : Engine.getAtlas().getCurrentZone().getItems(target)) {
			if(Engine.getStore().getEntity(uid) instanceof Door) {
				bus.publishAsync(new TransitionEvent("door", "door", Engine.getStore().getEntity(uid)));
				break;
			}
		}
	}
}
