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

package neon.core.states;

import neon.core.*;
import neon.core.event.*;
import neon.core.handlers.TurnHandler;
import neon.ui.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.Scanner;
import neon.maps.Atlas;
import neon.objects.entities.Player;
import neon.objects.resources.RScript;
import neon.util.fsm.*;
import net.engio.mbassy.listener.Handler;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;

public class GameState extends State implements KeyListener, CollisionListener {
	private Player player;
	private GamePanel panel;
	private GameSaver saver;
	private Atlas atlas;
	
	public GameState(Engine engine, TaskQueue queue, Atlas atlas) {
		super(engine, "game module");
		saver = new GameSaver(queue, atlas);
		panel = new GamePanel();
		this.atlas = atlas;
		setVariable("panel", panel);
		
		// maakt functies beschikbaar voor scripting:
		Engine.getScriptEngine().put("engine", new ScriptInterface(panel));
		Engine.getTimer().addListener(new TurnHandler(panel));
	}

	@Override
	public void enter(TransitionEvent e) {
		Engine.getUI().showPanel(panel);
		if(e.toString().equals("start")) {
			player = Engine.getPlayer();
			Engine.getPhysicsEngine().addListener(this);
			// in geval spel start, moeten de events van de huidige kloktick nu uitgevoerd worden
			Engine.getTimer().start();
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
			Engine.post(new TransitionEvent("inventory"));
			break;
		case KeyEvent.VK_F5:
			save(false); break;
		case KeyEvent.VK_ESCAPE:
			save(true); break;
		case KeyEvent.VK_F1:
			InputStream input = Engine.class.getResourceAsStream("help.html");
			String help = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
			Engine.getUI().showHelp(help);
			break;
		case KeyEvent.VK_F2:
			panel.toggleHUD();
			break;
		case KeyEvent.VK_F3:
			Engine.getUI().showConsole(Engine.getScriptEngine());
			break;
		default:
			if(code == KeyConfig.map) {
				new MapDialog(Engine.getUI().getWindow(), atlas.getCurrentZone()).show();
			} else if(code == KeyConfig.sneak) {
				player.setSneaking(!player.isSneaking());
				panel.repaint();
			} else if(code == KeyConfig.journal) {
				Engine.post(new TransitionEvent("journal"));
			}
		}
	}
	
	private void save(boolean quit) {
		if(quit) {
			if(Engine.getUI().showQuestion("Do you wish to quit?")) {
				if(Engine.getUI().showQuestion("Do you wish to save?")) {
					saver.saveGame();
				} 
				Engine.quit();
			} else {
				panel.repaint();
			}
		} else {
			if(Engine.getUI().showQuestion("Do you wish to save?")) {
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
