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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import neon.core.Engine;
import neon.core.event.CombatEvent;
import neon.core.event.TurnEvent;
import neon.core.handlers.*;
import neon.entities.*;
import neon.entities.property.Condition;
import neon.entities.property.Slot;
import neon.resources.CClient;
import neon.resources.RItem;
import neon.ui.GamePanel;
import neon.ui.UserInterface;
import neon.util.fsm.TransitionEvent;
import neon.util.fsm.State;
import net.engio.mbassy.bus.MBassador;

public class MoveState extends State implements KeyListener {
	private Player player;
	private GamePanel panel;
	private CClient keys;
	private MBassador<EventObject> bus;
	private UserInterface ui;

	public MoveState(State parent, MBassador<EventObject> bus, UserInterface ui) {
		super(parent, "move module");
		this.bus = bus;
		this.ui = ui;
		keys = (CClient)Engine.getResources().getResource("client", "config");
	}
	
	@Override
	public void enter(TransitionEvent e) {
		player = Engine.getPlayer();
		panel = (GamePanel)getVariable("panel");
		panel.addKeyListener(this);
	}
	
	@Override
	public void exit(TransitionEvent e) {
		panel.removeKeyListener(this);
	}
	
	private void move(int x, int y) {
		// TODO: dit moet gedeeltelijk naar MotionHandler?
		Rectangle bounds = player.getShapeComponent();
		Point p = new Point(bounds.x + x, bounds.y + y);

		// kijken of creature in de weg staat
		Creature other = Engine.getAtlas().getCurrentZone().getCreature(p);
		if(other != null && !other.hasCondition(Condition.DEAD)) {
			if(other.brain.isHostile()) {
				bus.publishAsync(new CombatEvent(player, other));
				bus.publishAsync(new TurnEvent(Engine.getTimer().addTick())); // volgende beurt
			} else {
				bus.publishAsync(new TransitionEvent("bump", "creature", other));
			}
		} else {	// niemand in de weg, dus moven
			if(MotionHandler.move(player, p) == MotionHandler.DOOR) {
				for(long uid : Engine.getAtlas().getCurrentZone().getItems(p)) {
					if(Engine.getStore().getEntity(uid) instanceof Door) {
						bus.publishAsync(new TransitionEvent("door", "door", Engine.getStore().getEntity(uid)));
					}
				}
//			} else if(Configuration.audio) {	// TODO: audio hoort hier niet thuis
//				new WavePlayer("data/step.wav").start();
			}
			bus.publishAsync(new TurnEvent(Engine.getTimer().addTick())); // volgende beurt
		}
	}
	
	/*
	 * dingen om te doen als spatie is gebruikt
	 */
	private void act() {
		// hier de lijst klonen, anders concurrentmodificationexceptions bij item oppakken
		Rectangle bounds = player.getShapeComponent();
		ArrayList<Long> items = new ArrayList<Long>(Engine.getAtlas().getCurrentZone().getItems(bounds));
		Creature c = Engine.getAtlas().getCurrentZone().getCreature(bounds.getLocation());
		if(c != null) {
			items.add(c.getUID());
		}

		if(items.size() == 1) {
			Entity entity = Engine.getStore().getEntity(items.get(0));
			if(entity instanceof Container) {
				Container container = (Container)entity;
				if(container.lock.isLocked()) {
					if(container.lock.hasKey() && hasItem(player, container.lock.getKey())) {
						bus.publishAsync(new TransitionEvent("container", "holder", entity));
					} else {
						bus.publishAsync(new TransitionEvent("lock", "lock", container.lock));						
					}
				} else {
					bus.publishAsync(new TransitionEvent("container", "holder", entity));
				}
			} else if(entity instanceof Door) {
				if(MotionHandler.teleport(player, (Door)entity) == MotionHandler.OK) {
					bus.publishAsync(new TurnEvent(Engine.getTimer().addTick()));
				}
			} else if(entity instanceof Creature){
				bus.publishAsync(new TransitionEvent("container", "holder", entity));							
			} else {
				Engine.getAtlas().getCurrentZone().removeItem((Item)entity);
				InventoryHandler.addItem(player, entity.getUID());
			}
		} else if(items.size() > 1) {
			bus.publishAsync(new TransitionEvent("container", "holder", Engine.getAtlas().getCurrentZone()));
		}
	}

	public void keyReleased(KeyEvent key) { }
	public void keyTyped(KeyEvent key) { }
	public void keyPressed(KeyEvent key) {
		int code = key.getKeyCode();
		if(code == keys.up) {
			move(0, - 1);
		} else if(code == keys.upright) {
			move(1, - 1);
		} else if(code == keys.right) {
			move(1, 0);
		} else if(code == keys.downright) {
			move(1, 1);
		} else if(code == keys.down) {
			move(0, 1);
		} else if(code == keys.downleft) {
			move(- 1, 1);
		} else if(code == keys.left) {
			move(- 1, 0);
		} else if(code == keys.upleft) {
			move(- 1, - 1);
		} else if(code == keys.wait) {
			move(0, 0);
		} else if(code == keys.act) {
			act();
		} else if(code == keys.look) {
			bus.publishAsync(new TransitionEvent("aim"));
		} else if(code == keys.shoot) {
			bus.publishAsync(new TransitionEvent("aim"));
		} else if(code == keys.talk) {
			bus.publishAsync(new TransitionEvent("aim"));
		} else if(code == keys.unmount) {
			if(player.isMounted()) {
				Creature mount = player.getMount();
				player.unmount();
				Engine.getAtlas().getCurrentZone().addCreature(mount);
				Rectangle pBounds = player.getShapeComponent();
				Rectangle mBounds = mount.getShapeComponent();
				mBounds.setLocation(pBounds.x, pBounds.y);
			}
		} else if(code == keys.magic) {
			int out = MagicHandler.RANGE;
			if(player.getMagicComponent().getSpell() != null) {
				out = MagicHandler.cast(player, player.getMagicComponent().getSpell());
			} else if(player.getInventoryComponent().hasEquiped(Slot.MAGIC)) {
				Item item = (Item)Engine.getStore().getEntity(player.getInventoryComponent().get(Slot.MAGIC));
				out = MagicHandler.cast(player, item);
			} 
			switch(out) {
			case MagicHandler.MANA: ui.showMessage("Not enough mana to cast this spell.", 1); break;
			case MagicHandler.NONE: ui.showMessage("No spell equiped.", 1); break;
			case MagicHandler.SKILL: ui.showMessage("Casting failed.", 1); break;
			case MagicHandler.OK: ui.showMessage("Spell cast.", 1); break;
			case MagicHandler.NULL: ui.showMessage("No spell equiped!", 1); break;
			case MagicHandler.LEVEL: ui.showMessage("Spell is too difficult to cast.", 1); break;
			case MagicHandler.SILENCED: ui.showMessage("You are silenced", 1); break;
			case MagicHandler.INTERVAL: ui.showMessage("Can't cast this power yet.", 1); break;
			case MagicHandler.RANGE: bus.publishAsync(new TransitionEvent("aim")); break;
			}
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
