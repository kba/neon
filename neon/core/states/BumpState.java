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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Popup;
import neon.core.Engine;
import neon.core.event.CombatEvent;
import neon.core.handlers.MotionHandler;
import neon.maps.Atlas;
import neon.objects.entities.Animal;
import neon.objects.entities.Creature;
import neon.objects.entities.Player;
import neon.objects.resources.RCreature.Size;
import neon.ui.GamePanel;
import neon.util.fsm.State;
import neon.util.fsm.TransitionEvent;

public class BumpState extends State implements KeyListener {
	private Creature creature;
	private Popup popup;
	private GamePanel panel;
	private Atlas atlas;
	
	public BumpState(State parent, Atlas atlas) {
		super(parent);
		this.atlas = atlas;
	}
	
	@Override
	public void enter(TransitionEvent t) {
		creature = (Creature)t.getParameter("creature");
		panel = (GamePanel)getVariable("panel");
		panel.addKeyListener(this);
		if(creature.hasDialog()) {
			popup = Engine.getUI().showPopup("1) attack 2) talk 3) pick pocket 4) switch place 0) cancel");
		} else if(isMount(creature)) {
			popup = Engine.getUI().showPopup("1) attack 3) pick pocket 4) switch place 5) mount 0) cancel");
		} else {
			popup = Engine.getUI().showPopup("1) attack 3) pick pocket 4) switch place 0) cancel");
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
			Engine.post(new CombatEvent(Engine.getPlayer(), creature));
			creature.brain.makeHostile(Engine.getPlayer());
			Engine.post(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_2:
		case KeyEvent.VK_NUMPAD2: 
			if(creature.hasDialog()) {
				Engine.post(new TransitionEvent("dialog", "speaker", creature));
			}
			break;
		case KeyEvent.VK_3: 
		case KeyEvent.VK_NUMPAD3: 
			System.out.println("not implemented"); 
			Engine.post(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_4:
		case KeyEvent.VK_NUMPAD4: 
			swap();
			Engine.post(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_5: 
		case KeyEvent.VK_NUMPAD5: 
			if(isMount(creature)) {
				Player player = Engine.getPlayer();
				player.mount(creature);
				player.getBounds().setLocation(creature.getBounds().x, creature.getBounds().y);
				atlas.getCurrentZone().removeCreature(creature.getUID());
				panel.repaint();
				Engine.post(new TransitionEvent("return"));
			}
			break;
		case KeyEvent.VK_0: 
		case KeyEvent.VK_NUMPAD0: 
			Engine.post(new TransitionEvent("return"));
			break;
		}
	}
	
	private void swap() {
		Player player = Engine.getPlayer();
		int px = player.getBounds().x;
		int py = player.getBounds().y;
		if(MotionHandler.move(player, creature.getBounds().x, creature.getBounds().y) == MotionHandler.OK); {
			creature.getBounds().setLocation(px, py);			
		}
	}
	
	private boolean isMount(Creature mount) {
		return mount instanceof Animal && mount.species.size == Size.large
				&& !mount.brain.isHostile();
	}
}
