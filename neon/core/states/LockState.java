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

package neon.core.states;

import neon.core.*;
import neon.entities.components.Lock;

import java.awt.event.*;
import javax.swing.Popup;
import neon.ui.GamePanel;
import neon.util.fsm.*;

public class LockState extends State implements KeyListener {
	private Lock lock;
	private GamePanel panel;
	private Popup popup;
	
	public LockState(State state) {
		super(state);
	}
	
	@Override
	public void enter(TransitionEvent e) {
		panel = (GamePanel)getVariable("panel");
		panel.addKeyListener(this);
		lock = (Lock)e.getParameter("lock");
		if(lock.isLocked()) {
			popup = Engine.getUI().showPopup("1) pick lock 2) bash lock 0) cancel");
		} else {
			Engine.post(new TransitionEvent("return"));
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
		switch(ke.getKeyCode()) {
		case KeyEvent.VK_1:
		case KeyEvent.VK_NUMPAD1:
			if(Engine.getPlayer().pickLock(lock)) {
				lock.unlock();
				Engine.getUI().showMessage("Lock picked.", 1);
			} else {
				Engine.getUI().showMessage("The lock doesn't budge.", 1);
			}
			Engine.post(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_2:
		case KeyEvent.VK_NUMPAD2:
			if(lock.isLocked()) {
				lock.open();
				lock.setLockDC(0);
				Engine.getUI().showMessage("Lock broken", 1);
				panel.repaint();
			}
			Engine.post(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_0:
		case KeyEvent.VK_NUMPAD0: 
			Engine.post(new TransitionEvent("return"));
			break;
		}
	}
}
