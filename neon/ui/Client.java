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

import java.io.File;
import java.util.EventObject;
import javax.swing.UIManager;
import de.muntjak.tinylookandfeel.Theme;
import neon.core.Engine;
import neon.core.event.LoadEvent;
import neon.core.event.MagicEvent;
import neon.core.event.MessageEvent;
import neon.core.event.UpdateEvent;
import neon.core.handlers.MagicHandler;
import neon.entities.Player;
import neon.resources.CClient;
import neon.systems.io.Port;
import neon.ui.states.*;
import neon.util.fsm.FiniteStateMachine;
import neon.util.fsm.Transition;
import neon.util.fsm.TransitionEvent;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

public class Client implements Runnable {
	private UserInterface ui;
	private final FiniteStateMachine fsm;
	private final MBassador<EventObject> bus;
	private final String version;
	
	public Client(Port port, String version) {
		bus = port.getBus();
		this.version = version;
		fsm = new FiniteStateMachine();
		bus.subscribe(new BusAdapter());
	}
	
	@Override
	public void run() {
		initUI();
		initFSM();
	}
	
	private void initUI() {
		// look and feel setten
		try {
			Theme.loadTheme(new File("data/neon.theme"));
			UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// UI dingen
		CClient client = (CClient)Engine.getResources().getResource("client", "config");
		ui = new UserInterface(client.getTitle());
		ui.show();
	}
	
	private void initFSM() {
		// main menu 
		MainMenuState main = new MainMenuState(fsm, bus, ui, version);

		// alle game substates. 
		GameState game = new GameState(fsm, bus, ui);
		bus.subscribe(game);
		// deuren
		DoorState doors = new DoorState(game, bus, ui);
		// locks
		LockState locks = new LockState(game, bus, ui);
		// bumpen
		BumpState bump = new BumpState(game, bus, ui);
		// move
		MoveState move = new MoveState(game, bus);
		// aim
		AimState aim = new AimState(game, bus, ui);

		// dialog state
		DialogState dialog = new DialogState(fsm, bus, ui);
		// inventory state
		InventoryState inventory = new InventoryState(fsm, bus, ui);
		// containers
		ContainerState container = new ContainerState(fsm, bus, ui);
		// journal state
		JournalState journal = new JournalState(fsm, bus, ui);
		
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
	
	@Listener(references = References.Strong)
	private class BusAdapter {
		@Handler public void transition(TransitionEvent te) {
			fsm.transition(te);
		}

	
		@Handler public void update(UpdateEvent ue) {
			ui.update();
		}
		
		@Handler public void message(MessageEvent me) {
			ui.showMessage(me.toString(), me.getDuration());
		}
		
		@Handler public void load(LoadEvent le) {
			if(le.getMode() == LoadEvent.Mode.DONE) {
				fsm.transition(new TransitionEvent("start"));
			}
		}
		
		@Handler public void result(MagicEvent.Result me) {
			if(me.getCaster() instanceof Player) {
				switch(me.getResult()) {
				case MagicHandler.MANA: ui.showMessage("Not enough mana to cast this spell.", 1); break;
				case MagicHandler.RANGE: ui.showMessage("Target out of range.", 1); break;
				case MagicHandler.NONE: ui.showMessage("No spell equiped.", 1); break;
				case MagicHandler.SKILL: ui.showMessage("Casting failed.", 1); break;
				case MagicHandler.OK: ui.showMessage("Spell cast.", 1); break;
				case MagicHandler.NULL: ui.showMessage("No target selected.", 1); break;
				case MagicHandler.LEVEL: ui.showMessage("Spell is too difficult to cast.", 1); break;
				case MagicHandler.SILENCED: ui.showMessage("You are silenced", 1); break;
				case MagicHandler.INTERVAL: ui.showMessage("Can't cast this power yet.", 1); break;
				}
			}
		}
	}
}
