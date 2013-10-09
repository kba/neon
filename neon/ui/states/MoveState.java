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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import neon.core.Engine;
import neon.core.event.CombatEvent;
import neon.core.event.TurnEvent;
import neon.core.handlers.*;
import neon.entities.Container;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Entity;
import neon.entities.Item;
import neon.entities.Player;
import neon.entities.property.Condition;
import neon.entities.property.Slot;
import neon.resources.CClient;
import neon.resources.RItem;
import neon.ui.Client;
import neon.ui.GamePanel;
import neon.util.fsm.TransitionEvent;
import neon.util.fsm.State;

public class MoveState extends State implements KeyListener {
	private Player player;
	private GamePanel panel;
	private CClient keys;

	public MoveState(State parent) {
		super(parent, "move module");
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
		Point p = new Point(player.getBounds().x + x, player.getBounds().y + y);

		// kijken of creature in de weg staat
		Creature other = Engine.getAtlas().getCurrentZone().getCreature(p);
		if(other != null && !other.hasCondition(Condition.DEAD)) {
			if(other.brain.isHostile()) {
				Engine.post(new CombatEvent(player, other));
				Engine.post(new TurnEvent(Engine.getTimer().addTick())); // volgende beurt
			} else {
				transition(new TransitionEvent("bump", "creature", other));
			}
		} else {	// niemand in de weg, dus moven
			if(MotionHandler.move(player, p) == MotionHandler.DOOR) {
				for(long uid : Engine.getAtlas().getCurrentZone().getItems(p)) {
					if(Engine.getStore().getEntity(uid) instanceof Door) {
						transition(new TransitionEvent("door", "door", Engine.getStore().getEntity(uid)));
					}
				}
//			} else if(Configuration.audio) {	// TODO: audio hoort hier niet thuis
//				new WavePlayer("data/step.wav").start();
			}
			Engine.post(new TurnEvent(Engine.getTimer().addTick())); // volgende beurt
		}
	}
	
	/*
	 * dingen om te doen als spatie is gebruikt
	 */
	private void act() {
		// hier de lijst klonen, anders concurrentmodificationexceptions bij item oppakken
		ArrayList<Long> items = new ArrayList<Long>(Engine.getAtlas().getCurrentZone().getItems(player.bounds));
		Creature c = Engine.getAtlas().getCurrentZone().getCreature(player.bounds.getLocation());
		if(c != null) {
			items.add(c.getUID());
		}

		if(items.size() == 1) {
			Entity entity = Engine.getStore().getEntity(items.get(0));
			if(entity instanceof Container) {
				Container container = (Container)entity;
				if(container.lock.isLocked()) {
					if(container.lock.hasKey() && hasItem(player, container.lock.getKey())) {
						transition(new TransitionEvent("container", "holder", entity));
					} else {
						transition(new TransitionEvent("lock", "lock", container.lock));						
					}
				} else {
					transition(new TransitionEvent("container", "holder", entity));
				}
			} else if(entity instanceof Door) {
				if(MotionHandler.teleport(player, (Door)entity) == MotionHandler.OK) {
					Engine.post(new TurnEvent(Engine.getTimer().addTick()));
				}
			} else if(entity instanceof Creature){
				transition(new TransitionEvent("container", "holder", entity));							
			} else {
				Engine.getAtlas().getCurrentZone().removeItem((Item)entity);
				InventoryHandler.addItem(player, entity.getUID());
			}
		} else if(items.size() > 1) {
			transition(new TransitionEvent("container", "holder", Engine.getAtlas().getCurrentZone()));
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
			transition(new TransitionEvent("aim"));
		} else if(code == keys.shoot) {
			transition(new TransitionEvent("aim"));
		} else if(code == keys.talk) {
			transition(new TransitionEvent("aim"));
		} else if(code == keys.unmount) {
			if(player.isMounted()) {
				Creature mount = player.getMount();
				player.unmount();
				Engine.getAtlas().getCurrentZone().addCreature(mount);
				mount.getBounds().setLocation(player.getBounds().x, player.getBounds().y);
			}
		} else if(code == keys.magic) {
			int out = MagicHandler.RANGE;
			if(player.animus.getSpell() != null) {
				out = MagicHandler.cast(player, player.animus.getSpell());
			} else if(player.inventory.hasEquiped(Slot.MAGIC)) {
				Item item = (Item)Engine.getStore().getEntity(player.inventory.get(Slot.MAGIC));
				out = MagicHandler.cast(player, item);
			} 
			switch(out) {
			case MagicHandler.MANA: Client.getUI().showMessage("Not enough mana to cast this spell.", 1); break;
			case MagicHandler.NONE: Client.getUI().showMessage("No spell equiped.", 1); break;
			case MagicHandler.SKILL: Client.getUI().showMessage("Casting failed.", 1); break;
			case MagicHandler.OK: Client.getUI().showMessage("Spell cast.", 1); break;
			case MagicHandler.NULL: Client.getUI().showMessage("No spell equiped!", 1); break;
			case MagicHandler.LEVEL: Client.getUI().showMessage("Spell is too difficult to cast.", 1); break;
			case MagicHandler.SILENCED: Client.getUI().showMessage("You are silenced", 1); break;
			case MagicHandler.INTERVAL: Client.getUI().showMessage("Can't cast this power yet.", 1); break;
			case MagicHandler.RANGE: transition(new TransitionEvent("aim")); break;
			}
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
