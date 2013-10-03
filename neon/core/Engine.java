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
import neon.resources.CClient;
import neon.resources.ResourceManager;
import neon.resources.builder.IniBuilder;
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
	// wordt door engine geïnitialiseerd
	private static UserInterface UI;
	private static ScriptEngine engine;	
	private static FileSystem files;		// virtual file system
	private static PhysicsSystem physics;	// de physics engine
	private static Logger logger;
	private static QuestTracker quests;	
	private static MBassador<EventObject> bus;	// event bus
	private static TaskQueue queue;
	private static ResourceManager resources;

	// wordt extern geset
	private static Game game;
	
	private Configuration config;

	/**
	 * Initializes the engine. Most of the engine (server) configuration is 
	 * done in the constructor. User interface (client) configuration is mainly
	 * done in the {@code run()} method, as this is best done on the swing
	 * event-dispatch thread.
	 */
	public Engine() {
		// engine componenten opzetten
		engine = new ScriptEngineManager().getEngineByName("JavaScript");
		files = new FileSystem();
		physics = new PhysicsSystem();
		queue = new TaskQueue();
		
		// create a resourcemanager to keep track of all the resources
		resources = new ResourceManager();
		// we use an IniBuilder to add all resources to the manager
		new IniBuilder("neon.ini", files, queue).build(resources);

		// nog engine componenten opzetten
		logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		quests = new QuestTracker();
		initEvents();
		config = new Configuration(resources);
	}
	
	private void initEvents() {
		EventAdapter adapter = new EventAdapter(quests);
		bus = new MBassador<EventObject>(BusConfiguration.Default());
		bus.subscribe(this);
		bus.subscribe(queue);
		bus.subscribe(new CombatHandler());	
		bus.subscribe(new DeathHandler());
		bus.subscribe(adapter);
		bus.subscribe(quests);
	}
	
	private void initFSM() {
		// main menu 
		MainMenuState main = new MainMenuState(this);

		// alle game substates. 
		GameState game = new GameState(this, queue, bus);
		bus.subscribe(game);
		// deuren
		DoorState doors = new DoorState(game);
		// locks
		LockState locks = new LockState(game);
		// bumpen
		BumpState bump = new BumpState(game);
		// move
		MoveState move = new MoveState(game);
		// aim
		AimState aim = new AimState(game);

		// dialog state
		DialogState dialog = new DialogState(this);
		// inventory state
		InventoryState inventory = new InventoryState(this);
		// containers
		ContainerState container = new ContainerState(this);
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
		CClient client = (CClient)resources.getResource("client", "config");
		UI = new UserInterface(client.getTitle());
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
		return game.getPlayer();
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
	
	public Configuration getConfig() {
		return config;
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

/*
 * alle setters
 */	
	/**
	 * Sets the player.
	 * 
	 * @param p	the player
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
