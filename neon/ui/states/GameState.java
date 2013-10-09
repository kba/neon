/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
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
import neon.core.event.*;
import neon.core.handlers.TurnHandler;
import neon.entities.Player;
import neon.resources.CClient;
import neon.resources.RScript;
import neon.ui.*;
import neon.ui.dialog.MapDialog;
import java.awt.event.*;
import java.io.InputStream;
import java.util.EventObject;
import java.util.Scanner;
import neon.util.fsm.*;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;

public class GameState extends State implements KeyListener, CollisionListener {
	private Player player;
	private GamePanel panel;
	private GameSaver saver;
	private CClient keys;
	
	public GameState(State parent, TaskQueue queue, MBassador<EventObject> bus) {
		super(parent, "game module");
		keys = (CClient)Engine.getResources().getResource("client", "config");
		saver = new GameSaver(queue);
		panel = new GamePanel();
		setVariable("panel", panel);
		
		// maakt functies beschikbaar voor scripting:
		Engine.getScriptEngine().put("engine", new ScriptInterface(panel));
		bus.subscribe(new TurnHandler(panel));
	}

	@Override
	public void enter(TransitionEvent e) {
		Client.getUI().showPanel(panel);
		if(e.toString().equals("start")) {
			player = Engine.getPlayer();
			Engine.getPhysicsEngine().addListener(this);
			// in geval spel start, moeten de events van de huidige kloktick nu uitgevoerd worden
			Engine.post(new TurnEvent(Engine.getTimer().getTime(), true));
		}
		panel.setVisible(true);
		panel.addKeyListener(this);
		panel.repaint();
		
		if(e.getParameter("message") != null) {
			panel.print(e.getParameter("message").toString());	
		}
	}

	@Override
	public void exit(TransitionEvent t) {
		panel.removeKeyListener(this);
	}

	public void keyTyped(KeyEvent key) { 
		switch (key.getKeyChar()) {
		case '+': panel.zoomIn(); break;
		case '-': panel.zoomOut(); break;	
		}
	}
	
	public void keyReleased(KeyEvent key) { }
	public void keyPressed(KeyEvent key) {
		int code = key.getKeyCode();
		switch(code) {
		case KeyEvent.VK_CONTROL:
			transition(new TransitionEvent("inventory"));
			break;
		case KeyEvent.VK_F5:
			save(false); break;
		case KeyEvent.VK_ESCAPE:
			save(true); break;
		case KeyEvent.VK_F1:
			InputStream input = Engine.class.getResourceAsStream("help.html");
			String help = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
			Client.getUI().showHelp(help);
			break;
		case KeyEvent.VK_F2:
			panel.toggleHUD();
			break;
		case KeyEvent.VK_F3:
			Client.getUI().showConsole(Engine.getScriptEngine());
			break;
		default:
			if(code == keys.map) {
				new MapDialog(Client.getUI().getWindow(), Engine.getAtlas().getCurrentZone()).show();
			} else if(code == keys.sneak) {
				player.setSneaking(!player.isSneaking());
				panel.repaint();
			} else if(code == keys.journal) {
				transition(new TransitionEvent("journal"));
			}
		}
	}
	
	private void save(boolean quit) {
		if(quit) {
			if(Client.getUI().showQuestion("Do you wish to quit?")) {
				if(Client.getUI().showQuestion("Do you wish to save?")) {
					saver.saveGame();
				} 
				Engine.quit();
			} else {
				panel.repaint();
			}
		} else {
			if(Client.getUI().showQuestion("Do you wish to save?")) {
				saver.saveGame();
			}
			panel.repaint();
		}
	}

	// voorlopig alleen controleren of de player op een region staat die een script moet draaien
	public void collisionOccured(CollisionEvent event) {
		Object one = event.getBodyA().getUserData();
		Object two = event.getBodyB().getUserData();
		try {
			if(one instanceof Player && two instanceof neon.maps.Region) {
				for(String s : ((neon.maps.Region)two).getScripts()) {
					RScript rs = (RScript)Engine.getResources().getResource(s, "script");
					Engine.execute(rs.script);
				}
			} else if(one instanceof neon.maps.Region && two instanceof Player) {
				for(String s : ((neon.maps.Region)one).getScripts()) {
					RScript rs = (RScript)Engine.getResources().getResource(s, "script");
					Engine.execute(rs.script);
				}
			}
		} catch(Exception e) { }
	}
	
	@Handler public void handleCombat(CombatEvent ce) {
		if(ce.isFinished()) {
			if(ce.getDefender() == player) {
				panel.print("You were attacked!");
			} else {
				switch(ce.getResult()) {
				case CombatEvent.DODGE:
					panel.print("The " + ce.getDefender() + " dodges the attack.");
					break;
				case CombatEvent.BLOCK:
					panel.print("The " + ce.getDefender() + " blocks the attack.");
					break;
				case CombatEvent.ATTACK:
					panel.print("You strike the " + ce.getDefender() + " (" + ce.getDefender().getHealth() + ").");
					break;
				case CombatEvent.DIE:
					panel.print("You killed the " + ce.getDefender() + ".");
					Engine.post(new DeathEvent(ce.getDefender(), Engine.getTimer().getTime()));
					break;
				}
			}
		}
	}

	@Handler public void handleSkill(SkillEvent se) {
		if(se.getStat() > 0) {
			panel.print("Stat raised: " + se.getSkill().getStat());
		} else if(se.hasLevelled()) {
			panel.print("Level up.");			
		} else {
			panel.print("Skill raised: " + se.getSkill() + ".");
		}
	}
}
