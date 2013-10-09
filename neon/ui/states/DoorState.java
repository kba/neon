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

import neon.core.*;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Player;
import java.awt.event.*;
import javax.swing.Popup;
import neon.resources.RItem;
import neon.ui.Client;
import neon.ui.GamePanel;
import neon.util.fsm.*;

public class DoorState extends State implements KeyListener {
	private Door door;
	private GamePanel panel;
	private Popup popup;
	
	public DoorState(State state) {
		super(state);
	}
	
	@Override
	public void enter(TransitionEvent e) {
		panel = (GamePanel)getVariable("panel");
		panel.addKeyListener(this);
		door = (Door)e.getParameter("door");
			
		if(Engine.getPlayer().bounds.getLocation().distance(door.bounds.getLocation()) < 2) {
			if(door.lock.isClosed()) {
				popup = Client.getUI().showPopup("1) open door 2) lock door 0) cancel");
			} else if(door.lock.isLocked()) {
				popup = Client.getUI().showPopup("1) pick lock 2) unlock door 3) bash door 0) cancel");
			} else {
				popup = Client.getUI().showPopup("1) close door 2) lock door 0) cancel");
			} 
		} else {
			transition(new TransitionEvent("return"));
		}
	}

	@Override
	public void exit(TransitionEvent e) {
		popup.hide();
		panel.removeKeyListener(this);
		panel.repaint();
	}

	public void keyReleased(KeyEvent ke) {}
	public void keyTyped(KeyEvent ke) {}
	public void keyPressed(KeyEvent ke) {
		Player player = Engine.getPlayer();
		switch(ke.getKeyCode()) {
		case KeyEvent.VK_1:
		case KeyEvent.VK_NUMPAD1:
			if(door.lock.isClosed()) {
				door.lock.open();
				Client.getUI().showMessage("Door opened.", 1);
				panel.repaint();
			} else if(door.lock.isLocked()) {
				if(player.pickLock(door.lock)) {
					door.lock.unlock();
					Client.getUI().showMessage("Lock picked.", 1);
				} else {
					Client.getUI().showMessage("The lock doesn't budge.", 1);
				}
			} else if(door.lock.isOpen()) {
				door.lock.close();
				Client.getUI().showMessage("Door closed.", 1);
			}
			transition(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_2:
		case KeyEvent.VK_NUMPAD2:
			if(door.lock.getLockDC() == 0) {
				Client.getUI().showMessage("This door has no lock.", 1);
			} else if(door.lock.getKey() != null && hasItem(player, door.lock.getKey())) {
				if(door.lock.isClosed() || door.lock.isOpen()) {
					door.lock.lock();
					Client.getUI().showMessage("Door locked.", 1);
				} else if(door.lock.isLocked()) {
					door.lock.unlock();	
					Client.getUI().showMessage("Door unlocked.", 1);
				}
			} else {
				Client.getUI().showMessage("No key for this door.", 1);
			}
			transition(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_3: 
		case KeyEvent.VK_NUMPAD3: 
			if(door.lock.isLocked()) {
				door.lock.open();
				door.lock.setLockDC(0);
				Client.getUI().showMessage("Lock broken", 1);
				panel.repaint();
			}
			transition(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_0:
		case KeyEvent.VK_NUMPAD0: 
			transition(new TransitionEvent("return"));
			break;
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
