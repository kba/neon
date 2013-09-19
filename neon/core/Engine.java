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
import neon.ui.UserInterface;
import javax.script.*;
import neon.core.event.*;
import neon.core.handlers.CombatHandler;
import neon.core.handlers.DeathHandler;
import neon.core.states.*;
import neon.entities.Player;
import neon.entities.UIDStore;
import neon.maps.Atlas;
import neon.narrative.EventAdapter;
import neon.narrative.QuestTracker;
import neon.resources.ResourceManager;
import neon.systems.physics.PhysicsSystem;
import neon.systems.timing.Timer;
import neon.systems.files.FileSystem;
import neon.util.fsm.*;
import net.engio.mbassy.bus.BusConfiguration;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

/**
 * The engine class is the core of the neon roguelike engine. It is essentially
 * a finite state machine with some extras to keep track of all game elements.
 * 
 * @author mdriesen
 */
public class Engine extends FiniteStateMachine implements Runnable {
	private static UserInterface UI;
	private static Timer timer;				// klok
	private static Player player;
	private static ScriptEngine engine;	
	private static FileSystem files;		// virtual file system
	private static PhysicsSystem physics;	// de physics engine
	private static Logger logger;
	private static QuestTracker quests;	
	private static MBassador<EventObject> bus;	// event bus
	private static TaskQueue queue;
	
	private static UIDStore store;
	private static ResourceManager resources;
	private static Atlas atlas;
	private Configuration config;

	/**
	 * Initializes the engine. 
	 */
	public Engine() {
		// engine componenten opzetten
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
		timer = new Timer();
		files = new FileSystem();
		physics = new PhysicsSystem();
		quests = new QuestTracker();
		timer.addListener(quests);
		queue = new TaskQueue(timer);
		initEvents();
		resources = new ResourceManager();
		store = new UIDStore();
		atlas = new Atlas();
		config = new Configuration(queue);
	}
	
	private void initEvents() {
		EventAdapter adapter = new EventAdapter(quests);
		bus = new MBassador<EventObject>(BusConfiguration.Default());
		bus.subscribe(this);
		bus.subscribe(queue);
		bus.subscribe(new CombatHandler());	
		bus.subscribe(new DeathHandler());
		bus.subscribe(adapter);
	}
	
	private void initFSM() {
		// main menu 
		MainMenuState main = new MainMenuState(this, config);

		// alle game substates. 
		GameState game = new GameState(this, queue, atlas);
		bus.subscribe(game);
		// deuren
		DoorState doors = new DoorState(game);
		// locks
		LockState locks = new LockState(game);
		// bumpen
		BumpState bump = new BumpState(game, atlas);
		// move
		MoveState move = new MoveState(game, atlas);
		// aim
		AimState aim = new AimState(game, atlas);

		// dialog state
		DialogState dialog = new DialogState(this, config);
		// inventory state
		InventoryState inventory = new InventoryState(this, config, atlas);
		// containers
		ContainerState container = new ContainerState(this, config, atlas);
		// journal state
		JournalState journal = new JournalState(this);
		
		// start states setten
		addStartStates(main, move);
		
		// transitions
		addTransition(new Transition(main, game, "start"));
		addTransition(new Transition(journal, game, "cancel"));
		addTransition(new Transition(game, journal, "journal"));
		addTransition(new Transition(inventory, game, "cancel"));
		addTransition(new Transition(game, inventory, "inventory"));
		addTransition(new Transition(aim, move, "return"));
		addTransition(new Transition(move, aim, "aim"));
		addTransition(new Transition(aim, dialog, "dialog"));
		addTransition(new Transition(dialog, game, "return"));
		addTransition(new Transition(move, doors, "door"));
		addTransition(new Transition(aim, doors, "door"));
		addTransition(new Transition(doors, move, "return"));
		addTransition(new Transition(move, locks, "lock"));
		addTransition(new Transition(locks, move, "return"));
		addTransition(new Transition(game, container, "container"));
		addTransition(new Transition(container, game, "return"));
		addTransition(new Transition(dialog, game, "return"));
		addTransition(new Transition(move, bump, "bump"));
		addTransition(new Transition(bump, move, "return"));
		addTransition(new Transition(bump, dialog, "dialog"));
	}
	
	/**
	 * This method is the run method of the gamethread. It initializes the finite state 
	 * machine and user interface, and starts executing the first engine state.
	 */
	public void run() {
		initFSM();
		// UI dingen
		UI = new UserInterface(config.getProperty("title"));
		UI.show();
		// eerste state initialiseren en wachten op input
		start(new TransitionEvent("start"));
	}
	
	/**
	 * Convenience method to post an event to the event bus.
	 * 
	 * @param message
	 */
	public static void post(EventObject message) {
		bus.publishAsync(message);
	}
	
	@Handler public void handleTransition(TransitionEvent te) {
		transition(te);
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
		return player;
	}
	
	public static QuestTracker getQuestTracker() {
		return quests;
	}
	
	/**
	 * @return	the main window of the user interface
	 */
	public static UserInterface getUI() {
		return UI;
	}
	
	/**
	 * @return	the timer
	 */
	public static Timer getTimer() {
		return timer;
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
	
	public Configuration getConfig() {
		return config;
	}
	
	public static UIDStore getStore() {
		return store;
	}
	
	public static ResourceManager getResources() {
		return resources;
	}

	public static Atlas getAtlas() {
		return atlas;
	}
	
	public static TaskQueue getQueue() {
		return queue;
	}

/*
 * alle setters
 */	
	/**
	 * Sets the player.
	 * 
	 * @param p	the player
	 */
	public static void setPlayer(Player p) {
		player = p;
		// toevoegen aan script engine
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
