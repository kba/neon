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
import neon.entities.components.HealthComponent;
import neon.entities.property.Attribute;
import neon.resources.CClient;
import neon.resources.RScript;
import neon.ui.*;
import neon.ui.dialog.MapDialog;
import neon.util.fsm.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.EventObject;
import java.util.Scanner;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.phys2d.raw.CollisionEvent;
import net.phys2d.raw.CollisionListener;

public class GameState extends State implements KeyListener, CollisionListener {
	private Player player;
	private GamePanel panel;
	private CClient keys;
	private MBassador<EventObject> bus;
	private UserInterface ui;
	
	public GameState(State parent, MBassador<EventObject> bus, UserInterface ui) {
		super(parent, "game module");
		this.bus = bus;
		this.ui = ui;
		keys = (CClient)Engine.getResources().getResource("client", "config");
		panel = new GamePanel();
		setVariable("panel", panel);
		
		// maakt functies beschikbaar voor scripting:
		Engine.getScriptEngine().put("engine", new ScriptInterface(panel));
		bus.subscribe(new TurnHandler(panel));
	}

	@Override
	public void enter(TransitionEvent e) {
		ui.showPanel(panel);
		if(e.toString().equals("start")) {
			player = Engine.getPlayer();
			Engine.getPhysicsEngine().addListener(this);
			// in geval spel start, moeten de events van de huidige kloktick nu uitgevoerd worden
			bus.publishAsync(new TurnEvent(Engine.getTimer().getTime(), true));
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
			bus.publishAsync(new TransitionEvent("inventory"));
			break;
		case KeyEvent.VK_F5:
			save(false); break;
		case KeyEvent.VK_ESCAPE:
			save(true); break;
		case KeyEvent.VK_F1:
			InputStream input = Engine.class.getResourceAsStream("help.html");
			String help = new Scanner(input, "UTF-8").useDelimiter("\\A").next();
			ui.showHelp(help);
			break;
		case KeyEvent.VK_F2:
			panel.toggleHUD();
			break;
		case KeyEvent.VK_F3:
			ui.showConsole(Engine.getScriptEngine());
			break;
		default:
			if(code == keys.map) {
				new MapDialog(ui.getWindow(), Engine.getAtlas().getCurrentZone()).show();
			} else if(code == keys.sneak) {
				player.setSneaking(!player.isSneaking());
				panel.repaint();
			} else if(code == keys.journal) {
				bus.publishAsync(new TransitionEvent("journal"));
			}
		}
	}
	
	private void save(boolean quit) {
		if(quit) {
			if(ui.showQuestion("Do you wish to quit?")) {
				if(ui.showQuestion("Do you wish to save?")) {
					bus.publish(new SaveEvent(this));
				} 
				Engine.quit();
			} else {
				panel.repaint();
			}
		} else {
			if(ui.showQuestion("Do you wish to save?")) {
				bus.publish(new SaveEvent(this));
			}
			panel.repaint();
		}
	}

	// voorlopig alleen controleren of de player op een region staat die een script moet draaien
	public void collisionOccured(CollisionEvent event) {
		Object one = event.getBodyA().getUserData();
		Object two = event.getBodyB().getUserData();

		try {
			if(one.equals(0L) && two instanceof neon.maps.Region) {
				for(String s : ((neon.maps.Region)two).getScripts()) {
					RScript rs = (RScript)Engine.getResources().getResource(s, "script");
					Engine.execute(rs.script);
				}
			} else if(one instanceof neon.maps.Region && two.equals(0L)) {
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
					HealthComponent health = ce.getDefender().getHealthComponent();
					panel.print("You strike the " + ce.getDefender() + " (" + health.getHealth() + ").");
					break;
				case CombatEvent.DIE:
					panel.print("You killed the " + ce.getDefender() + ".");
					bus.publishAsync(new DeathEvent(ce.getDefender(), Engine.getTimer().getTime()));
					break;
				}
			}
		}
	}

	@Handler public void handleSkill(SkillEvent se) {
		if(se.getStat() != Attribute.NONE) {
			panel.print("Stat raised: " + se.getSkill().stat);
		} else if(se.hasLevelled()) {
			panel.print("Level up.");			
		} else {
			panel.print("Skill raised: " + se.getSkill() + ".");
		}
	}
}
