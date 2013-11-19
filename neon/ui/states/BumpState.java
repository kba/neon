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

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;

import javax.swing.Popup;

import neon.core.Engine;
import neon.core.event.CombatEvent;
import neon.core.handlers.MotionHandler;
import neon.entities.Creature;
import neon.entities.Player;
import neon.entities.components.ShapeComponent;
import neon.resources.RCreature;
import neon.resources.RCreature.Size;
import neon.ui.GamePanel;
import neon.ui.UserInterface;
import neon.util.fsm.State;
import neon.util.fsm.TransitionEvent;
import net.engio.mbassy.bus.MBassador;

public class BumpState extends State implements KeyListener {
	private Creature creature;
	private Popup popup;
	private GamePanel panel;
	private MBassador<EventObject> bus;
	private UserInterface ui;
	
	public BumpState(State parent, MBassador<EventObject> bus, UserInterface ui) {
		super(parent);
		this.bus = bus;
		this.ui = ui;
	}
	
	@Override
	public void enter(TransitionEvent t) {
		creature = (Creature)t.getParameter("creature");
		panel = (GamePanel)getVariable("panel");
		panel.addKeyListener(this);
		if(creature.hasDialog()) {
			popup = ui.showPopup("1) attack 2) talk 3) pick pocket 4) switch place 0) cancel");
		} else if(isMount(creature)) {
			popup = ui.showPopup("1) attack 3) pick pocket 4) switch place 5) mount 0) cancel");
		} else {
			popup = ui.showPopup("1) attack 3) pick pocket 4) switch place 0) cancel");
		}
	}

	@Override
	public void exit(TransitionEvent t) {
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
			bus.publishAsync(new CombatEvent(Engine.getPlayer(), creature));
			creature.brain.makeHostile(Engine.getPlayer());
			bus.publishAsync(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_2:
		case KeyEvent.VK_NUMPAD2: 
			if(creature.hasDialog()) {
				bus.publishAsync(new TransitionEvent("dialog", "speaker", creature));
			}
			break;
		case KeyEvent.VK_3: 
		case KeyEvent.VK_NUMPAD3: 
			System.out.println("not implemented"); 
			bus.publishAsync(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_4:
		case KeyEvent.VK_NUMPAD4: 
			swap();
			bus.publishAsync(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_5: 
		case KeyEvent.VK_NUMPAD5: 
			if(isMount(creature)) {
				Player player = Engine.getPlayer();
				player.mount(creature);
				player.bounds.setLocation(creature.bounds.x, creature.bounds.y);
				Engine.getAtlas().getCurrentZone().removeCreature(creature.getUID());
				panel.repaint();
				bus.publishAsync(new TransitionEvent("return"));
			}
			break;
		case KeyEvent.VK_0: 
		case KeyEvent.VK_NUMPAD0: 
			bus.publishAsync(new TransitionEvent("return"));
			break;
		}
	}
	
	private void swap() {
		Player player = Engine.getPlayer();
		Rectangle pBounds = player.getComponent(ShapeComponent.class);
		Rectangle cBounds = creature.getComponent(ShapeComponent.class);
		
		
		if(MotionHandler.move(player, cBounds.x, cBounds.y) == MotionHandler.OK) {
			cBounds.setLocation(pBounds.x, pBounds.y);			
		}
	}
	
	private boolean isMount(Creature mount) {
		return mount.species.type == RCreature.Type.animal && mount.species.size == Size.large
				&& !mount.brain.isHostile();
	}
}
