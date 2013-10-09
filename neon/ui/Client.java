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

package neon.ui;

import java.util.EventObject;
import neon.core.Engine;
import neon.core.event.TaskQueue;
import neon.resources.CClient;
import neon.systems.io.Port;
import neon.ui.states.AimState;
import neon.ui.states.BumpState;
import neon.ui.states.ContainerState;
import neon.ui.states.DialogState;
import neon.ui.states.DoorState;
import neon.ui.states.GameState;
import neon.ui.states.InventoryState;
import neon.ui.states.JournalState;
import neon.ui.states.LockState;
import neon.ui.states.MainMenuState;
import neon.ui.states.MoveState;
import neon.util.fsm.FiniteStateMachine;
import neon.util.fsm.Transition;
import neon.util.fsm.TransitionEvent;
import net.engio.mbassy.bus.MBassador;

public class Client implements Runnable {
	private static UserInterface ui;
	private static FiniteStateMachine fsm;
	
	private final TaskQueue queue;
	private final MBassador<EventObject> bus;
	private final Port port;
	
	public Client(Port port, TaskQueue queue, MBassador<EventObject> bus) {
		// TODO: bus en queue uit client halen
		this.port = port;
		this.queue = queue;
		this.bus = bus;
		fsm = new FiniteStateMachine();
	}
	
	@Override
	public void run() {
		initUI();
		initFSM();
	}
	
	private void initUI() {
		// UI dingen
		CClient client = (CClient)Engine.getResources().getResource("client", "config");
		ui = new UserInterface(client.getTitle());
		ui.show();
	}
	
	private void initFSM() {
		// main menu 
		MainMenuState main = new MainMenuState(fsm);

		// alle game substates. 
		GameState game = new GameState(fsm, queue, bus);
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
		DialogState dialog = new DialogState(fsm);
		// inventory state
		InventoryState inventory = new InventoryState(fsm);
		// containers
		ContainerState container = new ContainerState(fsm);
		// journal state
		JournalState journal = new JournalState(fsm);
		
		// start states setten
		fsm.addStartStates(main, move);
		
		// transitions
		fsm.addTransition(new Transition(main, game, "start"));
		fsm.addTransition(new Transition(journal, game, "cancel"));
		fsm.addTransition(new Transition(game, journal, "journal"));
		fsm.addTransition(new Transition(inventory, game, "cancel"));
		fsm.addTransition(new Transition(game, inventory, "inventory"));
		fsm.addTransition(new Transition(aim, move, "return"));
		fsm.addTransition(new Transition(move, aim, "aim"));
		fsm.addTransition(new Transition(aim, dialog, "dialog"));
		fsm.addTransition(new Transition(dialog, game, "return"));
		fsm.addTransition(new Transition(move, doors, "door"));
		fsm.addTransition(new Transition(aim, doors, "door"));
		fsm.addTransition(new Transition(doors, move, "return"));
		fsm.addTransition(new Transition(move, locks, "lock"));
		fsm.addTransition(new Transition(locks, move, "return"));
		fsm.addTransition(new Transition(game, container, "container"));
		fsm.addTransition(new Transition(container, game, "return"));
		fsm.addTransition(new Transition(dialog, game, "return"));
		fsm.addTransition(new Transition(move, bump, "bump"));
		fsm.addTransition(new Transition(bump, move, "return"));
		fsm.addTransition(new Transition(bump, dialog, "dialog"));
		
		// en starten
		fsm.start(new TransitionEvent("start"));
	}
	
	/**
	 * @return	the main window of the user interface
	 */
	public static UserInterface getUI() {
		return ui;
	}
}
