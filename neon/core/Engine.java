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

package neon.core;

import java.util.EventObject;
import java.util.logging.Logger;
import javax.script.*;
import neon.core.event.*;
import neon.core.handlers.CombatHandler;
import neon.core.handlers.DeathHandler;
import neon.core.handlers.InventoryHandler;
import neon.entities.Player;
import neon.entities.UIDStore;
import neon.maps.Atlas;
import neon.narrative.EventAdapter;
import neon.narrative.QuestTracker;
import neon.resources.ResourceManager;
import neon.resources.builder.IniBuilder;
import neon.systems.physics.PhysicsSystem;
import neon.systems.timing.Timer;
import neon.systems.files.FileSystem;
import neon.systems.io.Port;
import net.engio.mbassy.bus.MBassador;

/**
 * The engine class is the core of the neon roguelike engine. It is essentially
 * a finite state machine with some extras to keep track of all game elements.
 * 
 * @author mdriesen
 */
public class Engine implements Runnable {
	// wordt door engine geïnitialiseerd
	private static ScriptEngine engine;	
	private static FileSystem files;		// virtual file system
	private static PhysicsSystem physics;	// de physics engine
	private static Logger logger;
	private static QuestTracker quests;	
	private static MBassador<EventObject> bus;	// event bus
	private static TaskQueue queue;
	private static ResourceManager resources;
	
	private Configuration config;

	// wordt extern geset
	private static Game game;
	
	/**
	 * Initializes the engine. 
	 */
	public Engine(Port port) {
		// engine componenten opzetten
		bus = port.getBus();
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
		files = new FileSystem();
		physics = new PhysicsSystem();
		queue = new TaskQueue();
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		
		// create a resourcemanager to keep track of all the resources
		resources = new ResourceManager();
		// we use an IniBuilder to add all resources to the manager
		new IniBuilder("neon.ini", files, queue).build(resources);

		// nog engine componenten opzetten
		quests = new QuestTracker();
		config = new Configuration(resources);
		initEvents();		
	}
	
	private void initEvents() {
		EventAdapter adapter = new EventAdapter(quests);
		bus.subscribe(queue);
		bus.subscribe(new CombatHandler());	
		bus.subscribe(new DeathHandler());
		bus.subscribe(new InventoryHandler());
		bus.subscribe(adapter);
		bus.subscribe(quests);
		bus.subscribe(new GameLoader(config));
	}
	
	/**
	 * This method is the run method of the gamethread. It does nothing at the moment.
	 */
	public void run() {
	}
	
	/**
	 * Convenience method to post an event to the event bus.
	 * 
	 * @param message
	 */
	public static void post(EventObject message) {
		bus.publishAsync(message);
	}
	
/*
 * alle scriptbrol
 */
	/**
	 * Executes a script.
	 * 
	 * @param script	the script to execute
	 * @return			the result of the script
	 */
	public static Object execute(String script) {
		try {
			return engine.eval(script);
		} catch(Exception e) {
			return null;	// niet geweldig goed
		}
	}
	
/*
 * alle getters
 */
	/**
	 * @return	the player
	 */
	public static Player getPlayer() {
		return game.getPlayer();
	}
	
	public static QuestTracker getQuestTracker() {
		return quests;
	}
	
	/**
	 * @return	the timer
	 */
	public static Timer getTimer() {
		return game.getTimer();
	}
	
	/**
	 * @return	the virtual filesystem of the engine
	 */
	public static FileSystem getFileSystem() {
		return files;
	}
	
	/**
	 * @return	the physics engine
	 */
	public static PhysicsSystem getPhysicsEngine() {
		return physics;
	}
	
	/**
	 * @return	the script engine
	 */
	public static ScriptEngine getScriptEngine() {
		return engine;
	}
	
	/**
	 * @return	the logger
	 */
	public static Logger getLogger() {
		return logger;
	}
	
	public static UIDStore getStore() {
		return game.getStore();
	}
	
	public static ResourceManager getResources() {
		return resources;
	}

	public static Atlas getAtlas() {
		return game.getAtlas();
	}
	
	public static TaskQueue getQueue() {
		return queue;
	}
	
	/**
	 * Starts a new game.
	 */
	public static void startGame(Game g) {
		game = g;
		Player player = g.getPlayer();
		
		// player registreren
		engine.put("journal", player.getJournal());	
		engine.put("player", player);
		engine.put("PC", player);
		physics.register(player.physics);
	}
	
	/**
	 * quit the game
	 */
	public static void quit() {
		System.exit(0);
	}
}
